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

import me.max.migrational.annotations.Exempt;
import me.max.migrational.annotations.Migratable;
import me.max.migrational.exceptions.InvalidConstructorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class will manage migration of one class and one data map.
 * You can instantiate this class multiple times.
 * This will always make a new instance of the class on migration unless {@link Migrator#migrateToMap()} is used.
 *
 * @author Max Berkelmans
 * @see Migratable
 * @since 1.0.0
 */
public class Migrator {

    private final Field[] migratableFields;
    private Map<String, Object> data;
    private Constructor<?> constructor;
    private Object lastMigratedObject;

    /**
     * Loads the data migration with preset data and preset constructor
     *
     * @param clazz       the class/object that contains the fields and constructor
     * @param constructor the constructor of said class that should be used
     * @param data        the data to be used on migration
     */
    public Migrator(Class<?> clazz, Constructor<?> constructor, Map<String, Object> data) {
        this(clazz, constructor);
        this.data = data;
    }

    /**
     * Loads the data migration with preset data
     *
     * @param clazz the class/object that contains the fields and constructor
     * @param data  the data to be used on migration
     */
    public Migrator(Class<?> clazz, Map<String, Object> data) {
        this(clazz);
        this.data = data;
    }

    /**
     * Loads the data migration with preset constructor
     *
     * @param clazz       the class/object that contains the fields and constructor
     * @param constructor the constructor of said class that should be used
     */
    public Migrator(Class<?> clazz, Constructor<?> constructor) {
        this(clazz);
        setConstructor(constructor);
    }

    /**
     * Loads data migration
     *
     * @param clazz the class/object that contains the fields and constructor
     */
    public Migrator(Class<?> clazz) {
        //Loop over constructors to find the one we can use
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            //Needs to have zero parameters
            if (constructor.getParameterCount() == 0) {
                if (Modifier.isPrivate(constructor.getModifiers())) constructor.setAccessible(true);
                this.constructor = constructor;
                break;
            }
        }

        //Create a list of the migratable fields.
        List<Field> migratableFields = new ArrayList<>();
        //Loop over the declared fields and check if it is a field we migrateToClass
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Migratable.class) && !clazz.isAnnotationPresent(Migratable.class))
                continue; //Needs to be our annotation
            if (field.isAnnotationPresent(Exempt.class))
                continue; //If the field is exempted then do not migrateToClass.
            if (Modifier.isPrivate(field.getModifiers())) field.setAccessible(true); //Allow us to access private fields
            migratableFields.add(field);
        }
        //Change it into an array as we do not need to change the list anymore.
        this.migratableFields = migratableFields.toArray(new Field[0]);
    }


    /**
     * Migrate the data if necessary
     * This will always create a new instance of the class
     *
     * @return the migrated object
     * @throws IllegalAccessException      see {@link Constructor#newInstance(Object...)}
     * @throws InvocationTargetException   see {@link Constructor#newInstance(Object...)}
     * @throws InstantiationException      see {@link Constructor#newInstance(Object...)}
     * @throws InvalidConstructorException if the constructor is null this will be thrown.
     *
     * @apiNote This method was renamed in version 1.0.0 -> 1.1.0 due to it being more consistent with other method namings
     *
     * @since 1.1.0
     */
    public Object migrateToClass() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        //Check if the constructor is null due to not being found on instantiation
        if (constructor == null)
            throw new InvalidConstructorException("Constructor provided was null, no matching constructor was found on instantiation make sure to set one.", constructor);

        //Create a new instance of the object
        lastMigratedObject = constructor.newInstance();

        Map<String, Object> migratedMap = migrateToMap();
        for (Field field : migratableFields) {
            Migratable migratable = getAnnotationFromField(field); //Get the annotation
            //Get correct key
            String key = (migratable.key().isEmpty()) ? field.getName() : migratable.key();
            field.set(lastMigratedObject, migratedMap.get(key));
        }

        return lastMigratedObject;
    }

    /**
     * This method migrates to a map rather than straight to an object instance.
     * This can be used to instantiate the object yourself with or without reflection.
     *
     * @return a map of migrated data
     * @since 1.1.0
     */
    public Map<String, Object> migrateToMap() {
        Map<String, Object> migratedMap = new HashMap<>();

        //Loop over known migratable fields.
        for (Field field : migratableFields) {
            Migratable migratable = getAnnotationFromField(field); //Get the annotation
            //Get the correct key if no key is given in the annotation use field name.
            String key = (migratable.key().isEmpty()) ? field.getName() : migratable.key();
            //Check if the data contains this field
            if (data.containsKey(key)) {
                //It does so lets set the data
                migratedMap.put(key, data.get(key));
                continue;
            }
            //It does not, so let's set the default value
            migratedMap.put(key, migratable.defaultValue().isEmpty() ? data.get(key) : migratable.defaultValue());
        }

        //Return the migrated map.
        return migratedMap;
    }

    /**
     * Retrieve the {@link Migratable} annotation from a field
     * It works by checking the field for the annotation and if it exists return else get the class's annotation.
     *
     * @param field the field to get the annotation from
     * @return the {@link Migratable} annotation
     * @since 1.1.0
     */
    private Migratable getAnnotationFromField(Field field) {
        return field.isAnnotationPresent(Migratable.class) ? field.getAnnotation(Migratable.class) : field.getDeclaringClass().getAnnotation(Migratable.class);
    }

    /**
     * This will return the last migrated object.
     * This can be null if the data was not migrated yet.
     *
     * @return the last migrated object still in memory
     */
    public Object getLastMigratedObject() {
        return lastMigratedObject;
    }

    /**
     * Returns the constructor being used
     *
     * @return the constructor being used
     */
    public Constructor<?> getConstructor() {
        return constructor;
    }

    /**
     * Set the constructor to be used to migrateToClass.
     *
     * @param constructor the constructor to use
     * @throws InvalidConstructorException if the constructor is null or has parameters or is not of the correct class
     */
    public void setConstructor(Constructor<?> constructor) throws InvalidConstructorException {
        if (constructor == null) throw new InvalidConstructorException("Constructor provided was null", null);
        if (!constructor.getDeclaringClass().equals(migratableFields[0].getDeclaringClass()))
            throw new InvalidConstructorException("The provided constructor was not from the correct class", constructor);
        if (constructor.getParameterCount() != 0)
            throw new InvalidConstructorException("The provided constructor has parameters!", constructor);
        this.constructor = constructor;
    }

    /**
     * Get the data which will be migrated
     *
     * @return the data to migrateToClass
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Set the data to be migrated
     *
     * @param data the data to migrateToClass
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * The array of fields that will be migrated
     * This array was made on instantiation and cannot be changed
     * This array will only contain fields which
     * have been annotated with {@link Migratable}
     * or which's class has been annotated with {@link Migratable} and not exempted using {@link Exempt}
     *
     * @return the array with the migratable fields
     */
    public Field[] getMigratableFields() {
        return migratableFields;
    }
}
