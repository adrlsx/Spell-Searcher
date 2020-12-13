import org.apache.spark.sql.functions.{collect_set, explode}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkRequest {
  //Démarrer Spark
  private val spark: SparkSession = SparkSession.builder.appName("Advanced Spell Search").master("local[4]").getOrCreate()
  private val sc: spark.sparkContext.type = spark.sparkContext
  sc.setLogLevel("WARN")

  private val creature_path: String = "output/creature.jsonl"
  private val spell_path: String = "output/spell.jsonl"
  private val df_creature: DataFrame = load_json(creature_path, multiline = true)
  private val df_spell: DataFrame = load_json(spell_path, multiline = false)
  //private val reverse_index: DataFrame = reverseIndex(df_creature)
  println(df_creature.schema.prettyJson)
  println(df_spell.schema.prettyJson)

  private def load_json(json_path: String, multiline: Boolean): DataFrame = {
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

  def getSpellList(classArray: Array[String], classOperator: String, schoolArray: Array[String], componentArray: Array[String],
                   componentOperator: String, spellResistance: String, spellName: String, description: Array[String]): DataFrame = {
    var classRequest = ""
    for(spellClass <- classArray){
      classRequest += " class = " + spellClass + " " + classOperator
    }

    return null
  }

  def getSpellInfo(spellName: String): DataFrame = {

    return null
  }

  def getCreatureList(spellName: String): DataFrame = {

    return null
  }

  def getCreatureInfo(creatureName: String): DataFrame = {

    return null
  }
}
