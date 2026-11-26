def call(Map config) {
    String environment = config.environment
    String imageTag    = config.imageTag
    String expectedSha = config.expectedSha ?: ''

    try {
        sh "bash ./scripts/deploy-env.sh ${environment} ${imageTag}"
        sh "bash ./scripts/smoke-test.sh ${environment} ${expectedSha}"
    } catch (Exception err) {
        echo "Deployment or smoke test failed for '${environment}' — triggering automatic rollback!"
        sh "bash ./scripts/rollback.sh ${environment}"
        throw err
    }
}
