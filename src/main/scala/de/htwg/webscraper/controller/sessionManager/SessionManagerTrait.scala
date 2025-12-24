package de.htwg.webscraper.controller.sessionManager

import de.htwg.webscraper.model.data.DataTrait
import de.htwg.webscraper.util.Observable

trait SessionManagerTrait extends Observable {
  def data: DataTrait
  def downloadFromUrl(url: String): Unit
  def loadFromFile(path: String): Unit
  def loadFromText(text: String): Unit
  def saveSession(path: String): Unit 
  def filter(word: String): Unit
  def undo(): Unit
  def redo(): Unit
  def reset(): Unit
  def storageMode: String
}