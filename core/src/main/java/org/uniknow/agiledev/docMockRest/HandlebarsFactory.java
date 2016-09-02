/**
 * Copyright (C) 2016 UniKnow (info.uniknow@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		handlebars.registerHelper("highlight",
				HandlebarsHelpers.highlightHelper());
		handlebars.registerHelper("preOrLink", HandlebarsHelpers.preOrLink());

		return handlebars;
	}

	public static Handlebars defaultHandlebars() {
		return withHelpers(newHandlebarsWithLoopSupport());
	}

}