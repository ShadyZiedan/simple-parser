package com.example.parser
package http

import models.{ParsingRequest, ParsingResult}
import services.ParsingService

import cats.effect.Concurrent
import fs2.Stream
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl

object ParsingApi {



  def routes[F[_]](parsingService: ParsingService[F])(implicit F: Concurrent[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      case req @ POST -> Root / "parse" =>
        val stream = Stream.eval(req.as[ParsingRequest])
          .flatMap(r => Stream.emits(r.uris))
          .map(uriString => parsingService.parseUri(uriString).leftMap(error => (uriString, error.errorMessage)))
          .parEvalMap(20)(res => res.flatMap(uri => parsingService.parseDocument(uri)
            .map(doc => (uri, doc))
            .leftMap(error => (uri.toString, error.errorMessage))).value)
          .map {
            case Left(l) => ParsingResult(uri = l._1, title = None, error = Some(l._2))
            case Right(r) => ParsingResult(uri = r._1.toString, title = Some(r._2.title), error = None)
          }
          .compile
          .toList

        Ok(stream)

    }
  }

}
