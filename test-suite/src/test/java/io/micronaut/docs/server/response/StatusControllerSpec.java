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
package io.micronaut.docs.server.response;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusControllerSpec {

    private static EmbeddedServer server;
    private static HttpClient client;

    @BeforeAll
    static void setupServer() {
        server = ApplicationContext.run(EmbeddedServer.class, Collections.singletonMap("spec.name", "httpstatus"));
        client = server
                .getApplicationContext()
                .createBean(HttpClient.class, server.getURL());
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.stop();
        }
    }

    @Test
    void testStatus() {
        HttpResponse<String> response = client.toBlocking().exchange(HttpRequest.GET("/status"), String.class);
        Optional<String> body = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("success", body.get());

        response = client.toBlocking().exchange(HttpRequest.GET("/status/http-response"), String.class);
        body = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("success", body.get());

        response = client.toBlocking().exchange(HttpRequest.GET("/status/http-status"), String.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
    }

}
