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
package io.micronaut.docs.aop.validation

import io.micronaut.context.ApplicationContext
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import jakarta.validation.ConstraintViolationException

/**
 * @author graemerocher
 * @since 1.0
 */
class BookServiceSpec extends Specification {

    @Shared @AutoCleanup ApplicationContext applicationContext = ApplicationContext.run()

    // tag::test[]
    void "test validate book service"() {
        given:
        BookService bookService = applicationContext.getBean(BookService)

        when:"An invalid title is passed"
        bookService.getAuthor("")

        then:"A constraint violation occurred"
        def e = thrown(ConstraintViolationException)
        e.message == 'getAuthor.title: must not be blank'
    }
    // end::test[]
}
