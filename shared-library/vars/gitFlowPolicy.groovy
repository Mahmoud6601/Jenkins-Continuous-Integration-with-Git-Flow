import org.gitflow.BranchRules

def call(Map config = [:]) {
    String branch = config.branch ?: (env.BRANCH_NAME ?: 'develop')
    String sha    = config.shortSha ?: (env.GIT_COMMIT ? env.GIT_COMMIT.take(7) : 'local00')
    String build  = config.buildNumber ?: (env.BUILD_NUMBER ?: '0')

    Map policy = BranchRules.evaluate(branch, sha, build)
    echo "Git Flow Policy -> branch=${branch} type=${policy.branchType} target=${policy.targetEnvironment} publish=${policy.publishImage} tags=${policy.imageTags.join(',')}"
    return policy
}
