package nihon_tc.com.ssltest.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collections;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

/**
 * Created by kimura on 2017/04/20.
 */

public class DebugTLSALL_OkHttpUtil extends OkHttpUtil {
    private static DebugTLSALL_OkHttpUtil instance;

    public static OkHttpUtil getInstance() {
        if(instance == null){
            instance = new DebugTLSALL_OkHttpUtil();
        }
        return instance;
    }


    @Override
    public OkHttpClient getOkhttpClient() {
//        if(client != null){
//            return client;
//        }

        OkHttpClient.Builder builder = getInstance().getBuilder();

        client  = builder.build();

        return client;
    }


    @NonNull
    @Override
    public OkHttpClient.Builder getBuilder() {
        Log.d(TAG,"getBuilder");
        OkHttpClient.Builder builder =  super.getBuilder();

        //== TODO ConnectionSpec Setting ====
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0, TlsVersion.SSL_3_0)
                .build();
        builder.connectionSpecs(Collections.singletonList(spec));

        //to disable TLS fallback:
//        builder.connectionSpecs(Arrays.asList(
//                ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT));

        //To disable cleartext connections, permitting https URLs only:
//        builder.connectionSpecs(Arrays.asList(
//                ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS));

        return builder;
    }
}
