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

package me.max.migrational.testobjects;

import me.max.migrational.annotations.Migratable;

/**
 * This is a test object
 * This test object is used to test the {@link Migratable} annotated fields.
 * It will be migrated as a test.
 *
 * @author Max Berkelmans
 * @since 1.1.0
 */
public class FieldObject {

    @Migratable(key = "name", defaultValue = "Max")
    private String name = "Lily";
    @Migratable
    private int age;
    @Migratable(defaultValue = "Netherlands")
    private String country;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getCountry() {
        return country;
    }
}
