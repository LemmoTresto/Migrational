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

package me.max.migrational.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@link java.lang.reflect.Field} migratable.
 * OR Marks all {@link java.lang.reflect.Field} in a {@link Class} migratable.
 * Unless a field is marked with {@link Exempt}.
 * key will default to the field name and defaultValue will be the already initialised value.
 * This means this data is able to be migrated.
 *
 * @author Max Berkelmans
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Migratable {

    /**
     * The key of this field in the data
     *
     * @return the key of this field if none specified will default to field name.
     */
    String key() default "";

    /**
     * The default value if none is found in the data
     *
     * @return default value of field
     */
    String defaultValue() default "";


}
