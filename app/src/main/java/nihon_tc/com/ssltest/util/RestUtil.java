package nihon_tc.com.ssltest.util;

import android.content.Context;
import nihon_tc.com.ssltest.net.AsyncOkHttpClient;
import nihon_tc.com.ssltest.net.RestService;
import okhttp3.HttpUrl;

/**
 * Created by kimura on 2017/04/15.
 */
public class RestUtil {
    //URLのSCHEMEの定数
    public static String SCHEME_HTTPS = "https";
    public static String SCHEME_HTTP  = "http";
    public static int BASE_PORT_HTTPS = 443;
    public static int BASE_PORT_HTTP = 80;

    public static String BASE_SCHEME = SCHEME_HTTP;
    public static String BASE_HOST = "www.test.jp";
    public static int BASE_PORT = BASE_PORT_HTTP;

    //カスタムURL用
    public static HttpUrl.Builder createBaseUrl(){

        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme(BASE_SCHEME)
                .host(BASE_HOST)
                .port(BASE_PORT)
                .addPathSegment("api")
                .addPathSegment("v1")
                .addQueryParameter("sensor","false");

        return builder;
    }

    public static void getApiHoge(Context context, AsyncOkHttpClient.Callback callback){
        HttpUrl.Builder builder = createBaseUrl();
        builder.addPathSegment("hoge");

        new RestService(context).get(builder.build(), null, callback);
    }

    public static void getApiFuga(Context context, AsyncOkHttpClient.Callback callback){
        HttpUrl.Builder builder = createBaseUrl();
        builder.addPathSegment("fuga");
        //builder.addQueryParameter("address","address");

        new RestService(context).get(builder.build(), null, callback);
    }

    public static void getApiMaiu(Context context, AsyncOkHttpClient.Callback callback){
        HttpUrl.Builder builder = createBaseUrl();
        builder.addPathSegment("maiu");

        new RestService(context).get(builder.build(), null, callback);
    }

}
