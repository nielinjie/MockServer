
package org.uniknow.agiledev.docMockRest.swagger;

import io.swagger.jaxrs.Reader;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import org.uniknow.agiledev.docMockRest.JsonIOResponsesMappingsLoader;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import static org.uniknow.agiledev.docMockRest.swagger.SwaggerMockServer.LOG;

/**
 * Created by nielinjie on 9/2/16.
 */
public class SwaggerConfig {
    public static SwaggerConfig create() {
        return new SwaggerConfig();
    }

    /**
     * Returns specification based on annotated classes.
     * 
     * @param prefix
     *            Package that need to be scanned for annotated classes
     * @return Swagger specification
     */
    private static Swagger getSpecification(String prefix) {
        LOG.info("Create swagger model based on annotated classes within {}",
            prefix);

        // Get all swagger annotated classes within specified package
        SwaggerAnnotationScanner scanner = new SwaggerAnnotationScanner();
        Set<Class<?>> resources = scanner.getResources(prefix);
        LOG.debug("Found annotated classes are {}", resources);

        // Read all annotated classes and create rest specifications
        Swagger swagger = null;
        Reader reader = new Reader(swagger);
        swagger = reader.read(resources);

        return swagger;
    }

    public SwaggerConfig setSwaggerResourceName(String resourceName) {
        return this.setSwaggerFileLocation(SwaggerConfig.class.getResource(
            resourceName).getFile());
    }

    public SwaggerConfig setSwaggerFileLocation(String swaggerFileLocation) {
        this.swaggerFileLocation = swaggerFileLocation;
        return this;
    }

    public SwaggerConfig setResponseFileLocation(String responseFileLocation) {
        this.responseFileLocation = responseFileLocation;
        return this;
    }

    public SwaggerConfig setSwaggerPrefix(String swaggerPrefix) {
        this.swaggerPrefix = swaggerPrefix;
        return this;
    }

    public SwaggerConfig setResponseFileUrl(URL responseFileUrl) {
        this.responseFileUrl = responseFileUrl;
        return this;
    }

    private String swaggerFileLocation;
    private String responseFileLocation;
    private String swaggerPrefix;
    private URL responseFileUrl;

    void precessServer(SwaggerMockServer server) throws IOException {
        if (null != swaggerFileLocation && null != swaggerPrefix)
            throw new IllegalArgumentException("both file location and prefix");
        if (null != responseFileLocation && null != responseFileUrl)
            throw new IllegalArgumentException("both response location and url");

        if (null != swaggerFileLocation) {
            Swagger swagger = new SwaggerParser().read(swaggerFileLocation);
            server.stubHelper.createStubs(swagger);

        }
        if (null != swaggerPrefix) {
            Swagger specification = SwaggerConfig
                .getSpecification(swaggerPrefix);
            server.stubHelper.createStubs(specification);
        }

        if (null != responseFileUrl) {
            LOG.info("Loading responses of {}", responseFileUrl);
            server.wireMockServer
                .loadMappingsUsing(new JsonIOResponsesMappingsLoader(server,
                    responseFileUrl));
        }
        if (null != responseFileLocation) {
            LOG.info("Loading responses of {}", responseFileLocation);
            server.wireMockServer
                .loadMappingsUsing(new JsonIOResponsesMappingsLoader(server,
                    responseFileLocation));
        }
    }
}
