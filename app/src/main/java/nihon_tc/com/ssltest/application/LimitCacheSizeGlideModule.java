package nihon_tc.com.ssltest.application;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import nihon_tc.com.ssltest.util.OkHttpUtil;

import java.io.InputStream;

/**
 * Created by kimura on 2017/04/17.
 */
public class LimitCacheSizeGlideModule implements GlideModule {
    private final String TAG = LimitCacheSizeGlideModule.class.getSimpleName();

    // Modern device should have 8GB (=7.45GiB) or more!
    private static final int SMALL_INTERNAL_STORAGE_THRESHOLD_GIB = 6;
    private static final int DISK_CACHE_SIZE_FOR_SMALL_INTERNAL_STORAGE_MIB = 50;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        double totalGiB = getTotalBytesOfInternalStorage() / 1024.0 / 1024.0 / 1024.0;
        Log.d(TAG,String.format("Internal Storage Size: %.1fGiB", totalGiB));
        if (totalGiB < SMALL_INTERNAL_STORAGE_THRESHOLD_GIB) {
            Log.d(TAG,"Limiting image cache size to " + DISK_CACHE_SIZE_FOR_SMALL_INTERNAL_STORAGE_MIB + "MiB");
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, DISK_CACHE_SIZE_FOR_SMALL_INTERNAL_STORAGE_MIB));
        }
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(OkHttpUtil.getOkhttpClient()));
        glide.register(InputStream.class, InputStream.class,
                new PassthroughStreamLoader.Factory());
    }

    private long getTotalBytesOfInternalStorage() {
        // http://stackoverflow.com/a/4595449/1474113
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return getTotalBytesOfInternalStorageWithStatFs(stat);
        }
        return getTotalBytesOfInternalStorageWithStatFsPreJBMR2(stat);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private long getTotalBytesOfInternalStorageWithStatFs(StatFs stat) {
        return stat.getTotalBytes();
    }

    @SuppressWarnings("deprecation")
    private long getTotalBytesOfInternalStorageWithStatFsPreJBMR2(StatFs stat) {
        return (long) stat.getBlockSize() * stat.getBlockCount();
    }
}