package com.verdorabackend.validation;

import com.verdorabackend.dto.request.ProductRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DiscountPriceValidator implements ConstraintValidator<ValidDiscountPrice, ProductRequest> {

    @Override
    public boolean isValid(ProductRequest request, ConstraintValidatorContext context) {
        if (request == null || request.price() == null || request.discountPrice() == null) {
            return true;
        }

        if (request.discountPrice().compareTo(request.price()) < 0) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Discount price must be lower than regular price")
                .addPropertyNode("discountPrice")
                .addConstraintViolation();

        return false;
    }

}
