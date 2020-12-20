import org.apache.spark.sql.functions.{array_contains, arrays_overlap, collect_set, explode, typedLit}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

import scala.collection.mutable

object SparkRequest {
  // Start Apache Spark
  private val spark: SparkSession = SparkSession.builder.appName("Advanced Spell Search").master("local[*]").getOrCreate()
  private val sc: spark.sparkContext.type = spark.sparkContext
  sc.setLogLevel("WARN")
  // Primitive types (Int, String, etc) and Product types (case classes) encoders are
  // supported by importing this when creating a Dataset.
  import spark.implicits._

  private val creature_path: String = "output/creature.jsonl"
  private val spell_path: String = "output/spell.jsonl"
  private val df_creature: DataFrame = load_json(creature_path).persist()
  private val df_spell: DataFrame = load_json(spell_path).persist()
  private val reverse_index: DataFrame = reverseIndex(df_creature).persist()

  private def load_json(json_path: String, multiline: Boolean = true): DataFrame = {
    // JSON: https://spark.apache.org/docs/latest/sql-data-sources-json.html
    // Saves the schema of the first line
    val json_schema: StructType = spark.read.option("multiline", value = multiline).json(json_path).schema
    // Read all the JSON file with the given schema
    spark.read.schema(json_schema).json(json_path)
  }

  private def reverseIndex(df: DataFrame): DataFrame = {
    // explode: https://sparkbyexamples.com/spark/explode-spark-array-and-map-dataframe-column/
    val reverse_index: DataFrame = df.select(explode($"spells").as("spells"), $"name")
    // groupBy: https://sparkbyexamples.com/spark/using-groupby-on-dataframe/
    // collect_set: https://stackoverflow.com/questions/43357727/how-to-do-opposite-of-explode-in-pyspark
    reverse_index.groupBy("spells").agg(collect_set("name").as("creatures"))
  }

  private def dfToMap(df: DataFrame): Map[String, String] = {
    var infoMap: Map[String, String] = Map()
    for(key <- df.columns){
      val value: String = df.select(key).first().toString()

      var formattedValue = value.stripPrefix("[").stripSuffix("]")

      // Only remove WrappedArray(.....) for necessary key, else can remove ending ")" for spells like "Ablative sphere (garundi)"
      if (key.equals("classes") || key.equals("components") || key.equals("description") || key.equals("spells")) {
        formattedValue = formattedValue.replaceAll("WrappedArray\\(", "").replaceAll("\\)", "")
      }

      infoMap += (key -> formattedValue)
    }

    infoMap
  }

  private def sanitize(string: String): String = string.toLowerCase.capitalize

  private def sortRequest(df: DataFrame, infoToSort: String, inputArray: List[String], operator: String) : DataFrame = {
    // Union start with an empty dataframe and will increase its size every time
    var spell_list: DataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], df.schema)

    if (operator == "OR") {
      // https://stackoverflow.com/questions/43904622/how-to-filter-spark-dataframe-by-array-column-containing-any-of-the-values-of-so
      // If there is at least one of the selection (inputArray) in the column then the two arrays overlap
      spell_list = df.where(arrays_overlap(df(infoToSort), typedLit(inputArray)))
    }
    else if (operator == "AND") {
      // Intersections start with a full dataframe and will reduce its size every time
      spell_list = df
      // Test every value chosen by the user
      for(value <- inputArray) {
        // Each row contains a array, so checks the existence of the value for each array
        spell_list = spell_list.where(array_contains(spell_list(infoToSort), value))
      }
    }

    spell_list
  }

  def getSpellList(classArray: List[String], classOperator: String, schoolArray: List[String], componentArray: List[String],
                   componentOperator: String, spellResistance: String, description: List[String]): List[String] = {
    var df_sort: DataFrame = df_spell

    if (classArray.nonEmpty) {
      df_sort = sortRequest(df_sort, "classes", classArray, classOperator)
    }

    if (schoolArray.nonEmpty) {
      // For schools, each row is a single value and not an array so a specific sort is implemented
      // If the value is one of the selection (schoolArray) then return the spell
      df_sort = df_sort.where(array_contains(typedLit(schoolArray), $"school"))
    }

    if (componentArray.nonEmpty) {
      df_sort = sortRequest(df_sort, "components", componentArray, componentOperator)
    }

    if (spellResistance.nonEmpty) {
      df_sort = df_sort.where($"spell_resistance" === spellResistance)
    }

    if (description.nonEmpty) {
      df_sort = sortRequest(df_sort, "description", description, "AND")
    }

    val formatted_df = df_sort.select($"name").map(row => row.toString().stripPrefix("[").stripSuffix("]"))

    formatted_df.collect().toList
  }

  def getSpellInfo(spellName: String): Map[String, String] = {
    val spellInfo: DataFrame = df_spell.where($"name" === sanitize(spellName))
    dfToMap(spellInfo)
  }

  def getCreatureList(spellName: String): List[String] = {
    val creatureList = reverse_index.where($"spells" === sanitize(spellName)).select($"creatures")
    var formattedList: List[String] = List[String]()
    if (! creatureList.isEmpty){
      formattedList = creatureList.map(row => row(0).asInstanceOf[mutable.WrappedArray[String]].toList).first()
    }
    formattedList
  }

  def getCreatureInfo(creatureName: String): Map[String, String] = {
    val creatureInfo = df_creature.where($"name" === sanitize(creatureName))
    dfToMap(creatureInfo)
  }
}
