package net.sigusr

import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.middleware.Logger

import scala.concurrent.ExecutionContext

object Main extends TaskApp {

  val user = "fcabestre"

  override def run(args: List[String]): Task[ExitCode] =
    Config.load[Task].flatMap { config =>
      BlazeClientBuilder[Task](ExecutionContext.global).resource.use { client =>
        val loggedClient = Logger(logBody = true, logHeaders = true)(client)
        val githubClient = new LiveGithubClient[Task](config, loggedClient)
        val githubStats = new LiveGithubStats[Task](githubClient)
        val showStats = new LiveShowStats[Task]
        for {
          userStats <- githubStats.getUserStats(user)
          _ <- showStats.display(userStats)
        } yield (ExitCode.Success)
      }
    }
}
