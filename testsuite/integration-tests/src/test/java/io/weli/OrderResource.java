package io.weli;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/order")
public interface OrderResource {

   @POST
   @Path("/update")
   @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
   @Produces(MediaType.APPLICATION_XML)
   @FooMarshallerDecorator
   Response update(@FooUnmarshallerDecorator FooJaxbEntity obj);
}
