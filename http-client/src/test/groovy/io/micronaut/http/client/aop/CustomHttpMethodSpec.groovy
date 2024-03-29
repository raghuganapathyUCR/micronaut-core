package io.micronaut.http.client.aop

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.CustomHttpMethod
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author spirit-1984
 * @since 1.2.1
 */
class CustomHttpMethodSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer server = ApplicationContext.run(EmbeddedServer, [
            'spec.name':'CustomHttpMethodSpec',
    ])

    @Shared
    WebdavClient webdavClient = server.applicationContext.getBean(WebdavClient)

    def "Lock method with uri parameter should run fine"() {
        given:
        String response = webdavClient.lock("John")

        expect:
        response == "LOCK John"
    }

    def "Get method with uri parameter should run fine"() {
        given:
        String response = webdavClient.get("John")

        expect:
        response == "GET John"
    }

    @Client('/customHttpMethod')
    @Requires(property = 'spec.name', value = 'CustomHttpMethodSpec')
    static interface WebdavClient extends MyApi {}

    @Controller("/customHttpMethod")
    @Requires(property = 'spec.name', value = 'CustomHttpMethodSpec')
    static class MethodController {
        @CustomHttpMethod(method = "LOCK", value = "/{name}")
        String lock(String name) {
            return "LOCK " + name
        }

        @Get("/{name}")
        String get(String name) {
            return "GET " + name
        }
    }

    static interface MyApi {
        @CustomHttpMethod(method = "LOCK", value = "/{name}")
        String lock(String name)

        @Get("/{name}")
        String get(String name)
    }
}
