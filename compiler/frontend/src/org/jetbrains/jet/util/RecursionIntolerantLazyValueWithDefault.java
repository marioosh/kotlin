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

package org.jetbrains.jet.util;

import org.jetbrains.jet.utils.RecursionIntolerantLazyValue;

public abstract class RecursionIntolerantLazyValueWithDefault<T> extends RecursionIntolerantLazyValue<T> {
    private final T defaultValue;

    protected RecursionIntolerantLazyValueWithDefault(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    protected T getValueOnErrorReentry() {
        return defaultValue;
    }
}