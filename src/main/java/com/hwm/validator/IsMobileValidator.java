package com.hwm.validator;

import com.hwm.utils.ValidatorUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {
    private boolean required=false;
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required=constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required){
            //值是必须的
            return ValidatorUtils.isMobile(s);
        }else{
            //值不是必须的
            if(StringUtils.isEmpty(s)){
                return true;
            }else{
                return ValidatorUtils.isMobile(s);
            }
        }
    }
}
