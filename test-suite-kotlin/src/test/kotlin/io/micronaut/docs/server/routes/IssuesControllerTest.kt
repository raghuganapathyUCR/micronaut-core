package io.micronaut.docs.server.routes

// tag::imports[]
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
// end::imports[]

// tag::startclass[]
class IssuesControllerTest: StringSpec() {

    val embeddedServer = autoClose( // <2>
        ApplicationContext.run(EmbeddedServer::class.java) // <1>
    )

    val client = autoClose( // <2>
        embeddedServer.applicationContext.createBean(
            HttpClient::class.java,
            embeddedServer.url) // <1>
    )
    // end::startclass[]

    // tag::normal[]
    init {
        "test issue" {
            val body = client.toBlocking().retrieve("/issues/12") // <3>

            body shouldNotBe null
            body shouldBe "Issue # 12!" // <4>
        }

        "test issue from id" {
            val body = client.toBlocking().retrieve("/issues/issue/13")

            body shouldNotBe null
            body shouldBe "Issue # 13!" // <5>
        }

        "test issue with invalid integer" {
            val e = shouldThrow<HttpClientResponseException> {
                client.toBlocking().exchange<Any>("/issues/hello")
            }

            e.status.code shouldBe 400 // <6>
        }

        "test issue without number" {
            val e = shouldThrow<HttpClientResponseException> {
                client.toBlocking().exchange<Any>("/issues/")
            }

            e.status.code shouldBe 404 // <7>
        }
        // end::normal[]

        // tag::defaultvalue[]
        "test issue from id" {
            val body = client.toBlocking().retrieve("/issues/default")

            body shouldNotBe null
            body shouldBe "Issue # 0!" // <1>
        }

        "test issue from id" {
            val body = client.toBlocking().retrieve("/issues/default/1")

            body shouldNotBe null
            body shouldBe "Issue # 1!" // <2>
        }
        // end::defaultvalue[]

// tag::endclass[]
    }
}
// end::endclass[]
