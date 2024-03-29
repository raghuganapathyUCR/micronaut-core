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
package io.micronaut.docs.inject.generated;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.beans.BeanIntrospector;
import io.micronaut.test.generated.IntrospectedExample;
import org.junit.jupiter.api.Test;

public class VerifyIntrospectionSpec {

    @Test
    public void test() {
        ApplicationContext context = ApplicationContext.run();

        assertTrue(BeanIntrospector.SHARED.findIntrospection(IntrospectedExample.class).isPresent());

        context.stop();
    }

}
