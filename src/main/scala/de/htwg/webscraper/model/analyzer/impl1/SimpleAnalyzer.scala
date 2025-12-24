package de.htwg.webscraper.model.analyzer.impl1

import de.htwg.webscraper.model.analyzer.AnalyzerTrait
import de.htwg.webscraper.model.data.DataTrait
import de.htwg.webscraper.model.data.impl1.Data

class SimpleAnalyzer extends AnalyzerTrait {
  override def process(original: List[String], filtered: Option[List[String]], source: String): DataTrait = {
    filtered match {
      case Some(lines) => Data.fromFiltered(original, lines, source)
      case None        => Data.fromContent(original, source)
    }
  }
}