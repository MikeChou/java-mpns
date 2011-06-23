/*
 * Copyright 2011, Mahmood Ali.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following disclaimer
 *     in the documentation and/or other materials provided with the
 *     distribution.
 *   * Neither the name of Mahmood Ali. nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.notnoop.mpns.internal;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map.Entry;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.notnoop.mpns.MpnsNotification;
import com.notnoop.mpns.MpnsService;
import com.notnoop.mpns.exceptions.NetworkIOException;

public abstract class AbstractMpnsService implements MpnsService {

    protected final HttpClient httpClient;

    protected AbstractMpnsService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    protected HttpPost postMessage(String subscriptionUri, String requestBody, Collection<? extends Entry<String, String>> headers) {
        HttpPost method = new HttpPost(subscriptionUri);
        try {
            method.setEntity(new StringEntity(requestBody,
                    "text/xml", HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 isn't supported!!!");
        }

        for (Entry<String, String> header: headers) {
            method.addHeader(header.getKey(), header.getValue());
        }

        return method;
    }

    protected abstract void push(HttpPost request);

    public void push(String subscriptionUri, String payload,
            Collection<? extends Entry<String, String>> headers)
            throws NetworkIOException {
        this.push(postMessage(subscriptionUri, payload, headers));
    }

    public void push(String subscriptionUri, MpnsNotification message)
            throws NetworkIOException {
        this.push(postMessage(subscriptionUri, message.getRequestBody(),
                message.getHttpHeaders()));
    }

    public void start() {}

    public void stop() {
        this.httpClient.getConnectionManager().shutdown();
    }
}
