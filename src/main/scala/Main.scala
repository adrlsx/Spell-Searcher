import com.formdev.flatlaf.FlatIntelliJLaf

object Main {
  def main(args: Array[String]) {
    FlatIntelliJLaf.install()
    // Initialize the dynamic elements necessary for the construction of the main window
    val searcher = Searcher

    val mainUI = new SearchFrame(searcher)
    mainUI.centerOnScreen()
    mainUI.open()

    mainUI.disableResearch("Loading Apache Spark")

    // Initialize Apache Spark module with the reverse index, and pass it to interface
    mainUI.setSparkRequest(SparkRequest)

    mainUI.enableResearch("Waiting for search request")
  }
}