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
package io.micronaut.docs.sse;

import io.micronaut.context.ApplicationContext;
import io.micronaut.docs.streaming.Headline;
import io.micronaut.http.sse.Event;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeadlineControllerSpec {

    // tag::streamingClient[]
    @Test
    void testClientAnnotationStreaming() {
        try( EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class) ) {
            HeadlineClient headlineClient = embeddedServer
                    .getApplicationContext()
                    .getBean(HeadlineClient.class);

            Event<Headline> headline = Mono.from(headlineClient.streamHeadlines()).block();

            assertNotNull(headline);
            assertTrue(headline.getData().getText().startsWith("Latest Headline"));
        }
    }
    // end::streamingClient[]

}
