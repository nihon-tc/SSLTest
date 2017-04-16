package nihon_tc.com.ssltest.util;

import android.content.Context;
import nihon_tc.com.ssltest.application.HogeApplication;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by kimura on 2017/04/15.
 */
public class OkHttpUtil {
    private static OkHttpClient client;

    public static OkHttpClient getOkhttpClient() {
        if(client != null){
            return client;
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        //キャッシュコントロールとか
        long currentTimeMillis = System.currentTimeMillis();
        currentTimeMillis -= 3 * 60 * 60 *1000;//3時間前のデータを消す


        Context context = HogeApplication.getInstance().getApplicationContext();

        int MAX_CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB
        File cachedFile = new File(context.getCacheDir(), "responses");
        if(cachedFile.exists() && cachedFile.lastModified() < currentTimeMillis){
            cachedFile.delete();
        }

        Cache cache = new Cache(cachedFile, MAX_CACHE_SIZE);
        builder.cache(cache);

        client  = builder.build();
        return client;
    }
}
