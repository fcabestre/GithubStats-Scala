package net.sigusr

import cats.implicits._
import cats.kernel.Monoid
import io.circe.Decoder
import net.sigusr.Codecs._
import org.http4s.Method.GET
import org.http4s.circe.{JsonDecoder, _}
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.{Accept, Authorization}
import org.http4s.util.CaseInsensitiveString
import org.http4s.{Credentials, MediaType, Status, Uri}

import scala.util.control.NoStackTrace

case class Repository(name: String, languagesUrl: String, contributorsUrl: String)
case class Contributor(login: String)

trait GithubClient[F[_]] {
  type Languages = Map[String, Int]

  def findUserRepositories(user: String): F[List[Repository]]
  def findRepositoryLanguages(repo: Repository): F[Languages]
  def findRepositoryContributors(repo: Repository): F[List[Contributor]]
}

case class SomeError(cause: String) extends NoStackTrace

class LiveGithubClient[F[_]: JsonDecoder: MonadThrow](client: Client[F]) extends GithubClient[F] with Http4sClientDsl[F] {

  private def baseUri(user: String) = s"https://api.github.com/users/$user/repos"

  private val token = "a8c6d8906ef4abac6b2cea4068540f6a189cb2d5"
  private val authHeader = Authorization(Credentials.Token(CaseInsensitiveString("Token"), token))
  private val contentHeader = Accept(MediaType.application.json)

  private def get[R: Decoder: Monoid](uri: String): F[R] = Uri
    .fromString(uri)
    .liftTo[F]
    .flatMap { uri =>
      client.fetch[R](GET(uri, contentHeader, authHeader)) { r =>
        r.status match {
          case Status.Ok => r.asJsonDecode[R]
          case Status.NoContent => MonadThrow[F].pure(Monoid[R].empty)
          case _ => SomeError(Option(r.status.reason).getOrElse("Unknown")).raiseError[F, R]
        }
      }
    }

  def findUserRepositories(user: String): F[List[Repository]] = get(baseUri(user))

  def findRepositoryLanguages(repo: Repository): F[Languages] = get(repo.languagesUrl)

  def findRepositoryContributors(repo: Repository): F[List[Contributor]] = get(repo.contributorsUrl)
}

