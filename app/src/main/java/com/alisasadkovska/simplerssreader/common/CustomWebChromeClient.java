package com.alisasadkovska.simplerssreader.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.alisasadkovska.simplerssreader.R;

/**
 * Created by Ryan on 8/17/2017.
 * Edited by Alisa Sadkovska
 */


public class CustomWebChromeClient extends WebChromeClient {

    private Context context;
    private ProgressListener mListener;

    public CustomWebChromeClient(Context context, ProgressListener mListener) {
        this.context = context;
        this.mListener = mListener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        mListener.onUpdateProgress(newProgress);
        super.onProgressChanged(view, newProgress);
    }



    public interface ProgressListener {
        void onUpdateProgress(int progressValue);
    }



    @Override
    public Bitmap getDefaultVideoPoster() {
        if (super.getDefaultVideoPoster() == null) {
            return BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.paws);
        } else {
            return super.getDefaultVideoPoster();
        }
    }

}