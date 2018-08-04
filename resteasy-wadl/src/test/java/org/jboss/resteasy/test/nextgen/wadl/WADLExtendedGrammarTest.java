package org.jboss.resteasy.test.nextgen.wadl;

import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.wadl.ResteasyWadlGrammar;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WADLExtendedGrammarTest extends WADLBasicTest {
    @Test
    public void extendedTest() throws Exception {

        ResteasyWadlGrammar.includeGrammars("application-grammars.xml");
        ResteasyWadlGrammar.enableSchemaGeneration();

        System.out.println(ResteasyWadlGrammar.grammars);


        testGrammarGeneration();
        // test again to make sure the grammar generation is re-entrant
        testGrammarGeneration();

        {
            org.jboss.resteasy.wadl.jaxb.Application application;
            String url = "http://127.0.0.1:${port}/application.xml".replaceAll("\\$\\{port\\}",
                    Integer.valueOf(TestPortProvider.getPort()).toString());
            System.out.println(url);
            setUrl(url);

            WebTarget target = getClient().target(url);
            Response response = target.request().get();
            application = response.readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);

            String url2 = "http://127.0.0.1:${port}/wadl-extended/${schema}".replaceAll("\\$\\{port\\}",
                    Integer.valueOf(TestPortProvider.getPort()).toString())
                    .replaceAll("\\$\\{schema\\}", application.getGrammars().getInclude().get(0).getHref());
            System.out.println(url2);

            WebTarget target2 = getClient().target(url2);
            Response response2 = target2.request().get();

            System.out.println(response2.readEntity(String.class));
        }

//        Thread.currentThread().join();
    }

    private void testGrammarGeneration() {
        org.jboss.resteasy.wadl.jaxb.Application application;
        String url = "http://127.0.0.1:${port}/application.xml".replaceAll("\\$\\{port\\}",
                Integer.valueOf(TestPortProvider.getPort()).toString());
        System.out.println(url);
        setUrl(url);

        WebTarget target = getClient().target(url);
        Response response = target.request().get();
        application = response.readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);
        assertNotNull("application not null", application);
        assertNotNull(application.getGrammars());
        assertEquals(2, application.getGrammars().getInclude().size());
    }
}
