/*
 * Copyright 2024 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.testing.restassured;

import org.openrewrite.*;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.TreeVisitingPrinter;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.J;

public class RequestSpecificationToWebTestClient extends Recipe {

    @Override
    public String getDisplayName() {
        return "Migrate from Restassured `RequestSpecification` to WebTestClient";
    }

    @Override
    public String getDescription() {
        return "Migrate from Restassured `RequestSpecification` to WebTestClient.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return Preconditions.check(
            new UsesType<>("io.restassured.specification.RequestSpecification", false), new MigrateToWebTestClientVisitor());
    }

    private class MigrateToWebTestClientVisitor extends JavaIsoVisitor<ExecutionContext> {

        @Override
        public J.CompilationUnit visitCompilationUnit(J.CompilationUnit compUnit, ExecutionContext executionContext) {
            // This next line could be omitted in favor of a breakpoint
            // if you'd prefer to use the debugger instead.
            System.out.println(TreeVisitingPrinter.printTree(getCursor()));
            return super.visitCompilationUnit(compUnit, executionContext);
        }
    }
}
