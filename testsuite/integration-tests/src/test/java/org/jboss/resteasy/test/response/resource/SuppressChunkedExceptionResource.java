package org.jboss.resteasy.test.response.resource;

import org.jboss.resteasy.spi.HttpResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/chunked")
public class SuppressChunkedExceptionResource {
    @Path("ErrorAfterFlushWithoutBody")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public void errorAfterFlushWithoutBody(@Context HttpResponse response) throws IOException {
        response.setSuppressExceptionDuringChunkedTransfer(false);
        response.getOutputStream().flush();
        throw new IOException("a strange io error");
    }

    @Path("IgnoreErrorAfterFlushWithoutBody")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public void ignoreErrorAfterFlushWithoutBody(@Context HttpResponse response) throws IOException {
        response.setSuppressExceptionDuringChunkedTransfer(true);
        response.getOutputStream().flush();
        throw new IOException("a strange io error");
    }
}
