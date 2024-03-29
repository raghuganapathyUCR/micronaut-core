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
package io.micronaut.docs.client.upload;

// tag::imports[]
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
// end::imports[]

// tag::multipartBodyImports[]
import io.micronaut.http.client.multipart.MultipartBody;
// end::multipartBodyImports[]

// tag::controllerImports[]
import io.micronaut.http.annotation.Controller;
import reactor.core.publisher.Flux;
// end::controllerImports[]

// tag::class[]
class MultipartFileUploadSpec {
// end::class[]

    private static ApplicationContext context;
    private static EmbeddedServer embeddedServer;
    private static HttpClient client;

    @BeforeAll
    static void setupServer() {
        context = ApplicationContext.run();
        embeddedServer = context.getBean(EmbeddedServer.class).start();
        client = context.createBean(HttpClient.class, embeddedServer.getURL());
    }

    @AfterAll
    static void stopServer() {
        if (embeddedServer != null) {
            embeddedServer.stop();
        }
        if (client != null) {
            client.stop();
        }
    }

    @Test
    void testMultipartFileRequestByteArray() throws IOException {
        // tag::file[]
        String toWrite = "test file";
        File file = File.createTempFile("data", ".txt");
        FileWriter writer = new FileWriter(file);
        writer.write(toWrite);
        writer.close();
        // end::file[]

        // tag::multipartBody[]
        MultipartBody requestBody = MultipartBody.builder()     // <1>
                .addPart(                                       // <2>
                    "data",
                    file.getName(),
                    MediaType.TEXT_PLAIN_TYPE,
                    file
                ).build();                                      // <3>

        // end::multipartBody[]

        Flux<HttpResponse<String>> flowable = Flux.from(client.exchange(
                // tag::request[]
                HttpRequest.POST("/multipart/upload", requestBody)    // <1>
                           .contentType(MediaType.MULTIPART_FORM_DATA_TYPE) // <2>
                // end::request[]
                           .accept(MediaType.TEXT_PLAIN_TYPE),
                String.class
        ));
        HttpResponse<String> response = flowable.blockFirst();
        String body = response.getBody().get();

        assertEquals("Uploaded 9 bytes", body);
    }

    @Test
    void testMultipartFileRequestByteArrayWithContentType() {
        // tag::multipartBodyBytes[]
        MultipartBody requestBody = MultipartBody.builder()
                .addPart("data", "sample.txt", MediaType.TEXT_PLAIN_TYPE, "test content".getBytes())
                .build();
        // end::multipartBodyBytes[]

        Flux<HttpResponse<String>> flowable = Flux.from(client.exchange(
                HttpRequest.POST("/multipart/upload", requestBody)
                        .contentType(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .accept(MediaType.TEXT_PLAIN_TYPE),
                String.class
        ));
        HttpResponse<String> response = flowable.blockFirst();
        String body = response.getBody().get();

        assertEquals("Uploaded 12 bytes", body);
    }

    @Test
    void testMultipartFileRequestByteArrayWithoutContentType() throws IOException {
        String toWrite = "test file";
        File file = File.createTempFile("data", ".txt");
        FileWriter writer = new FileWriter(file);
        writer.write(toWrite);
        writer.close();
        file.createNewFile();

        Flux<HttpResponse<String>> flowable = Flux.from(client.exchange(
                HttpRequest.POST("/multipart/upload", MultipartBody.builder().addPart("data", file.getName(), file))
                        .contentType(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .accept(MediaType.TEXT_PLAIN_TYPE),
                String.class
        ));
        HttpResponse<String> response = flowable.blockFirst();
        String body = response.getBody().get();

        assertEquals("Uploaded 9 bytes", body);
    }

    @Controller("/multipart")
    static class MultipartController {

        @Post(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
        HttpResponse<String> upload(byte[] data) {
            return HttpResponse.ok("Uploaded " + data.length + " bytes");
        }
    }
}
