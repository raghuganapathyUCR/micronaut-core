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
package io.micronaut.docs.server.json

import io.micronaut.context.annotation.Requires
import io.micronaut.core.async.annotation.SingleResult
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.hateoas.Link
import io.micronaut.json.JsonSyntaxException
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

@Requires(property = "spec.name", value = "PersonControllerSpec")
// tag::class[]
@Controller("/people")
class PersonController {

    internal var inMemoryDatastore: MutableMap<String, Person> = ConcurrentHashMap()
    // end::class[]

    @Get
    fun index(): Collection<Person> {
        return inMemoryDatastore.values
    }

    @Get("/{name}")
    @SingleResult
    operator fun get(name: String): Publisher<Person> {
        return if (inMemoryDatastore.containsKey(name)) {
            Mono.just(inMemoryDatastore[name])
        } else Mono.empty()
    }

    // tag::single[]
    @Post("/saveReactive")
    @SingleResult
    fun save(@Body person: Publisher<Person>): Publisher<HttpResponse<Person>> { // <1>
        return Mono.from(person).map { p ->
            inMemoryDatastore[p.firstName] = p // <2>
            HttpResponse.created(p) // <3>
        }
    }
    // end::single[]

    // tag::args[]
    @Post("/saveWithArgs")
    fun save(firstName: String, lastName: String, age: Optional<Int>): HttpResponse<Person> {
        val p = Person(firstName, lastName)
        age.ifPresent { p.age = it }
        inMemoryDatastore[p.firstName] = p
        return HttpResponse.created(p)
    }
    // end::args[]

    // tag::future[]
    @Post("/saveFuture")
    fun save(@Body person: CompletableFuture<Person>): CompletableFuture<HttpResponse<Person>> {
        return person.thenApply { p ->
            inMemoryDatastore[p.firstName] = p
            HttpResponse.created(p)
        }
    }
    // end::future[]

    // tag::regular[]
    @Post
    fun save(@Body person: Person): HttpResponse<Person> {
        inMemoryDatastore[person.firstName] = person
        return HttpResponse.created(person)
    }
    // end::regular[]

    // tag::localError[]
    @Error
    fun jsonError(request: HttpRequest<*>, e: JsonSyntaxException): HttpResponse<JsonError> { // <1>
        val error = JsonError("Invalid JSON: ${e.message}") // <2>
                .link(Link.SELF, Link.of(request.uri))

        return HttpResponse.status<JsonError>(HttpStatus.BAD_REQUEST, "Fix Your JSON")
                .body(error) // <3>
    }
    // end::localError[]

    @Get("/error")
    fun throwError(): String {
        throw RuntimeException("Something went wrong")
    }

    // tag::globalError[]
    @Error(global = true) // <1>
    fun error(request: HttpRequest<*>, e: Throwable): HttpResponse<JsonError> {
        val error = JsonError("Bad Things Happened: ${e.message}") // <2>
                .link(Link.SELF, Link.of(request.uri))

        return HttpResponse.serverError<JsonError>()
                .body(error) // <3>
    }
    // end::globalError[]

    // tag::statusError[]
    @Error(status = HttpStatus.NOT_FOUND)
    fun notFound(request: HttpRequest<*>): HttpResponse<JsonError> { // <1>
        val error = JsonError("Person Not Found") // <2>
                .link(Link.SELF, Link.of(request.uri))

        return HttpResponse.notFound<JsonError>()
                .body(error) // <3>
    }
    // end::statusError[]

    // tag::endclass[]
}
// end::endclass[]
