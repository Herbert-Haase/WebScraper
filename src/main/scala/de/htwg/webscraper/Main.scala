package de.htwg.webscraper

import de.htwg.webscraper.controller.impl1.controller.*
import de.htwg.webscraper.controller.exporter.impl1.jsonExporter.*
import de.htwg.webscraper.model.analyzer.impl1.simpleAnalyzer.*
import de.htwg.webscraper.model.webClient.impl1.simpleWebClient.*
import de.htwg.webscraper.aview.{Tui, Gui}
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage

object Main extends JFXApp3 {
  override def start(): Unit = {

    val client = new SimpleWebClient()
    val analyzer = new SimpleAnalyzer()

    val controller = new Controller(analyzer, client)
    val jsonExporter = new JsonExporter()

    val tui = new Tui(controller, jsonExporter)
    val gui = new Gui(controller, jsonExporter)

    val tuiThread = new Thread(() => {
      tui.run()
    })
    tuiThread.setDaemon(true)
    tuiThread.start()

    stage = new PrimaryStage {
      title = "WebScraper Pro"
      width = 1100
      height = 800
      scene = gui.createScene()
    }
  }
}