/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.seatunnel.connectors.seatunnel.http.util;

import org.apache.seatunnel.connectors.seatunnel.http.config.HttpParameter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

public class AuthorizationUtilTest {

    @Test
    @DisplayName("Test getHttpSignatureClass with valid source code")
    public void testGetHttpSignatureClass() {
        // Given
        String sourceCode =
                "public String HttpSignature(String vs) {\n" + "        return vs;\n" + "}\n";

        // When
        Object signatureClass = AuthorizationUtil.getHttpSignatureClass(sourceCode);

        // Then
        Assertions.assertNotNull(signatureClass);
    }

    @Test
    @DisplayName("Test getHttpSignatureClass with invalid source code")
    public void testGetHttpSignatureClassWithInvalidCode() {
        // Given
        String invalidSourceCode = "invalid java code";

        // When & Then
        assertThrows(
                RuntimeException.class,
                () -> {
                    AuthorizationUtil.getHttpSignatureClass(invalidSourceCode);
                });
    }

    @Test
    @DisplayName("Test getSignatureHttpParameter with valid signature class")
    public void testGetSignatureHttpParameter() {
        // Given
        String sourceCode =
                "import org.apache.seatunnel.connectors.seatunnel.http.config.HttpParameter;"
                        + " import java.util.Map;"
                        + " import java.util.HashMap;"
                        + "    public HttpParameter HttpSignature( HttpParameter httpParameter) {"
                        + "        Map<String, String> params = new HashMap();"
                        + "        String signature = \"custom_sign_token\";"
                        + "        params.put(\"token\", signature);"
                        + "        httpParameter.setParams(params);"
                        + "        return httpParameter;"
                        + "    }"
                        + " ";

        Object signatureClass = AuthorizationUtil.getHttpSignatureClass(sourceCode);
        HttpParameter httpParameter = new HttpParameter();

        // When
        HttpParameter result =
                AuthorizationUtil.getSignatureHttpParameter(signatureClass, httpParameter);

        // Then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("custom_sign_token", result.getParams().get("token"));
    }

    @Test
    @DisplayName("Test getSignatureHttpParameter with invalid method")
    public void testGetSignatureHttpParameterWithInvalidMethod() {
        // Given
        String sourceCode =
                "import org.apache.seatunnel.connectors.seatunnel.http.config.HttpParameter;"
                        + "    public HttpParameter InvalidMethodName(\n"
                        + "            HttpParameter httpParameter) {\n"
                        + "        return httpParameter;\n"
                        + "    }\n";

        Object signatureClass = AuthorizationUtil.getHttpSignatureClass(sourceCode);
        HttpParameter httpParameter = new HttpParameter();

        // When & Then
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    AuthorizationUtil.getSignatureHttpParameter(signatureClass, httpParameter);
                });
    }
}
