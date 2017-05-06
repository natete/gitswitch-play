package simplegit.services

import javax.inject.Inject

/**
  * Created by natete on 16/04/17.
  */
class GitServiceFactory @Inject()(gitHubService: GitHubService) {

  def getGitService(gitType: String = ""): GitService = {
    gitType match {
      case "GITHUB" => gitHubService
      case _ => gitHubService
    }
  }
}
