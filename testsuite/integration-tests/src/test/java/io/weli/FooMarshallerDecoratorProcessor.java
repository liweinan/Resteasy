package io.weli;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.spi.DecoratorProcessor;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.Marshaller;
import java.lang.annotation.Annotation;

@DecorateTypes({"text/*+xml", "application/*+xml"})
public class FooMarshallerDecoratorProcessor implements DecoratorProcessor<Marshaller, FooMarshallerDecorator>
{
   public Marshaller decorate(Marshaller target, FooMarshallerDecorator annotation,
                              Class type, Annotation[] annotations, MediaType mediaType)
   {
      System.out.println("FooMarshallerDecoratorProcessor for Marshaller");
      return target;
   }
}
