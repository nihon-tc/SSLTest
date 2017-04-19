package nihon_tc.com.ssltest.net;

import android.os.Handler;
import android.os.Looper;

import nihon_tc.com.ssltest.application.HogeApplication;
import nihon_tc.com.ssltest.util.OkHttpUtil;
import okhttp3.*;

import java.io.IOException;

public class AsyncOkHttpClient {

    private AsyncOkHttpClient() {
    }

    private static OkHttpClient getOkHttpClient(){
        return HogeApplication.getInstance().getOkhttpClient();
    }

    public static Response get(HttpUrl httpUrl,Callback callback) {
        return get(httpUrl, null, callback);
    }

    public static Response get(HttpUrl httpUrl, Headers headers, Callback callback) {
        Request.Builder builder = new Request.Builder();

        //[TODO]StackOverFlowに載ってた tag付け。tag経由でキャンセルできる？
        String pathSegments = "";
        for (String s : httpUrl.encodedPathSegments()) {
            pathSegments += s;
        }
        builder.tag(pathSegments);

        builder.url(httpUrl.url()).get();
        if(headers != null){
            builder.headers(headers);
        }

        return execute(builder.build(), callback);
    }

    // tag経由で実行キャンセルする処理
    public static void cancel(final String tag){
        for(Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            if(call.request().tag().equals(tag))
                call.cancel();
        }
        for(Call call : getOkHttpClient().dispatcher().runningCalls()) {
            if(call.request().tag().equals(tag))
                call.cancel();
        }
    }
    public static void post(HttpUrl httpUrl, Headers headers, RequestBody requestBody, final Callback callback){
        Request.Builder builder = new Request.Builder();

        builder.url(httpUrl.url());
        builder.headers(headers);
        builder.post(requestBody);

        execute(builder.build(), callback);
    }

    private static Response execute(final Request request, final Callback callback) {

        getOkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, final IOException e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(null, e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String content = response.body().string();

                boolean isSuccessful = response.isSuccessful();
                if (isSuccessful) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onSuccess(response, content);
                            }
                        }
                    });
                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onFailure(response, null);
                            }
                        }
                    });
                }
            }
        });
        return null;
    }

    public interface Callback {
        public void onFailure(Response response, Throwable throwable);
        public void onSuccess(Response response, String content);
        public void onRetryCancel();
    }
}

