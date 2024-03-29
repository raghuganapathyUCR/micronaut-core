package io.micronaut.docs.qualifiers.replaces.defaultimpl

import io.micronaut.context.ApplicationContext
import spock.lang.Specification

class DefaultImplementationSpec extends Specification {

    void "test the default is replaced"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()

        when:
        ResponseStrategy responseStrategy = ctx.getBean(ResponseStrategy)

        then:
        responseStrategy instanceof CustomResponseStrategy

        cleanup:
        ctx.close()
    }
}
