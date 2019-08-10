/*
 *
 *  *     Copyright 2019 Max Berkelmans
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package me.max.migrational.exceptions;

import java.lang.reflect.Constructor;

/**
 * The exception to be thrown when the provided constructor is invalid
 * Cases of this happening is when the constructor was not found during instantiation
 * Calling {@link me.max.migrational.Migrator#migrateToClass()} will then throw this exception
 * Another case is if the provided constructor in {@link me.max.migrational.Migrator#setConstructor(java.lang.reflect.Constructor)} has parameters
 * If this constructor is not of class which was passed on in instantiation it will also throw this exception.
 *
 * @author Max Berkelmans
 * @since 1.0.0
 */
public class InvalidConstructorException extends RuntimeException {

    private String reason;
    private Constructor<?> constructor;

    /**
     * @param reason      the reason why the constructor was invalid
     * @param constructor the constructor used
     */
    public InvalidConstructorException(String reason, Constructor<?> constructor) {
        this.reason = reason;
        this.constructor = constructor;
    }

    /**
     * This returns the reason why the constructor is invalid
     *
     * @return the string reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * This returns the constructor that was tried to be used
     * If the reason was because of the constructor being null this will return null.
     *
     * @return the constructor which was tried to be used
     */
    public Constructor<?> getConstructor() {
        return constructor;
    }
}
