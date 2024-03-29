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
package io.micronaut.http.multipart;

import io.micronaut.http.exceptions.HttpException;

/**
 * Exception thrown during multipart handling.
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public class MultipartException extends HttpException {

    /**
     * @param message The message
     * @param cause   The throwable
     */
    public MultipartException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message The message
     */
    public MultipartException(String message) {
        super(message);
    }
}
