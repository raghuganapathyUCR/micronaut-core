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
package io.micronaut.docs.client.filter;

//tag::class[]

import io.micronaut.context.BeanProvider;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.ClientFilter;
import io.micronaut.http.annotation.RequestFilter;
import io.micronaut.http.client.HttpClient;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

@Requires(env = Environment.GOOGLE_COMPUTE)
@ClientFilter(patterns = "/google-auth/api/**")
public class GoogleAuthFilter {

    private final BeanProvider<HttpClient> authClientProvider;

    public GoogleAuthFilter(BeanProvider<HttpClient> httpClientProvider) { // <1>
        this.authClientProvider = httpClientProvider;
    }

    @RequestFilter
    @ExecuteOn(TaskExecutors.BLOCKING)
    public void filter(MutableHttpRequest<?> request) throws Exception {
        String uri = encodeURI(request);
        String t = authClientProvider.get().toBlocking().retrieve(HttpRequest.GET(uri) // <2>
            .header("Metadata-Flavor", "Google"));
        request.bearerAuth(t);
    }

    private String encodeURI(MutableHttpRequest<?> request) throws UnsupportedEncodingException {
        URI fullURI = request.getUri();
        String receivingURI = fullURI.getScheme() + "://" + fullURI.getHost();
        return "http://metadata/computeMetadata/v1/instance/service-accounts/default/identity?audience=" +
                URLEncoder.encode(receivingURI, "UTF-8");
    }
}
//end::class[]
