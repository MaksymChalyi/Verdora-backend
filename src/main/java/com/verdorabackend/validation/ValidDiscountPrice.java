package com.verdorabackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DiscountPriceValidator.class)
public @interface ValidDiscountPrice {

    String message() default "Discount price must be lower than regular price";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
