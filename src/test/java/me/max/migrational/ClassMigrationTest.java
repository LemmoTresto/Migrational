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

import me.max.migrational.testobjects.ClassObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This is test class and will check if migration is still working correctly
 * This was made to not need to manually test if it still works after making changes
 *
 * @author Max Berkelmans
 * @since 1.1.0
 */
public class ClassMigrationTest {

    private static ClassObject testObject;

    @BeforeClass
    public static void setUp() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Stijn");

        testObject = (ClassObject) new Migrator(ClassObject.class, data).migrateToClass();

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
    public void migrate_Age_ReturnsTwentySeven() {
        final int expected = 27;

        final int actual = testObject.getAge();

        assertEquals(expected, actual);
    }

    @Test
    public void migrate_IsCool_ReturnsTrue() {
        final boolean actual = testObject.isCool();

        assertTrue(actual);
    }
}
