package net.sigusr

import cats.Monad
import cats.effect.Sync
import cats.implicits._

trait ShowStats[F[_]] {
  def display(userStats: UserStats): F[Unit]
}

class LiveShowStats[F[_]: Monad: Sync] extends ShowStats[F] {
  private def putStrLn(s: String) = Sync[F].delay(println(s))
  override def display(userStats: UserStats): F[Unit] =
    for {
      _ <- putStrLn(s"User: ${userStats.name}")
      _ <- userStats.repositories.traverse(r =>
        for {
          _ <- putStrLn(s"    Repository: ${r.repoName}")
          _ <- putStrLn(s"        Languages: ${r.languages.mkString(", ")}")
          _ <- putStrLn(s"     Contributors: ${r.contributors.mkString(", ")}")
        } yield ()
      )
      _ <- putStrLn(s"Language total: ${userStats.languageCount}")
      _ <- putStrLn(s"Contributors total: ${userStats.contributorCount}")
    } yield ()
}