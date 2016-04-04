package org.uniknow.agiledev.docMockRest;

public class IoUtil {

    public static String contentFromFile(String fileName) {
        return convertStreamToString(IoUtil.class.getClassLoader()
            .getResourceAsStream(fileName));
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}