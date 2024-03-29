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
package io.micronaut.docs.inject.scope;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.context.scope.Refreshable;
import io.micronaut.runtime.context.scope.refresh.RefreshEvent;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefreshEventSpec {

    private static EmbeddedServer embeddedServer;

    private static HttpClient client;

    @BeforeAll
    static void setup() {
        embeddedServer = ApplicationContext.run(EmbeddedServer.class, new HashMap<>() {{
            put("spec.name", "RefreshEventSpec");
            put("spec.lang", "java");
        }}, Environment.TEST);
        client = HttpClient.create(embeddedServer.getURL());
    }

    @AfterAll
    static void teardown() {
        if (client != null) {
            client.close();
        }
        if (embeddedServer != null) {
            embeddedServer.close();
        }
    }

    @Test
    void publishingARefreshEventDestroysBeanWithRefreshableScope() {
        String firstResponse = fetchForecast();

        assertTrue(firstResponse.contains("{\"forecast\":\"Scattered Clouds"));

        String secondResponse = fetchForecast();

        assertEquals(firstResponse, secondResponse);

        String response = evictForecast();

        assertEquals("{\"msg\":\"OK\"}", response);

        AtomicReference<String> thirdResponse = new AtomicReference<>(fetchForecast());
        await().atMost(5, SECONDS).until(() -> {
            if (!thirdResponse.get().equals(secondResponse)) {
                return true;
            }
            thirdResponse.set(fetchForecast());
            return false;
        });

        assertNotEquals(thirdResponse.get(), secondResponse);
        assertTrue(thirdResponse.get().contains("\"forecast\":\"Scattered Clouds"));
    }

    String fetchForecast() {
        return client.toBlocking().retrieve(HttpRequest.GET("/weather/forecast"));
    }

    String evictForecast() {
        return client.toBlocking().retrieve(HttpRequest.POST("/weather/evict", new LinkedHashMap()));
    }

    //tag::weatherService[]
    @Refreshable // <1>
    static class WeatherService {
        private String forecast;

        @PostConstruct
        public void init() {
            forecast = "Scattered Clouds " + new SimpleDateFormat("dd/MMM/yy HH:mm:ss.SSS").format(new Date());// <2>
        }

        public String latestForecast() {
            return forecast;
        }
    }
    //end::weatherService[]

    @Requires(property = "spec.name", value = "RefreshEventSpec")
    @Requires(property = "spec.lang", value = "java")
    @Controller("/weather")
    static class WeatherController {
        @Inject
        private WeatherService weatherService;
        @Inject
        private ApplicationContext applicationContext;

        @Get("/forecast")
        HttpResponse<Map<String, String>> index() {
            LinkedHashMap<String, String> map = new LinkedHashMap<>(1);
            map.put("forecast", weatherService.latestForecast());
            return HttpResponse.ok(map);
        }

        @Post("/evict")
        HttpResponse<Map<String, String>> evict() {
            //tag::publishEvent[]
            applicationContext.publishEvent(new RefreshEvent());
            //end::publishEvent[]
            LinkedHashMap<String, String> map = new LinkedHashMap<>(1);
            map.put("msg", "OK");
            return HttpResponse.ok(map);
        }
    }
}
