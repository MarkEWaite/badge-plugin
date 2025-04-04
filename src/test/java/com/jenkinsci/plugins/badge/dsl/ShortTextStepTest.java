/*
 * The MIT License
 *
 * Copyright (c) 2025, Badge Plugin Authors
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jenkinsci.plugins.badge.action.BadgeAction;
import hudson.model.BuildBadgeAction;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
@Deprecated(since = "2.0", forRemoval = true)
class ShortTextStepTest {

    private static JenkinsRule r;

    @BeforeAll
    static void setUp(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void text() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertNull(step.getText());

        String text = UUID.randomUUID().toString();
        step = new AddShortTextStep(text);
        assertEquals(text, step.getText());
    }

    @Test
    void color() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertNull(step.getColor());

        step.setColor("red");
        assertEquals("red", step.getColor());
    }

    @Test
    void background() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertNull(step.getBackground());

        step.setBackground("red");
        assertEquals("red", step.getBackground());
    }

    @Test
    void border() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertNull(step.getBorder());

        step.setBorder(1);
        assertEquals(1, step.getBorder());
    }

    @Test
    void borderColor() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertNull(step.getBorderColor());

        step.setBorderColor("red");
        assertEquals("red", step.getBorderColor());
    }

    @Test
    void link() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertNull(step.getLink());

        step.setLink("https://jenkins.io");
        assertEquals("https://jenkins.io", step.getLink());
    }

    @Test
    void deprecated() {
        AddShortTextStep step = new AddShortTextStep(null);
        assertTrue(step.getDescriptor().isAdvanced());
    }

    @Test
    void addShortText() throws Exception {
        String text = UUID.randomUUID().toString();
        String color = UUID.randomUUID().toString();
        String background = UUID.randomUUID().toString();
        int border = new Random().nextInt();
        String borderColor = UUID.randomUUID().toString();
        String link = "http://" + UUID.randomUUID();

        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition(
                "addShortText(text:\"" + text + "\", color:\"" + color + "\", background:\"" + background
                        + "\", border:" + border + ", borderColor:\"" + borderColor + "\", link:\"" + link + "\")",
                true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = b.getBadgeActions();
        assertEquals(1, badgeActions.size());

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertEquals(text, action.getText());
        assertNull(action.getIcon());
        assertEquals(link, action.getLink());
    }

    @Test
    void jenkinsColorStyle() throws Exception {
        String text = UUID.randomUUID().toString();
        String color = "jenkins-!-color-red";
        int border = 1;

        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition(
                "addShortText(text:\"" + text + "\", color:\"" + color + "\", border:\"" + border + "\")", true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = b.getBadgeActions();
        assertEquals(1, badgeActions.size());

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertEquals(text, action.getText());
        assertEquals("border: 1px solid ;color: var(---red);", action.getStyle());
    }

    @Test
    void jenkinsWarningStyle() throws Exception {
        String text = UUID.randomUUID().toString();
        String color = "jenkins-!-warning-color";
        int border = 1;

        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition(
                "addShortText(text:\"" + text + "\", color:\"" + color + "\", border:\"" + border + "\")", true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = b.getBadgeActions();
        assertEquals(1, badgeActions.size());

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertEquals(text, action.getText());
        assertEquals("border: 1px solid ;color: var(--warning-color);", action.getStyle());
    }

    @Test
    void addShortText_minimal() throws Exception {
        String text = UUID.randomUUID().toString();

        WorkflowJob p = r.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition("addShortText(text:\"" + text + "\")", true));
        WorkflowRun b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));

        List<BuildBadgeAction> badgeActions = b.getBadgeActions();
        assertEquals(1, badgeActions.size());

        BadgeAction action = (BadgeAction) badgeActions.get(0);
        assertEquals(text, action.getText());
        assertNull(action.getIcon());
        assertNull(action.getLink());
    }
}
