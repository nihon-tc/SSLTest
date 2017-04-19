package nihon_tc.com.ssltest.application;

import android.content.Context;
import com.android.annotations.NonNull;
import io.realm.*;
import net.cattaka.TelnetSqliteService;
import nihon_tc.com.ssltest.constant.Constants;
import nihon_tc.com.ssltest.util.RealmUtils;

/**
 * Created by kimura on 2017/04/12.
 */
public class DebugHogeDBApplication extends HogeApplication {

    private static final String TAG = DebugHogeDBApplication.class.getSimpleName();
    private static boolean useTelnetSqlite = false;

    private static DebugHogeDBApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if(!useTelnetSqlite){
            return;
        }

        /*
           see http://qiita.com/cattaka/items/b10ef658b03cfa489576

           adb forward tcp:12080 tcp:12080
           access RdbAssistant
               ラベル      :hoge.db (任意)
               ホスト名    :localhost
               port       :12080
               データベース :hoge.db (SQLiteで作成したDB名)
        */
        // SQLiteにアクセするためのサーバーを起動する
        try {
            TelnetSqliteService.createTelnetSqliteServer(getApplicationContext(),12080).start();
        } catch (Exception e) {
        }
    }

    @Override
    protected void initDBConfig() {
        //super.initDBConfig();
        setDefaultRealmConfigurationRemote(getApplicationContext(), Constants.REALM_MASTER, Constants.REALM_VERSION);
    }


    //◎HogeApplicationのinitConfigを上書き
	public static void setDefaultRealmConfigurationRemote(final Context context, final @NonNull String filename, final int schemaVersion) {
        Realm.init(context);

        SyncCredentials credentials = SyncCredentials.usernamePassword(
                "hoge@fuga.jp", "hogehoge", false); //△本当に適当なアカウントでOK
        SyncUser.loginAsync(credentials, "http://localhost:9080/auth", new SyncUser.Callback() {

            @Override
            public void onSuccess(SyncUser user) {
                RealmConfiguration config = new SyncConfiguration.Builder(user, "realm://localhost:9080/~/debug").build();
                Realm.setDefaultConfiguration(config);
            }

            @Override
            public void onError(ObjectServerError objectServerError) {
                //★ここに通常接続時の記述を書いておくとベター
                RealmUtils.setDefaultRealmConfiguration(context,filename,schemaVersion);
            }
        });
    }
}
