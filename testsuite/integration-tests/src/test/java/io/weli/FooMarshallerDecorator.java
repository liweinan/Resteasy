package io.weli;

import org.jboss.resteasy.annotations.Decorator;

import javax.xml.bind.Marshaller;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Decorator(processor = FooMarshallerDecoratorProcessor.class, target = Marshaller.class)
public @interface FooMarshallerDecorator {
}
