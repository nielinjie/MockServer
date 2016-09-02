package org.uniknow.agiledev.docMockRest.swagger;

import java.net.URL;

/**
 * Created by nielinjie on 9/2/16.
 */
//TODO 重构那n个构造函数
public class SwaggerConfig {
    private String swaggerFileLocation;
    private String responseFileLocation;
    private String swaggerPrefix;
    private URL responseFileUrl;

    public void proccessServer(SwaggerMockServer server) {
        if (null != swaggerFileLocation && null != swaggerPrefix)
            throw new IllegalArgumentException("both file location and prefix");
    }
}
