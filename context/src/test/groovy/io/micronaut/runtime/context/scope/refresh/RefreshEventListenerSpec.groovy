package io.micronaut.runtime.context.scope.refresh

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.context.env.PropertySource
import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.core.type.Argument
import jakarta.inject.Singleton
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class RefreshEventListenerSpec extends Specification {
    @Shared Map<String, Object> config = ['foo.bar': 'initial-value', 'secret.value': "foo".getBytes(StandardCharsets.UTF_8), 'other.config':'${secret.value}']
    @Shared @AutoCleanup ApplicationContext context = ApplicationContext
            .builder()
            .propertySources(PropertySource.of(config))
            .start()

    void "test refresh and diff"() {
        given:
        def env = context.getEnvironment()
        when:
        def result = env.refreshAndDiff()

        then:
        result.isEmpty()

        when:
        config.put("secret.value", "foo".getBytes(StandardCharsets.UTF_8))
        result = env.refreshAndDiff()

        then:
        result.isEmpty()

        when:
        config.put("secret.value", "bar".getBytes(StandardCharsets.UTF_8))
        result = env.refreshAndDiff()

        then:
        !result.isEmpty()
        result.size() == 2
        result.containsKey("secret.value")
        result.containsKey("other.config")

    }
    void 'test refresh event listener'() {
        given:
        def env = context.getEnvironment()
        MyRefreshListener listener = context.getBean(MyRefreshListener)

        when:
        def val = env.getRequiredProperty("foo.bar", String)

        then:
        val == 'initial-value'
        !env.getProperty('some.other', String).isPresent()

        when:
        config.put('some.other', 'changed')
        doRefresh(env)
        val = env.getRequiredProperty("foo.bar", String)

        then:
        val == 'initial-value'
        env.getProperty('some.other', String).isPresent()
        listener.lastEvent == null

        when:
        config.put('foo.bar', 'changed')
        doRefresh(env)
        val = env.getRequiredProperty("foo.bar", String)

        then:
        val == 'changed'
        env.getProperty('some.other', String).isPresent()
        listener.lastEvent != null

    }

    private void doRefresh(Environment env) {
        def diff = env.refreshAndDiff()
        context.getBean(Argument.of(ApplicationEventPublisher, RefreshEvent))
                .publishEvent(new RefreshEvent(diff))
    }

    @Singleton
    static class MyRefreshListener implements RefreshEventListener {
        RefreshEvent lastEvent
        @Override
        Set<String> getObservedConfigurationPrefixes() {
            ['foo.bar']
        }

        @Override
        void onApplicationEvent(RefreshEvent event) {
            lastEvent = event
        }
    }
}
