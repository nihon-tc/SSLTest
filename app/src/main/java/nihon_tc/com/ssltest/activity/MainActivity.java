package nihon_tc.com.ssltest.activity;

import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import nihon_tc.com.ssltest.R;
import nihon_tc.com.ssltest.databinding.ActivityMainBinding;
import nihon_tc.com.ssltest.net.AsyncOkHttpClient;
import nihon_tc.com.ssltest.net.AsyncOkHttpClient.Callback;
import nihon_tc.com.ssltest.util.RestUtil;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                RestUtil.getApiHoge(v.getContext(), new Callback() {
                    @Override
                    public void onFailure(Response response, Throwable throwable) {
                        hideProgress();
                        Log.e(TAG,"onFailure",throwable);
                        binding.text.setText(throwable.getMessage());
                    }

                    @Override
                    public void onSuccess(final Response response, final String content) {
                        hideProgress();
                        binding.text.setText(content);
                    }

                    @Override
                    public void onRetryCancel() {
                        hideProgress();
                    }
                });
            }
        });
    }

    private void hideProgress() {
        binding.progress.progressiveStop();
        binding.progress.setVisibility(View.INVISIBLE);
    }

    private void showProgress() {
        binding.progress.progressiveStart();
        binding.progress.setVisibility(View.VISIBLE);
    }
}
