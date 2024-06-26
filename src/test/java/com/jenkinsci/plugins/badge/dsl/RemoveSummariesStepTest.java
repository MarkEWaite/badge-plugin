/*
 * The MIT License
 *
 * Copyright (c) 2024, Badge Plugin Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.jenkinsci.plugins.badge.dsl;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jenkinsci.plugins.badge.action.BadgeSummaryAction;
import java.util.List;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;

class RemoveSummariesStepTest extends AbstractBadgeTest {

    @Test
    void removeSummaries_by_id(JenkinsRule r) throws Exception {
        removeSummaries(r, "removeSummaries(id:'a')", "b");
    }

    @Test
    void removeSummaries_all(JenkinsRule r) throws Exception {
        removeSummaries(r, "removeSummaries()");
    }

    private void removeSummaries(JenkinsRule r, String removeScript, String... remainingBadgeIds) throws Exception {
        String icon = randomUUID().toString();

        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition(
                "def summaryA = createSummary(id:'a', icon:\"" + icon + "\")\n"
                        + "def summaryB = createSummary(id:'b', icon:\"" + icon + "\")\n"
                        + removeScript,
                true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
        List<BadgeSummaryAction> summaryActions = b.getActions(BadgeSummaryAction.class);
        assertEquals(remainingBadgeIds.length, summaryActions.size());

        for (int i = 0; i < remainingBadgeIds.length; i++) {
            assertEquals(remainingBadgeIds[i], summaryActions.get(i).getId());
        }
    }
}
