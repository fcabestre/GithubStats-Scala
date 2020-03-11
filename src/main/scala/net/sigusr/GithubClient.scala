package net.sigusr

import cats.effect.Sync
import cats.implicits._
import io.circe.Decoder
import net.sigusr.Codecs._
import org.http4s.Method.GET
import org.http4s.circe.JsonDecoder
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.{Accept, Authorization}
import org.http4s.util.CaseInsensitiveString
import org.http4s.{Credentials, MediaType, Uri}

case class Repository(name: String, languagesUrl: String, contributorsUrl: String) {
  override def toString: String = {
    s"""
       |Name:             ${this.name}
       |Languages URL:    ${this.languagesUrl}
       |Contributors URL: ${this.contributorsUrl}
       |""".stripMargin
  }
}

case class Contributor(login: String)

trait GithubClient[F[_]] {
  def findUserRepositories(user: String): F[List[Repository]]
  def findRepositoryLanguages(repo: Repository): F[Map[String, Int]]
  def findRepositoryContributors(repo: Repository): F[List[Contributor]]
}

class LiveGithubClient[F[_]: JsonDecoder: MonadThrow: Sync](client: Client[F]) extends GithubClient[F] with Http4sClientDsl[F] {

  private def baseUri(user: String) = s"https://api.github.com/users/${user}/repos"

  private val token = "a8c6d8906ef4abac6b2cea4068540f6a189cb2d5"
  private val authHeader = Authorization(Credentials.Token(CaseInsensitiveString("Token"), token))

  private def get[R: Decoder](uri: String): F[R] = Uri
    .fromString(uri)
    .liftTo[F]
    .flatMap { uri =>
      client.expect[R](GET(
        uri,
        Accept(MediaType.application.json),
        authHeader
      ))
    }

  def findUserRepositories(user: String): F[List[Repository]] = get(baseUri(user))

  def findRepositoryLanguages(repo: Repository): F[Map[String, Int]] = get(repo.languagesUrl)

  def findRepositoryContributors(repo: Repository): F[List[Contributor]] = get(repo.contributorsUrl)
}

