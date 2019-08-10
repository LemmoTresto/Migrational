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

package me.max.migrational;

import me.max.migrational.testobjects.FieldObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * This test class will check for migration of {@link me.max.migrational.annotations.Migratable} {@link java.lang.reflect.Field}s.
 *
 * @author Max Berkelmans
 * @since 1.1.0
 */
public class FieldMigrationTest {

    private static FieldObject testObject;

    @BeforeClass
    public static void setUp() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Stijn");
        data.put("age", 20);

        testObject = (FieldObject) new Migrator(FieldObject.class, data).migrateToClass();
    }

    @AfterClass
    public static void tearDown() {
        testObject = null;
    }

    @Test
    public void migrate_Name_ReturnsStijn() {
        final String expected = "Stijn";

        final String actual = testObject.getName();

        assertEquals(expected, actual);
    }

    @Test
    public void migrate_Age_ReturnsTwenty() {
        final int expected = 20;

        final int actual = testObject.getAge();

        assertEquals(expected, actual);
    }

    @Test
    public void migrate_Country_ReturnsNetherlands() {
        final String expected = "Netherlands";

        final String actual = testObject.getCountry();

        assertEquals(expected, actual);
    }
}
