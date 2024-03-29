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
package io.micronaut.http.codec;

import io.micronaut.http.exceptions.HttpException;

/**
 * An exception thrown when an object cannot be decoded.
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public class CodecException extends HttpException {

    /**
     * @param message The message
     */
    public CodecException(String message) {
        super(message);
    }

    /**
     * @param message The message
     * @param cause   The throwable
     */
    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
