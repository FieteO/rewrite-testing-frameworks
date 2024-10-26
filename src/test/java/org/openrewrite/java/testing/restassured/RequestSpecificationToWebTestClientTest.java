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

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class RequestSpecificationToWebTestClientTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec
          .parser(JavaParser.fromJavaVersion()
            .classpathFromResources(new InMemoryExecutionContext(), "rest-assured-5.5.0", "spring-webflux-6.1.12", "spring-test-6.1.12"))
          .recipe(new RequestSpecificationToWebTestClient());
    }

    @DocumentExample
    @Test
    void singleStaticMethodNoMessage() {
        //language=java
        rewriteRun(
          java(
            """
              import static org.assertj.core.api.Assertions.assertThat;

              import io.restassured.RestAssured;
              import io.restassured.http.Header;
              import org.junit.jupiter.api.Test;
              import org.springframework.boot.test.web.server.LocalServerPort;
              import org.springframework.http.HttpStatus;
              import org.springframework.http.MediaType;

              class MyTest {

                  @LocalServerPort
                  protected int port;

                  @Test
                  void someTest() {
                      String response = RestAssured.given().port(port)
                              .given()
                              .accept(MediaType.APPLICATION_JSON_VALUE)
                              .header(new Header("X-Request-Id", "1234"))
                              .when()
                              .get("/some/resource")
                              .then()
                              .header("X-Request-Id", "1234")
                              .statusCode(HttpStatus.OK.value())
                              .extract()
                              .body()
                              .as(String.class);

                      assertThat(response).isEqualTo("someResponse");
                  }
              }
              """,
            """
              import static org.assertj.core.api.Assertions.assertThat;

              import org.junit.jupiter.api.Test;
              import org.springframework.boot.test.web.server.LocalServerPort;
              import org.springframework.http.MediaType;
              import org.springframework.test.web.reactive.server.WebTestClient;

              class MyTest {

                  @LocalServerPort
                  protected int port;

                  @Test
                  void someTest() {
                    String response = WebTestClient.bindToServer()
                            .baseUrl("http://localhost:" + port)
                            .build()
                            .get()
                            .uri("/some/resource")
                            .header("X-Request-Id", "1234")
                            .accept(MediaType.APPLICATION_JSON)
                            .exchange()
                            .expectStatus().isOk()
                            .expectHeader().valueEquals("X-Request-Id", "1234")
                            .expectBody(String.class)
                            .returnResult()
                            .getResponseBody();

                      assertThat(response).isEqualTo("someResponse");
                  }
              }
              """
          )
        );
    }
}
