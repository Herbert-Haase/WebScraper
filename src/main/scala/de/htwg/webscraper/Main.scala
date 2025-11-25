package de.htwg.webscraper

import de.htwg.webscraper.controller.Controller
import de.htwg.webscraper.aview.Tui

object Main {
  def main(args: Array[String]): Unit = {
    val controller = new Controller()
    val tui = new Tui(controller)
    tui.run()
  }
}
