package org.gitflow

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class BranchRulesTest {

    @Test
    void featureBranchValidatesAndSkipsDeploy() {
        Map res = BranchRules.evaluate('feature/add-trivy-gate', 'a1b2c3d', '14')
        assertEquals('feature', res.branchType)
        assertFalse(res.publishImage as boolean)
        assertEquals('none', res.targetEnvironment)
        assertFalse(res.requiresManualApproval as boolean)
        assertEquals(['sha-a1b2c3d'], res.imageTags)
    }

    @Test
    void developBranchPublishesAndDeploysStagingWithoutApproval() {
        Map res = BranchRules.evaluate('develop', 'a1b2c3d', '22')
        assertEquals('develop', res.branchType)
        assertTrue(res.publishImage as boolean)
        assertEquals('staging', res.targetEnvironment)
        assertFalse(res.requiresManualApproval as boolean)
        assertEquals(['sha-a1b2c3d', 'dev-22'], res.imageTags)
    }

    @Test
    void releaseBranchComputesSemverRcTagAndDeploysStaging() {
        Map res = BranchRules.evaluate('release/v1.4.0', '9f8e7d6', '5')
        assertEquals('release', res.branchType)
        assertTrue(res.publishImage as boolean)
        assertEquals('staging', res.targetEnvironment)
        assertEquals(['sha-9f8e7d6', 'v1.4.0-rc.5'], res.imageTags)
    }

    @Test
    void hotfixBranchRequiresManualApprovalForProduction() {
        Map res = BranchRules.evaluate('hotfix/v1.4.1', '1234567', '3')
        assertEquals('hotfix', res.branchType)
        assertTrue(res.publishImage as boolean)
        assertEquals('production', res.targetEnvironment)
        assertTrue(res.requiresManualApproval as boolean)
        assertEquals(['sha-1234567', 'v1.4.1-hotfix.3'], res.imageTags)
    }

    @Test
    void mainBranchRequiresManualApprovalForProduction() {
        Map res = BranchRules.evaluate('main', 'deadbee', '99')
        assertEquals('main', res.branchType)
        assertTrue(res.publishImage as boolean)
        assertEquals('production', res.targetEnvironment)
        assertTrue(res.requiresManualApproval as boolean)
        assertEquals(['sha-deadbee', 'prod-99'], res.imageTags)
    }

    @Test
    void invalidBranchNameThrowsPolicyException() {
        assertThrows(IllegalArgumentException) {
            BranchRules.evaluate('random-unmanaged-branch', 'abc1234', '1')
        }
        assertThrows(IllegalArgumentException) {
            BranchRules.evaluate('release/not-semver', 'abc1234', '1')
        }
    }
}
