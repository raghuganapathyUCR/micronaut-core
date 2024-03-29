package io.micronaut.docs.qualifiers.replaces.defaultimpl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.micronaut.context.ApplicationContext

class DefaultImplementationSpec : StringSpec({

    "test the default implementation is replaced" {
        val ctx = ApplicationContext.run()
        val responseStrategy = ctx.getBean(ResponseStrategy::class.java)

        responseStrategy.shouldBeInstanceOf<CustomResponseStrategy>()

        ctx.close()
    }
})
