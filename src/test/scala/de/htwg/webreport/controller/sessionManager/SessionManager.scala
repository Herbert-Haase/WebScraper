package de.htwg.webreport.controller.sessionManager

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.webreport.model.webClient.WebClientTrait
import de.htwg.webreport.model.webClient.impl1.SimpleWebClient
import de.htwg.webreport.model.analyzer.impl1.SimpleAnalyzer
import de.htwg.webreport.controller.sessionManager.impl1.SessionManager
import de.htwg.webreport.model.fileio.implXML.XmlFileIO
import scala.util.Failure
import scala.util.Try

class sessionManagerSpec extends AnyWordSpec with Matchers {

  class FailingClient extends WebClientTrait {
  override def get(url: String): Try[String] = 
    Failure(new RuntimeException("Injected Network Error"))

  override def download(url: String): Try[String] = 
    Failure(new RuntimeException("Injected Network Error"))
  }
  
  def createsessionManager(): SessionManager = {
    val client = new SimpleWebClient()
    val analyzer = new SimpleAnalyzer()
    val fileIO = new XmlFileIO()
    new SessionManager(analyzer, client, fileIO)
  }

  "A sessionManager" should {
    "load text correctly via Command" in {
      val sessionManager = createsessionManager()
      sessionManager.loadFromText("Hello\nWorld")
      sessionManager.data.displayLines should be(List("Hello", "World"))
    }

    "filter text correctly" in {
      val sessionManager = createsessionManager()
      sessionManager.loadFromText("Apple\nBanana\nApricot")
      sessionManager.filter("Ap")
      sessionManager.data.displayLines should contain("Apple")
      sessionManager.data.displayLines should not contain("Banana")
    }

    "Undo and Redo correctly" in {
      val sessionManager = createsessionManager()
      sessionManager.loadFromText("Initial")
      
      sessionManager.filter("NonExistentSearchTerm")
      sessionManager.data.displayLines should be(empty)
      
      sessionManager.undo()
      sessionManager.data.displayLines should be(List("Initial"))
      
      sessionManager.redo()
      sessionManager.data.displayLines should be(empty)
    }

    "handle download failure gracefully" in {
      val sessionManager = createsessionManager()
      sessionManager.downloadFromUrl("http://invalid.url.local")
      
      sessionManager.data.displayLines.head should startWith("Error")
    }

    "handle download failure with specific message" in {
      val sessionManager = createsessionManager()
      sessionManager.downloadFromUrl("http://non-existent-domain-12345.com")
      
      sessionManager.data.displayLines.mkString should include("Error")
      sessionManager.data.source should be("http://non-existent-domain-12345.com")
    }

    "handle failed downloads by creating error data" in {
      val sm = createsessionManager()
      sm.downloadFromUrl("not-a-valid-url")
      
      sm.data.source should include ("not-a-valid-url")
      sm.data.originalLines.head should startWith ("Error")
    }
    "handle download failure gracefully with real client" in {
      val sessionManager = createsessionManager()
      sessionManager.downloadFromUrl("http://invalid.url.local")
      sessionManager.data.displayLines.head should startWith("Error")
    }
    "cover the Failure branch using a stubbed client" in {
    val failingClient = new FailingClient()
    val sessionManager = new SessionManager(new SimpleAnalyzer(), failingClient, new XmlFileIO())
    
    sessionManager.downloadFromUrl("http://any-url.com")
    
    sessionManager.data.displayLines.head should include("Error")
    sessionManager.data.displayLines.head should include("Injected Network Error")
  }

  }
}