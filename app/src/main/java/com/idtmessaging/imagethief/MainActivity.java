package com.idtmessaging.imagethief;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.idtmessaging.imagethief.reactive.Updatable;
import com.idtmessaging.imagethief.util.ImageModel;
import com.idtmessaging.imagethief.util.ImageMutableRepository;
import com.idtmessaging.imagethief.util.Util;


public class MainActivity extends AppCompatActivity implements Updatable, View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 14;
    EditText mEditText;
    Button mButton;
    ImageView mImageView;

    private boolean mIsPause;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = (EditText) findViewById(R.id.et_url);
        mButton = (Button) findViewById(R.id.btn_download);
        mButton.setOnClickListener(this);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mImageView.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ImageMutableRepository.getInstance().addUpdatable(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsPause = false;

        Bitmap bitmap = ((ImageThiefApp) getApplication()).getBitmapFromMemCache(mEditText.getText().toString());

        if (bitmap != null) {
            mImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPause = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        ImageMutableRepository.getInstance().removeUpdatable(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageMutableRepository.getInstance().removeUpdatable(this);
    }

    @Override
    public void update() {
        final ImageModel imageModel = ImageMutableRepository.getInstance().get();
        if (imageModel.isSuccess() && imageModel.getBitmap() != null) {

            ((ImageThiefApp) getApplication()).addBitmapToMemoryCache(imageModel.getUrl(), imageModel.getBitmap());

            if (mIsPause) {
                return;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageBitmap(imageModel.getBitmap());
                    mImageView.setTag(imageModel.getName());
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_download) {
            String url = mEditText.getText().toString();
            if (url.equals("")) {
                url = "http://www.bensound.com/bensound-img/november.jpg";
//                mEditText.setError(getString(R.string.invalid_url_error));
            }
            Bitmap bitmap = ((ImageThiefApp) getApplication()).getBitmapFromMemCache(url);
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            } else {
                ImageThiefService.startDownload(this, url);
            }

        } else if (id == R.id.iv_image) {
            openImage(view);
        }
    }

    private void openImage(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }

        String uri = (String) view.getTag();
        if (uri != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Log.d(TAG, "image uri:" + uri);
            intent.setDataAndType(Util.getImageContentUri(getBaseContext(), uri), "image/*");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    openImage(mImageView);

                } else {

                    // permission denied, boo!
                }
            }

        }
    }

}
