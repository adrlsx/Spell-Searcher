import org.apache.spark.sql.DataFrame

import java.io.FileNotFoundException
import scala.io.Source

/*
 *  Object that contain class, school and component list for dynamically create the interface
 *  Also contains function for reserve search
 */
object Searcher {
  private val allClassArray = getArrayFromJsonFile("output/class.jsonl")
  private val allSchoolArray = getArrayFromJsonFile("output/school.jsonl")
  private val allComponentArray = Array("F", "M", "S", "V", "AF", "DF", "HD", "XP", "DF/F", "DF/M", "F/DF", "M/F", "M/DF")

  // Return an array from a JSON Lines (.jsonl) file
  private def getArrayFromJsonFile(filename: String): Array[String] = {
    try {
      // Open file
      val fileBuffer = Source.fromFile(filename)

      // Get file first line
      var line: String = fileBuffer.getLines.next()

      // Close file
      fileBuffer.close()

      // Remove start and end part from json using regex, like '{"classes": [' and ']}' in class.jsonl
      line = line.replaceFirst("^\\{.*\\[\"", "")
      line = line.replaceFirst("\"]}$", "")

      // Return a sorted string array
      line.split("\", \"").sorted

    } catch {
      case _:
        // Print error and exit if file not found (required)
        FileNotFoundException => println("Couldn't find required file: " + filename)
        sys.exit(1)
    }
  }

  // Getters
  def getAllClassName: Array[String] = { allClassArray }
  def getAllSchoolName: Array[String] = { allSchoolArray }
  def getAllComponentName: Array[String] = { allComponentArray }

  def getNbClass: Int = { allClassArray.length }
  def getNbSchool: Int = { allSchoolArray.length }



  // Functions declaration for link with reverse search

  def getSpellList(classArray: Array[String], classOperator: String, schoolArray: Array[String], componentArray: Array[String],
                   componentOperator: String, spellResistance: String, spellName: String, description: Array[String]): DataFrame = {

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
