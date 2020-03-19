package net.sigusr

import cats.effect.{Async, ContextShift}
import cats.implicits._
import ciris._

case class Config(githubToken: Secret[String])

object Config {
  def load[F[_]: Async: ContextShift]: F[Config] = {
    env("GITHUB_TOKEN").as[String].secret.map(token =>
      Config(token)).load[F]
  }
}
