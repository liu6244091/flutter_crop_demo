import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  //通道名称
  static final String CHANNEL_NAME = "flutter_crop_demo_channel";

  //基本消息通道
  late BasicMessageChannel channel;

  //裁剪图
  String cropFile = "";

  @override
  void initState() {
    super.initState();

    initChannel();
    getCropImg();
  }

  /**
   * 初始化通道
   */
  void initChannel() {
    channel = new BasicMessageChannel(CHANNEL_NAME, JSONMessageCodec());
    channel.setMessageHandler(messageHandler);
  }

  Future<dynamic> messageHandler(dynamic message) async {
    print("is message from native :" + message.toString());
    return "success";
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: <Widget>[
            Container(
              child: Image.asset("assets/origin.png", width: 180),
              margin: EdgeInsets.only(top: 50),
            ),
            Container(
              child: Image.asset("assets/mask.jpeg", width: 180),
              margin: EdgeInsets.only(top: 30),
            ),
            if ("" != cropFile)
              Container(
                child: Image.file(new File(cropFile), width: 180),
                margin: EdgeInsets.only(top: 30),
              ),
            GestureDetector(
              onTap: () {
                jumpNativePage();
              },
              child: Container(
                margin: EdgeInsets.only(top: 40),
                child: Text(
                  "原生实现",
                  style: TextStyle(fontSize: 24),
                ),
              ),
            )
          ],
        ),
      ),
    );
  }

  /**
   * 跳转原生页面
   */
  void jumpNativePage() {
    channel.send({
      'event': 'jumpNativePage',
      'data': {'origin': 'assets/origin.png', 'frame': 'assets/mask.jpeg'}
    });
  }

  /**
   * 获取裁剪图
   */
  void getCropImg() async {
    print("click here");
    var result = await channel.send({
      'event': 'getCropImg',
      'data': {'origin': 'assets/origin.png', 'frame': 'assets/mask.jpeg'}
    });
    String cropFile = result['crop'];
    print("result :" + cropFile);
    setState(() {
      this.cropFile = cropFile;
    });
  }
}
