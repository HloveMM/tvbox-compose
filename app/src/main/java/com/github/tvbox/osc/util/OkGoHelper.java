package com.github.tvbox.osc.util;

import java.net.ProxySelector;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.dnsoverhttps.DnsOverHttps;

public class OkGoHelper {
    public static DnsOverHttps dnsOverHttps = null;
    private static OkHttpClient client;

    public static synchronized OkHttpClient getDefaultClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }

    public static synchronized OkHttpClient getNoRedirectClient() {
        return getDefaultClient().newBuilder().followRedirects(false).followSslRedirects(false).build();
    }

    public static ProxySelector proxySelector() {
        return ProxySelector.getDefault();
    }

    public static Authenticator proxyAuthenticator() {
        return null;
    }
}
