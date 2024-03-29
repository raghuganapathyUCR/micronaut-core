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
package io.micronaut.kotlin.processing.aop.factory

import io.micronaut.inject.BeanDefinition
import io.micronaut.inject.writer.BeanDefinitionVisitor
import io.micronaut.inject.writer.BeanDefinitionWriter
import spock.lang.Specification

import static io.micronaut.annotation.processing.test.KotlinCompiler.*

class AdviceDefinedOnFactorySpec extends Specification {

    void "test advice defined at the class level of a  factory"() {
        when:"Advice is defined at the class level of the factory"
        BeanDefinition beanDefinition = buildBeanDefinition('test.$MyFactory' + BeanDefinitionWriter.CLASS_SUFFIX + BeanDefinitionVisitor.PROXY_SUFFIX, '''
package test

import io.micronaut.context.annotation.*
import io.micronaut.kotlin.processing.aop.simple.Mutating

@Factory
@Mutating("name")
open class MyFactory {

    @Bean
    @Executable
    open fun myBean(@Parameter name: String): String {
        return name
    }
}

''')
        then:"The methods of the factory have AOP advice applied, but not the created beans"
        beanDefinition.executableMethods.size() == 1
    }
}
