/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.docs.server.routes

// tag::imports[]
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
// end::imports[]

// tag::startclass[]
@Controller("/issues") // <1>
class IssuesController {
// end::startclass[]

    // tag::normal[]
    @Get("/{number}") // <2>
    fun issue(number: Int): String { // <3>
        return "Issue # $number!" // <4>
    }

    @Get("/issue/{number}")
    fun issueById(@PathVariable("number") id: Int): String {
        return "Issue # $id!" // <5>
    }
    // end::normal[]

    // tag::defaultvalue[]
    @Get("/default{/number}") // <1>
    fun issueFromIdOrDefault(@PathVariable(defaultValue = "0") number: Int): String { // <2>
        return "Issue # $number!"
    }
    // end::defaultvalue[]

// tag::endclass[]
}
// end::endclass[]
