import java.awt.Desktop
import java.net.URI
import scala.collection.mutable
import scala.swing.{BoxPanel, Button, Color, Component, Label, Orientation, RadioButton, Separator, Swing}

object SwingGeneralFunc {
  
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
      val websiteBtn: Button = Button("See on Archives of Nethys") { Desktop.getDesktop.browse(new URI(url)) }
      websiteBtn.background = new Color(224, 224, 224)

      contents += websiteBtn
      contents += Swing.HGlue
    }
  }

  def getOperatorBox(btnAnd: RadioButton, btnOr: RadioButton): BoxPanel = {
    new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Operator: ")
      contents += btnAnd
      contents += btnOr

      contents += Swing.Glue
    }
  }
}
