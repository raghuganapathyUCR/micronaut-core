/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.inject;

import io.micronaut.core.annotation.Internal;

/**
 * A variation of {@link ExecutionHandle} that invokes without arguments validation.
 *
 * @param <T> The target type
 * @param <R> The result type
 * @author Denis Stepanov
 * @since 4.0.0
 */
@Internal
public interface UnsafeExecutionHandle<T, R> extends ExecutionHandle<T, R> {

    /**
     * Invokes the method without validation.
     *
     * @param arguments The arguments
     * @return The result
     */
    R invokeUnsafe(Object... arguments);

}
