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
package io.micronaut.docs.annotation.requestattributes;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequestAttributeSpec {

    @Test
    void testSenderAttributes() {

        try (EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class)) {
            StoryClient client = embeddedServer.getApplicationContext().getBean(StoryClient.class);
            StoryClientFilter filter = embeddedServer.getApplicationContext().getBean(StoryClientFilter.class);

            Story story = Mono.from(client.getById("jan2019")).block();

            assertNotNull(story);

            Map<String, Object> attributes = filter.getLatestRequestAttributes();
            assertNotNull(attributes);
            assertEquals("jan2019", attributes.get("story-id"));
            assertEquals("storyClient", attributes.get("client-name"));
            assertEquals("1", attributes.get("version"));
        }
    }
}
