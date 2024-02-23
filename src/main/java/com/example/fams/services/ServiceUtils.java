package com.example.fams.services;

import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceUtils {

    public static <T> T fillMissingAttribute(T entity, T tempOldEntity) {
        List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = entity.getClass();
        try {
            // Traverse class hierarchy to collect fields from all superclasses
            while (currentClass != null) {
                Field[] declaredFields = currentClass.getDeclaredFields();
                allFields.addAll(Arrays.asList(declaredFields));
                currentClass = currentClass.getSuperclass();
            }

            // Iterate over all fields
            for (Field field : allFields) {
                field.setAccessible(true); // Enable access to private fields if any

                try {
                    Object newValue = field.get(entity); // Get the value of the field for the newEntity
                    if (newValue == null) {
                        // If the value is null, get the corresponding value from oldEntity
                        Object oldValue = field.get(tempOldEntity);
                        field.set(entity, oldValue); // Set the value of the field for the newEntity
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return entity;
        } catch (Exception e) {
            throw e;
        }
    }

    public static <T> T cloneFromEntity(T t) {
        T clone;
        try {
            clone = (T) t.getClass().newInstance();
            BeanUtils.copyProperties(clone, t);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return null;
        }
        return clone;
    }
}