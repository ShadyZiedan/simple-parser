package com.example.parser

import http.ParsingApi
import services.ParsingService

import cats.effect.std.Console
import cats.effect._
import fs2.INothing
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder


object Main extends IOApp{

  def stream[F[_]: Concurrent: Async]: fs2.Stream[F, INothing] = {
    for {
      client <- BlazeClientBuilder[F].stream
      parsingService = new ParsingService.Service[F](client)
      httpApp = ParsingApi.routes(parsingService).orNotFound
      exitcode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(httpApp)
        .serve

    } yield exitcode
  }.drain

  override def run(args: List[String]): IO[ExitCode] = {
    stream[IO].compile.drain.as(ExitCode.Success)

  }
}
