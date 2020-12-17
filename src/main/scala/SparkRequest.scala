import org.apache.spark.sql.functions.{array_contains, collect_set, explode}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{ColumnName, DataFrame, Dataset, Row, SparkSession}

object SparkRequest {
  //Démarrer Spark
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
      val value: String = df.select(key).first().toString().replaceAll("\\[", "").replaceAll("]", "")
      infoMap += (key -> value)
    }
    infoMap
  }

  private def dfToArray(df: DataFrame, columnName: ColumnName): Array[String] = {
    val infoArray = df.select(columnName).map(row => row.toString().stripPrefix("[").stripSuffix("]"))
    infoArray.collect()
  }

  private def sanitize(string: String): String = string.toLowerCase.capitalize

  def sortComponent(df: DataFrame, infoToSort: String, inputArray: Array[String], operator: String) : DataFrame = {
    var spell_list: DataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], df.schema)

    if (operator == "OR") {
      for(value <- inputArray) {
        val selection = df.where(array_contains(df(infoToSort), value))
        spell_list = spell_list.union(selection)
      }
    }
    else if (operator == "AND") {
      spell_list = df
      for(value <- inputArray) {
        val selection = df.where(array_contains(df(infoToSort), value))
        spell_list = spell_list.intersect(selection)
      }
    }

    spell_list
  }

  def sortSchool(df: DataFrame, infoToSort: String, inputArray: Array[String], operator: String) : DataFrame = {
    var spell_list: DataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], df.schema)
    if (operator == "OR") {
      for(value <- inputArray) {
        val selection = df.where(df(infoToSort) === value)
        spell_list = spell_list.union(selection)
      }
    }
    else if (operator == "AND") {
      spell_list = df
      for(value <- inputArray) {
        val selection = df.where(df(infoToSort) === value)
        spell_list = spell_list.intersect(selection)
      }
    }

    spell_list
  }

  def getSpellList(classArray: Array[String], classOperator: String, schoolArray: Array[String], componentArray: Array[String],
                   componentOperator: String, spellResistance: String, description: Array[String]): Array[String] = {
    var df_sort: DataFrame = df_spell
    if (classArray.nonEmpty) {
      //df_sort = sortRequest(df_sort, "classes.name", classArray, classOperator)
    }
    if (schoolArray.nonEmpty) {
      df_sort = sortSchool(df_sort, "school", schoolArray, "OR")
    }
    if (componentArray.nonEmpty) {
      df_sort = sortComponent(df_sort, "components", componentArray, componentOperator)
    }
    if (spellResistance.nonEmpty) {
      df_sort = df_sort.where($"spell_resistance" === spellResistance)
    }
    dfToArray(df_sort, $"name")
  }

  def getSpellInfo(spellName: String): Map[String, String] = {
    val spellInfo: DataFrame = df_spell.where($"name" === sanitize(spellName))
    dfToMap(spellInfo)
  }

  def getCreatureList(spellName: String): Array[String] = {
    val df = reverse_index.where($"spells" === sanitize(spellName))
    dfToArray(df, $"creatures")
  }

  def getCreatureInfo(creatureName: String): Map[String, String] = {
    val creatureInfo = df_creature.filter($"name" === sanitize(creatureName))
    dfToMap(creatureInfo)
  }
}
