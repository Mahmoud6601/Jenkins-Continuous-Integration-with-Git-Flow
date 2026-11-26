def call(Map config) {
    String imageRepo = config.imageRepo ?: 'localhost:5000/gitflow-payload'
    List<String> tags = config.tags ?: ['sha-local']
    String primaryTag = tags[0]
    String gitSha = config.gitSha ?: 'local'
    String branch = config.branch ?: 'local'
    String buildNumber = config.buildNumber ?: '0'
    boolean push = config.push ?: false

    sh """
        set -euo pipefail
        docker build \
          --build-arg APP_VERSION=${primaryTag} \
          --build-arg GIT_SHA=${gitSha} \
          --build-arg GIT_BRANCH=${branch} \
          --build-arg BUILD_NUMBER=${buildNumber} \
          -t ${imageRepo}:${primaryTag} ./payload
    """

    for (int i = 1; i < tags.size(); i++) {
        sh "docker tag ${imageRepo}:${primaryTag} ${imageRepo}:${tags[i]}"
    }

    sh """
        set -euo pipefail
        if command -v trivy >/dev/null 2>&1; then
          trivy image --vuln-type os --severity HIGH,CRITICAL --ignore-unfixed --exit-code 1 ${imageRepo}:${primaryTag}
          trivy image --format cyclonedx --output sbom-${primaryTag}.json ${imageRepo}:${primaryTag}
        else
          echo "trivy not installed on agent — skipping local scan"
        fi
    """

    if (push) {
        for (String tag : tags) {
            sh "docker push ${imageRepo}:${tag}"
        }
    }
}
