package org.uniknow.agiledev.docMockRest;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.MarkdownHelper;

public class HandlebarsFactory {

    public static Handlebars newHandlebarsWithLoopSupport() {
        Handlebars handlebars = new Handlebars();
        handlebars.setInfiniteLoops(true);
        return handlebars;
    }

    public static Handlebars withHelpers(Handlebars handlebars) {
        handlebars.registerHelper("md", new MarkdownHelper());
        handlebars.registerHelper("eachInMap", HandlebarsHelpers.eachInMap());
        handlebars.registerHelper("toUniqueID", HandlebarsHelpers.toUniqueID());
        handlebars.registerHelper("lower", HandlebarsHelpers.lowerCaseHelper());
        handlebars.registerHelper("lock", HandlebarsHelpers.lockHelper());
        handlebars.registerHelper("highlight", HandlebarsHelpers.highlightHelper());
        handlebars.registerHelper("preOrLink", HandlebarsHelpers.preOrLink());

        return handlebars;
    }

    public static Handlebars defaultHandlebars() {
        return withHelpers(newHandlebarsWithLoopSupport());
    }

}