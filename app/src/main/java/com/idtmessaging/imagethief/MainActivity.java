package com.idtmessaging.imagethief;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.idtmessaging.imagethief.reactive.Updatable;
import com.idtmessaging.imagethief.util.ImageModel;
import com.idtmessaging.imagethief.util.ImageMutableRepository;


public class MainActivity extends AppCompatActivity implements Updatable, View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 14;
    private static final String DEFAULT_URL = "http://www.freedigitalphotos.net/images/img/homepage/87357.jpg";
    EditText mEditText;
    Button mButton;
    ImageView mImageView;

    private boolean mIsPause;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = (EditText) findViewById(R.id.et_url);
        mEditText.setHint(DEFAULT_URL);
        mButton = (Button) findViewById(R.id.btn_download);
        mButton.setOnClickListener(this);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mImageView.setOnClickListener(this);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    downloadImage();
                }
                return false;
            }
        });

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

        String url = mEditText.getText().toString();
        if(url.equals("")){
            url = DEFAULT_URL;
        }

        ImageModel imageModel = new ImageModel(getApplicationContext(), url);

        if (imageModel.getBitmap() != null) {
            mImageView.setImageBitmap(imageModel.getBitmap());
            mImageView.setTag(imageModel.getUri());
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

            if (mIsPause) {
                return;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageBitmap(imageModel.getBitmap());
                    mImageView.setTag(imageModel.getUri());
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_download) {
            downloadImage();
        } else if (id == R.id.iv_image) {
            openImage(view);
        }
    }

    private void downloadImage(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }

        String url = mEditText.getText().toString();
        if (url.equals("")) {
            url = DEFAULT_URL;
        }

        ImageModel imageModel = new ImageModel(getApplicationContext(),url);
        if (imageModel.getBitmap() != null) {
            mImageView.setImageBitmap(imageModel.getBitmap());
            mImageView.setTag(imageModel.getUri());
        } else {
            ImageThiefService.startDownload(this, url);
        }
    }

    private void openImage(View view) {
        Uri uri = (Uri) view.getTag();
        if (uri != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Log.d(TAG, "image uri:" + uri);
            intent.setDataAndType(uri, "image/*");

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
                    downloadImage();

                } else {
                    // permission denied, boo!
                }
            }

        }
    }

}
