package io.weli;

import javax.ws.rs.core.Response;


public class OrderResourceImpl implements OrderResource {

   @Override
   public Response update(FooJaxbEntity obj) {
      System.out.println("in order resource impl");
      return Response.status(Response.Status.OK).entity(obj).build();
   }
}
