package com.tubes.musicappproject

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class MusicWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        associateIntents(context)
    }

    private fun associateIntents(context: Context) {
        try {
            val remoteViews = getRemoteViews(context)

            // Push update for this widget to the home screen
            val thisWidget = ComponentName(context, MusicWidget::class.java)
            AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, remoteViews)
        } catch (ignored: Exception){

        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        private const val TAG = "Music Widget"
        const val ACTION_PLAY_PAUSE = "com.tubes.musicappproject.play_pause"
        const val ACTION_NEXT = "com.tubes.musicappproject.next"
        const val ACTION_PREVIOUS = "com.tubes.musicappproject.previous"
        const val ACTION_REPEAT = "com.tubes.musicappproject.repeat"

        fun getRemoteViews(context: Context): RemoteViews {
            val remoteViews = RemoteViews(context.packageName, R.layout.music_widget)

            // For Play/Pause button
            remoteViews.setOnClickPendingIntent(R.id.play, getPendingIntent(context, ACTION_PLAY_PAUSE))

            // For Previous button
            remoteViews.setOnClickPendingIntent(R.id.previousBtn, getPendingIntent(context, ACTION_PREVIOUS))

            // For Next button
            remoteViews.setOnClickPendingIntent(R.id.nextBtn, getPendingIntent(context, ACTION_NEXT))
            remoteViews.setOnClickPendingIntent(R.id.repeat, getPendingIntent(context, ACTION_REPEAT))

            return remoteViews
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        private fun getPendingIntent(context: Context?, action: String?): PendingIntent {
            val intent = Intent(context, NotificationActionService::class.java)
                .setAction(action)
            val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            return PendingIntent.getBroadcast(context, 0, intent, flag)
        }
    }
}