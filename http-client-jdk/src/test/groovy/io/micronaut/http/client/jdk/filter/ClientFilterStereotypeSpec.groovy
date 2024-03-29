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
package io.micronaut.http.client.jdk.filter

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Order
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.annotation.ClientFilter
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.ResponseFilter
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.filter.ClientFilterChain
import io.micronaut.http.filter.HttpClientFilter
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import spock.lang.Specification

@Property(name = 'spec.name', value = 'ClientFilterStereotypeSpec')
@MicronautTest
class ClientFilterStereotypeSpec extends Specification {
    @Inject
    UnmarkedClient unmarkedClient

    @Inject
    MarkedClient markedClient

    @Inject
    MarkedTwiceClient markedTwiceClient

    @Inject
    ClientBeans clientBeans

    @Inject
    IndirectlyMarkedClient indirectlyMarkedClient

    void "test declarative client matching"() {
        expect:
        unmarkedClient.echo() == "echo URL" // not intercepted by

        and:
        markedClient.echoPost() == "echo Intercepted Post URL"
        markedClient.echo() == "echo Intercepted URL"

        and:
        markedTwiceClient.echoPost() == "echo Intercepted Twice Post URL"
        markedTwiceClient.echo() == "echo Intercepted Twice URL"

        and:
        indirectlyMarkedClient.echoPost() == "echo Intercepted Twice Post URL"
        indirectlyMarkedClient.echo() == "echo Intercepted Twice URL"
    }

    void "low-level client filter matching"() {
        expect:
        clientBeans.annotatedClient.toBlocking().retrieve('/') == "echo Intercepted URL"
        clientBeans.client.toBlocking().retrieve('/') == "echo URL"
        clientBeans.annotatedClient.toBlocking().retrieve(HttpRequest.POST('/', '')) == "echo Intercepted Post URL"
    }

    @Requires(property = 'spec.name', value = 'ClientFilterStereotypeSpec')
    @Singleton
    static class ClientBeans {
        HttpClient annotatedClient
        HttpClient client

        ClientBeans(
                @MarkerStereotypeAnnotation @Client('/filters/marked') HttpClient annotatedClient,
                @Client('/filters/marked') HttpClient client
        ) {
            this.client = client
            this.annotatedClient = annotatedClient
        }
    }

    @Requires(property = 'spec.name', value = 'ClientFilterStereotypeSpec')
    @Client("/filters/marked")
    @MarkerStereotypeAnnotation
    static interface MarkedClient {
        @Get
        String echo()

        @Post
        String echoPost()
    }

    @Requires(property = 'spec.name', value = 'ClientFilterStereotypeSpec')
    @Client("/filters/marked")
    @AnotherMarkerStereotypeAnnotation
    @MarkerStereotypeAnnotation
    static interface MarkedTwiceClient {
        @Get
        String echo()

        @Post
        String echoPost()
    }

    @Requires(property = 'spec.name', value = 'ClientFilterStereotypeSpec')
    @Client("/filters/marked")
    @IndirectMarkerStereotypeAnnotation
    static interface IndirectlyMarkedClient {
        @Get
        String echo()

        @Post
        String echoPost()
    }

    @Requires(property = 'spec.name', value = 'ClientFilterStereotypeSpec')
    @Client("/filters/marked")
    static interface UnmarkedClient {
        @Get
        String echo()
    }

    @Requires(property = 'spec.name', value = 'ClientFilterStereotypeSpec')
    @Controller('/filters/')
    static class UriController {

        @Get('/marked')
        String marked() {
            return "echo"
        }

        @Post('/marked')
        String post() {
            return "echo"
        }
    }

    @Requires(property = 'spec.name', value = 'ClientFilterStereotypeSpec')
    @MarkerStereotypeAnnotation
    @Singleton
    static class MarkerFilter implements HttpClientFilter {
        @Override
        int getOrder() {
            0
        }

        @Override
        Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {
            Flux.from(chain.proceed(request))
                    .map({ HttpResponse response ->
                        HttpResponse.ok(response.body().toString() + " Intercepted")
                    })
        }
    }

    @Requires(property = 'spec.name', value = 'ClientFilterStereotypeSpec')
    @AnotherMarkerStereotypeAnnotation
    @Singleton
    static class AnotherMarkerFilter implements HttpClientFilter {
        @Override
        int getOrder() {
            1
        }

        @Override
        Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {
            Flux.from(chain.proceed(request))
                    .map({ HttpResponse response ->
                        HttpResponse.ok(response.body().toString() + " Twice")
                    })
        }
    }

    @Requires(property = 'spec.name', value = 'ClientFilterStereotypeSpec')
    @MarkerStereotypeAnnotation(methods = HttpMethod.POST)
    @Singleton
    static class MarkerPostFilter implements HttpClientFilter {
        @Override
        int getOrder() {
            2
        }

        @Override
        Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {
            Flux.from(chain.proceed(request))
                    .map({ HttpResponse response ->
                        HttpResponse.ok(response.body().toString() + " Post")
                    })
        }
    }

    @Requires(property = 'spec.name', value = 'ClientFilterStereotypeSpec')
    @Order(3)
    @ClientFilter("/filters/marked")
    static class UrlFilter {
        @ResponseFilter
        HttpResponse<?> doFilter(HttpResponse<?> response) {
            HttpResponse.ok(response.body().toString() + " URL")
        }
    }
}
