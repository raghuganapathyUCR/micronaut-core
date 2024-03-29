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
package io.micronaut.docs.server.json;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonControllerSpec {

    private static EmbeddedServer server;
    private static HttpClient client;

    @BeforeAll
    static void setupServer() {
        server = ApplicationContext.run(EmbeddedServer.class, Collections.singletonMap("spec.name", PersonControllerSpec.class.getSimpleName()));
        client = server
                .getApplicationContext()
                .createBean(HttpClient.class, server.getURL());
    }

    @AfterAll
    static void stopServer() {
        if(server != null) {
            server.stop();
        }
        if(client != null) {
            client.stop();
        }
    }

    @Test
    void testGlobalErrorHandler() {
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
        Flux.from(client.exchange("/people/error", Map.class))
                .blockFirst());
        HttpResponse<Map> response = (HttpResponse<Map>) e.getResponse();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals("Bad Things Happened: Something went wrong", response.getBody().get().get("message"));
    }

    @Test
    void testSave() {
        HttpResponse<Person> response = client.toBlocking().exchange(HttpRequest.POST("/people", "{\"firstName\":\"Fred\",\"lastName\":\"Flintstone\",\"age\":45}"), Person.class);
        assertTrue(response.getBody().isPresent());
        Person person = response.getBody().get();

        assertEquals("Fred", person.getFirstName());
        assertEquals(HttpStatus.CREATED, response.getStatus());

        response = client.toBlocking().exchange(HttpRequest.GET("/people/Fred"), Person.class);
        person = response.getBody().get();

        assertEquals("Fred", person.getFirstName());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testSaveReactive() {
        HttpResponse<Person> response = client.toBlocking().exchange(HttpRequest.POST("/people/saveReactive", "{\"firstName\":\"Wilma\",\"lastName\":\"Flintstone\",\"age\":36}"), Person.class);
        assertTrue(response.getBody().isPresent());
        Person person = response.getBody().get();

        assertEquals("Wilma", person.getFirstName());
        assertEquals(HttpStatus.CREATED, response.getStatus());
    }

    @Test
    void testSaveFuture() {
        HttpResponse<Person> response = client.toBlocking().exchange(HttpRequest.POST("/people/saveFuture", "{\"firstName\":\"Pebbles\",\"lastName\":\"Flintstone\",\"age\":0}"), Person.class);
        assertTrue(response.getBody().isPresent());
        Person person = response.getBody().get();

        assertEquals("Pebbles", person.getFirstName());
        assertEquals(HttpStatus.CREATED, response.getStatus());
    }

    @Test
    void testSaveArgs() {
        HttpResponse<Person> response = client.toBlocking().exchange(HttpRequest.POST("/people/saveWithArgs", "{\"firstName\":\"Dino\",\"lastName\":\"Flintstone\",\"age\":3}"), Person.class);
        assertTrue(response.getBody().isPresent());
        Person person = response.getBody().get();

        assertEquals("Dino", person.getFirstName());
        assertEquals(HttpStatus.CREATED, response.getStatus());
    }

    @Test
    void testPersonNotFound() {
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                Flux.from(client.exchange("/people/Sally", Map.class))
                        .blockFirst());
        HttpResponse<Map> response = (HttpResponse<Map>) e.getResponse();

        assertEquals("Person Not Found", response.getBody().get().get("message"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void testSaveInvalidJson() {
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(HttpRequest.POST("/people", "{\""), Argument.of(Person.class), Argument.of(Map.class)));
        HttpResponse<Map> response = (HttpResponse<Map>) e.getResponse();
        assertTrue(response.getBody(Map.class).get().get("message").toString().startsWith("Invalid JSON: Unexpected end-of-input"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

}
