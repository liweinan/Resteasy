package org.jboss.resteasy.test.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.response.resource.SuppressChunkedExceptionResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

@RunWith(Arquillian.class)
@RunAsClient
public class SuppressChunkedExceptionTest {
    protected final Logger logger = LogManager.getLogger(SuppressChunkedExceptionTest.class.getName());

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SuppressChunkedExceptionTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SuppressChunkedExceptionResource.class);
    }

    @Test
    public void testError() throws Exception {
        InetAddress addr = InetAddress.getByName(PortProviderUtil.getHost());
        Socket socket = new Socket(addr, PortProviderUtil.getPort());
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("GET /SuppressChunkedExceptionTest/chunked/ErrorAfterFlushWithoutBody HTTP/1.1");
        out.println("Host: localhost");
        out.println("Connection: keep-alive");
        out.println();

        InputStream in = socket.getInputStream();
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) != -1) {
            bs.write(buffer, 0, len);
        }

        System.out.print(":::::");
        for (byte b : bs.toByteArray()) {
            System.out.print(String.valueOf(b) + ".");
        }
        System.out.println("]");
        socket.close();
    }

    @Test
    public void testSuppressed() throws Exception {
        InetAddress addr = InetAddress.getByName(PortProviderUtil.getHost());
        Socket socket = new Socket(addr, PortProviderUtil.getPort());
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("GET /SuppressChunkedExceptionTest/chunked/IgnoreErrorAfterFlushWithoutBody HTTP/1.1");
        out.println("Host: localhost");
        out.println("Connection: keep-alive");
        out.println();

        InputStream in = socket.getInputStream();
        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) != -1) {
            bs.write(buffer, 0, len);
        }

        System.out.print(":::::");
        for (byte b : bs.toByteArray()) {
            System.out.print(String.valueOf(b) + ".");
        }
        System.out.println("]");
        socket.close();
    }
}
