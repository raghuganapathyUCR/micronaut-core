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
package io.micronaut.docs.server.endpoint;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class AlertsEndpointSpec {

    @Test
    void testAddingAnAlert() {
        Map<String, Object> map = new HashMap<>();
        map.put("spec.name", AlertsEndpointSpec.class.getSimpleName());
        EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, map);
        HttpClient client = server.getApplicationContext().createBean(HttpClient.class, server.getURL());

        try {
            client.toBlocking().exchange(HttpRequest.POST("/alerts", "First alert").contentType(MediaType.TEXT_PLAIN_TYPE), String.class);
        } catch (HttpClientResponseException e) {
            assertEquals(401, e.getStatus().getCode());
        } catch (Exception e) {
            fail("Wrong exception thrown");
        } finally {
            server.close();
        }
    }

    @Test
    void testAddingAnAlertNotSensitive() {
        Map<String, Object> map = new HashMap<>();
        map.put("spec.name", AlertsEndpointSpec.class.getSimpleName());
        map.put("endpoints.alerts.add.sensitive", false);
        EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, map);
        HttpClient client = server.getApplicationContext().createBean(HttpClient.class, server.getURL());

        try {
            HttpResponse<?> response = client.toBlocking().exchange(HttpRequest.POST("/alerts", "First alert").contentType(MediaType.TEXT_PLAIN_TYPE), String.class);
            Assertions.assertEquals(response.status(), HttpStatus.OK);
        } catch (Exception e) {
            Assertions.fail("Wrong exception thrown");
        }

        try {
            HttpResponse<List<String>> response = client.toBlocking().exchange(HttpRequest.GET("/alerts"), Argument.LIST_OF_STRING);
            Assertions.assertEquals(response.status(), HttpStatus.OK);
            Assertions.assertEquals(response.body().get(0), "First alert");
        } catch (Exception e) {
            Assertions.fail("Wrong exception thrown");
        } finally {
            server.close();
        }
    }

    @Test
    void testClearingAlerts() {
        Map<String, Object> map = new HashMap<>();
        map.put("spec.name", AlertsEndpointSpec.class.getSimpleName());
        EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, map);
        HttpClient rxClient = server.getApplicationContext().createBean(HttpClient.class, server.getURL());

        try {
            rxClient.toBlocking().exchange(HttpRequest.DELETE("/alerts"), String.class);
        } catch (HttpClientResponseException e) {
            assertEquals(401, e.getStatus().getCode());
        } catch (Exception e) {
            Assertions.fail("Wrong exception thrown");
        } finally {
            server.close();
        }
    }
}
