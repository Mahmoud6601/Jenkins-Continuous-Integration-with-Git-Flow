package org.gitflow

import java.util.regex.Pattern

/**
 * Pure Git Flow policy and SemVer tag calculation rules.
 * Decoupled from Jenkins runtime so every branch rule is 100% unit-testable.
 */
class BranchRules implements Serializable {

    private static final Pattern FEATURE_PATTERN = Pattern.compile('^feature/[a-z0-9][a-z0-9._-]*$')
    private static final Pattern BUGFIX_PATTERN  = Pattern.compile('^bugfix/[a-z0-9][a-z0-9._-]*$')
    private static final Pattern RELEASE_PATTERN = Pattern.compile('^release/v(\\d+\\.\\d+\\.\\d+)$')
    private static final Pattern HOTFIX_PATTERN  = Pattern.compile('^hotfix/v(\\d+\\.\\d+\\.\\d+)$')

    static Map<String, Object> evaluate(String branchName, String shortSha, String buildNumber) {
        if (!branchName || branchName.trim().isEmpty()) {
            throw new IllegalArgumentException('Branch name must not be empty.')
        }

        String branch = branchName.trim()
        String shaTag = "sha-${shortSha ?: 'unknown'}".toString()
        String build = buildNumber ?: '0'

        if (branch == 'main') {
            return [
                branchType             : 'main',
                valid                  : true,
                publishImage           : true,
                targetEnvironment      : 'production',
                requiresManualApproval : true,
                imageTags              : [shaTag, "prod-${build}".toString()]
            ]
        }

        if (branch == 'develop') {
            return [
                branchType             : 'develop',
                valid                  : true,
                publishImage           : true,
                targetEnvironment      : 'staging',
                requiresManualApproval : false,
                imageTags              : [shaTag, "dev-${build}".toString()]
            ]
        }

        def releaseMatcher = RELEASE_PATTERN.matcher(branch)
        if (releaseMatcher.matches()) {
            String semver = releaseMatcher.group(1)
            return [
                branchType             : 'release',
                valid                  : true,
                publishImage           : true,
                targetEnvironment      : 'staging',
                requiresManualApproval : false,
                imageTags              : [shaTag, "v${semver}-rc.${build}".toString()]
            ]
        }

        def hotfixMatcher = HOTFIX_PATTERN.matcher(branch)
        if (hotfixMatcher.matches()) {
            String semver = hotfixMatcher.group(1)
            return [
                branchType             : 'hotfix',
                valid                  : true,
                publishImage           : true,
                targetEnvironment      : 'production',
                requiresManualApproval : true,
                imageTags              : [shaTag, "v${semver}-hotfix.${build}".toString()]
            ]
        }

        if (FEATURE_PATTERN.matcher(branch).matches() || BUGFIX_PATTERN.matcher(branch).matches()) {
            return [
                branchType             : branch.startsWith('feature/') ? 'feature' : 'bugfix',
                valid                  : true,
                publishImage           : false,
                targetEnvironment      : 'none',
                requiresManualApproval : false,
                imageTags              : [shaTag]
            ]
        }

        throw new IllegalArgumentException(
            "Branch '${branch}' violates Git Flow naming policy. Allowed: main, develop, feature/*, bugfix/*, release/vX.Y.Z, hotfix/vX.Y.Z."
        )
    }
}
