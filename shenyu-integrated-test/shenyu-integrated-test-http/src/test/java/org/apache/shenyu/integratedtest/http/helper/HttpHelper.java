/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shenyu.integratedtest.http.helper;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

@Slf4j
public class HttpHelper {
    public static final HttpHelper INSTANCE = new HttpHelper();
    private static final Gson GSON = new Gson();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .build();

    public static final String GATEWAY_END_POINT = "http://localhost:9195";

    public static final MediaType JSON = MediaType.parse("application/json");

    public <RESP, REQ> RESP postGateway(String path, REQ req, Class<RESP> respType) throws IOException {
        Request request = new Request.Builder()
                .url(GATEWAY_END_POINT + path)
                .post(RequestBody.create(GSON.toJson(req), JSON))
                .build();
        Response response = client.newCall(request).execute();
        String respBody = response.body().string();
        log.info("postGateway({}) resp({})", path, respBody);
        return GSON.fromJson(respBody, respType);
    }

    public <RESP> RESP getFromGateway(String path, Type type) throws IOException {
        return this.getFromGateway(path, null, type);
    }

    public <RESP> RESP getFromGateway(String path, Map<String, Object> headers, Type type) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(GATEWAY_END_POINT + path);
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach((key, value) -> requestBuilder.addHeader(key, String.valueOf(value)));
        }
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        String respBody = response.body().string();
        log.info("getFromGateway({}) resp({})", path, respBody);
        return GSON.fromJson(respBody, type);
    }
}
