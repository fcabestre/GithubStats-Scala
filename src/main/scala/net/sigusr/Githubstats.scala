package net.sigusr

import cats.effect.Console.io._
import cats.effect._
import cats.implicits._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.Logger

import scala.concurrent.ExecutionContext

case class Stats(repository: String, languages: List[String], contributors: List[String]) {
  override def toString: String =
    s"""
      | Repository:     ${this.repository}
      |   Languages:    ${this.languages.mkString(", ")}
      |   Contributors: ${this.contributors.mkString(", ")}
      |""".stripMargin
}

object Githubstats extends IOApp {

  val user = "fcabestre"

  override def run(args: List[String]): IO[ExitCode] =
    BlazeClientBuilder[IO](ExecutionContext.global).resource.use { client =>
      val githubClient = new LiveGithubClient[IO](Logger(logBody = true, logHeaders = true)(client))
      for {
        repos <- githubClient.findUserRepositories(user)
        stats <- repos.parTraverse(r => {
          val languages = githubClient.findRepositoryLanguages(r)
          val contributors = githubClient.findRepositoryContributors(r)
          (languages, contributors).parMapN(
            (languages, contributors) =>
              Stats(r.name, languages.keys.toList, contributors.map(_.login)))
        })
        _ <- stats.traverse(s => putStrLn(s.toString()))
      } yield ()
    }.as(ExitCode.Success)
}
