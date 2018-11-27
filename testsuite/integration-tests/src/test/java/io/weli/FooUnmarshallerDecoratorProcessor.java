package io.weli;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.spi.DecoratorProcessor;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.Unmarshaller;
import java.lang.annotation.Annotation;

@DecorateTypes({ "text/*+xml", "application/*+xml", MediaType.APPLICATION_XML, MediaType.TEXT_XML })
public class FooUnmarshallerDecoratorProcessor implements DecoratorProcessor<Unmarshaller, FooUnmarshallerDecorator> {

   @Override
   public Unmarshaller decorate(Unmarshaller target, FooUnmarshallerDecorator annotation, Class type, Annotation[] annotations, MediaType mediaType) {
      System.out.println("FooUnmarshallerDecorator for Unmarshaller.");
      return target;
   }
}
