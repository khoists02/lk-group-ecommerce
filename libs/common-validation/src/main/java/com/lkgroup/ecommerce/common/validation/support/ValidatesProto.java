package com.lkgroup.ecommerce.common.validation.support;

import com.google.protobuf.Message;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidatesProto {
    Class<? extends Message> value();

    /**
     * A SpEL expression defining if this validator should be executed or not
     * @return
     */
    String condition() default "";

    /**
     * If there are multiple matching validators, the one with the highest precedence will be run first
     * ie lowest first
     * @return
     */
    String order() default "2147483647";

    /**
     * If there are multiple matched validators, they will all be executed unless stop processing has been marked true
     * in which case only up to and including the current validator will be processed
     * @return
     */
    boolean stopProcessing() default false;
}
