import java.awt.Desktop
import java.net.URI
import scala.collection.mutable
import scala.swing.{BoxPanel, Button, CheckBox, Color, Component, GridPanel, Label, Orientation, RadioButton, Separator, Swing}

object SwingGeneralFunc {
  private final val nbGridColumn: Int = 7

  def addSeparator(contents: mutable.Buffer[Component]): Unit = {
    contents += Swing.VStrut(10)
    contents += new Separator()
    contents += Swing.VStrut(10)
  }

  def getHorizontalBox(text: String, value: String): BoxPanel = {
    new BoxPanel(Orientation.Horizontal) {
      contents += new Label(text + ": " + value)
      contents += Swing.HGlue
    }
  }

  def getWebsiteBtnBox(url: String): BoxPanel = {
    new BoxPanel(Orientation.Horizontal) {
      val websiteBtn: Button = Button("See spell on Archives of Nethys") { Desktop.getDesktop.browse(new URI(url)) }
      websiteBtn.background = new Color(52, 152, 219)

      contents += websiteBtn
      contents += Swing.HGlue
    }
  }

  def getOperatorBox(btnAnd: RadioButton, btnOr: RadioButton): BoxPanel = {
    new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Operator:")
      contents += Swing.HStrut(10)

      contents += btnAnd
      contents += btnOr

      contents += Swing.Glue
    }
  }

  def getGridBox(labelName: String, checkboxMap: mutable.TreeMap[String, CheckBox], elements: Array[String]): BoxPanel = {
    val nbLine: Int = elements.length/nbGridColumn + (if (elements.length % nbGridColumn == 0) 0 else 1)

    new BoxPanel(Orientation.Horizontal) {
      contents += new Label(labelName + ":")
      contents += Swing.HStrut(10)

      // Add available classes dynamically
      contents += new GridPanel(nbLine, nbGridColumn) {
        for (element <- elements) {
          checkboxMap += (element -> new CheckBox(element))
          contents += checkboxMap(element)
        }
      }
    }
  }

}
