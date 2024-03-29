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
package io.micronaut.docs.server.sse;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.sse.SseClient;
import io.micronaut.http.sse.Event;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HeadlineControllerSpec {

    private static EmbeddedServer embeddedServer;

    @BeforeAll
    static void setupServer() {
        embeddedServer = ApplicationContext.run(EmbeddedServer.class);
    }

    @AfterAll
    static void stopServer() {
        if(embeddedServer != null) {
            embeddedServer.stop();
        }
    }

    @Test
    void testConsumeEventStreamObject() {
        SseClient client = embeddedServer.getApplicationContext().createBean(SseClient.class, embeddedServer.getURL());

        List<Event<Headline>> events = new ArrayList<>();

        Flux.from(client.eventStream(HttpRequest.GET("/headlines"), Headline.class))
                .subscribe(events::add);

        await().until(() -> events.size() == 2);
        assertEquals("Micronaut 1.0 Released", events.get(0).getData().getTitle());
        assertEquals("Come and get it", events.get(0).getData().getDescription());
    }
}