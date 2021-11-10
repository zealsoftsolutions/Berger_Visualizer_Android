package com.berger.bergerXpressVisualiserPk.restApis;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {

    private final int mRetryTimes;

    public RetryInterceptor(int retryTimes) {
        mRetryTimes = retryTimes;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        int retryTimesLeft = mRetryTimes;
        for (; retryTimesLeft > 0; --retryTimesLeft) {
            try {
                return chain.proceed(chain.request());
            } catch (IOException ignore) {
            }
        }
        throw new IOException("still fail after retry " + mRetryTimes + " times");
    }
}
