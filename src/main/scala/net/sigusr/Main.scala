package net.sigusr

import cats.effect.Console.implicits.ioConsole
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.Logger

import scala.concurrent.ExecutionContext

object Main extends IOApp {

  val user = "fcabestre"

  override def run(args: List[String]): IO[ExitCode] =
    BlazeClientBuilder[IO](ExecutionContext.global).resource.use { client =>
      val loggedClient = Logger(logBody = true, logHeaders = true)(client)
      val githubClient = new LiveGithubClient[IO](loggedClient)
      val githubStats = new LiveGithubStats[IO](githubClient)
      val showStats = new LiveShowStats[IO]
      for {
        userStats <- githubStats.getUserStats(user)
        _ <- showStats.display(userStats)
      } yield ()
    }.as(ExitCode.Success)
}
