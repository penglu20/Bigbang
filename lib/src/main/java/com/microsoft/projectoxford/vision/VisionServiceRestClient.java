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
package com.microsoft.projectoxford.vision;

import com.microsoft.projectoxford.vision.rest.VisionServiceException;
import com.microsoft.projectoxford.vision.rest.WebServiceRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VisionServiceRestClient {
    private static final String DEFAULT_API_ROOT = "https://api.projectoxford.ai/vision/v1.0";
    private final String apiRoot;
    private final WebServiceRequest restCall;

    public VisionServiceRestClient(String subscriptKey) {
        this(subscriptKey, DEFAULT_API_ROOT);
    }

    public VisionServiceRestClient(String subscriptKey, String apiRoot) {
        this.restCall = new WebServiceRequest(subscriptKey);
        this.apiRoot = apiRoot.replaceAll("/$", "");
    }

    public String recognizeText(String url, String languageCode, boolean detectOrientation) throws VisionServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("language", languageCode);
        params.put("detectOrientation", detectOrientation);
        String path = apiRoot + "/ocr";
        String uri = WebServiceRequest.getUrl(path, params);

        params.clear();
        params.put("url", url);
        String json = (String) this.restCall.request(uri, "POST", params, null, false);
        return json;
    }

    public String recognizeText(byte[] stream, String languageCode, boolean detectOrientation) throws VisionServiceException, IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("language", languageCode);
        params.put("detectOrientation", detectOrientation);
        String path = apiRoot + "/ocr";
        String uri = WebServiceRequest.getUrl(path, params);
        params.put("data", stream);
        String json = (String) this.restCall.request(uri, "POST", params, "application/octet-stream", false);
        return json;
    }


    public void setOnTimeUseUp(WebServiceRequest.OnResult onTimeUseUp) {
        restCall.setOnTimeUseUp(onTimeUseUp);
    }
}
