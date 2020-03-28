package com.xu.xmusic.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.xu.xmusic.R;
import com.xu.xmusic.activities.MainActivity;
import com.xu.xmusic.bean.MusicInfo;
import com.xu.xmusic.providers.MusicWidgetProvider;
import com.xu.xmusic.utils.GlideAsyncTask;
import com.xu.xmusic.views.MyMusicPlayerView;
import com.xu.xxplayer.players.BasePlayerView;
import com.xu.xxplayer.players.MusicPlayerView;

public class MusicService extends Service {

    private static final String TAG = "MusicService";
    public static final int NOTIFICATION_ID = 1;
    private static final String ACTION_PLAY = "com.xu.xmaster.play";
    private static final String ACTION_PREVIOUS = "com.xu.xmaster.previous";
    private static final String ACTION_NEXT = "com.xu.xmaster.next";
    private static final String ACTION_CLOSE = "com.xu.xmaster.close";
    private static final int NOTIFICATION_CODE = 1;

    private NotificationManager notificationManager;
    private Notification notification;
    private RemoteViews views;
    private MyMusicPlayerView mPlayerView;
    private boolean isPlaying = false;

    private Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int max = (int) mPlayerView.getDuration();
                    int progress = (int) mPlayerView.getCurrentPosition();
                    views.setProgressBar(R.id.mn_progress, max, progress, false);
                    updateViews(views);

                    if (mPlayerView.isPlaying()) {
                        UIHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        isPlaying = true;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = createNotification();
        views = new RemoteViews(getPackageName(), R.layout.music_notification);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PREVIOUS);
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_CLOSE);
        registerReceiver(musicReceiver, filter);

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: '");
        return new MusicBind();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        isPlaying = false;
        stopForeground(true);
        notificationManager.cancel(NOTIFICATION_ID);
        try {
            unregisterReceiver(musicReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public class MusicBind extends Binder {
        public void playMusic(MyMusicPlayerView playerView) {
            mPlayerView = playerView;
            initView();
            initEvent();
        }
    }

    private void initView() {
        MusicInfo musicInfo = mPlayerView.getMusicInfo();
        views.setTextViewText(R.id.mn_text, musicInfo.getSongname() + " - " + musicInfo.getSinger());
        views.setProgressBar(R.id.mn_progress, 100, 0, false);
        updateViews(views);

        new GetBitmapAsyncTask(getApplicationContext()).execute(musicInfo.getImgUrl());
    }

    private void initEvent() {
        mPlayerView.setOnServiceStateChangedListener(new MusicPlayerView.OnServiceStateChangedListener() {
            @Override
            public void onServiceStateChanged(int state) {
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
                        UIHandler.sendEmptyMessage(0);
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
                views.setImageViewResource(R.id.mn_play, icon);
                updateViews(views);
            }
        });

        views.setOnClickPendingIntent(R.id.mn_play, sendBrocast(ACTION_PLAY));
        views.setOnClickPendingIntent(R.id.mn_previous, sendBrocast(ACTION_PREVIOUS));
        views.setOnClickPendingIntent(R.id.mn_next, sendBrocast(ACTION_NEXT));
        views.setOnClickPendingIntent(R.id.mn_close, sendBrocast(ACTION_CLOSE));
        updateViews(views);
    }

    private class GetBitmapAsyncTask extends GlideAsyncTask {

        public GetBitmapAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                views.setImageViewBitmap(R.id.mn_albumimg, bitmap);
                updateViews(views);
            }
        }
    }

    /**
     * 更新通知栏
     *
     * @param views
     */
    private void updateViews(RemoteViews views) {
        Log.d(TAG, "updateViews: " + isPlaying);
        if (isPlaying) {
            notification.contentView = views;
            notificationManager.notify(NOTIFICATION_ID, notification);

            Intent intent = new Intent(this, MusicWidgetProvider.class);
            intent.setAction(MusicWidgetProvider.ACTION_WIDGET_UPDATE);
            sendBroadcast(intent);
        }
    }

    /**
     * 创建通知栏
     *
     * @return
     */
    private Notification createNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0要通过NotificationChannel显示通知
            NotificationChannel channel = new NotificationChannel("id", "name", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(this, channel.getId());
        } else {
            builder = new Notification.Builder(this);
        }
        Notification notification = builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentIntent(startActivity())
                .build();
        return notification;
    }

    /**
     * 打开activity
     *
     * @return
     */
    private PendingIntent startActivity() {
        return PendingIntent.getActivity(this,
                NOTIFICATION_CODE,
                new Intent(this, MainActivity.class).putExtra("startFromNotification", true),
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    /**
     * 发送广播
     *
     * @param action
     * @return
     */
    private PendingIntent sendBrocast(String action) {
        return PendingIntent.getBroadcast(
                this,
                NOTIFICATION_CODE,
                new Intent(action),
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "musicReceiver: " + intent.getAction());
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    if (mPlayerView.isIdle()) {
                        mPlayerView.start();
                    } else if (mPlayerView.isPlaying() || mPlayerView.isBufferingPlaying()) {
                        mPlayerView.pause();
                    } else if (mPlayerView.isPaused() || mPlayerView.isBufferingPaused() ||
                            mPlayerView.isCompleted() || mPlayerView.isError()) {
                        mPlayerView.restart();
                    }
                    break;
                case ACTION_PREVIOUS:
                    mPlayerView.playNext(false);
                    break;
                case ACTION_NEXT:
                    mPlayerView.playNext(true);
                    break;
                case ACTION_CLOSE:
                    mPlayerView.stopService();
                    break;
            }
        }
    };
}
