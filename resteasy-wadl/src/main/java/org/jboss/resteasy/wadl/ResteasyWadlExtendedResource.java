package org.jboss.resteasy.wadl;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/wadl-extended")
public class ResteasyWadlExtendedResource {
    @GET
    @Path("{path}")
    @Produces("application/xml")
    public Response grammars(@PathParam("path") String path) {
        return Response
                .ok()
                .type(MediaType.APPLICATION_XML_TYPE)
                .entity(ResteasyWadlGrammar.getFromSchemas(path))
                .build();
    }
}
