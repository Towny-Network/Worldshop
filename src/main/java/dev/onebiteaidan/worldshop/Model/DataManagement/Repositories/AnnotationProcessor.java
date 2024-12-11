package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

public class AnnotationProcessor {
    public static Storable processClass(Class<?> clazz) throws IllegalAccessException {
        if (clazz.isAnnotationPresent(Storable.class)) {
            Storable annotation = clazz.getAnnotation(Storable.class);
            System.out.println("Table Name: " + annotation.collectionName());
            System.out.println("Primary Key: " + annotation.primaryKey());
            return annotation;
        } else {
            throw new IllegalAccessException("Class " + clazz.getName() + " is not annotated with @Storable");
        }
    }
}