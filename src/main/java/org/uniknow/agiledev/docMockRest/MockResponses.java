package org.uniknow.agiledev.docMockRest;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;

/**
 * Checks whether response for request is defined and it not returns default response.
 */
public class MockResponses extends ResponseTransformer {

    private final String pathResponseFiles;

    /**
     * Constructor
     *
     * @param responseFiles - Path to directory in which response files are defined.
     */
    public MockResponses(String responseFiles) {
        pathResponseFiles = responseFiles;
    }

    private String getExtensionResponseFile(Request request) {

        String responseExtenstion;
        ContentTypeHeader contentType = request.contentTypeHeader();
        if (contentType != null) {
            try {
                switch (contentType.mimeTypePart()) {
                    case MediaType.APPLICATION_JSON:
                        responseExtenstion = "json";
                        break;
                    case MediaType.TEXT_XML:
                    case MediaType.APPLICATION_XML:
                        responseExtenstion = "xml";
                        break;
                    case MediaType.TEXT_HTML:
                        responseExtenstion = "html";
                        break;
                    case MediaType.TEXT_PLAIN:
                        responseExtenstion = "txt";
                        break;
                    default:
                        responseExtenstion = "txt";
                }
            } catch (NullPointerException err) {
                responseExtenstion = "txt";
            }
        } else {
            responseExtenstion = "txt";
        }

        return responseExtenstion;
    }
    /**
     * TODO
     *
     * @param request
     * @param responseDefinition
     * @param fileSource
     * @return
     */
    @Override
    public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition, FileSource fileSource) {
        // TODO: Check whether there is reponse defined for request if not use passed response definition.
        System.out.println("Processing request for: " + request.getUrl());

        if (pathResponseFiles != null) {
            // Determine extension of response file
            String responseExtenstion = getExtensionResponseFile(request);

            // Check whether response for specific request is defined
            Path pathResponseFile = Paths.get(pathResponseFiles, request.getUrl(), "response." + responseExtenstion);
            if (Files.exists(pathResponseFile)) {
                System.out.println("Response defined for " + request.getUrl() + " : " + pathResponseFile);
                try {
                    StringBuffer response = new StringBuffer();
                    InputStream in = Files.newInputStream(pathResponseFile);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line = null;
                    while((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return ResponseDefinitionBuilder
                            .like(responseDefinition).but()
                            .withBody(response.toString())
                            .build();

                } catch(IOException error) {
                    return ResponseDefinitionBuilder
                            .like(responseDefinition).but()
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                            .withBody(error.getMessage())
                            .build();

                }
            }
        }

        if (responseDefinition.getBody() != null) {
            // Return (default) response as defined within RAML file
            return ResponseDefinitionBuilder
                    .like(responseDefinition)
                    .build();
        } else {
            return ResponseDefinitionBuilder.like(responseDefinition).but()
                    .withStatus(HttpStatus.SC_NOT_FOUND)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .withBody("No mocked response found for " + request.getUrl())
                    .build();
        }
    }

    @Override
    public String name() {
        return "mock-responses";
    }

}
