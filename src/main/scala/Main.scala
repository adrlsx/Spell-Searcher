import sys.process._
import com.formdev.flatlaf.FlatIntelliJLaf

object Main {
  def main(args: Array[String]) {
    println("Loading spell and creature data from https://www.aonprd.com/")
    // Execute python crawler
    // External command: https://alvinalexander.com/scala/scala-execute-exec-external-system-commands-in-scala/
    val crawler: Int = 0
    //val crawler: Int = "python3 src/main/python/main.py".!
    println("Crawling done. Starting GUI ...")

    if (crawler == 0){
      FlatIntelliJLaf.install()
      // Initialize the dynamic elements necessary for the construction of the main window
      val searcher = Searcher

      // Initialize Apache Spark module and the reverse index
      // val sparkRequest = SparkRequest

      val mainUI = new SearchFrame(searcher)
      mainUI.centerOnScreen()
      mainUI.open()
    }
    else {
      println("There was an error with the python crawler.\nExit code: " + crawler)
    }
  }
}