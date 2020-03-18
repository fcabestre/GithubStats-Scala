package net.sigusr

import cats.Parallel
import cats.effect.Sync
import cats.implicits._

case class RepoStats(repoName: String, languages: List[String], contributors: List[String])
case class UserStats(name: String, repositories: List[RepoStats], languageCount: Int, contributorCount: Int)

trait GithubStats[F[_]] {
  def getUserStats(user: String): F[UserStats]
}

class LiveGithubStats[F[_]: MonadThrow: Parallel: Sync](private val githubClient: GithubClient[F]) extends GithubStats[F] {

  private def getRepoStats: Repository => F[RepoStats] = r => {
    val languages = githubClient.findRepositoryLanguages(r)
    val contributors = githubClient.findRepositoryContributors(r)
    (languages, contributors).parMapN(
      (languagesMap, contributorsList) => {
        val languages = languagesMap.keys.toList
        val contributors = contributorsList.map(_.login)
        RepoStats(r.name, languages, contributors)
      })
  }

  def getUserStats(user: String): F[UserStats] =
    for {
      repos <- githubClient.findUserRepositories(user)
      stats <- repos.parTraverse(getRepoStats)
      accumulator = (Set[String](), Set[String]())
      (languages, contributors) = stats.foldRight(accumulator)(
        (r, acc) => (acc._1 ++ r.languages, acc._2 ++ r.contributors)
      )
    } yield UserStats(user, stats, languages.size, contributors.size)
}
