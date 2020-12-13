import SwingGeneralFunc.{addSeparator, getOperatorBox}

import java.awt.event.{MouseAdapter, MouseEvent}
import javax.swing.{BoxLayout, JLabel, JPanel, JScrollPane}
import scala.collection.immutable.TreeMap
import scala.swing._

// Main frame : spell research by criteria
class SearchFrame(searcher: Searcher.type, sparkRequest: SparkRequest.type) extends MainFrame {
  // Set Window title
  title = "Spell Searcher"

  val nbGridColumn: Int = 7

  // var is mutable contrary to val
  var nbResult: Int = 0

  // Initialisation for research by Class
  // Round to upper with (if (searcher.getNbClass % nbGridColumn == 0) 0 else 1)
  val nbClassLine: Int = searcher.getNbClass/nbGridColumn + (if (searcher.getNbClass % nbGridColumn == 0) 0 else 1)
  var checkBoxClassMap: TreeMap[String, CheckBox] = new TreeMap()

  val btnClassAnd = new RadioButton("AND")
  val btnClassOr = new RadioButton("OR")
  btnClassAnd.selected = true

  new ButtonGroup(btnClassAnd, btnClassOr)


  // Initialisation for research by School
  val nbSchoolLine: Int = searcher.getNbSchool/nbGridColumn + (if (searcher.getNbSchool % nbGridColumn == 0) 0 else 1)
  var checkBoxSchoolMap: TreeMap[String, CheckBox] = new TreeMap()


  // Initialisation for research by Component
  var checkBoxComponentMap: TreeMap[String, CheckBox] = new TreeMap()

  val btnComponentAnd = new RadioButton("AND")
  val btnComponentOr = new RadioButton("OR")
  btnComponentAnd.selected = true

  new ButtonGroup(btnComponentAnd, btnComponentOr)


  // Initialisation for research by Spell Resistance
  val btnSpellResistanceYes = new RadioButton("Yes")
  val btnSpellResistanceNo = new RadioButton("No")
  val btnSpellResistanceNotCheck = new RadioButton("Not checked")
  btnSpellResistanceNotCheck.selected = true

  new ButtonGroup(btnSpellResistanceYes, btnSpellResistanceNo, btnSpellResistanceNotCheck)


  // Initialisation for research by Spell Name
  val spellNameTextField = new TextField()


  // Initialisation for research by Description
  val descriptionTextField = new TextField()


  // Initialisation for results
  val jPanelResult = new JPanel()
  jPanelResult.setLayout(new BoxLayout(jPanelResult, BoxLayout.Y_AXIS))

  val labelNbResult = new Label("Number: " + nbResult)


  contents = new BoxPanel(Orientation.Vertical) {
    // Set frame border margin
    border = Swing.EmptyBorder(10, 10, 10, 10)

    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Choose any of the options below to narrow the results of your research:")

      // Takes remaining space
      contents += Swing.Glue
    }

    addSeparator(contents)

    // Add box for class selection
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Classes: ")

      // Add available classes dynamically
      contents += new GridPanel(nbClassLine, nbGridColumn) {
        for (className <- searcher.getAllClassName) {
          checkBoxClassMap += (className -> new CheckBox(className))
          contents += checkBoxClassMap(className)
        }
      }
    }

    // Add box for class operator selection (AND or OR)
    contents += getOperatorBox(btnClassAnd, btnClassOr)

    addSeparator(contents)

    // Add box for school selection
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("School: ")

      // Add available schools dynamically
      contents += new GridPanel(nbSchoolLine, nbGridColumn) {
        for (schoolName <- searcher.getAllSchoolName) {
          checkBoxSchoolMap += (schoolName -> new CheckBox(schoolName))
          contents += checkBoxSchoolMap(schoolName)
        }
      }
    }

    // Add box description for school selection
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("OR operator is applied, because a spell can only have one school. You will get the spells for each selected school")
      contents += Swing.Glue
    }

    addSeparator(contents)

    // Add box for component selection
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Component: ")

      // Add available components dynamically
      for (componentName <- searcher.getAllComponentName) {
        checkBoxComponentMap += (componentName -> new CheckBox(componentName))
        contents += checkBoxComponentMap(componentName)

        // Add horizontal spacing
        contents += Swing.HStrut(5)
      }

      // Takes remaining space
      contents += Swing.Glue
    }

    // Add box for component operator selection (AND or OR)
    contents += getOperatorBox(btnComponentAnd, btnComponentOr)

    addSeparator(contents)

    // Add box for spell resistance selection
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Spell Resistance: ")

      contents += btnSpellResistanceYes
      contents += btnSpellResistanceNo
      contents += btnSpellResistanceNotCheck

      contents += Swing.Glue
    }

    addSeparator(contents)

    // Add box for research by exact spell name
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Exact spell Name: ")
      contents += spellNameTextField
      contents += Swing.Glue
    }

    addSeparator(contents)

    // Add box for research by spell description (full text search)
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Description: ")
      contents += descriptionTextField
      contents += Swing.Glue
    }

    contents += Swing.VStrut(10)
    contents += Swing.Glue

    // Add reset and research buttons
    contents += new BoxPanel(Orientation.Horizontal) {
      val resetBtn: Button = Button("Reset") { resetField() }
      // https://colorswall.com/palette/3/
      resetBtn.background = new Color(217, 83, 79)

      val researchBtn: Button = Button("Search") { launchResearch() }
      // https://colorswall.com/palette/3/
      researchBtn.background = new Color(92, 184, 92)

      contents += resetBtn
      contents += Swing.HStrut(50)
      contents += researchBtn
    }

    addSeparator(contents)

    // Add box for spell result number
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += new Label("Spell result: ")
      contents += Swing.Glue
      contents += labelNbResult
    }

    contents += Swing.VStrut(5)

    val jScrollPaneResult = new JScrollPane(jPanelResult)
    jScrollPaneResult.setPreferredSize(new Dimension(0, 180))

    // Use Component.wrap() for link between javax.swing and scala.swing
    // Add scroll pane for spell results
    contents += Component.wrap(jScrollPaneResult)
  }


  def launchResearch(): Unit = {
    val classArray: Array[String] = getArrayFromCheckbox(checkBoxClassMap)
    val schoolArray: Array[String] = getArrayFromCheckbox(checkBoxSchoolMap)
    val componentArray: Array[String] = getArrayFromCheckbox(checkBoxComponentMap)

    val classOperator: String = getOperatorFromButton(btnClassOr)
    val componentOperator: String = getOperatorFromButton(btnComponentOr)
    val spellResistance: String = getSpellResistance

    val exactSpellName: String = spellNameTextField.text
    val description: Array[String] = getDescription

    sparkRequest.getSpellList(classArray, classOperator, schoolArray, componentArray, componentOperator, spellResistance, exactSpellName, description )

    val jLabel = new JLabel("SPELL NAME")


    jLabel.addMouseListener(new MouseAdapter() {
      // https://stackoverflow.com/questions/2440134/is-this-the-proper-way-to-initialize-null-references-in-scala
      // https://alvinalexander.com/scala/initialize-scala-variables-option-none-some-null-idiom/
      private var spellDisplay = Option.empty[SpellFrame]
      override def mouseClicked(e: MouseEvent) {
        // Create the SpellFrame if it has not been created before
        if (spellDisplay.isEmpty){
          spellDisplay = Some(new SpellFrame(searcher, sparkRequest, jLabel.getText))
          spellDisplay.get.centerOnScreen()
          spellDisplay.get.open()
        }
        // elsewhere simply bring the window to the front
        else{
          spellDisplay.get.open()
        }
      }
    })

    jPanelResult.add(jLabel).revalidate()

    nbResult += 1
    labelNbResult.text = "Number: " + nbResult
  }


  def getArrayFromCheckbox(map: TreeMap[String, CheckBox]): Array[String] = {
    var stringArray: Array[String] = Array()

    // Get selected checkbox, and put key in Array
    for (mapElement <- map) {
      if (mapElement._2.selected) {
        stringArray :+= mapElement._1
      }
    }

    return stringArray
  }

  def getOperatorFromButton(button: RadioButton): String = {
    // Get selected operator : set operator to OR if selected, else set to AND
    if (button.selected) {
       return "OR"
    } else {
      return "AND"
    }
  }

  def getSpellResistance: String = {
    if (btnSpellResistanceYes.selected) {
      return "true"
    } else if (btnSpellResistanceNo.selected) {
      return "false"
    } else {
      return null
    }
  }

  def getDescription: Array[String] = {
    val tempDesc = descriptionTextField.text.replaceAll("[^a-zA-Z]", " ")
    tempDesc.split(" +")
  }

  def resetField(): Unit = {
    // Reset classes buttons and set AND operator
    for (classElement <- checkBoxClassMap) { classElement._2.selected = false }
    btnClassAnd.selected = true

    // Reset schools buttons and set AND operator
    for (schoolElement <- checkBoxSchoolMap) { schoolElement._2.selected = false }

    // Reset components buttons and set AND operator
    for (componentElement <- checkBoxComponentMap) { componentElement._2.selected = false }
    btnComponentAnd.selected = true

    // Set spell resistance not checked
    btnSpellResistanceNotCheck.selected = true

    // Reset spell name and description text fields
    spellNameTextField.text = ""
    descriptionTextField.text = ""

    // Remove all spells result
    jPanelResult.removeAll()
    jPanelResult.revalidate()
    jPanelResult.repaint()

    // Reset result number
    labelNbResult.text = "Number: 0"
  }
}