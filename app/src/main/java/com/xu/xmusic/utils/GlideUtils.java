package com.xu.xmusic.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class GlideUtils {

    public static void loadImage(Context context, Object url, ImageView imageView) {
        if (!isDestroy(((Activity) context))) {
            Glide.with(context)
                    .load(url)
                    .into(imageView);
        } else {
            Log.e("GlideUtil", "You cannot start a load for a destroyed activity");
        }
    }

    public static void loadImage(Context context, Object url, int placeholder, ImageView imageView) {
        if (!isDestroy(((Activity) context))) {
            Glide.with(context)
                    .load(url)
                    .placeholder(placeholder)
                    .into(imageView);
        } else {
            Log.e("GlideUtil", "You cannot start a load for a destroyed activity");
        }
    }

    public static void loadImageTransform(Context context, Object url, final ImageView imageView) {
        if (!isDestroy(((Activity) context))) {
            Glide.with(context)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(5, 5)))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            imageView.setImageDrawable(resource);
                        }
                    });
        } else {
            Log.e("GlideUtil", "You cannot start a load for a destroyed activity");
        }
    }

    public static void loadBackgroundTransform(Context context, Object url, final View view) {
        if (!isDestroy(((Activity) context))) {
            Glide.with(context)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(5, 5)))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            view.setBackground(resource);
                        }
                    });
        } else {
            Log.e("GlideUtil", "You cannot start a load for a destroyed activity");
        }
    }

    private static boolean isDestroy(Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return true;
        } else {
            return false;
        }
    }
}
