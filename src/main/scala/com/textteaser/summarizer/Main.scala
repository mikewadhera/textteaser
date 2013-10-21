package com.textteaser.summarizer

import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import com.google.inject.Guice
import com.mongodb._
import net.liftweb.mongodb._
import com.textteaser.summarizer.models.Keyword
import com.foursquare.rogue.LiftRogue._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j._
import scala.io.Source

object Main extends App {

  implicit val formats = DefaultFormats
  val config = new Config
  val guice = new ScalaInjector(Guice.createInjector(new GuiceModule(config)))

  val summarizer = guice.instance[Summarizer]
  val log = guice.instance[Logger]

  MongoDB.defineDb(DefaultMongoIdentifier, guice.instance[Mongo], config.db.name)
  
  val source = Source.fromFile(args(0))
  
  val json = parse(source.mkString)

  val id = "anythingyoulikehere"
  val title = (json \ "title").extract[String]
  val text = (json \ "text").extract[String]

  val article = Article(id, title, text, "", "", "Sports")
  val summary = summarizer.summarize(article.article, article.title, article.id, article.blog, article.category)

  println(summarizer.toJSON(summary))
}
