package io.weli;

import org.jboss.resteasy.annotations.Decorator;

import javax.xml.bind.Unmarshaller;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Decorator(processor = FooUnmarshallerDecoratorProcessor.class, target = Unmarshaller.class)
public @interface FooUnmarshallerDecorator {}
