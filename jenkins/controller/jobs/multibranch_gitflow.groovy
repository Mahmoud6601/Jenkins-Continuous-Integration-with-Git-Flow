multibranchPipelineJob('gitflow-ci-cd') {
    displayName('Git Flow CI/CD Multibranch Pipeline')
    description('Automated Git Flow pipeline enforcing branch policy, security scans, staged deploys, and auto-rollback.')

    branchSources {
        git {
            id('gitflow-origin')
            remote('https://github.com/Mahmoud6601/jenkins-gitflow-platform.git')
            includes('main develop feature/* bugfix/* release/v* hotfix/v*')
        }
    }

    orphanedItemStrategy {
        discardOldItems {
            numToKeep(10)
            daysToKeep(30)
        }
    }

    factory {
        workflowBranchProjectFactory {
            scriptPath('Jenkinsfile')
        }
    }
}
