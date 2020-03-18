package net.sigusr

import cats.Monad
import cats.effect.Console
import cats.implicits._

trait ShowStats[F[_]] {
  def display(userStats: UserStats): F[Unit]
}

class LiveShowStats[F[_]: Monad: Console] extends ShowStats[F] {
  private val C = Console[F]
  override def display(userStats: UserStats): F[Unit] =
    for {
      _ <- C.putStrLn(s"User: ${userStats.name}")
      _ <- userStats.repositories.traverse(r =>
        for {
          _ <- C.putStrLn(s"    Repository: ${r.repoName}")
          _ <- C.putStrLn(s"        Languages: ${r.languages.mkString(", ")}")
          _ <- C.putStrLn(s"     Contributors: ${r.contributors.mkString(", ")}")
        } yield ()
      )
      _ <- C.putStrLn(s"Language total: ${userStats.languageCount}")
      _ <- C.putStrLn(s"Contributors total: ${userStats.contributorCount}")
    } yield ()
}