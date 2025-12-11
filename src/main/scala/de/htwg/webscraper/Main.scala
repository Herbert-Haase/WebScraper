package de.htwg.webscraper

import de.htwg.webscraper.controller.Controller
import de.htwg.webscraper.aview.{Tui, Gui}
import scalafx.application.JFXApp3

object Main extends JFXApp3 {
  override def start(): Unit = {
    // 1. Initialize Controller
    val controller = new Controller()

    // 2. Initialize TUI (Observer)
    val tui = new Tui(controller)

    // 3. Initialize GUI (Observer)
    val gui = new Gui(controller)

    // 4. Start TUI in a separate thread (Daemon)
    // Disable TUI in GUI mode since stdin isn't available
    // Uncomment if you want TUI to run alongside GUI
    /*
    val tuiThread = new Thread(() => {
      tui.run()
    })
    tuiThread.setDaemon(true)
    tuiThread.start()
    */

    // 5. Create and configure the primary stage
    stage = new JFXApp3.PrimaryStage {
      // GUI will configure this stage
    }

    // 6. Start GUI with the configured stage
    gui.start(stage)
  }
}