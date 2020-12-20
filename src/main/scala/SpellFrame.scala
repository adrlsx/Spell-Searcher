import SwingGeneralFunc.{addSeparator, getHorizontalBox, getWebsiteBtnBox}

import java.awt.{Desktop, Image}
import java.awt.event.{MouseAdapter, MouseEvent}
import java.net.URI
import java.nio.file.{Files, Paths}
import javax.swing._
import scala.swing._

class SpellFrame(sparkRequest: SparkRequest.type, spellName: String, val spellInfo: Map[String, String], val creatureList: List[String]) extends Frame {

  private val spellUrl: String = getFormatUrl(spellInfo("url"))
  private val spellSchool: String = spellInfo("school")
  private val spellClasses: String = spellInfo("classes")
  private val spellComponents: String = spellInfo("components")
  private val spellResistance: String = spellInfo("spell_resistance")

  private val spellImgPath: String = {
    val formatName: String = getFormatName(spellName)
    val path = "img"

    if (Files.exists(Paths.get(s"$path/$formatName.jpg"))) {
      s"$path/$formatName.jpg"
    } else {
      s"$path/.back.png"
    }
  }

  // var is mutable contrary to val
  private var nbResult: Int = 0

  // Set Window title
  title = s"Spell: $spellName"

  private var imageIcon: ImageIcon = new ImageIcon(spellImgPath); // load the image to a imageIcon
  private val image: Image = imageIcon.getImage; // transform it
  private val newImg: Image = image.getScaledInstance(409, 583,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
  imageIcon = new ImageIcon(newImg);  // transform it back

  // Initialisation for results
  private val labelNbResult = new Label(s"Number: $nbResult")
  private val jPanelResult = new JPanel()
  jPanelResult.setLayout(new BoxLayout(jPanelResult, BoxLayout.Y_AXIS))

  // initialisation for creature frame
  private val creatureNameLabel: Label = new Label()
  private val creatureSpellLabel: Label = new Label()

  private val creatureWebSiteBtn: Button = Button("") {}
  creatureWebSiteBtn.background = new Color(52, 152, 219)

  private val descriptionTextArea = new JTextArea()
  descriptionTextArea.setEditable(false)
  descriptionTextArea.setLineWrap(true)
  descriptionTextArea.setWrapStyleWord(true)

  // Create box for creature panel
  private val creatureBoxPanel: BoxPanel = new BoxPanel(Orientation.Horizontal) {
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

  // Set content Frame
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

          // Init scroll panel for creature results
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

    // Add creature box panel to frame
    contents += creatureBoxPanel
  }

  private def showCreatureFrame(selectedCreatureName: String): Unit = {
    val worker = new SwingWorker[Map[String, String], Map[String, String]] {
      override protected def doInBackground(): Map[String, String] = {
        val creatureInfo: Map[String, String] = sparkRequest.getCreatureInfo(selectedCreatureName)
        creatureInfo
      }

      override protected def done(): Unit = {
        val creatureInfo: Map[String, String] = get()

        creatureNameLabel.text = selectedCreatureName
        creatureSpellLabel.text = creatureInfo("spells")

        creatureWebSiteBtn.action = swing.Action("See creature on Archives of Nethys") {
          Desktop.getDesktop.browse(new URI(getFormatUrl(creatureInfo("url"))))
        }

        descriptionTextArea.setText(creatureInfo("description"))

        creatureBoxPanel.visible = true

        // Resize window after set creature box visible
        pack()
        centerOnScreen()
      }
    }

    worker.execute()
  }

  addCreatureResult()

  private def addCreatureResult(): Unit = {
    for (creature <- creatureList) {
      val jLabel = new JLabel(creature)

      jLabel.addMouseListener(new MouseAdapter() {
        override def mouseClicked(e: MouseEvent) {
          showCreatureFrame(jLabel.getText)
        }
      })

      jPanelResult.add(jLabel).revalidate()

      nbResult += 1
    }

    labelNbResult.text = s"Number: $nbResult"
  }

  private def getFormatUrl(url: String): String = {
    url.replaceAll(" ", "%20")
  }

  private def getFormatName(name: String): String = {
    name.toLowerCase.replaceAll("'", "_").replaceAll(" \\(.*\\)", "").replaceAll("/", " ")
  }
}