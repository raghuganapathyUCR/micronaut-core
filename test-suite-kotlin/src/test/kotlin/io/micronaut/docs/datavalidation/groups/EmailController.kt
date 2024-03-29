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
package io.micronaut.docs.datavalidation.groups

import io.micronaut.context.annotation.Requires
//tag::imports[]
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import jakarta.validation.Valid
//end::imports[]

@Requires(property = "spec.name", value = "datavalidationgroups")
//tag::clazz[]
@Validated // <1>
@Controller("/email")
open class EmailController {

    @Post("/createDraft")
    open fun createDraft(@Body @Valid email: Email): HttpResponse<*> { // <2>
        return HttpResponse.ok(mapOf("msg" to "OK"))
    }

    @Post("/send")
    @Validated(groups = [FinalValidation::class]) // <3>
    open fun send(@Body @Valid email: Email): HttpResponse<*> { // <4>
        return HttpResponse.ok(mapOf("msg" to "OK"))
    }
}
//end::clazz[]
