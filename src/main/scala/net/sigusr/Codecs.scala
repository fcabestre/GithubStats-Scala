package net.sigusr

import cats.Applicative
import cats.effect.Sync
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

trait JsonCodecs {
  implicit val repoEncoder: Encoder[Repository] =
    Encoder.forProduct3("name", "languages_url", "contributors_url")(u =>
      (u.name, u.languagesUrl, u.contributorsUrl))
  implicit val repoDecoder: Decoder[Repository] =
    Decoder.forProduct3("name", "languages_url", "contributors_url")(Repository.apply)

  implicit val contribEncoder: Encoder[Contributor] =
    Encoder.forProduct1("login")(_.login)
  implicit val contribDecoder: Decoder[Contributor] =
    Decoder.forProduct1("login")(Contributor.apply)
}

object Codecs extends JsonCodecs {
  implicit def deriveEntityEncoder[F[_]: Applicative, A: Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]

  implicit def deriveEntityDecoder[F[_]: Applicative: Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]
}
