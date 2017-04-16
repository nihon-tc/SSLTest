package nihon_tc.com.ssltest.application;

import android.content.res.AssetManager;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import nihon_tc.com.ssltest.util.RestUtil;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;

/**
 * Created by kimura on 2017/04/12.
 */
public class DebugHogeApplication extends DebugHogeDBApplication {

    private static final String TAG = DebugHogeApplication.class.getSimpleName();

    //MockServerを使うか？
    private static boolean useMockServer = true;
    //MockServerでSSLを使うか？
    private boolean useHttps = true;

    private MockWebServer server;

    private static DebugHogeApplication instance;

    public static DebugHogeApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (!useMockServer) {
            return;
        }

        //StricModeでNW通信を許可する◎
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectNetwork()
                .build());

        initServer();

        //StricModeを元に戻す
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .build());
    }

    @Override
    public void finalize() {
        super.onCreate();
        shutdown();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        shutdown();
    }

    private void shutdown() {
        if (server != null) {
            try {
                server.shutdown();
            } catch (Exception e) {
            }
        }
    }


    private void initServer() {
        server = new MockWebServer();

        //SSLの初期化設定
        if (useHttps) {  //★ 今回追加
            useHttps = setUseHttps();
            if (useHttps) {
                RestUtil.BASE_SCHEME = RestUtil.SCHEME_HTTPS;
            }
        }

        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(final RecordedRequest request) throws InterruptedException {
                if (request == null || request.getPath() == null) {
                    return new MockResponse().setResponseCode(400);
                }

                MockResponse res = new MockResponse()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("charset", "utf-8")
                        .setResponseCode(200);

                MockResponse resB = new MockResponse()
                        .addHeader("Content-Type", "image/png")
                        .setResponseCode(200);

                String path = request.getPath();

                if (path.indexOf("/hoge") != -1) {
                    return res.setBody(readJson("hoge.json"));
                }
                if (path.indexOf("/fuga") != -1) {
                    return res.setBody(readJson("fuga.json"));
                }
                if (path.indexOf("/maiu") != -1) {
                    return resB.setBody(readBinary("sample.png"));
                }

                return new MockResponse().setResponseCode(404);
            }
        };

        server.setDispatcher(dispatcher);

        try {
            server.start();

            if (!useHttps) { //★ 今回追加
                RestUtil.BASE_SCHEME = RestUtil.SCHEME_HTTP;
            }
            RestUtil.BASE_HOST = server.getHostName();
            RestUtil.BASE_PORT = server.getPort();
        } catch (Exception ex) {
            shutdown();
            server = null;
        }
    }

    private String readJson(String filename) {
        InputStream is = null;
        BufferedReader br = null;
        String json = "";

        try {
            AssetManager as = getApplicationContext().getResources().getAssets();
            is = as.open(filename);
            br = new BufferedReader(new InputStreamReader(is));

            // １行ずつ読み込み、改行を付加する
            String str;
            while ((str = br.readLine()) != null) {
                json += str + "\n";
            }
        } catch (Exception e) {
            return "";
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return json;
    }

    private Buffer readBinary(String filename) {
        InputStream is = null;
        BufferedInputStream br = null;
        Buffer buffer = new Buffer();

        try {
            AssetManager as = getApplicationContext().getResources().getAssets();
            is = as.open(filename);
            br = new BufferedInputStream(is);

            byte[] data = new byte[1024];
            int count = 0;
            while ((count = br.read(data)) != -1) {
                buffer.write(data, 0, count);
            }
            buffer.flush();
        } catch (Exception e) {
            return buffer;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return buffer;
    }

    //see setUseHttpsWithDER()
    private boolean setUseHttps() {
        // [TODO] 実際の試行状態 初期版
        return setUseHttpsWithBKS();
        // [TODO] 2/22追加検証版
        //return setUseHttpsWithDER();
    }

    private boolean setUseHttpsWithBKS() {
        try {
            AssetManager assetManager = getApplicationContext().getResources().getAssets();

            char[] serverKeyStorePassword = "android".toCharArray();

            InputStream stream = assetManager.open("mystore.bks");
            KeyStore serverKeyStore = KeyStore.getInstance("BKS");
            serverKeyStore.load(stream, serverKeyStorePassword);


            String kmfAlgoritm = KeyManagerFactory.getDefaultAlgorithm();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfAlgoritm);
            kmf.init(serverKeyStore, serverKeyStorePassword);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(kmfAlgoritm);
            trustManagerFactory.init(serverKeyStore);


            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            SSLSocketFactory sf = sslContext.getSocketFactory();
            server.useHttps(sf, false);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "setUseHttps", e);
        }

        return false;
    }

    private boolean setUseHttpsWithDER() {
        try {
            AssetManager assetManager = getApplicationContext().getResources().getAssets();

            char[] serverKeyStorePassword = "android".toCharArray();
/*
            InputStream stream = assetManager.open("mystore.bks");
            KeyStore serverKeyStore = KeyStore.getInstance("BKS");
            serverKeyStore.load(stream, serverKeyStorePassword);
*/
            //変更箇所
            InputStream stream = assetManager.open("server.der.crt");

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            InputStream caInput = new BufferedInputStream(stream);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore serverKeyStore = KeyStore.getInstance(keyStoreType);
            serverKeyStore.load(null, null);
            serverKeyStore.setCertificateEntry("ca", ca);

            String kmfAlgoritm = KeyManagerFactory.getDefaultAlgorithm();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfAlgoritm);
            kmf.init(serverKeyStore, serverKeyStorePassword);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(kmfAlgoritm);
            trustManagerFactory.init(serverKeyStore);


            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            SSLSocketFactory sf = sslContext.getSocketFactory();
            server.useHttps(sf, false);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "setUseHttps", e);
        }

        return false;
    }

}