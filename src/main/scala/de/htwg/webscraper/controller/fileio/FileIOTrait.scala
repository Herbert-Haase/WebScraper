package de.htwg.webscraper.model.fileio

import de.htwg.webscraper.model.data.DataTrait

trait FileIOTrait {
  def save(data: List[DataTrait], filePath: String): Unit
  def load(filePath: String): List[DataTrait]
  def mode: String
}