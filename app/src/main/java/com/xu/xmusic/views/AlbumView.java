package com.xu.xmusic.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xu.xmusic.R;
import com.xu.xmusic.utils.GlideUtils;

public class AlbumView extends FrameLayout {

    private View mView;
    private TextView tv_singer, tv_lyric;
    private ImageView iv_album;

    private ObjectAnimator animator;

    public AlbumView(@NonNull Context context) {
        this(context, null);
    }

    public AlbumView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.album_view, this, false);
        tv_singer = mView.findViewById(R.id.tv_singer);
        tv_lyric = mView.findViewById(R.id.tv_lyric);
        iv_album = mView.findViewById(R.id.iv_album);
        addView(mView);

        animator = ObjectAnimator.ofFloat(iv_album, "rotation", 0f, 360f);
        animator.setDuration(40000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
    }

    public void setSinger(String singer) {
        tv_singer.setText(singer);
    }

    public TextView getSinger() {
        return tv_singer;
    }

    public void setLyric(String lyric) {
        tv_lyric.setText(lyric);
    }

    public TextView getLyric() {
        return tv_lyric;
    }

    public void setAlbumImg(String url) {
        GlideUtils.loadImage(getContext(), url, R.drawable.pic_launcher, iv_album);
    }

    public ImageView getAlbum() {
        return iv_album;
    }

    public void startAnim() {
        animator.start();
    }

    public void resumeAnim() {
        if (!animator.isStarted()) {
            animator.start();
        } else {
            animator.resume();
        }
    }

    public void pauseAnim() {
        animator.pause();
    }

    public void endAnim() {
        if (animator.isRunning()) {
            animator.end();
        }
    }
}
