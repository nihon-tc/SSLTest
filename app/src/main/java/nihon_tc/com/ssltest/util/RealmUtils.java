package nihon_tc.com.ssltest.util;

import android.content.Context;
import android.support.annotation.NonNull;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmModel;

/**
 * Created by kimura on 2017/04/20.
 */
public class RealmUtils {
    private static String TAG = RealmUtils.class.getSimpleName();

    /**
     * RealmDB インスタンスを取得
     * マイグレーションなし
     * @return
     */
    public static synchronized Realm getInstance() {
        Realm realm = Realm.getDefaultInstance();
        return realm;
    }

    /**
     * Applicationの全体共通設定
     * 使うときはRealm.getDefaultInstance()で使うべき
     *
     * @param context
     * @param filename
     * @param schemaVersion
     */
    public static void setDefaultRealmConfiguration(Context context, @NonNull String filename, int schemaVersion) {
        setDefaultRealmConfiguration(context,filename,schemaVersion,null);
    }



    public static void setDefaultRealmConfiguration(Context context, @NonNull String filename, int schemaVersion, RealmMigration migration) {
        Realm.init(context);
        RealmConfiguration config = getRealmConfiguration(filename, schemaVersion,migration);
        Realm.setDefaultConfiguration(config);
    }

    //[TODO] aarのclass.jar に io.realmパッケージ追加＋RealmModelの変換済みのクラスが同梱される
//    @RealmModule(library = true, allClasses = true)
//    public static class MyLibraryModule {
//    }

    private static RealmConfiguration getRealmConfiguration(@NonNull String filename, int schemaVersion, RealmMigration migration) {
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder()//context)
                .name(filename)
                //.modules(Realm.getDefaultModule(), new MyLibraryModule()) //★ ココを変更
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(schemaVersion);

        if(migration != null){
            builder.migration(migration);
        }

        return builder.build();
    }


    public static class AutoIncrement {
        public long newId(Realm realm, Class<? extends RealmModel> clazz) {
            return newIdWithIdName(realm, clazz, "id");
        }

        public long newIdWithIdName(Realm realm, Class<? extends RealmModel> clazz, String idName) {
            Number num = realm.where(clazz).max(idName);
            return num == null ? 1 : num.longValue() + 1;
        }
    }
}
