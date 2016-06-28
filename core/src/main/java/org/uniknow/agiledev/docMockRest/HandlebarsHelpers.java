/**
 * Copyright (C) 2016 UniKnow (info.uniknow@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uniknow.agiledev.docMockRest;

import com.github.jknack.handlebars.*;
import org.raml.model.SecurityReference;

import java.io.IOException;
import java.util.*;

public class HandlebarsHelpers {

    public static Helper<Object> lowerCaseHelper() {
        return new Helper<Object>() {

            @Override
            public CharSequence apply(Object s, Options options)
                throws IOException {
                if (s != null) {
                    return s.toString().toLowerCase();
                }
                return "";
            }
        };
    }

    public static Helper<Object> eachInMap() {
        return new Helper<Object>() {

            /**
             * The helper's name.
             */
            public static final String NAME = "eachInMap";

            @Override
            public CharSequence apply(Object context, Options options)
                throws IOException {

                if (context instanceof Iterable) {
                    Options.Buffer buffer = options.buffer();
                    Iterator<Object> loop = ((Iterable) context).iterator();
                    int base = options.hash("base", 0);
                    int index = base;
                    boolean even = index % 2 == 0;
                    Context parent = options.context;
                    Template fn = options.fn;
                    while (loop.hasNext()) {
                        Object it = loop.next();
                        Context itCtx = Context.newContext(parent, it);
                        itCtx.combine("@index", index)
                            .combine("@first", index == base ? "first" : "")
                            .combine("@last", !loop.hasNext() ? "last" : "")
                            .combine("@odd", even ? "" : "odd")
                            .combine("@even", even ? "even" : "")
                            // 1-based index
                            .combine("@index_1", index + 1);
                        buffer.append(options.apply(fn, itCtx,
                            Arrays.asList(it, index)));
                        index += 1;
                        even = !even;
                    }
                    // empty?
                    if (base == index) {
                        buffer.append(options.inverse());
                    }
                    return buffer;
                } else if (context != null) {
                    Iterator<Map.Entry<String, Object>> loop = options
                        .propertySet(context).iterator();
                    Context parent = options.context;
                    boolean first = true;
                    Options.Buffer buffer = options.buffer();
                    Template fn = options.fn;
                    while (loop.hasNext()) {
                        Map.Entry<String, Object> entry = loop.next();
                        Object key = entry.getKey();
                        Object value = entry.getValue();
                        Context itCtx = Context.newBuilder(parent, value)
                            .combine("@key", key.toString())
                            .combine("@first", first ? "first" : "")
                            .combine("@last", !loop.hasNext() ? "last" : "")
                            .build();
                        buffer.append(options.apply(fn, itCtx,
                            Arrays.asList(value, key)));
                        first = false;
                    }
                    // empty?
                    if (first) {
                        buffer.append(options.inverse());
                    }
                    return buffer;
                }
                return options.buffer();
            }
        };
    }

    public static Helper<Object> highlightHelper() {
        return new Helper<Object>() {
            @Override
            public CharSequence apply(Object o, Options options)
                throws IOException {
                return o.toString();
            }
        };
    }

    public static Helper<Object> preOrLink() {
        return new Helper<Object>() {
            @Override
            public CharSequence apply(Object o, Options options)
                throws IOException {
                if (o == null) {
                    return "";
                }
                if (o.toString().trim().startsWith("{")) {
                    return new Handlebars.SafeString(
                        "<pre><code class=\"json\">" + o.toString()
                            + "</code></pre>");
                } else {
                    return new Handlebars.SafeString(String.format(
                        "<a href=\"#%s\" >%s</a>", o.toString(), o.toString()));
                }
            }
        };
    }

    public static Helper<Object> toUniqueID() {
        return new Helper<Object>() {
            @Override
            public CharSequence apply(Object o, Options options)
                throws IOException {
                if (o == null) {
                    return "";
                } else if (o instanceof String) {
                    try {
                        java.security.MessageDigest md = java.security.MessageDigest
                            .getInstance("MD5");
                        byte[] array = md.digest(((String) o).getBytes());
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < array.length; ++i) {
                            sb.append(Integer.toHexString(
                                (array[i] & 0xFF) | 0x100).substring(1, 3));
                        }
                        return sb.toString();
                    } catch (java.security.NoSuchAlgorithmException e) {
                    }
                    return null;
                } else {
                    return "FAILED";
                }
            }
        };
    }

    public static Helper<List<SecurityReference>> lockHelper() {
        return new Helper<List<SecurityReference>>() {
            @Override
            public CharSequence apply(
                List<SecurityReference> securityReferences, Options options)
                throws IOException {
                if (!securityReferences.isEmpty()) {
                    return "<span class=\"glyphicon glyphicon-lock\" title=\"Authentication required\"></span>";
                }
                return "";
            }
        };
    }
}
