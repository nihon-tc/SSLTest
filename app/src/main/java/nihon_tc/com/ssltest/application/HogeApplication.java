package nihon_tc.com.ssltest.application;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by kimura on 2017/04/12.
 */
public class HogeApplication extends Application{

    private static HogeApplication instance;

    public static HogeApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //追記したいDebug記述
        initConfig();
    }

    protected void initConfig() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
    }
}
