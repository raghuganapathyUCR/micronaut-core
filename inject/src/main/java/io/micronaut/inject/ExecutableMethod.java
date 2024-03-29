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

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.naming.Described;
import io.micronaut.core.type.Argument;
import io.micronaut.core.type.Executable;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * <p>An executable method is a compile time produced invocation of a method call. Avoiding the use of reflection and
 * allowing the JIT to optimize the call</p>
 *
 * @param <T> The declaring type
 * @param <R> The result of the method call
 * @author Graeme Rocher
 * @since 1.0
 */
public interface ExecutableMethod<T, R> extends Executable<T, R>, MethodReference<T, R>, Described {
    /**
     * Defines whether the method is abstract.
     *
     * @return Is the method abstract.
     * @since 1.2.3
     */
    default boolean isAbstract() {
        return false;
    }

    /**
     * Defines whether the method is Kotlin suspend function.
     *
     * @return Is the method Kotlin suspend function.
     * @since 1.3.0
     */
    @Experimental
    default boolean isSuspend() {
        return false;
    }

    /**
     * Get the method description.
     * @param simple If simple type names are to be used
     * @return The method description
     */
    @Override
    default String getDescription(boolean simple) {
        Argument<R> argument = getReturnType().asArgument();
        String typeString = argument.getTypeString(simple);
        String args = Arrays.stream(getArguments()).map(arg -> arg.getTypeString(simple) + " " + arg.getName()).collect(Collectors.joining(","));
        return typeString + " " + getName() + "(" + args + ")";
    }

    @Override
    default String getDescription() {
        return getDescription(true);
    }
}
