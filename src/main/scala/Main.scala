import com.formdev.flatlaf.FlatIntelliJLaf
import sys.process._
import scala.reflect.io.File

object Main {
  def main(args: Array[String]) {

    firstExecutionCheck()

    FlatIntelliJLaf.install()

    val mainUI = new SearchFrame
    mainUI.centerOnScreen()
    mainUI.open()

    mainUI.disableResearch("Loading Apache Spark")

    // Initialize Apache Spark module with the reverse index, and pass it to interface
    mainUI.setSparkRequest(SparkRequest)

    mainUI.enableResearch("Waiting for search request")
  }

  private def firstExecutionCheck(): Unit = {
    val directory_path = "output"
    val output = File(directory_path)
    // verify if the needed category exists
    if (! output.exists || ! output.isDirectory){
      updateDatabase()
    }
    else{
      val json_list = Array("class", "creature", "school", "spell")
      for (json_file <- json_list) {
        val current_file = File(s"$directory_path/$json_file.jsonl")
        // verify is all the json files need are valid
        if (! current_file.exists || ! current_file.isFile || current_file.isEmpty){
          updateDatabase()
        }
      }
    }
  }

  private def updateDatabase(): Unit = {
    val crawler: Int = "python3 src/main/python/main.py".!
    if (crawler != 0){
      println(s"There was an error with the python crawler.\nExit code: $crawler")
      sys.exit(crawler)
    }
  }
}