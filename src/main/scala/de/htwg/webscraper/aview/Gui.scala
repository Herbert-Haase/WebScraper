package de.htwg.webscraper.aview

import de.htwg.webscraper.controller.{Controller, Observer}
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, VBox, HBox, Priority}
import scalafx.scene.control.{TextArea, TextField, Button, Label, ToolBar, Separator}
import scalafx.scene.web.WebView
import scalafx.stage.{FileChooser, Stage}
import scalafx.geometry.Insets
import scalafx.Includes._
import scala.compiletime.uninitialized

class Gui(controller: Controller) extends Observer {
  controller.add(this)

  // Content Containers
  private val webView = new WebView()
  private val textArea = new TextArea {
    editable = false
    style = "-fx-font-family: 'Consolas', 'Monospaced'; -fx-font-size: 12pt;"
  }

  // Controls
  private val pathField = new TextField { hgrow = Priority.Always }
  private val filterField = new TextField { promptText = "Filter word..." }

  // Status
  private val statusLabel = new Label("Welcome to WebScraper")

  // Store reference to the main layout
  private var mainLayout: BorderPane = uninitialized

  def start(stage: Stage): Unit = {
    stage.title = "WebScraper GUI"
    stage.width = 1024
    stage.height = 768

    val toolbar = new ToolBar {
      content = List(
        new Button("Open File") {
          onAction = _ => openFileChooser(stage)
        },
        new Button("Undo") { onAction = _ => controller.undo() },
        new Button("Redo") { onAction = _ => controller.redo() },
        new Separator,
        new Label("Filter: "),
        filterField,
        new Button("Apply") { onAction = _ => controller.filter(filterField.text.value) },
        new Button("Reset") {
          onAction = _ => {
            controller.reset()
            filterField.text = ""
          }
        }
      )
    }

    // Main Layout: Switch between WebView and TextArea based on content
    mainLayout = new BorderPane {
      top = new VBox(toolbar)
      center = textArea // Default view
      bottom = new HBox {
        padding = Insets(5)
        children = statusLabel
      }
    }

    stage.scene = new Scene {
      root = mainLayout
    }

    stage.show()

    // Initial Render
    update(false)
  }

  private def openFileChooser(stage: Stage): Unit = {
    val fileChooser = new FileChooser()
    fileChooser.title = "Open Resource File"
    val selectedFile = fileChooser.showOpenDialog(stage)
    if (selectedFile != null) {
      controller.loadFromFile(selectedFile.getAbsolutePath)
    }
  }

  override def update(isFilterUpdate: Boolean): Unit = {
    // GUI updates must run on the JavaFX Application Thread
    Platform.runLater {
      val content = controller.data.displayLines.mkString("\n")
      val stats = s"Chars: ${controller.data.characterCount} | Words: ${controller.data.wordCount}"
      statusLabel.text = if(isFilterUpdate) s"[Filtered] $stats" else stats

      // Heuristic: If content looks like HTML, use WebView, otherwise TextArea
      if (isHtml(content)) {
        webView.engine.loadContent(content)
        if (mainLayout.center.value != webView) {
          mainLayout.center = webView
        }
      } else {
        textArea.text = content
        if (mainLayout.center.value != textArea) {
          mainLayout.center = textArea
        }
      }
    }
  }

  private def isHtml(content: String): Boolean = {
    val c = content.trim.toLowerCase
    c.startsWith("<!doctype html") || c.startsWith("<html")
  }
}