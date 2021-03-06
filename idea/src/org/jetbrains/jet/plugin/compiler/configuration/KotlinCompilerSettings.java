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

package org.jetbrains.jet.plugin.compiler.configuration;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.compiler.CompilerSettings;

import static org.jetbrains.jet.compiler.SettingConstants.KOTLIN_COMPILER_SETTINGS_SECTION;
import static org.jetbrains.jet.compiler.SettingConstants.KOTLIN_COMPILER_SETTINGS_PATH;

@State(
    name = KOTLIN_COMPILER_SETTINGS_SECTION,
    storages = {
        @Storage(file = StoragePathMacros.PROJECT_FILE),
        @Storage(file = KOTLIN_COMPILER_SETTINGS_PATH, scheme = StorageScheme.DIRECTORY_BASED)
    }
)
public class KotlinCompilerSettings extends BaseKotlinCompilerSettings<CompilerSettings> {

    @NotNull
    @Override
    protected CompilerSettings createSettings() {
        return new CompilerSettings();
    }

    public static KotlinCompilerSettings getInstance(Project project) {
        return ServiceManager.getService(project, KotlinCompilerSettings.class);
    }
}
