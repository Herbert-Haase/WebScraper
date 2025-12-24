package de.htwg.webscraper

import com.google.inject.AbstractModule
import de.htwg.webscraper.controller.sessionManager.SessionManagerTrait
import de.htwg.webscraper.controller.sessionManager.impl1.SessionManager
import de.htwg.webscraper.model.fileio.FileIOTrait
import de.htwg.webscraper.model.fileio.implXML.XmlFileIO 
// import de.htwg.webscraper.model.fileio.implJSON.JsonFileIO
import de.htwg.webscraper.model.analyzer.AnalyzerTrait
import de.htwg.webscraper.model.analyzer.impl1.SimpleAnalyzer
import de.htwg.webscraper.model.webClient.WebClientTrait
import de.htwg.webscraper.model.webClient.impl1.SimpleWebClient

class WebScraperModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[AnalyzerTrait]).to(classOf[SimpleAnalyzer])
    bind(classOf[WebClientTrait]).to(classOf[SimpleWebClient])

    bind(classOf[SessionManagerTrait]).to(classOf[SessionManager])

    // bind(classOf[FileIOTrait]).to(classOf[JsonFileIO])
    bind(classOf[FileIOTrait]).to(classOf[XmlFileIO])
  }
}