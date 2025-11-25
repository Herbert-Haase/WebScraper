package de.htwg.webscraper.aview

import de.htwg.webscraper.controller.Controller
import scala.io.StdIn.readLine
import scala.collection.mutable.ListBuffer

trait TuiState {
  def handleInput(input: String, tui: Tui, controller: Controller): Unit
  def displayPrompt(): Unit
}

class InitialState extends TuiState {
  override def displayPrompt(): Unit = {
    println("\n[Start] Enter 'file <path>', 'text', or 'exit':")
  }

  override def handleInput(input: String, tui: Tui, controller: Controller): Unit = {
    input.split(" ").toList match {
      case "file" :: path :: Nil => 
        controller.loadFromFile(path)
        tui.changeState(new FilterState)
        
      case "text" :: Nil =>
        println("Enter text. Type '.' on a new line to finish:")
        val buffer = ListBuffer[String]()
        var line = readLine()
        // Strict loop to consume input safely
        while (line != null && line != ".") {
          buffer += line
          line = readLine()
        }
        controller.loadFromText(buffer.mkString("\n"))
        tui.changeState(new FilterState)
        
      case "exit" :: Nil => System.exit(0)
      case _ => println("Invalid command. Try 'file <path>' or 'text'.")
    }
  }
}

class FilterState extends TuiState {
  override def displayPrompt(): Unit = {
    println("\n[Filter] Enter word to filter, 'undo', 'redo', 'numbers', 'lower', 'reset', or 'exit':")
  }

  override def handleInput(input: String, tui: Tui, controller: Controller): Unit = {
    input.toLowerCase match {
      case "undo" => controller.undo()
      case "redo" => controller.redo()
      case "numbers" => tui.toggleLineNumbers()
      case "lower" => tui.toggleLowerCase()
      case "reset" => 
        controller.reset()
        tui.changeState(new InitialState)
      case "exit" => System.exit(0)
      case "" => // ignore empty enter
      case word => controller.filter(word)
    }
  }
}