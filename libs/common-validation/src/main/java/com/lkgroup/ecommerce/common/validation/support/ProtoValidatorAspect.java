/*
 * AdvaHealth Solutions Pty. Ltd. ("AHS") CONFIDENTIAL
 * Copyright (c) 2022 AdvaHealth Solutions Pty. Ltd. All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of AHS. The intellectual and technical concepts contained
 * herein are proprietary to AHS and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from AHS.  Access to the source code contained herein is hereby forbidden to anyone except current AHS employees, managers or contractors who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes
 * information that is confidential and/or proprietary, and is a trade secret, of AHS. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE,
 * OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF COMPANY IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE
 * LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 * TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 */

package com.lkgroup.ecommerce.common.validation.support;

import com.google.protobuf.Message;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Aspect
@Component
public class ProtoValidatorAspect {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private List<ProtoValidator> protoValidators;
    @Autowired(required = false)
    private List<ValidationCondition> validationConditions;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private Validator beanValidator;

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    record ValidatorAnnotation(Class<? extends ProtoValidator> clazz, ValidatesProto annotation) {
    }

    record ValidationFunction(String name, Method method) {
    }

    private HashMap<Class<? extends Message>, List<ValidatorAnnotation>> protoValidatorMap;
    private List<ValidationFunction> validationFunctionsReflected = new ArrayList<>();


    @PostConstruct
    protected void link() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (this.protoValidators != null) {
            protoValidatorMap = new HashMap<>(protoValidators.size());
            logger.info("Loaded {} proto validators, processing...", protoValidators.size());

            for (ProtoValidator protoValidator : this.protoValidators) {
                Class<? extends ProtoValidator> clazz = protoValidator.getClass();
                if (!clazz.isAnnotationPresent(ValidatesProto.class)) {
                    throw new IllegalStateException(String.format("A class that extends ProtoValidator must be annotated with ValidatesProto and mapped to a Proto. %s is not annotated", clazz.getCanonicalName()));
                }
                ValidatesProto validatesProtoAnnotation = clazz.getAnnotation(ValidatesProto.class);
                protoValidatorMap.compute(validatesProtoAnnotation.value(), (key, validators) -> {
                    if (validators == null)
                        validators = new ArrayList<>();
                    validators.add(new ValidatorAnnotation(protoValidator.getClass(), validatesProtoAnnotation));
                    validators.sort(Comparator.comparingInt(x -> Integer.parseInt(x.annotation().order())));
                    return validators;
                });
            }
            //Don't need these anymore
            protoValidators = null;
        }

        if (validationConditions != null) {
            for (ValidationCondition vc : this.validationConditions) {
                logger.debug("Loaded {} validation conditions", validationConditions.size());
                try {
                    logger.debug("Adding function {}() to the validation condition context", vc.getName());
                    //Load it in a more round about way so that we don't need to know the params to select it
                    Method method = Arrays.stream(vc.getClass().getMethods()).filter(m -> m.getName().equals(vc.getName())).findFirst().orElseThrow(NoSuchMethodException::new);
                    Assert.isTrue(Modifier.isStatic(method.getModifiers()), "Validation condition methods must be static!");
                    validationFunctionsReflected.add(
                            new ValidationFunction(vc.getName(), method)
                    );
                } catch (NoSuchMethodException e) {
                    //Will always be defined as its required by the interface
                    throw new RuntimeException(e);
                }
            }
            //Don't need these anymore
            validationConditions = null;
        }


    }
    /*
     * This pointcut will be executed on any method in the com.lkgroup.ecommerce package that has any arguments that
     * are annotated with @Valid and are a Protobuf class
     */
    @Before("execution(* com.lkgroup.ecommerce..*(.., @jakarta.validation.Valid (com.google.protobuf.GeneratedMessage+), ..))")
    public void validateProto(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Annotation[][] annotations = signature.getMethod().getParameterAnnotations();

        Object[] args = joinPoint.getArgs();
        for (AtomicInteger i = new AtomicInteger(); i.get() < args.length; i.incrementAndGet()) {
            if (!(args[i.get()] instanceof Message)) {
                continue;
            }

            for (Annotation a : annotations[i.get()]) {
                if (a.annotationType().equals(Valid.class)) {
                    if (protoValidatorMap.containsKey(args[i.get()].getClass())) {
                        logger.debug("Found argument that is Protobuf message and annotated with @Valid. Searching for validator for {}", args[i.get()].getClass());

                        List<ValidatorAnnotation> validators = protoValidatorMap.get(args[i.get()].getClass());

                        AtomicBoolean stopProcessing = new AtomicBoolean(false);
                        Set<ConstraintViolation<Object>> violations = validators
                                .stream()
                                .takeWhile(v -> !stopProcessing.get())
                                .filter(v -> v.annotation().condition().isBlank() || evaluateCondition(v.annotation().condition(), mapper.map(args[i.get()], v.clazz()), signature.getParameterNames(), joinPoint.getArgs()))
                                .map(v -> {
                                    stopProcessing.set(v.annotation().stopProcessing());
                                    return v;
                                }).flatMap(v -> {
                                    Object mapped = mapper.map(args[i.get()], v.clazz());
                                    logger.trace("{}", mapped);
                                    Set<ConstraintViolation<Object>> errors = beanValidator.validate(mapped);
                                    return errors.stream();
                                }).collect(Collectors.toSet());

                        if (!violations.isEmpty())
                            throw new ConstraintViolationException(violations);

                    } else {
                        throw new IllegalArgumentException(String.format("Method: %s annotated with @Valid but no matching validator can be located. Did you mean to define one?", signature.getName()));
                    }

                }
            }

        }
    }

    protected boolean evaluateCondition(String condition, Object target, String[] params, Object[] args) {
        Expression expression = expressionParser.parseExpression(condition);
        StandardEvaluationContext context = new StandardEvaluationContext(target);
        validationFunctionsReflected.forEach(f -> context.registerFunction(f.name(), f.method()));
        for (int i = 0; i < args.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        return expression.getValue(context, boolean.class);
    }


}
