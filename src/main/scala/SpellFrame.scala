import SwingGeneralFunc.{addSeparator, getHorizontalBox, getWebsiteBtnBox}

import java.awt.Image
import java.awt.event.{MouseAdapter, MouseEvent}
import javax.swing._
import scala.swing._

class SpellFrame(searcher: Searcher.type, sparkRequest: SparkRequest.type, spellName: String) extends Frame {

  sparkRequest.getSpellInfo(spellName)
  sparkRequest.getCreatureList(spellName)

  val spellUrl: String = "https://aonprd.com/SpellDisplay.aspx?ItemName=Ablative%20Barrier"
  val spellSchool: String = "Conjuration"
  val spellClasses: String = "alchemist 2, arcanist 3, bloodrager 2, investigator 2, magus 2, occultist 2, psychic 3, sorcerer 3, summoner 2, summoner (unchained) 3, wizard 3"
  val spellComponents: String = "V, S, M"
  val spellResistance: String = "false"
  val spellImgPath: String = "input/img/ablative barrier.png"

  // var is mutable contrary to val
  var nbResult: Int = 0

  // Set Window title
  title = "Spell: " + spellName

  var imageIcon: ImageIcon = new ImageIcon(spellImgPath); // load the image to a imageIcon
  val image: Image = imageIcon.getImage; // transform it
  val newImg: Image = image.getScaledInstance(409, 583,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
  imageIcon = new ImageIcon(newImg);  // transform it back

  // Initialisation for results
  val labelNbResult = new Label("Number: " + nbResult)
  val jPanelResult = new JPanel()
  jPanelResult.setLayout(new BoxLayout(jPanelResult, BoxLayout.Y_AXIS))

  contents = new BoxPanel(Orientation.Vertical) {
    // Set frame border margin
    border = Swing.EmptyBorder(10, 10, 10, 10)

    contents += new BoxPanel(Orientation.Horizontal) {
      // Add spell picture
      contents += new Label { icon = imageIcon }

      contents += Swing.HStrut(10)

      contents += new BoxPanel(Orientation.Vertical) {

        contents += new BoxPanel(Orientation.Horizontal) {
          contents += new Label("Creature Result: ")
          contents += Swing.HGlue
          contents += labelNbResult
        }

        contents += Swing.VStrut(5)

        val jScrollPaneResult = new JScrollPane(jPanelResult)
        jScrollPaneResult.setPreferredSize(new Dimension(300, 0))

        // Use Component.wrap() for link between javax.swing and scala.swing
        contents += Component.wrap(jScrollPaneResult)
      }
    }

    addSeparator(contents)

    // Add spell web page button
    contents += getWebsiteBtnBox(spellUrl)
    contents += Swing.VStrut(5)

    // Add spell name box
    contents += getHorizontalBox("Name", spellName)
    contents += Swing.VStrut(5)

    // Add spell schools box
    contents += getHorizontalBox("School", spellSchool)
    contents += Swing.VStrut(5)

    // Add spell classes box
    contents += getHorizontalBox("Classes", spellClasses)
    contents += Swing.VStrut(5)

    // Add spell components box
    contents += getHorizontalBox("Components", spellComponents)
    contents += Swing.VStrut(5)

    // Add spell resistance box
    contents += getHorizontalBox("Spell Resistance", spellResistance)
  }

  addCreatureResult()

  def addCreatureResult(): Unit = {
    for (_ <- 0 until 50) {
      val jLabel = new JLabel("TEST")

      jLabel.addMouseListener(new MouseAdapter() {
        // https://stackoverflow.com/questions/2440134/is-this-the-proper-way-to-initialize-null-references-in-scala
        private var creatureDisplay = Option.empty[CreatureFrame]
        override def mouseClicked(e: MouseEvent) {
          // Create the SpellFrame if it has not been created before
          if (creatureDisplay.isEmpty){
            creatureDisplay = Some(new CreatureFrame(searcher, sparkRequest, jLabel.getText))
            creatureDisplay.get.centerOnScreen()
            creatureDisplay.get.open()
          }
          // elsewhere simply bring the window to the front
          else{
            creatureDisplay.get.open()
          }
        }
      })

      jPanelResult.add(jLabel).revalidate()

      nbResult += 1
    }

    labelNbResult.text = "Number: " + nbResult
  }
}