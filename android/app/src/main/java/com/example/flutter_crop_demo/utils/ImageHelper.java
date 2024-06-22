package com.example.flutter_crop_demo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

import io.flutter.util.PathUtils;

/**
 * 图片工具类
 */
public class ImageHelper {


    /**
     * 根据原图以及遮罩图获得裁剪图
     * @param originAssetsFile
     * @param maskAssetsFile
     * @return
     */
    public static String getCropImageFile(Context context, String originAssetsFile, String maskAssetsFile){
        //flutter_assets
        //获取原图，遮罩图bitmap
        Bitmap originBitmap = ImageHelper.getImageFromAssets(context, "flutter_assets/"+ originAssetsFile);
        Bitmap maskBitmap = ImageHelper.getImageFromAssets(context, "flutter_assets/"+ maskAssetsFile);

        //根据原图，遮罩图获得裁剪图
        Bitmap cropBitmap = ImageHelper.getCropBitmap(originBitmap, maskBitmap);
        String cropFile = ImageHelper.saveBitmap(context, cropBitmap);

        //返回路径
        return cropFile;
    }


    /**
     * 从assets目录读取Bitmap文件
     * @param context
     * @param path
     * @return
     */
    public static Bitmap getImageFromAssets(Context context, String path) {
        Bitmap image = null;
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(path);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 从文件里面读取bitmap
     * @param file
     * @return
     */
    public static Bitmap getBitmapFromFile(String file){
        return BitmapFactory.decodeFile(file);
    }

    /**
     * 获取裁剪图
     * @return
     */
    public static Bitmap getCropBitmap(Bitmap originBitmap, Bitmap maskBitmap){
        int[] originPixs = getBitmapPixels(originBitmap);
        int[] maskPixs = getBitmapPixels(maskBitmap);
        log("origin width:"+ originBitmap.getWidth() + " height:"+originBitmap.getHeight());
        log("mask width:"+ maskBitmap.getWidth() + " height:"+maskBitmap.getHeight());
        log("mask pixels： "+ String.format("%X", maskPixs[0]));

        for(int i=0; i<maskPixs.length; i++){
            if(maskPixs[i] == 0xFF000000){
                originPixs[i] = 0x00000000;
            }
        }

        Bitmap cropBitmap = Bitmap.createBitmap(originBitmap.getWidth(), originBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(cropBitmap);
        // 使用透明颜色
        canvas.drawARGB(0, 0, 0, 0);
        cropBitmap.setPixels(originPixs, 0, originBitmap.getWidth(), 0, 0, originBitmap.getWidth(), originBitmap.getHeight());

        return cropBitmap;
    }

    /**
     * 保存图片
     * @param context
     * @param bitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap bitmap){

        //文件名
        String fileName = System.currentTimeMillis()+".png";

        //filePath路径为/data/user/0/com.example.flutter_crop_demo/app_flutter
        String filePath = PathUtils.getDataDirectory(context.getApplicationContext());
        String file = filePath + File.separator+ fileName;

        try{
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            log("crop file is exits : "+ new File(file).exists());
        }catch (Exception e){
            e.printStackTrace();
            log("crop file save failed");
        }

        return file;
    }


    /**
     * bitmap转像素byte[]
     * @param bmp
     * @return
     */
    public static int[] getBitmapPixels(Bitmap bmp){
      int width = bmp.getWidth();
      int height = bmp.getHeight();
      int[] pixels = new int[width * height];
      bmp.getPixels(pixels, 0, width, 0, 0, width, height);
      return pixels;
    }

    private static void log(String msg){
        Log.d("FullscreenActivity---", msg);
    }
}