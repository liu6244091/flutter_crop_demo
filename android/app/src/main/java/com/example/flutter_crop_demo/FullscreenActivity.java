package com.example.flutter_crop_demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.flutter_crop_demo.thread.ThreadPoolManager;
import com.example.flutter_crop_demo.utils.ImageHelper;

public class FullscreenActivity extends Activity{

    //原图
    private ImageView originIv;

    //黑白框图
    private ImageView maskIv;

    //裁剪图
    private ImageView cropIv;

    //原图，遮罩图
    private String originImg;
    private String maskImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        initView();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        //long time = System.currentTimeMillis();
        Intent intent = getIntent();
        originImg = intent.getStringExtra("origin");
        maskImg = intent.getStringExtra("frame");
        log("FullscreenActivity origin:" + originImg);
        log("FullscreenActivity mask:" + maskImg);

        //加载图片
        loadImage();


        //long useTime = System.currentTimeMillis() - time;
        //log("耗时: "+ useTime);
    }

    private void initView(){
        originIv = findViewById(R.id.originIv);
        maskIv = findViewById(R.id.frameIv);
        cropIv = findViewById(R.id.cropIv);
    }

    /**
     * 加载图片
     */
    private void loadImage(){
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                //获取原图
                final Bitmap originBitmap = ImageHelper.getImageFromAssets(FullscreenActivity.this, "flutter_assets/"+ originImg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        originIv.setImageBitmap(originBitmap);
                    }
                });

                //获取遮罩图
                final Bitmap maskBitmap = ImageHelper.getImageFromAssets(FullscreenActivity.this, "flutter_assets/"+ maskImg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        maskIv.setImageBitmap(maskBitmap);
                    }
                });


                //获取裁剪图
                Bitmap cropBitmap = ImageHelper.getCropBitmap(originBitmap, maskBitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cropIv.setImageBitmap(cropBitmap);
                    }
                });

                //保存裁剪图
                String cropFile = ImageHelper.saveBitmap(FullscreenActivity.this, cropBitmap);
                log("cropFile: "+ cropFile);
            }
        });
    }


    private void log(String msg){
        Log.d("FullscreenActivity---", msg);
    }

    @Override
    protected void onDestroy() {
        releaseImageBitmap(originIv);
        releaseImageBitmap(maskIv);
        releaseImageBitmap(cropIv);
        super.onDestroy();
    }

    /**
     * 释放imageView的图片资源
     * @param imageView
     */
    private void releaseImageBitmap(ImageView imageView){
        try{
            Drawable drawable = imageView.getDrawable();
            if(null == drawable){
                return;
            }
            if(drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                imageView.setImageBitmap(null);
                bitmap.recycle();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}