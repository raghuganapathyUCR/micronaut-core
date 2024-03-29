/*
 * Copyright 2017-2020 original authors
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
package org.atinject.javaxtck.auto

import org.atinject.javaxtck.auto.accessories.SpareTire

import javax.inject.Inject
import javax.inject.Named

abstract class Engine {

    var publicNoArgsConstructorInjected: Boolean = false

    var overriddenTwiceWithOmissionInMiddleInjected: Boolean = false
    var overriddenTwiceWithOmissionInSubclassInjected: Boolean = false

    protected var seatA: Seat? = null
    protected var seatB: Seat? = null
    protected var tireA: Tire? = null
    protected var tireB: Tire? = null

    var qualifiersInheritedFromOverriddenMethod: Boolean = false
    var overriddenMethodInjected: Boolean = false

    @Inject
    open fun injectQualifiers(@Drivers seatA: Seat, seatB: Seat,
                              @Named("spare") tireA: Tire, tireB: Tire) {
        overriddenMethodInjected = true
        if (seatA !is DriversSeat
                || seatB is DriversSeat
                || tireA !is SpareTire
                || tireB is SpareTire) {
            qualifiersInheritedFromOverriddenMethod = true
        }
    }

    @Inject
    open fun injectTwiceOverriddenWithOmissionInMiddle() {
        overriddenTwiceWithOmissionInMiddleInjected = true
    }

    @Inject
    open fun injectTwiceOverriddenWithOmissionInSubclass() {
        overriddenTwiceWithOmissionInSubclassInjected = true
    }
}
