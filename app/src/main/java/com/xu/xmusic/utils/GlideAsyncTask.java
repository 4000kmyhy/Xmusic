package com.xu.xmusic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.xu.xmusic.R;

import java.util.concurrent.ExecutionException;

public class GlideAsyncTask extends AsyncTask<String, Void, Bitmap> {

    protected Context context;

    public GlideAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context)
                    .asBitmap()
                    .load(strings[0])
                    .placeholder(R.drawable.pic_launcher)
                    .submit()
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
