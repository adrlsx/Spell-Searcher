import sys.process._
import com.formdev.flatlaf.FlatIntelliJLaf

object Main {
  def main(args: Array[String]) {
    println("Loading spell and creature data from https://www.aonprd.com/")
    // Execute python crawler
    // External command: https://alvinalexander.com/scala/scala-execute-exec-external-system-commands-in-scala/
    val crawler: Int = 0
    //val crawler: Int = "python3 src/main/python/main.py".!
    println("\nCrawling done. Starting GUI ...")

    if (crawler == 0){
      FlatIntelliJLaf.install()
      val searcher = Searcher
      val mainUI = new SearchFrame(searcher)
      mainUI.centerOnScreen()
      mainUI.visible = true
    }
    else {
      println("There was an error with the python crawler.\nExit code: " + crawler)
    }
  }
}