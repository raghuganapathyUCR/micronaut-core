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
package io.micronaut.inject.factory.named;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.concurrent.TimeUnit;

@Factory
public class TestCacheFactory {

    private Cache<String, Flux<String>> orgRepoCache = Caffeine.newBuilder()
            .maximumSize(20)
            .expireAfterWrite(30, TimeUnit.DAYS)
            .build();
    private Cache<String, Mono<String>> repoCache = Caffeine.newBuilder()
            .maximumSize(20)
            .expireAfterWrite(30, TimeUnit.DAYS)
            .build();

    @Singleton
    @Named("orgRepositoryCache")
    public Cache<String, Flux<String>> orgRepositoryCache() {
        return orgRepoCache;
    }

    @Singleton
    @Named("repositoryCache")
    public Cache<String, Mono<String>> repositoryCache() {
        return repoCache;
    }

    public Cache<String, Flux<String>> getOrgRepoCache() {
        return orgRepoCache;
    }

    public Cache<String, Mono<String>> getRepoCache() {
        return repoCache;
    }
}
