package nihon_tc.com.ssltest.net;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import nihon_tc.com.ssltest.R;
import okhttp3.*;

import java.io.UnsupportedEncodingException;

/**
 * Created by kimura on 2017/04/15.
 */
public class RestService {
    private  Context mContext;

    public RestService(Context context) {
        mContext = context;
    }

    /**
     * 通信環境を確認してGET通信を行う
     * @param callback
     * @param headers
     * @param httpUrl
     */
    public Response get(final HttpUrl httpUrl, final Headers headers, final AsyncOkHttpClient.Callback callback){
        if(mContext instanceof Activity) {
            if (((Activity)mContext).isFinishing()) {
                return null;
            }
        }

        //同期通信のコード
        if(callback == null){
            if(checkNetWorkConnected(null)){
                return  AsyncOkHttpClient.get(httpUrl, headers, null);
            }
            return null;
        }

        //通信環境のチェック
        checkNetWorkConnected(new OnAccessNetworkStateCallback() {
            @Override
            public void onAccessNetworkState() {
                //通信開始
                AsyncOkHttpClient.get(httpUrl, headers, callback);
            }

            @Override
            public void onCancelNetworkState() {
                callback.onRetryCancel();
            }
        });
        return null;
    }

    /**
     * 通信環境を確認してPOST通信を行う
     * @param httpUrl
     * @param requestBody
     * @param callback
     */
    public void post(final HttpUrl httpUrl, final RequestBody requestBody, final AsyncOkHttpClient.Callback callback){
        post(httpUrl, null, requestBody, callback);
    }

    /**
     * 通信環境を確認してPOST通信を行う
     * @param httpUrl
     * @param headers
     * @param requestBody
     * @param callback
     */
    public void post(final HttpUrl httpUrl, final Headers headers, final RequestBody requestBody, final AsyncOkHttpClient.Callback callback){
        if(mContext instanceof Activity) {
            if (((Activity)mContext).isFinishing()) {
                return;
            }
        }

        //通信環境のチェック
        checkNetWorkConnected(new OnAccessNetworkStateCallback() {
            @Override
            public void onAccessNetworkState() {
                //通信開始
                AsyncOkHttpClient.post(httpUrl, headers, requestBody, callback);
            }

            @Override
            public void onCancelNetworkState() {
                callback.onRetryCancel();
            }
        });
    }

    /**
     * 通信環境を確認してPOST通信を行う
     * @param httpUrl
     * @param json
     * @param callback
     */
    public void post(final HttpUrl httpUrl, final String json, final AsyncOkHttpClient.Callback callback){
        post(httpUrl, null, json, callback);

    }
    /**
     * 通信環境を確認してPOST通信を行う
     * @param httpUrl
     * @param headers
     * @param json
     * @param callback
     */
    public void post(final HttpUrl httpUrl, final Headers headers, final String json, final AsyncOkHttpClient.Callback callback){
        if(mContext instanceof Activity) {
            if (((Activity)mContext).isFinishing()) {
                return;
            }
        }

        //通信環境のチェック
        checkNetWorkConnected(new OnAccessNetworkStateCallback() {
            @Override
            public void onAccessNetworkState() {
                //jsonがあればボディーを作成
                RequestBody requestBody = null;
                if (!TextUtils.isEmpty(json)) {
                    // リクエストボディの作成
                    try {
                        final String s = new String(json.getBytes(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e("utf8", "conversion", e);
                    }
                    requestBody = RequestBody.create(MediaType.parse("application/json"), json);
                }

                //通信開始
                AsyncOkHttpClient.post(httpUrl, headers, requestBody, callback);
            }

            @Override
            public void onCancelNetworkState() {
                callback.onRetryCancel();
            }
        });

    }

    /**
     * 通信環境の確認を行う
     * @param onAccessNetworkStateCallback
     */
    public boolean checkNetWorkConnected(final OnAccessNetworkStateCallback onAccessNetworkStateCallback){
        if(isNetWorkConnected(mContext)){
            if(onAccessNetworkStateCallback != null){
                onAccessNetworkStateCallback.onAccessNetworkState();
            }
            return true;
        }

        if(onAccessNetworkStateCallback == null){
            return false;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.Base_Theme_AppCompat_Light_Dialog_Alert)
                .setCancelable(false)
                .setTitle("確認")
                .setMessage("通信に失敗しました。再度通信しますか？")
                .setPositiveButton("通信する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkNetWorkConnected(onAccessNetworkStateCallback);//再帰呼び出し
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(onAccessNetworkStateCallback != null){
                            onAccessNetworkStateCallback.onCancelNetworkState();
                        }
                    }
                });
        builder.show();
        return false;
    }

    /**
     * ネットワークが接続されているかどうかを返す
     * @param context
     * @return
     */
    public boolean isNetWorkConnected(Context context){
        ConnectivityManager cm =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if( info != null ){
            return info.isConnected();
        }
        return false;
    }

    /**
     * 通信環境がOKなときに呼ばれるイベントリスナー
     */
    public static interface OnAccessNetworkStateCallback {
        void onAccessNetworkState();
        void onCancelNetworkState();
    }
}
