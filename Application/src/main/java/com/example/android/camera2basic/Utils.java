package com.example.android.camera2basic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.TextureView;
import android.widget.TextView;

import java.nio.ByteBuffer;

/**
 * Created by chao on 2019/2/13.
 */

public class Utils {

    public static void startConvert(final TextureView textureView, final TextView textView) {
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(textureView != null) {
                    Bitmap bitmap = textureView.getBitmap();
                    if(bitmap != null) {
                        String s = Utils.bitmap2string(bitmap);
                        textView.setText(s);
                    }
                }
                sendEmptyMessageDelayed(0, 20);
            }
        };
        handler.sendEmptyMessage(0);
    }

    public static Bitmap imageReader2Bitmap(ImageReader imageReader) {
        Image image = null;
        ByteBuffer buffer = null;

        try {
            image = imageReader.acquireNextImage();
            buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(buffer != null) {
                buffer.clear();
            }
            if(image != null) {
                image.close();
            }
        }
        return null;
    }

    public static String yuv2string(ImageReader imageReader) {
        Image image = null;
        ByteBuffer buffer = null;

        try {
            image = imageReader.acquireNextImage();
            Image.Plane[] planes = image.getPlanes();
            buffer = planes[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            int w = image.getWidth();
            int h = image.getHeight();
//            Log.e("chao", "planes " + planes.length + " " + w + "," + h);
            StringBuilder sb = new StringBuilder();
            for(int j = 0; j < h; j+=6) {
                for (int i = 0; i < w; i+=6) {
//                    int y = bytes[i * w + j] + 128;
                    int y = bytes[j * w + i] + 128;
                    char c = sChars[y * (sChars.length - 1) / 255];
                    sb.append(c);
                }
                sb.append("\r\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(buffer != null) {
                buffer.clear();
            }
            if(image != null) {
                image.close();
            }
        }
        return null;
    }

    public static String bitmap2string(Bitmap bitmap) {
        StringBuilder sb = new StringBuilder();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        for(int j = 0; j < h; j+=20) {
            for(int i = 0; i < w; i+=15) {
                int pixel = bitmap.getPixel(i, j);
                sb.append(color2char(pixel));
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }

//    private static char[] sChars = " .,:;i1tfLCG08@".toCharArray();
//    private static char[] sChars = "龘龍簇数菜米本木十大一　".toCharArray();
    private static char[] sChars = "　一大十木本米菜数簇龍龘".toCharArray();

    public static Character color2char(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int brightness = Math.round(0.299f * red + 0.587f * green + 0.114f * blue);
        return sChars[brightness * (sChars.length - 1) / 255];
    }
}
