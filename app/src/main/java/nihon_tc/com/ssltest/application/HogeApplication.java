package nihon_tc.com.ssltest.application;

import android.app.Application;
import nihon_tc.com.ssltest.constant.Constants;
import nihon_tc.com.ssltest.util.RealmUtils;

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
        initDBConfig();
    }

    protected void initDBConfig() {
        RealmUtils.setDefaultRealmConfiguration(getApplicationContext(), Constants.REALM_MASTER, Constants.REALM_VERSION);
    }
}
