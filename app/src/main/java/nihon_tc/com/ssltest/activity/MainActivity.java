package nihon_tc.com.ssltest.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.bumptech.glide.Glide;
import nihon_tc.com.ssltest.R;
import nihon_tc.com.ssltest.databinding.ActivityMainBinding;
import nihon_tc.com.ssltest.net.AsyncOkHttpClient.Callback;
import nihon_tc.com.ssltest.util.RestUtil;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        clickAction();
/*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showImage();
            }
        },3000);
*/
    }

    private void clickAction() {
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

    private void showImage() {
        RestUtil.getApiMaiu(this, new Callback() {
            @Override
            public void onFailure(Response response, Throwable throwable) {
                Log.e(TAG,"onFailure",throwable);
                binding.text.setText(throwable.getMessage());
            }

            @Override
            public void onSuccess(Response response, String content) {
                //通信が成功しない場合は弾く
                if(!response.isSuccessful()){
                    Log.e(TAG,"onSuccess:" + response.isSuccessful());
                    return;
                }


                InputStream is = null;
                try {
                    //byte[] data= getImage(response);
//                    byte[] data= content.getBytes();
//                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                    Glide.with(MainActivity.this).load(bmp).into(binding.image);
                    Glide.with(MainActivity.this).load(response.body().byteStream()).into(binding.image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private byte[] getImage(Response response){

                //通信が成功しない場合は弾く
                if(!response.isSuccessful()){
                    return null;
                }


                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                InputStream is = null;
                try {
                    is = response.body().byteStream();

                    int nRead = 0;
                    byte[] data = new byte[1024];

                    while ((nRead = is.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                } catch (Exception e) {
                    Log.e(TAG,"getImage",e);
                    return null;
                }
                finally{
                    if(is!= null){
                        try {
                            is.close();
                            response.body().close();
                        } catch (Exception e) {
                        }
                    }
                }
                return buffer.toByteArray();
            }

            @Override
            public void onRetryCancel() {

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
