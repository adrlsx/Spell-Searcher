import org.apache.spark.sql.functions.{collect_set, explode}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SparkSession}

object SparkRequest {
  //Démarrer Spark
  val spark: SparkSession = SparkSession.builder.appName("Advanced Spell Search").master("local[4]").getOrCreate()
  val sc: spark.sparkContext.type = spark.sparkContext
  sc.setLogLevel("WARN")

  val creature_path: String = "output/creature.jsonl"

  def load_json(json_path: String): DataFrame = {
    // JSON: https://spark.apache.org/docs/latest/sql-data-sources-json.html
    // Saves the schema of the first line
    val json_schema: StructType = spark.read.option("multiline", "true").json(json_path).schema
    // Read all the JSON file with the given schema
    spark.read.schema(json_schema).json(json_path)
  }

  def reverseIndex(df: DataFrame): DataFrame = {
    // Primitive types (Int, String, etc) and Product types (case classes) encoders are
    // supported by importing this when creating a Dataset.
    import spark.implicits._
    // explode: https://sparkbyexamples.com/spark/explode-spark-array-and-map-dataframe-column/
    val reverse_index: DataFrame = df.select(explode($"spells").as("spells"), $"name")
    // groupBy: https://sparkbyexamples.com/spark/using-groupby-on-dataframe/
    // collect_set: https://stackoverflow.com/questions/43357727/how-to-do-opposite-of-explode-in-pyspark
    reverse_index.groupBy("spells").agg(collect_set("name").as("creatures"))
  }

  val df: DataFrame = load_json(creature_path)
  val reverse_index: DataFrame = reverseIndex(df)
  reverse_index.show(false)
}
