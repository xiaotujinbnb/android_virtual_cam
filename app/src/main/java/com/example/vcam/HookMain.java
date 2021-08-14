package com.example.vcam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import android.annotation.SuppressLint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {
    public static Surface msurf;
    public static SurfaceTexture msurftext;
    public static MediaPlayer mMedia;
    public static SurfaceTexture virtual_st;
    public static Camera reallycamera;

    public static Camera data_camera;
    public static byte[] data_buffer;
    public static MediaPlayer data_mediaplayer;
    public static ImageReader data_imagereader;
    public static int mhight;
    public static int mwidth;

    public static Surface c2_ori_Surf ;
    public static Surface c2_vir_Surf;
    public static MediaPlayer c2_player;
    public static CaptureRequest.Builder c2_builder ;
    public static SurfaceTexture c2_virt_st;
    public Handler mHandler;


    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam)  {


        /*Class cameraclass = findClass("android.hardware.Camera", lpparam.classLoader);
        XposedHelpers.findAndHookMethod(cameraclass, "setPreviewTexture", 	SurfaceTexture.class, new XC_MethodHook() {
            @SuppressLint("SdCardPath")
            @Override
            protected void beforeHookedMethod(MethodHookParam param){

                if (reallycamera != null && reallycamera.equals((Camera) param.thisObject)){
                    param.args[0]= HookMain.virtual_st;
                    XposedBridge.log("发现重复" + reallycamera.toString());
                    return;
                }

                reallycamera = (Camera) param.thisObject;
                HookMain.msurftext = (SurfaceTexture) param.args[0];

                if (HookMain.virtual_st == null){
                    HookMain.virtual_st = new SurfaceTexture(10);
                }else{
                    HookMain.virtual_st.release();
                    HookMain.virtual_st = new SurfaceTexture(10);
                }
                param.args[0]= HookMain.virtual_st;

                if (HookMain.msurf == null){
                    HookMain.msurf = new Surface(HookMain.msurftext);
                }else {
                    HookMain.msurf.release();
                    HookMain.msurf = new Surface(HookMain.msurftext);
                }

                if (HookMain.mMedia == null) {
                    HookMain.mMedia = new MediaPlayer();
                }else{
                    HookMain.mMedia.release();
                    HookMain.mMedia = new MediaPlayer();
                }

                HookMain.mMedia.setSurface(HookMain.msurf);


                HookMain.mMedia.setVolume(0, 0);
                HookMain.mMedia.setLooping(true);

                HookMain.mMedia.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        HookMain.mMedia.start();
                    }
                });


            }
        });*/


/* 此段代码有些问题，暂时不启用
        XposedHelpers.findAndHookMethod("android.hardware.camera2.CaptureRequest.Builder" ,lpparam.classLoader, "addTarget",android.view.Surface.class, new XC_MethodHook() {
            @SuppressLint("SdCardPath")
            @Override
            protected void beforeHookedMethod(MethodHookParam param){

                if (HookMain.c2_builder != null && HookMain.c2_builder.equals((CaptureRequest.Builder) param.thisObject)){
                    param.args[0]= HookMain.c2_vir_Surf;
                    XposedBridge.log("发现重复" + HookMain.c2_builder.toString());
                    return;
                }

                HookMain.c2_builder = (CaptureRequest.Builder) param.thisObject;
                XposedBridge.log("啊棒啊棒啊" + HookMain.c2_builder.toString());

                HookMain.c2_ori_Surf = (Surface) param.args[0];

                if (HookMain.c2_virt_st == null){
                    HookMain.c2_virt_st = new SurfaceTexture(10);
                }else{
                    HookMain.c2_virt_st.release();
                    HookMain.c2_virt_st = new SurfaceTexture(10);
                }

                if (HookMain.c2_vir_Surf == null){
                    HookMain.c2_vir_Surf = new Surface(c2_virt_st);
                }else{
                    HookMain.c2_vir_Surf.release();
                    HookMain.c2_vir_Surf = new Surface(c2_virt_st);
                }

                param.args[0]=HookMain.c2_vir_Surf;

                if (HookMain.c2_player == null) {
                    HookMain.c2_player = new MediaPlayer();
                }else{
                    HookMain.c2_player.release();
                    HookMain.c2_player = new MediaPlayer();
                }

                HookMain.c2_player.setSurface(HookMain.c2_ori_Surf);


                HookMain.c2_player.setVolume(0, 0);
                HookMain.c2_player.setLooping(true);

                HookMain.c2_player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        HookMain.c2_player.start();
                    }
                });


                try {
                    HookMain.c2_player.setDataSource("/sdcard/DCIM/Camera/virtual.mp4");
                    HookMain.c2_player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
        });
        */

        XposedHelpers.findAndHookMethod("android.hardware.Camera" ,lpparam.classLoader, "setPreviewCallbackWithBuffer", Camera.PreviewCallback.class, new XC_MethodHook() {
            @SuppressLint("SdCardPath")
            @Override
            protected void beforeHookedMethod(MethodHookParam param){
                Class nmb = param.args[0].getClass();
                XposedHelpers.findAndHookMethod(nmb, "onPreviewFrame", byte[].class, android.hardware.Camera.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam paramd) throws Throwable {
                        Camera localcam = (android.hardware.Camera) paramd.args[1];
                        if (localcam.equals(data_camera)){
                            byte[] temp_data = data_buffer;
                            paramd.args[0] =temp_data;
                            XposedBridge.log("我在替换"+String.valueOf(temp_data.length));
                        }else {
                            XposedBridge.log("初始化");
                            HookMain.data_camera = (android.hardware.Camera) paramd.args[1];
                            byte[] bt = (byte[])paramd.args[0];
                            int lt =0;
                            lt = bt.length;
                            HookMain.data_buffer = new byte[lt];
                            for (int i=0 ; i<lt ;i++){
                                HookMain.data_buffer[i] = (byte)0xc8;
                            }
                            mwidth = data_camera.getParameters().getPreviewSize().width;
                            mhight = data_camera.getParameters().getPreviewSize().height;
                            XposedBridge.log("初始化：宽"+String.valueOf(mwidth)+"长："+String.valueOf(mhight));
                            if (data_imagereader!=null){
                                data_imagereader = null;
                            }
                            data_imagereader = ImageReader.newInstance(mwidth,mhight, ImageFormat.YUV_420_888,2);
                            data_imagereader.setOnImageAvailableListener(mOnImageAvailableListener,mHandler);
                            data_mediaplayer = new MediaPlayer();
                            data_mediaplayer.setSurface(data_imagereader.getSurface());
                            HookMain.data_mediaplayer.setVolume(0, 0);
                            HookMain.data_mediaplayer.setLooping(true);
                            HookMain.data_mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    HookMain.data_mediaplayer.start();
                                }
                            });
                            try {
                                XposedBridge.log("开始播放1111");
                                HookMain.data_mediaplayer.setDataSource("/sdcard/DCIM/Camera/virtual.mp4");
                                HookMain.data_mediaplayer.prepare();
                                XposedBridge.log("开始播放");
                            } catch (IOException e) {
                                e.printStackTrace();
                                XposedBridge.log(e.toString());
                            }
                        }
                        //param.arg[0]是一个byte[]，里面是YUV420P格式的帧数据，此处buffer可以换成其他数据。
                    }
                });
            }
        });
    }


    public ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            for (int i=0 ; i<HookMain.mwidth*HookMain.mhight*3/2 ;i++){
                HookMain.data_buffer[i] = (byte)0xc8;
            }
            XposedBridge.log("触发回调");
            Image image = reader.acquireLatestImage();
            XposedBridge.log("得到image");
            try{
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            //这里不知道为啥有BUG，却catch不到问题
            XposedBridge.log("得到buffer长度：" +String.valueOf(buffer.remaining()));
            buffer.get(HookMain.data_buffer);
            XposedBridge.log("数据长度" + String.valueOf(HookMain.data_buffer.length));
            }catch (Exception ee){
                XposedBridge.log(ee.toString());
            }
            image.close();
        }
    };
}