package org.jboss.resteasy.links.impl;

import java.lang.annotation.Annotation;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
//import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.plugins.providers.jackson.DecoratedEntityContainer;
import org.jboss.resteasy.plugins.providers.jackson.JAXBMarshallerOrJacksonContainer;
import org.jboss.resteasy.spi.DecoratorProcessor;
import org.jboss.resteasy.spi.Registry;

public class LinkDecorator implements DecoratorProcessor<JAXBMarshallerOrJacksonContainer, AddLinks> {

    public JAXBMarshallerOrJacksonContainer decorate(JAXBMarshallerOrJacksonContainer target, final AddLinks annotation,
                               Class type, Annotation[] annotations, MediaType mediaType) {
        if (target.getClass().equals(DecoratedEntityContainer.class)) {
            UriInfo uriInfo = ResteasyContext.getContextData(UriInfo.class);
            ResourceMethodRegistry registry = (ResourceMethodRegistry) ResteasyContext.getContextData(Registry.class);

            // find all rest service classes and scan them
            RESTUtils.addDiscovery(((DecoratedEntityContainer) target).getEntity(), uriInfo, registry);
            return target;
        } else {
            target.setListener(new Listener() {
                @Override
                public void beforeMarshal(Object entity) {
                    UriInfo uriInfo = ResteasyContext.getContextData(UriInfo.class);
                    ResourceMethodRegistry registry = (ResourceMethodRegistry) ResteasyContext.getContextData(Registry.class);

                    // find all rest service classes and scan them
                    RESTUtils.addDiscovery(entity, uriInfo, registry);
                }
            });
            return target;
        }
    }
}
