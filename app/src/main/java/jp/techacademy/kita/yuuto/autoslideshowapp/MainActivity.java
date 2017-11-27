package jp.techacademy.kita.yuuto.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Timer mTimer = null;
    ImageView mTimerImage;
    Handler mHandler = new Handler();
    Cursor cursor;

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

        private void showImage() {
            //カーソルの指している画像をimageViewに設定するというロジック
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.Image_View);
            imageVIew.setImageURI(imageUri);
        }

    Button mGoButton;
    Button mBackButton;
    Button mStartButton;

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null
        ); // ソート (null ソートなし)

        mTimerImage = (ImageView) findViewById(R.id.Image_View);
        mGoButton = (Button) findViewById(R.id.go_button);
        mBackButton = (Button) findViewById(R.id.back_button);
        mStartButton = (Button) findViewById(R.id.start_button);

        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.this.cursor.moveToNext()) {
                    MainActivity.this.cursor.moveToFirst();
                }
                showImage();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.this.cursor.moveToPrevious()) {
                    MainActivity.this.cursor.moveToLast();
                }
                showImage();
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showImage();
                                }
                            });
                        }
                    }, 2000, 2000);
            }
        });
    }
}
