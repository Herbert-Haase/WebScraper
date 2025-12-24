package de.htwg.webreport.aview

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.controller.sessionManager.impl1.SessionManager
import de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer
import de.htwg.webreport.model.webClient.impl1.SimpleWebClient
import de.htwg.webreport.model.fileio.implXML.XmlFileIO

class TuiStateSpec extends AnyWordSpec with Matchers {
  
  def createDeps() = {
    val fileIO = new XmlFileIO()
    val sm = new SessionManager(new SimpleAnalyzer(), new SimpleWebClient(), fileIO)
    val tui = new Tui(sm, fileIO)
    (sm, tui, fileIO)
  }

  "InitialState" should {
    "handle 'download' command" in {
      val (sm, tui, fileIO) = createDeps()
      val state = new InitialState()
      state.handleInput("download http://example.com", tui, sm, fileIO)
    }

    "handle 'text' command input loop" in {
        val (sm, tui, fileIO) = createDeps()
        val state = new InitialState()
        state.handleInput("invalid", tui, sm, fileIO)
    }
    
    "handle 'import' command" in {
        val (sm, tui, fileIO) = createDeps()
        val state = new InitialState()
        state.handleInput("import ./nonexistent.xml", tui, sm, fileIO)
    }
  }

  "FilterState" should {
    "handle 'ln', 'undo', 'redo', 'reset'" in {
      val (sm, tui, fileIO) = createDeps()
      val state = new FilterState()
      
      noException should be thrownBy state.handleInput("ln", tui, sm, fileIO)
      noException should be thrownBy state.handleInput("undo", tui, sm, fileIO)
      noException should be thrownBy state.handleInput("redo", tui, sm, fileIO)
      noException should be thrownBy state.handleInput("reset", tui, sm, fileIO)
    }

    "handle 'save' and 'load'" in {
       val (sm, tui, fileIO) = createDeps()
       val state = new FilterState()
    }
    
    "handle valid filter term" in {
        val (sm, tui, fileIO) = createDeps()
        sm.loadFromText("content")
        val state = new FilterState()
        state.handleInput("content", tui, sm, fileIO)
        sm.data.displayLines should contain("content")
    }
  }
  "FilterState" should {
    "trigger various commands" in {
        val state = new FilterState()
        val (sm, tui, fileIO) = createDeps()
        state.handleInput("ln", tui, sm, fileIO)
        state.handleInput("scala", tui, sm, fileIO)
        state.handleInput("reset", tui, sm, fileIO)
    }

    "handle commands without crashing" in {
      val state = new FilterState()
      val (sm, tui, fileIO) = createDeps()
      noException should be thrownBy state.handleInput("ln", tui, sm, fileIO)
      
      noException should be thrownBy state.handleInput("someFilterWord", tui, sm, fileIO)
    }
    }
}