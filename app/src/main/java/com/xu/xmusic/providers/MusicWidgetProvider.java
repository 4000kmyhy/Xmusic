package com.xu.xmusic.providers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.xu.xmusic.R;
import com.xu.xmusic.activities.MainActivity;
import com.xu.xmusic.bean.MusicInfo;
import com.xu.xmusic.utils.GlideAsyncTask;
import com.xu.xmusic.utils.MusicPlayerUtils;
import com.xu.xmusic.views.MyMusicPlayerView;
import com.xu.xxplayer.players.BasePlayerView;
import com.xu.xxplayer.utils.XXPlayerManager;

public class MusicWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "MusicWidgetProvider";
    public static final String ACTION_WIDGET_UPDATE = "com.xu.xmaster.widget.music.update";
    private static final String ACTION_WIDGET_PLAY = "com.xu.xmaster.widget.play";
    private static final String ACTION_WIDGET_PREVIOUS = "com.xu.xmaster.widget.previous";
    private static final String ACTION_WIDGET_NEXT = "com.xu.xmaster.widget.next";
    private static final int WIDGET_CODE = 2;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "onEnabled: ");
        //第一个被添加
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "onUpdate: ");
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_widget);
        views.setImageViewResource(R.id.mw_albumimg, R.drawable.pic_launcher);
        views.setTextViewText(R.id.mw_text, "选择一首音乐");
        views.setProgressBar(R.id.mw_progress, 100, 0, false);
        views.setOnClickPendingIntent(R.id.mw_layout, startActivity(context));
        views.setOnClickPendingIntent(R.id.mw_play, sendBrocast(context, ACTION_WIDGET_PLAY));
        views.setOnClickPendingIntent(R.id.mw_previous, sendBrocast(context, ACTION_WIDGET_PREVIOUS));
        views.setOnClickPendingIntent(R.id.mw_next, sendBrocast(context, ACTION_WIDGET_NEXT));
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(TAG, "onDisabled: ");
        //最后一个被移除
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive: " + intent.getAction());
        if (MusicPlayerUtils.isMusicPlayer()) {
            MyMusicPlayerView mPlayerView = (MyMusicPlayerView) XXPlayerManager.instance().getCurrentPlayer();
            switch (intent.getAction()) {
                case ACTION_WIDGET_UPDATE:
                    updatePlayer(context, mPlayerView);
                    break;
                case ACTION_WIDGET_PLAY:
                    if (mPlayerView.isIdle()) {
                        mPlayerView.start();
                    } else if (mPlayerView.isPlaying() || mPlayerView.isBufferingPlaying()) {
                        mPlayerView.pause();
                    } else if (mPlayerView.isPaused() || mPlayerView.isBufferingPaused() ||
                            mPlayerView.isCompleted() || mPlayerView.isError()) {
                        mPlayerView.restart();
                    }
                    break;
                case ACTION_WIDGET_PREVIOUS:
                    mPlayerView.playNext(false);
                    break;
                case ACTION_WIDGET_NEXT:
                    mPlayerView.playNext(true);
                    break;
            }
        } else {
            switch (intent.getAction()) {
                case ACTION_WIDGET_PLAY:
                case ACTION_WIDGET_PREVIOUS:
                case ACTION_WIDGET_NEXT:
                    Toast.makeText(context, "选择一首音乐", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void updatePlayer(Context context, MyMusicPlayerView mPlayerView) {
        MusicInfo musicInfo = mPlayerView.getMusicInfo();
        int max = (int) mPlayerView.getDuration();
        int progress = (int) mPlayerView.getCurrentPosition();
        int state = mPlayerView.getCurrentState();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_widget);
        views.setTextViewText(R.id.mw_text, musicInfo.getSongname() + " - " + musicInfo.getSinger());
        views.setProgressBar(R.id.mw_progress, max, progress, false);
        int icon = R.drawable.ic_play;
        switch (state) {
            case BasePlayerView.STATE_IDLE:
                icon = R.drawable.ic_play;
                break;
            case BasePlayerView.STATE_PREPARING:
                icon = R.drawable.ic_pause;
                break;
            case BasePlayerView.STATE_PLAYING:
                icon = R.drawable.ic_pause;
                break;
            case BasePlayerView.STATE_PAUSED:
                icon = R.drawable.ic_play;
                break;
            case BasePlayerView.STATE_BUFFERING_PLAYING:
                break;
            case BasePlayerView.STATE_BUFFERING_PAUSED:
                break;
            case BasePlayerView.STATE_COMPLETED:
            case BasePlayerView.STATE_ERROR:
                icon = R.drawable.ic_play;
                break;
        }
        views.setImageViewResource(R.id.mw_play, icon);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, MusicWidgetProvider.class);
        appWidgetManager.updateAppWidget(componentName, views);

        new GetBitmapAsyncTask(context).execute(musicInfo.getImgUrl());
    }

    private class GetBitmapAsyncTask extends GlideAsyncTask {

        public GetBitmapAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_widget);
                views.setImageViewBitmap(R.id.mw_albumimg, bitmap);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName componentName = new ComponentName(context, MusicWidgetProvider.class);
                appWidgetManager.updateAppWidget(componentName, views);
            }
        }
    }

    private PendingIntent startActivity(Context context) {
        return PendingIntent.getActivity(context,
                WIDGET_CODE,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private PendingIntent sendBrocast(Context context, String action) {
        Intent intent = new Intent(context, MusicWidgetProvider.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(
                context,
                WIDGET_CODE,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
