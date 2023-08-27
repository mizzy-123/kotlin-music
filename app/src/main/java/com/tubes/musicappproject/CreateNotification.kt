package com.tubes.musicappproject

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object CreateNotification {
    const val CHANNEL_ID = "channel1"
    val ACTION_PREV = "actionprevious"
    val ACTION_PLAY = "actionplay"
    val ACTION_NEXT = "actionnext"
    val ACTION_REPEAT = "actionrepeat"
    lateinit var notification: Notification

    @SuppressLint("UnspecifiedImmutableFlag")
    fun createNotification(context: Context, track: MusicTrack, playbutton: Int, pos: Int, size: Int, repeat: Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationManagerCompat: NotificationManagerCompat = NotificationManagerCompat.from(context)
            val mediaSessionCompat: MediaSessionCompat = MediaSessionCompat(context, "tag")

            //get uri from EXTERNAL CONTENT
            val contentUri: Uri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                PlayerActivity.musicListPlayer[PlayerActivity.songPosition].id
            )
            val imgArt = getImgArt(context, contentUri)
            val img = if (imgArt != null){
                BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
            } else {
                BitmapFactory.decodeResource(context.resources, R.drawable.cover_white)
            }
//            val img = BitmapFactory.decodeResource(context.resources, R.drawable.cover_white)
            val intent = Intent(context, MainActivity::class.java)
            val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val contentIntent = PendingIntent.getActivity(context, 0, intent, flag)

            var pendingIntentPrevious: PendingIntent? = null
            var drw_previous = 0

            val intentPrevious: Intent = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_PREV)
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT)
            drw_previous = R.drawable.ic_skip_previous_black_24dp

            val intentPlay: Intent = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_PLAY)
            val pendingIntentPlay: PendingIntent = PendingIntent.getBroadcast(context, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)

            var pendingIntentNext: PendingIntent? = null
            var drw_next = 0

            val intentNext: Intent = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_NEXT)
            pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                intentNext, PendingIntent.FLAG_UPDATE_CURRENT)
            drw_next = R.drawable.ic_skip_next_black_24dp

            val intentRepeat: Intent = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_REPEAT)
            val pendingIntentRepeat: PendingIntent = PendingIntent.getBroadcast(context, 0,
                intentRepeat, PendingIntent.FLAG_UPDATE_CURRENT)

            notification = NotificationCompat.Builder(context, CHANNEL_ID)
//                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(track.title)
                .setContentText(track.artist)
                .setLargeIcon(img)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .addAction(drw_previous, "Previous", pendingIntentPrevious)
                .addAction(playbutton, "Play", pendingIntentPlay)
                .addAction(drw_next, "Next", pendingIntentNext)
                .addAction(repeat, "Repeat", pendingIntentRepeat)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSessionCompat.sessionToken))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                val playbackSpeed = if(PlayerActivity.isPlaying) 1F else 0F
                mediaSessionCompat.setMetadata(MediaMetadataCompat.Builder()
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, PlayerActivity.mediaPlayer!!.duration.toLong())
                    .build())
                val playBackState = PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, PlayerActivity.mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .build()
                mediaSessionCompat.setPlaybackState(playBackState)
                mediaSessionCompat.setCallback(object : MediaSessionCompat.Callback(){
                    override fun onSeekTo(pos: Long) {
                        super.onSeekTo(pos)
                        PlayerActivity.mediaPlayer!!.seekTo(pos.toInt())
                        val playBackStateNew = PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PLAYING, PlayerActivity.mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                            .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                            .build()
                        mediaSessionCompat.setPlaybackState(playBackStateNew)
                    }
                })
            }

            notificationManagerCompat.notify(1, notification)
        }
    }

    // for get image from bitmap
    fun getImgArt(context: Context, path: Uri): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, path)
        return retriever.embeddedPicture
    }
}