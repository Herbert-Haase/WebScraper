package de.htwg.webscraper.controller.impl1.controller

import com.google.inject.Inject
import de.htwg.webscraper.controller.ControllerInterface
import de.htwg.webscraper.model.analyzer.Analyzer
import de.htwg.webscraper.model.data.ProjectData
import de.htwg.webscraper.model.webClient.WebClient
import de.htwg.webscraper.model.fileio.FileIO // Inject FileIO
import de.htwg.webscraper.util.{Command, Memento, Originator, UndoManager}
import scala.util.{Failure, Success, Try, Using}
import scala.io.Source
import scala.compiletime.uninitialized

class Controller @Inject() (
    val analyzer: Analyzer,
    val client: WebClient,
    val fileIO: FileIO // Added Injection
) extends ControllerInterface with Originator {

  private var dataState: ProjectData = analyzer.process(List.empty, Nil, "empty")
  private val undoManager = new UndoManager
  
  // Requirement: "Cumulative export... all data states between resets"
  private var sessionHistory: List[ProjectData] = Nil

  override def data: ProjectData = dataState

  override def createMemento(): Memento = Memento(dataState)

  override def restore(m: Memento): Unit = {
    dataState = m.state match { case d: ProjectData => d }
    notifyObservers()
  }

  // --- Helper to update history ---
  private def addToHistory(newData: ProjectData): Unit = {
    dataState = newData
    sessionHistory = sessionHistory :+ newData
    notifyObservers()
  }

  // --- Commands ---
  class LoadCommand(path: Option[String], manualText: Option[String]) extends Command {
    var memento: Memento = uninitialized
    override def execute(): Unit = {
      memento = createMemento()
      val lines = if (path.isDefined) {
        Using(Source.fromFile(path.get))(_.getLines().toList)
          .getOrElse(List(s"Error: Could not read file '${path.get}'"))
      } else {
        manualText.getOrElse("").split("\n").toList
      }
      
      val label = path.getOrElse("text-input")
      // Update state and History
      addToHistory(analyzer.process(lines, Nil, label))
    }
    override def undo(): Unit = restore(memento)
    override def redo(): Unit = execute()
  }

  class DownloadCommand(url: String) extends Command {
    var memento: Memento = uninitialized
    override def execute(): Unit = {
      memento = createMemento()
      client.download(url) match {
        case Success(content) =>
          addToHistory(analyzer.process(content.split("\n").toList, Nil, url))
        case Failure(e) =>
          addToHistory(analyzer.process(List(s"Error: ${e.getMessage}"), Nil, url))
      }
    }
    override def undo(): Unit = restore(memento)
    override def redo(): Unit = execute()
  }

  class FilterCommand(word: String) extends Command {
    var memento: Memento = uninitialized
    override def execute(): Unit = {
      memento = createMemento()
      val filteredLines = dataState.originalLines.filter(_.toLowerCase.contains(word.toLowerCase))
      // Filter does NOT add to session history (usually), just updates view
      dataState = analyzer.process(dataState.originalLines, filteredLines, dataState.source)
      notifyObservers(isFilterUpdate = true)
    }
    override def undo(): Unit = restore(memento)
    override def redo(): Unit = execute()
  }
  
  // --- New Import Logic ---
  // We don't wrap this in a Command because it REPLACES the whole session history
  private def importSession(path: String): Boolean = {
    Try(fileIO.load(path)) match {
      case Success(history) if history.nonEmpty =>
        sessionHistory = history
        dataState = history.last
        notifyObservers()
        true
      case _ => false
    }
  }

  // --- API ---
  override def loadFromFile(path: String): Unit = {
    // Smart Load: Try to load as XML/JSON session first. If fail, load as text.
    if (!importSession(path)) {
      undoManager.doStep(new LoadCommand(Some(path), None))
    }
  }
  
  // NEW: Save Cumulative Session
  override def saveSession(path: String): Unit = {
    fileIO.save(sessionHistory, path)
  }

  override def loadFromText(text: String): Unit = undoManager.doStep(new LoadCommand(None, Some(text)))
  override def downloadFromUrl(url: String): Unit = undoManager.doStep(new DownloadCommand(url))
  override def filter(word: String): Unit = undoManager.doStep(new FilterCommand(word))
  override def undo(): Unit = undoManager.undoStep()
  override def redo(): Unit = undoManager.redoStep()

  override def reset(): Unit = {
    dataState = analyzer.process(List.empty, Nil, "empty")
    sessionHistory = Nil // Clear history on reset
    notifyObservers()
  }
}