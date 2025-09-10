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

import org.apache.seatunnel.shade.org.codehaus.commons.compiler.CompileException;
import org.apache.seatunnel.shade.org.codehaus.janino.ClassBodyEvaluator;

import org.apache.seatunnel.common.utils.ReflectionUtils;
import org.apache.seatunnel.connectors.seatunnel.http.config.HttpConfig;
import org.apache.seatunnel.connectors.seatunnel.http.config.HttpParameter;

import static org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString;

public class AuthorizationUtil {
    // Basic Auth
    public static String getTokenByBasicAuth(String username, String password) {
        // get accessToken by base64 password
        String accountMessage = username + ":" + password;
        String accessToken =
                HttpConfig.BASIC + " " + encodeBase64URLSafeString(accountMessage.getBytes());
        return accessToken;
    }

    public static Object getHttpSignatureClass(String SourceCode) {
        try {
            ClassBodyEvaluator cbe = new ClassBodyEvaluator();

            cbe.cook(SourceCode);

            return cbe.getClazz().newInstance();

        } catch (CompileException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpParameter getSignatureHttpParameter(
            Object signatureClass, HttpParameter httpParameter) {
        Object result;
        try {
            result = ReflectionUtils.invoke(signatureClass, "HttpSignature", httpParameter);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "CompileCode error, please check signature algorithms code: " + e.getMessage());
        }
        return (HttpParameter) result;
    }
}
