package org.jboss.resteasy.test.nextgen.wadl.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/extended")
public class ExtendedResource {

    @POST
    @Consumes({"application/xml"})
    public String post(ListType income) {
        return "foo";
    }
}

