/*
 * Copyright 2010-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.plugin.quickfix;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.quickFix.LightQuickFixTestCase;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.SuppressIntentionAction;
import com.intellij.codeInspection.SuppressableProblemGroup;
import com.intellij.ide.startup.impl.StartupManagerImpl;
import com.intellij.lang.annotation.ProblemGroup;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.psi.JetFile;
import org.jetbrains.jet.plugin.PluginTestCaseBase;
import org.jetbrains.jet.test.TestMetadata;
import org.jetbrains.jet.testing.ConfigLibraryUtil;

import java.util.List;

public abstract class AbstractQuickFixTest extends LightQuickFixTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ((StartupManagerImpl) StartupManager.getInstance(getProject())).runPostStartupActivities();
    }

    protected void doTest(@NotNull String beforeFileName) throws Exception {
        boolean isWithRuntime = beforeFileName.endsWith("Runtime.kt");

        try {
            if (isWithRuntime) {
                ConfigLibraryUtil.configureKotlinRuntime(getModule(), getFullJavaJDK());
            }

            doSingleTest(getTestName(false) + ".kt");
            checkAvailableActionsAreExpected();
            checkForUnexpectedErrors();
        }
        finally {
            if (isWithRuntime) {
                ConfigLibraryUtil.unConfigureKotlinRuntime(getModule(), getProjectJDK());
            }
        }
    }

    public void checkAvailableActionsAreExpected() {
        List<IntentionAction> actions = getAvailableActions();
        Pair<String, Boolean> pair = parseActionHintImpl(getFile(), getEditor().getDocument().getText());
        if (!pair.getSecond()) {
            // Action shouldn't be found. Check that other actions are expected and thus tested action isn't there under another name.
            QuickFixActionsUtils.checkAvailableActionsAreExpected((JetFile) getFile(), actions);
        }
    }

    public static void checkForUnexpectedErrors() {
        QuickFixActionsUtils.checkForUnexpectedErrors((JetFile) getFile());
    }

    @Override
    protected IntentionAction findActionWithText(String text) {
        IntentionAction intention = super.findActionWithText(text);
        if (intention != null) return intention;

        // Support warning suppression
        for (HighlightInfo highlight : doHighlighting()) {
            ProblemGroup group = highlight.getProblemGroup();
            if (group instanceof SuppressableProblemGroup) {
                SuppressableProblemGroup problemGroup = (SuppressableProblemGroup) group;
                PsiElement at = getFile().findElementAt(highlight.getActualStartOffset());
                SuppressIntentionAction[] actions = problemGroup.getSuppressActions(at);
                for (SuppressIntentionAction action : actions) {
                    if (action.getText().equals(text)) {
                        return action;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected String getBasePath() {
        return getClass().getAnnotation(TestMetadata.class).value();
    }

    @NotNull
    @Override
    protected String getTestDataPath() {
        return "./";
    }

    @Override
    protected Sdk getProjectJDK() {
        return PluginTestCaseBase.jdkFromIdeaHome();
    }

    protected static Sdk getFullJavaJDK() {
        return JavaSdk.getInstance().createJdk("JDK", SystemUtils.getJavaHome().getAbsolutePath());
    }
}
