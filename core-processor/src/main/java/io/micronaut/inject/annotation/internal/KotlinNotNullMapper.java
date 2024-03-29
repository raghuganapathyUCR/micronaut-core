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
package io.micronaut.inject.annotation.internal;

import io.micronaut.core.annotation.AnnotationUtil;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Internal;
import io.micronaut.inject.annotation.NamedAnnotationTransformer;
import io.micronaut.inject.visitor.VisitorContext;

import io.micronaut.core.annotation.NonNull;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * An annotation mapper that maps Kotlin's NotNull to a common annotation.
 *
 * @author graemerocher
 * @since 1.1.4
 */
@Internal
public class KotlinNotNullMapper implements NamedAnnotationTransformer {
    @NonNull
    @Override
    public String getName() {
        return "org.jetbrains.annotations.NotNull";
    }

    @Override
    public List<AnnotationValue<?>> transform(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        return Collections.singletonList(
                AnnotationValue.builder(AnnotationUtil.NON_NULL).build()
        );
    }
}
