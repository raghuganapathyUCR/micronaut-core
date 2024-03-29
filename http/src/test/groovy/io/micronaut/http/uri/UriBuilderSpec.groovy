/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.http.uri

import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Unroll

class UriBuilderSpec extends Specification {

    void "test uri builder expand"() {
        given:
        def builder = UriBuilder.of("/person/{name}")

        when:
        builder.path("/features/{feature}")
        def result = builder.expand(name:"Fred Flintstone", feature:"age")

        then:
        result.toString() == '/person/Fred%20Flintstone/features/age'

        when:
        builder.fragment('{#hash}')
        result = builder.expand(name:"Fred Flintstone", feature:"age", hash: "val")

        then:
        result.toString() == '/person/Fred%20Flintstone/features/age#val'

        when:
        builder.queryParam("q", "hello world")
        result = builder.expand(name:"Fred Flintstone", feature:"age", hash: "val")

        then:
        result.toString() == '/person/Fred%20Flintstone/features/age?q=hello+world#val'

        when:
        builder.queryParam("a", "b")
        result = builder.expand(name:"Fred Flintstone", feature:"age", hash: "val")

        then:
        result.toString() == '/person/Fred%20Flintstone/features/age?q=hello+world&a=b#val'

        when:
        builder.host("myhost")
        builder.scheme("http")
        builder.port(9090)
        builder.userInfo("username:p@s\$w0rd")
        result = builder.expand(name:"Fred Flintstone", feature:"age", hash: "val")

        then:
        result.toString() == 'http://username:p%40s%24w0rd@myhost:9090/person/Fred%20Flintstone/features/age?q=hello+world&a=b#val'
    }

    void "test query param order"() {
        Map<String, String> params = new LinkedHashMap<>()
        params.put("t_param", "t_value")
        params.put("s_param", "s_value")
        params.put("a_param", "a_value")

        UriBuilder uriBuilder = UriBuilder.of("/api").path("v1").path("secretendpoint");
        for (String paramKey : params.keySet()) {
            System.out.println(paramKey)
            uriBuilder = uriBuilder.queryParam(paramKey, params.get(paramKey));
        }

        expect:
        uriBuilder.build().toString() == "/api/v1/secretendpoint?t_param=t_value&s_param=s_value&a_param=a_value"
    }

    void "test uri builder toString()"() {
        given:
        def builder = UriBuilder.of("")

        when:
        builder.path("/foo")

        then:
        builder.toString() == '/foo'

        when:
        builder.path("/bar/")
               .path('/baz')

        then:
        builder.toString() == '/foo/bar/baz'

        when:
        builder.host("myhost")

        then:
        builder.toString() == '//myhost/foo/bar/baz'

        when:
        builder.port(9090)

        then:
        builder.toString() == '//myhost:9090/foo/bar/baz'

        when:
        builder.scheme("https")

        then:
        builder.toString() == 'https://myhost:9090/foo/bar/baz'

        when:
        builder.queryParam("offset", 10)

        then:
        builder.toString() == 'https://myhost:9090/foo/bar/baz?offset=10'
    }

    @Unroll
    void "test queryParam method for uri #uri"() {
        given:
        def builder = UriBuilder.of(uri)
        for (p in params) {
            if (p.value instanceof List) {
                builder.queryParam(p.key, *p.value)
            } else {
                builder.queryParam(p.key, p.value)
            }
        }

        expect:
        builder.toString() == expected

        where:
        uri                  | params                              | expected
        '/foo?existing=true' | ['foo': 'bar']                      | '/foo?existing=true&foo=bar'
        '/foo'               | ['foo': 'bar']                      | '/foo?foo=bar'
        '/foo'               | ['foo': 'hello world']              | '/foo?foo=hello+world'
        '/foo'               | ['foo': ['bar', 'baz']]             | '/foo?foo=bar&foo=baz'
        '/foo'               | ['foo': null, 'bar': 'baz']         | '/foo?bar=baz'
        '/foo'               | ['foo': [null, null], 'bar': 'baz'] | '/foo?bar=baz'
    }

    @Unroll
    void "test replaceQueryParam method for uri #uri"() {
        given:
        def builder = UriBuilder.of(uri)
        for (p in params) {
            if (p.value instanceof List) {
                builder.replaceQueryParam(p.key, *p.value)
            } else {
                builder.replaceQueryParam(p.key, p.value)
            }
        }

        expect:
        builder.toString() == expected

        where:
        uri             | params                              | expected
        '/foo?foo=old'  | ['foo': 'bar']                      | '/foo?foo=bar'
        '/foo?old=keep' | ['foo': 'bar']                      | '/foo?old=keep&foo=bar'
        '/foo?foo=old'  | ['foo': 'hello world']              | '/foo?foo=hello+world'
        '/foo?foo=old'  | ['foo': ['bar', 'baz']]             | '/foo?foo=bar&foo=baz'
        '/foo?foo=old'  | ['foo': null, 'bar': 'baz']         | '/foo?foo=old&bar=baz'
        '/foo?foo=old'  | ['foo': [null, null], 'bar': 'baz'] | '/foo?foo=old&bar=baz'
    }

    @Issue("https://github.com/micronaut-projects/micronaut-core/issues/2823")
    void "test parameter names with special characters"() {
        given:
        UriBuilder builder = UriBuilder.of("myurl")
                .queryParam("\$top", "10")
                .queryParam("\$filter", "xyz")
        String uri = builder.build().toString()

        expect:
        uri == 'myurl?%24top=10&%24filter=xyz'
    }
    
    @Issue("https://github.com/micronaut-projects/micronaut-core/issues/6246")
    void "test uri build parse query param"() {
        given:
        String stringUri = "https://google.com/search?q1=v1"
        UriBuilder builder = UriBuilder.of(stringUri)

        when:
        builder.queryParam("q2", "v2")

        then:
        builder.build().toString() == "https://google.com/search?q1=v1&q2=v2"
    }
}
