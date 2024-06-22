package com.example.flutter_crop_demo;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.flutter_crop_demo.utils.ImageHelper;

import org.json.JSONException;
import org.json.JSONObject;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.JSONMessageCodec;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMessageCodec;

public class MainActivity extends FlutterActivity implements BasicMessageChannel.MessageHandler{

    private static final String CHANNEL_NAME = "flutter_crop_demo_channel";

    private BasicMessageChannel channel;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        System.out.println("configureFlutterEngine---------");
        super.configureFlutterEngine(flutterEngine);
        channel = new BasicMessageChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL_NAME, JSONMessageCodec.INSTANCE);
        channel.setMessageHandler(this);
    }

    @Override
    public void onMessage(@Nullable Object message, @NonNull BasicMessageChannel.Reply reply) {
        System.out.println("is message from flutter:" + message.toString());
        JSONObject jsonObject = (JSONObject) message;

        String event = jsonObject.optString("event");
        JSONObject dataObject = jsonObject.optJSONObject("data");
        String originFile = dataObject.optString("origin");
        String maskFile = dataObject.optString("frame");

        if(event.equals("jumpNativePage")){

            //原生调试代码
            Intent intent = new Intent(this, FullscreenActivity.class);
            intent.putExtra("origin", originFile);
            intent.putExtra("frame", maskFile);
            startActivity(intent);

        }else if(event.equals("getCropImg")){

            //获取裁剪图，直接返回到flutter
            String cropFile = ImageHelper.getCropImageFile(this.getApplicationContext(), originFile, maskFile);
            JSONObject json = new JSONObject();
            try {
                json.put("crop", cropFile);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            reply.reply(json);
        }




    }
}
