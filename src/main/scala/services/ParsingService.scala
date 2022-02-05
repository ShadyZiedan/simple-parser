package com.example.parser
package services

import cats.data.EitherT
import cats.effect.Concurrent
import cats.implicits._
import org.http4s.Uri
import org.http4s.client.Client
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


trait ParsingService[F[_]] {
  def parseUri(uriString: String): EitherT[F, ParsingError, Uri]

  def parseDocument(uri: Uri): EitherT[F, ParsingError, Document]
}

sealed trait ParsingError {
  val errorMessage: String
}
case object UriParsingFailure extends ParsingError {
  override val errorMessage: String = "failed to parse uri"
}
case object DocumentFailure extends ParsingError {
  override val errorMessage: String = "Failed to parse document"
}

object ParsingService {

  class Service[F[_]](client: Client[F])(implicit F: Concurrent[F]) extends ParsingService[F] {
    override def parseDocument(uri: Uri): EitherT[F, ParsingError, Document] = {
      val res = client.expect[String](uri)
        .attempt
        .map(e => e.map(v => Jsoup.parse(v)).leftMap(_ => DocumentFailure))
      EitherT.liftF(res)
    }

    override def parseUri(uriString: String): EitherT[F, ParsingError, Uri] = {
      EitherT.fromOptionF(F.pure(Uri.fromString(uriString).toOption), UriParsingFailure)
    }
  }
}
