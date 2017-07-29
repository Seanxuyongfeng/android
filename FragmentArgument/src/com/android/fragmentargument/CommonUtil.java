package com.android.fragmentargument;


import java.io.InputStream;

import com.android.fragmentargument.GifHelper.GifFrame;


public class CommonUtil {
    /**
     * ����GIFͼƬ
     * 
     * @param is
     * @return
     */
    public static GifFrame[] getGif(InputStream is) {
        GifHelper gifHelper = new GifHelper();
        if (GifHelper.STATUS_OK == gifHelper.read(is)) {
            return gifHelper.getFrames();
        }
        return null;
    }
    /**
     * �ж�ͼƬ�Ƿ�ΪGIF��ʽ
     * @param is
     * @return
     */
    public static boolean isGif(InputStream is) {
        GifHelper gifHelper = new GifHelper();
        return gifHelper.isGif(is);
    }
}
