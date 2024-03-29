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
package io.micronaut.docs.annotation.headers

import io.micronaut.docs.annotation.Pet
import io.micronaut.docs.annotation.PetOperations
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.client.annotation.Client
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import io.micronaut.core.async.annotation.SingleResult
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

// tag::class[]
@Client("/pets")
@Header(name="X-Pet-Client", value='${pet.client.id}')
interface PetClient extends PetOperations {

    @Override
    @SingleResult
    Publisher<Pet> save(@NotBlank String name, @Min(1L) int age)

    @Get("/{name}")
    @SingleResult
    Publisher<Pet> get(String name)
}
// end::class[]
