import SwingGeneralFunc.{getHorizontalBox, getWebsiteBtnBox}

import scala.swing.{BoxPanel, Frame, Orientation, Swing}

class CreatureFrame(searcher: Searcher.type, sparkRequest: SparkRequest.type, creatureName: String) extends Frame {

  sparkRequest.getCreatureInfo(creatureName)
  val creatureUrl: String = "https://aonprd.com/MonsterDisplay.aspx?ItemName=Theletos"
  val creatureSpells: String = "Effect, Glitterdust, Cure, Frequency, Save"
  val creatureDescription: String = "Four bandy limbs, each splitting at the elbow into two three-fingered forearms, emerge from this creature’s crystalline body."

  // Set window title
  title = "Creature: " + creatureName

  contents = new BoxPanel(Orientation.Vertical) {
    // Set frame border margin
    border = Swing.EmptyBorder(10, 10, 10, 10)

    // Add creature web page button
    contents += getWebsiteBtnBox(creatureUrl)
    contents += Swing.VStrut(5)

    // Add creature name box
    contents += getHorizontalBox("Name", creatureName)
    contents += Swing.VStrut(5)

    // Add creature spells box
    contents += getHorizontalBox("Spells", creatureSpells)
    contents += Swing.VStrut(5)

    // Add creature description box
    contents += getHorizontalBox("Description", creatureDescription)
  }
}