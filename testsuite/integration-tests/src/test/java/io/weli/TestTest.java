package io.weli;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

public class TestTest {

   private static UndertowJaxrsServer server;

   @BeforeClass
   public static void init() throws Exception {
      server = new UndertowJaxrsServer().start();
   }

   @AfterClass
   public static void stop() throws Exception {
      server.stop();
   }

   @Test
   public void testApplicationPath() throws Exception {
      server.deploy(TestApp.class);
      Client client = ClientBuilder.newClient();

      Response resp = client
            .target(TestPortProvider.generateURL("/base/order/update"))
            .request()
            .post(Entity.xml(new FooJaxbEntity("test_test")));
      System.out.println("entity: " + resp.getEntity());
//      Assert.assertEquals("hello world", val);
      client.close();
   }

   @ApplicationPath("/base")
   public static class TestApp extends Application {
      @Override
      public Set<Class<?>> getClasses() {
         HashSet<Class<?>> classes = new HashSet<>();
         classes.add(OrderResourceImpl.class);
         return classes;
      }
   }

}
