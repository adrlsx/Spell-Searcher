import SwingGeneralFunc.{addSeparator, getHorizontalBox, getWebsiteBtnBox}
import org.apache.spark.sql.{Column, DataFrame, Row}

import java.awt.{Desktop, Image}
import java.awt.event.{MouseAdapter, MouseEvent}
import java.net.URI
import javax.swing._
import javax.swing.text.html.StyleSheet.BoxPainter
import scala.swing
import scala.swing._

class SpellFrame(searcher: Searcher.type, sparkRequest: SparkRequest.type, spellName: String) extends Frame {

  val spellInfo: DataFrame = sparkRequest.getSpellInfo(spellName)
  val creatureList: DataFrame = sparkRequest.getCreatureList(spellName)

  spellInfo.show()

  println(getStringFromDataFrame(spellInfo, "classes"))
  println(getStringFromDataFrame(spellInfo, "components"))
  println(getStringFromDataFrame(spellInfo, "school"))
  println(getStringFromDataFrame(spellInfo, "spell_resistance"))
  println(getStringFromDataFrame(spellInfo, "url"))


  def getStringFromDataFrame(dataFrame: DataFrame, column: String): String = {
    dataFrame.select(column).first().toString().replaceAll("\\[", "").replaceAll("]", "")
  }


  val spellUrl: String = "https://aonprd.com/SpellDisplay.aspx?ItemName=Ablative%20Barrier"
  val spellSchool: String = "Conjuration"
  val spellClasses: String = "alchemist 2, arcanist 3, bloodrager 2, investigator 2, magus 2, occultist 2, psychic 3, sorcerer 3, summoner 2, summoner (unchained) 3, wizard 3"
  val spellComponents: String = "V, S, M"
  val spellResistance: String = "false"
  val spellImgPath: String = "output/img/ablative barrier.png"

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

  // initialisation for creature frame
  val creatureNameLabel: Label = new Label()
  val creatureSpellLabel: Label = new Label()

  val creatureWebSiteBtn: Button = Button("") {}
  creatureWebSiteBtn.background = new Color(52, 152, 219)

  val descriptionTextArea = new JTextArea()
  descriptionTextArea.setEditable(false)
  descriptionTextArea.setLineWrap(true)
  descriptionTextArea.setWrapStyleWord(true)

  val creatureBoxPanel: BoxPanel = new BoxPanel(Orientation.Horizontal) {
    contents += Swing.HStrut(10)
    contents += Component.wrap(new JSeparator(SwingConstants.VERTICAL))
    contents += Swing.HStrut(10)

    contents += new BoxPanel(Orientation.Vertical) {
      // Add creature web page button
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += creatureWebSiteBtn
        contents += Swing.HGlue
      }

      contents += Swing.VStrut(5)

      // Add creature name box
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += new Label("Creature Name: ")
        contents += creatureNameLabel
        contents += Swing.HGlue
      }

      contents += Swing.VStrut(5)

      // Add creature spells box
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += new Label("Spell: ")
        contents += creatureSpellLabel
        contents += Swing.HGlue
      }

      contents += Swing.VStrut(5)

      // Add creature description box
      contents += getHorizontalBox("Description", "")
      contents += Component.wrap(descriptionTextArea)

      contents += Swing.Glue
    }
  }

  contents = new BoxPanel(Orientation.Horizontal) {
    // Set frame border margin
    border = Swing.EmptyBorder(10, 10, 10, 10)

    contents += new BoxPanel(Orientation.Vertical) {
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
          jScrollPaneResult.setPreferredSize(new Dimension(300, 583))

          // https://stackoverflow.com/questions/5583495/how-do-i-speed-up-the-scroll-speed-in-a-jscrollpane-when-using-the-mouse-wheel
          jScrollPaneResult.getVerticalScrollBar.setUnitIncrement(16)

          // Use Component.wrap() for link between javax.swing and scala.swing
          contents += Component.wrap(jScrollPaneResult)
        }
      }

      addSeparator(contents)

      // Add spell web page button
      contents += getWebsiteBtnBox(spellUrl)
      contents += Swing.VStrut(5)

      // Add spell name box
      contents += getHorizontalBox("Spell name", spellName)
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

    creatureBoxPanel.preferredSize = new Dimension(350, 0)
    creatureBoxPanel.visible = false

    contents += creatureBoxPanel
  }

  def showCreatureFrame(selectedCreatureName: String): Unit = {
    sparkRequest.getCreatureInfo(selectedCreatureName)

    creatureNameLabel.text = selectedCreatureName
    creatureSpellLabel.text = "Spells of " + selectedCreatureName

    creatureWebSiteBtn.action = swing.Action("See creature on Archives of Nethys") {
      Desktop.getDesktop.browse(new URI("https://aonprd.com/MonsterDisplay.aspx?ItemName=" + selectedCreatureName))
    }

    descriptionTextArea.setText("Long description qui ne tient pas sur une seule ligne sinon c'est pas drôle de " + selectedCreatureName)

    creatureBoxPanel.visible = true

    // Resize window after set creature box visible
    this.pack()
    this.centerOnScreen()
  }

  addCreatureResult()

  def addCreatureResult(): Unit = {
    for (i <- 0 until 50) {
      val jLabel = new JLabel("TEST" + i)

      jLabel.addMouseListener(new MouseAdapter() {
        override def mouseClicked(e: MouseEvent) {
          showCreatureFrame(jLabel.getText)
        }
      })

      jPanelResult.add(jLabel).revalidate()

      nbResult += 1
    }

    labelNbResult.text = "Number: " + nbResult
  }
}