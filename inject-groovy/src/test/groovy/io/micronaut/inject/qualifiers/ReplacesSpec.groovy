/*
 * Copyright 2017-2019 original authors
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
package io.micronaut.inject.qualifiers

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Replaces
import jakarta.inject.Inject
import jakarta.inject.Singleton
import spock.lang.Specification
/**
 * Created by graemerocher on 26/05/2017.
 */
class ReplacesSpec extends Specification {

    void "test that a bean can be marked to replace another bean"() {
        given:
        ApplicationContext context = ApplicationContext.run()

        when:"A bean has a dependency on an interface with multiple impls"
        B b = context.getBean(B)

        then:"The impl that replaces the other impl is the only one present"
        b.all.size() == 1
        !b.all.any() { it instanceof A1 }
        b.all.any() { it instanceof A2 }
        b.a instanceof A2

        cleanup:
        context.close()
    }

    static interface A {}


    @Singleton
    static class A1 implements A {}

    @Replaces(A1)
    @Singleton
    static class A2 implements A {}

    static class B {
        @Inject
        List<A> all

        @Inject
        A a
    }
}
