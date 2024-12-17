package dev.onebiteaidan.worldshop.Model.DataManagement.Repositories;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Storable {
    // Database drivers rely on these names to match their in-class variables (if applicable)
    String collectionName();
    String primaryKey();
}
