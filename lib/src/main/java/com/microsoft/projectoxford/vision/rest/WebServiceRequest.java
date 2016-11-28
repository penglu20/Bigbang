//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Vision-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.microsoft.projectoxford.vision.rest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class WebServiceRequest {
    private static final String headerKey = "ocp-apim-subscription-key";
    private HttpClient client = new DefaultHttpClient();
    private String subscriptionKey;

    public WebServiceRequest(String key) {
        this.subscriptionKey = key;
    }

    public Object request(String url, String method, Map<String, Object> data, String contentType, boolean responseInputStream) throws VisionServiceException {
        if (method.matches("POST")) {
            return post(url, data, contentType, responseInputStream);
        }
        throw new VisionServiceException("Error! Incorrect method provided: " + method);
    }

    private Object post(String url, Map<String, Object> data, String contentType, boolean responseInputStream) throws VisionServiceException {
        return webInvoke("POST", url, data, contentType, responseInputStream);
    }

    public void setOnTimeUseUp(OnResult onTimeUseUp) {
        onResult = onTimeUseUp;
    }

    public  interface  OnResult{
        void onTimeUseUp();
        void onSuccess();
    }
    private  OnResult onResult;
    private Object webInvoke(String method, String url, Map<String, Object> data, String contentType, boolean responseInputStream) throws VisionServiceException {
        HttpPost request = null;

        if (method.matches("POST")) {
            request = new HttpPost(url);
        } else if (method.matches("PATCH")) {
            request = new HttpPatch(url);
        }

        boolean isStream = false;

        /*Set header*/
        if (contentType != null && !contentType.isEmpty()) {
            request.setHeader("Content-Type", contentType);
            if (contentType.toLowerCase().contains("octet-stream")) {
                isStream = true;
            }
        } else {
            request.setHeader("Content-Type", "application/json");
        }

        request.setHeader(headerKey, this.subscriptionKey);

        try {

            request.setEntity(new ByteArrayEntity((byte[]) data.get("data")));
            //
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            //读取超时
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            HttpResponse response = this.client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 403){
                if(onResult != null)
                    onResult.onTimeUseUp();
            }
            if (statusCode == 200) {
                if(onResult != null)
                    onResult.onSuccess();
                if (!responseInputStream) {
                    return readInput(response.getEntity().getContent());
                } else {
                    return response.getEntity().getContent();
                }

            } else {
                throw new Exception("Error executing POST request! Received error code: " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            throw new VisionServiceException(e.getMessage());
        }
    }


    public static String getUrl(String path, Map<String, Object> params) {
        StringBuffer url = new StringBuffer(path);

        boolean start = true;
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (start) {
                url.append("?");
                start = false;
            } else {
                url.append("&");
            }

            try {
                url.append(param.getKey() + "=" + URLEncoder.encode(param.getValue().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return url.toString();
    }

    private String readInput(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer json = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            json.append(line);
        }

        return json.toString();
    }
}
