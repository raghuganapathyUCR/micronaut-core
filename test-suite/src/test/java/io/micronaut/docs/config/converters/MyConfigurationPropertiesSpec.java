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
package io.micronaut.docs.config.converters;

import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

//tag::configSpec[]
class MyConfigurationPropertiesSpec {

    //tag::runContext[]
    private static ApplicationContext ctx;

    @BeforeAll
    static void setupCtx() {
        ctx = ApplicationContext.run(
                new LinkedHashMap<>() {{
                    put("myapp.updatedAt", // <1>
                            new LinkedHashMap<String, Integer>() {{
                                put("day", 28);
                                put("month", 10);
                                put("year", 1982);
                            }}
                    );
                }}
        );
    }

    @AfterAll
    static void teardownCtx() {
        if(ctx != null) {
            ctx.stop();
        }
    }
    //end::runContext[]

    @Test
    void testConvertDateFromMap() {
        MyConfigurationProperties props = ctx.getBean(MyConfigurationProperties.class);

        LocalDate expectedDate = LocalDate.of(1982, 10, 28);
        assertEquals(expectedDate, props.getUpdatedAt());
    }
}
//end::configSpec[]