package org.jboss.resteasy.wadl;

import org.jboss.resteasy.wadl.jaxb.Doc;
import org.jboss.resteasy.wadl.jaxb.Grammars;
import org.jboss.resteasy.wadl.jaxb.Include;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ResteasyWadlGrammar {

    // included grammars or generated grammars, or both.
    public static Grammars grammars = null;
    public static Map<String, byte[]> externalSchemas = new ConcurrentHashMap<>();
    public static Map<String, byte[]> generatedSchemas = new ConcurrentHashMap<>();

    // the JAXB annotated classes need to be included to generate schemas
    public static Set<Class> schemaClasses = Collections.synchronizedSet(new HashSet<>());

    static ClassLoader loader = Thread.currentThread().getContextClassLoader();

    private static AtomicBoolean generateSchema = new AtomicBoolean(false);


    public static boolean hasGrammars() {
        return grammars != null;
    }

    // include grammars provided by users.
    public synchronized static void includeGrammars(String grammarFileName) {
        externalSchemas.clear();

        try (final InputStream is = loader.getResourceAsStream(grammarFileName)) {
            if (is != null) {
                Grammars grammars = unmarshall(is);
                List<Include> includes = grammars.getInclude();
                for (Include include : includes) {
                    addExternalSchema(include.getHref());
                }
                addGrammars(grammars);
            } else {
                throw new RuntimeException(new FileNotFoundException(grammarFileName));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void enableSchemaGeneration() {
        generateSchema.set(true);
    }

    public static boolean schemaGenerationEnabled() {
        return generateSchema.get();
    }

    public synchronized static void addGrammars(Grammars grammars) {
        if (ResteasyWadlGrammar.grammars == null) {
            ResteasyWadlGrammar.grammars = grammars;
        } else {
            if (!grammars.getAny().isEmpty()) {
                ResteasyWadlGrammar.grammars.getAny().addAll(grammars.getAny());
            }
            if (!grammars.getDoc().isEmpty()) {
                ResteasyWadlGrammar.grammars.getDoc().addAll(grammars.getDoc());
            }
            if (!grammars.getInclude().isEmpty()) {
                ResteasyWadlGrammar.grammars.getInclude().addAll(grammars.getInclude());
            }
        }
    }

    public static synchronized void collectClassesForSchemaGeneration(ResteasyWadlMethodMetaData methodMetaData) {
        if (!schemaGenerationEnabled())
            return;
        // support runtime rescan.
        schemaClasses.clear();
        generatedSchemas.clear();

        _addClass(methodMetaData.getMethod().getReturnType());

        for (ResteasyWadlMethodParamMetaData paramMetaData : methodMetaData.getParameters()) {
            _addClass(paramMetaData.getType());
        }

        processClassesForSchema();
    }

    private static void _addClass(Class clazz) {
        if (clazz.getAnnotation(XmlRootElement.class) != null) {
            ResteasyWadlGrammar.schemaClasses.add(clazz);
        }
    }

    public synchronized static void processClassesForSchema() {
        try {
            final JAXBContext context = JAXBContext.newInstance(schemaClasses.toArray(new Class[schemaClasses.size()]));

            final List<StreamResult> results = new ArrayList<>();

            context.generateSchema(new SchemaOutputResolver() {
                int counter = 0;

                @Override
                public Result createOutput(final String namespaceUri, final String suggestedFileName) {
                    final StreamResult result = new StreamResult(new CharArrayWriter());
                    String systemId = "xsd" + (counter++) + ".xsd";
                    result.setSystemId(systemId);
                    results.add(result);
                    return result;
                }
            });


            if (grammars != null) {
                Iterator<Include> iter = grammars.getInclude().iterator();
                while (iter.hasNext()) {
                    for (Doc doc : iter.next().getDoc()) {
                        if ("Generated".equals(doc.getTitle())) ;
                        iter.remove();
                    }
                }
            }

            // in case grammars is null
            addGrammars(new Grammars());

            for (final StreamResult result : results) {
                final CharArrayWriter writer = (CharArrayWriter) result.getWriter();
                final byte[] contents = writer.toString().getBytes("UTF8");
                generatedSchemas.put(
                        result.getSystemId(),
                        contents);

                Include inc = new Include();
                inc.setHref(result.getSystemId());
                Doc doc = new Doc();
                doc.setTitle("Generated");
                doc.setLang("en");
                inc.getDoc().add(doc);

                ResteasyWadlGrammar.grammars.getInclude().add(inc);
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addExternalSchema(String href) {
        try (InputStream is = loader.getResourceAsStream(href)) {
            if (is != null) {
                externalSchemas.put(href, toBytes(is));
            } else {
                throw new RuntimeException(new FileNotFoundException(href));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] toBytes(InputStream is) throws Exception {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (is, StandardCharsets.UTF_8.name()))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        return textBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static Grammars unmarshall(InputStream is) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(Grammars.class);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        Grammars out = (Grammars) unmarshaller.unmarshal(is);
        return out;
    }

    public static byte[] getFromSchemas(String path) {
        byte[] result;
        result = externalSchemas.get(path);
        if (result == null) {
            result = generatedSchemas.get(path);
        }
        return result;
    }
}
