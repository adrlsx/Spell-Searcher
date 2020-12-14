import org.apache.spark.sql.functions.{array_contains, collect_set, explode}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{ColumnName, DataFrame, SparkSession}

object SparkRequest {
  //Démarrer Spark
  private val spark: SparkSession = SparkSession.builder.appName("Advanced Spell Search").master("local[4]").getOrCreate()
  private val sc: spark.sparkContext.type = spark.sparkContext
  sc.setLogLevel("WARN")

  private val creature_path: String = "output/creature.jsonl"
  private val spell_path: String = "output/spell.jsonl"
  private val df_creature: DataFrame = load_json(creature_path)
  private val df_spell: DataFrame = load_json(spell_path)
  private val reverse_index: DataFrame = reverseIndex(df_creature)
  df_creature.printSchema()
  println()
  df_spell.printSchema()
  println()
  reverse_index.printSchema()

  private def load_json(json_path: String, multiline: Boolean = true): DataFrame = {
    // JSON: https://spark.apache.org/docs/latest/sql-data-sources-json.html
    // Saves the schema of the first line
    val json_schema: StructType = spark.read.option("multiline", value = multiline).json(json_path).schema
    // Read all the JSON file with the given schema
    spark.read.schema(json_schema).json(json_path)
  }

  private def reverseIndex(df: DataFrame): DataFrame = {
    // Primitive types (Int, String, etc) and Product types (case classes) encoders are
    // supported by importing this when creating a Dataset.
    import spark.implicits._
    // explode: https://sparkbyexamples.com/spark/explode-spark-array-and-map-dataframe-column/
    val reverse_index: DataFrame = df.select(explode($"spells").as("spells"), $"name")
    // groupBy: https://sparkbyexamples.com/spark/using-groupby-on-dataframe/
    // collect_set: https://stackoverflow.com/questions/43357727/how-to-do-opposite-of-explode-in-pyspark
    reverse_index.groupBy("spells").agg(collect_set("name").as("creatures"))
  }

  def sortRequest(df: DataFrame, infoToSort: String, inputArray: Array[String], operator: String) : DataFrame = {
    var spell_list: DataFrame = df
    if (operator == "OR"){
      spell_list = spell_list.where(spell_list(infoToSort) isin inputArray)
    }
    else if (operator == "AND"){
      for(spellClass <- inputArray){
        spell_list = spell_list.where(array_contains(spell_list("classes.name"), spellClass))
      }
    }
    spell_list
  }

  def getSpellList(classArray: Array[String], classOperator: String, schoolArray: Array[String], componentArray: Array[String],
                   componentOperator: String, spellResistance: String, spellName: String, description: Array[String]): DataFrame = {

    val classSort: DataFrame = sortRequest(df_spell, "class.name", classArray, classOperator)
    val schoolSort: DataFrame = sortRequest(classSort, "school", schoolArray, "OR")
    val componentSort: DataFrame = sortRequest(schoolSort, "components", componentArray, componentOperator)
    val resistanceSort: DataFrame = componentSort.where(componentSort("spell_resistance") === spellResistance)
    val nameSort: DataFrame = resistanceSort.where(resistanceSort("name") === spellName)

    nameSort
  }

  def getSpellInfo(spellName: String): DataFrame = {
    df_spell.where(df_spell("name") === spellName)
  }

  def getCreatureList(spellName: String): DataFrame = {
    reverse_index.where(reverse_index("spells") === spellName).select(reverse_index("creatures"))
  }

  def getCreatureInfo(creatureName: String): DataFrame = {
    df_creature.filter(df_creature("name") === creatureName)
  }
}
