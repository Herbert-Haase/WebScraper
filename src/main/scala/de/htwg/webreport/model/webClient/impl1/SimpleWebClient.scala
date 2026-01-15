package de.htwg.webreport.model.webClient.impl1

import de.htwg.webreport.model.webClient.WebClientTrait

import scala.io.Source
import scala.util.Using
import scala.util.{Try, Success, Failure}
import java.net.{HttpURLConnection, URL}

class SimpleWebClient extends WebClientTrait {
  override def get(url: String): Try[String] = Try {
    val source = Source.fromURL(url)
    val content = source.mkString
    source.close()
    content
  }
  def download(url: String): Try[String] = Try {
    val connection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
    
    connection.setRequestProperty("User-Agent", "WebReport/1.0 (Educational Project)")
    
    val is = connection.getInputStream
    val content = Source.fromInputStream(is).mkString
    is.close()
    content
  }
}