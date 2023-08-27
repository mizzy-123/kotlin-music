package com.tubes.musicappproject

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.*
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_player.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity() {
    lateinit var fab_play: ImageView
    lateinit var avd: AnimatedVectorDrawableCompat
    lateinit var avd2: AnimatedVectorDrawable
    var switchNumber:Int = 0;

    companion object {
        lateinit var musicListPlayer: ArrayList<MusicTrack>
        var songPosition: Int = 0
        var mediaPlayer: MediaPlayer? = null
        var isPlaying: Boolean = false
        var repeat_one: Boolean = false
        var duration: Double = 0.0
        var current_time: Double = 0.0
        private lateinit var runnable: Runnable
    }

    lateinit var notificationManager: NotificationManager

    override fun onBackPressed() {
        super.onBackPressed()
        mediaPlayer!!.stop()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        fab_play=findViewById(R.id.fab_play)

        //bikin marquee di player-activity
        player_judul.ellipsize = TextUtils.TruncateAt.MARQUEE;
        player_judul.ellipsize = TextUtils.TruncateAt.MARQUEE;
        player_judul.setHorizontallyScrolling(true);
        player_judul.marqueeRepeatLimit = -1;
        player_judul.isFocusable = true;
        player_judul.isFocusableInTouchMode = true;

        fab_back.setOnClickListener {
            mediaPlayer!!.stop()
            finish()
        }

        btnSetting.setOnClickListener {
            val intent:Intent = Intent(this, SettingAplikasi::class.java)
            startActivity(intent)
        }

        try {
            val bundle: Bundle? = intent.extras

            if (bundle != null) {
                initializeLayout(bundle)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                createChannel()
                registerReceiver(broadcastReceiver, IntentFilter("TRACKS_TRACKS"))
                startService(Intent(baseContext, OnClearFromRecentService::class.java))
                updateWidget(R.drawable.pause)
                if (repeat_one){
                    CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                        R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
                    updateWidgetRepeat(R.drawable.repeat_one)
                } else {
                    CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                        R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
                    updateWidgetRepeat(R.drawable.repeat_all)
                }
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2) {
                        mediaPlayer!!.seekTo(p1)
                        start.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
                        if (isPlaying){
                            if (repeat_one){
                                CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                                    R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
                            } else {
                                CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                                    R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
                            }
                        } else {
                            if (repeat_one){
                                CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                                    R.drawable.ic_play_arrow_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
                            } else {
                                CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                                    R.drawable.ic_play_arrow_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
                            }
                        }
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })


        } catch (e: Exception) {
            Toast.makeText(this, "Error" + e.message, Toast.LENGTH_SHORT).show()
        }

        fab_next.setOnClickListener { prevNextSong(increment = true) }
        fab_prev.setOnClickListener { prevNextSong(increment = false) }
        fab_play.setOnClickListener {
            if (isPlaying)
                pause()
            else
                play()
        }

        fab_repeat.setOnClickListener {
            if (!repeat_one) {
                repeat_one = true
                fab_repeat.setBackgroundResource(R.drawable.repeat_one)
                updateWidgetRepeat(R.drawable.repeat_one)
                Toast.makeText(this, "Repeat One", Toast.LENGTH_SHORT).show()
                if (isPlaying){
                    CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                        R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
                } else {
                    CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                        R.drawable.ic_play_arrow_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
                }
            } else {
                repeat_one = false
                fab_repeat.setBackgroundResource(R.drawable.repeat_all)
                updateWidgetRepeat(R.drawable.repeat_all)
                Toast.makeText(this, "Repeat All", Toast.LENGTH_SHORT).show()
                if (isPlaying){
                    CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                        R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
                } else {
                    CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                        R.drawable.ic_play_arrow_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
                }
            }
        }

    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel: NotificationChannel = NotificationChannel(CreateNotification.CHANNEL_ID,
                "dev", NotificationManager.IMPORTANCE_LOW)
            notificationManager = getSystemService(NotificationManager::class.java)
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1!!.extras!!.getString("actionname")
            when (action) {
                CreateNotification.ACTION_PREV -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    prev()
                }
                CreateNotification.ACTION_PLAY -> {
                    if (isPlaying) {
                        pause()
                    } else {
                        play()
                    }
                }
                CreateNotification.ACTION_NEXT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    next()
                }
                MusicWidget.ACTION_PLAY_PAUSE -> {
                    if (isPlaying) {
                        pause()

                    } else {
                        play()

                    }
                }
                MusicWidget.ACTION_PREVIOUS -> {
                    prev()
                }
                MusicWidget.ACTION_NEXT -> {
                    next()
                }
                MusicWidget.ACTION_REPEAT -> {
                    if (!repeat_one) {
                        repeat_one = true
                        updateWidgetRepeat(R.drawable.repeat_one)
                        fab_repeat.setBackgroundResource(R.drawable.repeat_one)
                        if (isPlaying){
                            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                                R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
                        } else {
                            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                                R.drawable.ic_play_arrow_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
                        }

                    } else {
                        repeat_one = false
                        updateWidgetRepeat(R.drawable.repeat_all)
                        fab_repeat.setBackgroundResource(R.drawable.repeat_all)
                        if (isPlaying){
                            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                                R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
                        } else {
                            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                                R.drawable.ic_play_arrow_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
                        }
                    }
                }
                CreateNotification.ACTION_REPEAT -> {
                    if (!repeat_one) {
                        repeat_one = true
                        updateWidgetRepeat(R.drawable.repeat_one)
                        fab_repeat.setBackgroundResource(R.drawable.repeat_one)
                        if (isPlaying){
                            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                                R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
                        } else {
                            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                                R.drawable.ic_play_arrow_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
                        }
                    } else {
                        repeat_one = false
                        updateWidgetRepeat(R.drawable.repeat_all)
                        fab_repeat.setBackgroundResource(R.drawable.repeat_all)
                        if (isPlaying){
                            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                                R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
                        } else {
                            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                                R.drawable.ic_play_arrow_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initializeLayout(bundle: Bundle){
        when (bundle.getString("music", "")){
            "track" -> {
                musicListPlayer = GridTrackAdapter.trackList
                songPosition = bundle.getInt("position", 0)
                initialize()
                createMedia()
            }

            "album" -> {
                val item = bundle.getString("item", "")
                musicListPlayer = getDetailAlbum(this@PlayerActivity, item)
                songPosition = bundle.getInt("position", 0)
                initialize()
                createMedia()
            }
            "folder" -> {
                val item = bundle.getString("item", "")
                musicListPlayer= DetailFolderActivity.currentFolderVideos
                songPosition = bundle.getInt("position", 0)
                initialize()
                createMedia()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDetailAlbum(context: Context, item: String): ArrayList<MusicTrack>{

        //store shared preference setting path
        val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val cek = sharedPreference.getString("username","defaultName")
        sharedPreference.getLong("l",1L)
        Log.i("implemensetting", "Ini pathnya $cek")

        val sort = sharedPreference.getString("sort","ASC")
        sharedPreference.getLong("l",1L)
        Log.i("implemensort", "Ini sortnya $sort")

        val arrayAlbum = DataMusic.getAllAudioTrack(context, cek.toString(), sort.toString())
        val newArray: ArrayList<MusicTrack> = ArrayList()

        arrayAlbum.forEach {
            if (it.album.equals(item)){
                newArray.add(it)
            }
        }

        return newArray

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun seekbar() {

    }

    fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initialize() {

        player_judul.text = musicListPlayer[songPosition].title
        player_album.text = musicListPlayer[songPosition].album
        player_artist.text = musicListPlayer[songPosition].artist
        Log.i("arturi", musicListPlayer[songPosition].artUri)
        Glide.with(this)
            .load(musicListPlayer[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.cover_white).fitCenter())
            .into(img_player)

    }

    fun updateWidgetRepeat(iconRepeat: Int) {
        val remoteViews = MusicWidget.getRemoteViews(this@PlayerActivity)
        remoteViews.setImageViewResource(R.id.repeat, iconRepeat)
        val thisWidget = ComponentName(this@PlayerActivity, MusicWidget::class.java)
        AppWidgetManager.getInstance(this@PlayerActivity).updateAppWidget(thisWidget, remoteViews)
    }

    fun updateWidget(iconPlay: Int){
        val remoteViews = MusicWidget.getRemoteViews(this@PlayerActivity)
        remoteViews.setTextViewText(R.id.title_widget, musicListPlayer[songPosition].title)
        remoteViews.setImageViewResource(R.id.play, iconPlay)
        if (musicListPlayer[songPosition].artUri.equals("content://media/external/audio/albumart/20")){
            remoteViews.setImageViewResource(R.id.songImage, R.drawable.cover_white)
        } else {
            remoteViews.setImageViewUri(R.id.songImage, Uri.parse(musicListPlayer[songPosition].artUri))
        }
        val thisWidget = ComponentName(this@PlayerActivity, MusicWidget::class.java)
        AppWidgetManager.getInstance(this@PlayerActivity).updateAppWidget(thisWidget, remoteViews)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMedia() {
        val contentUri: Uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            musicListPlayer[songPosition].id
        )
        mediaPlayer = MediaPlayer()

        start.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
        mediaPlayer!!.reset()
        mediaPlayer?.setDataSource(applicationContext, contentUri)
        mediaPlayer!!.prepare()
        mediaPlayer?.start()
        isPlaying = true
        fab_play.setImageResource(R.drawable.pause)
        seekBar.progress = 0
        seekBar.max = mediaPlayer!!.duration
        end.text = formatDuration(mediaPlayer!!.duration.toLong())

        runnable = Runnable {
            start.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            seekBar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)

        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
        mediaPlayer!!.setOnCompletionListener {

            if (repeat_one) {
                initialize()
                createMedia()
            } else {
                next()
            }
        }
        updateWidget(R.drawable.pause)
    }

    private fun repeat_one() {
        val contentUri: Uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            musicListPlayer[songPosition].id
        )
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.reset()
        mediaPlayer?.setDataSource(applicationContext, contentUri)
        mediaPlayer!!.prepare()
        mediaPlayer!!.isLooping
        mediaPlayer?.start()
        isPlaying = true
        fab_play.setImageResource(R.drawable.pause)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun next() {
        mediaPlayer!!.pause()
        if(musicListPlayer.size-1 == songPosition) {
            songPosition = 0
        } else {
            ++songPosition
            notificationManager.cancelAll()
            createChannel()
        }
        initialize()
        createMedia()
        if (repeat_one){
            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
        } else {
            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prev() {
        mediaPlayer!!.pause()
        if(songPosition == 0) {
            songPosition = 0
        } else {
            --songPosition
            notificationManager.cancelAll()
            createChannel()
        }
        initialize()
        createMedia()
        if (repeat_one){
            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
        } else {
            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun prevNextSong(increment: Boolean) {
        if (increment) {
            if (repeat_one) {
                mediaPlayer!!.pause()
                initialize()
                repeat_one()
            } else {
                next()
            }


        } else {
            if (repeat_one) {
                mediaPlayer!!.pause()
                initialize()
                repeat_one()
            } else {
                prev()
            }

        }
    }

    private fun play() {
        fab_play.setImageDrawable(getResources().getDrawable(R.drawable.avd_play_to_pause))
        var drawable:Drawable = fab_play.getDrawable();

        if(drawable is AnimatedVectorDrawableCompat){

            avd = drawable as AnimatedVectorDrawableCompat;
            avd.start();

        }else if(drawable is AnimatedVectorDrawable){

            avd2 = drawable as AnimatedVectorDrawable;
            avd2.start();

        }
        isPlaying = true
        mediaPlayer!!.start()
        if (repeat_one){
            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
        } else {
            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                R.drawable.ic_pause_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
        }
        updateWidget(R.drawable.pause)
    }

    private fun pause() {
        fab_play.setImageDrawable(getResources().getDrawable(R.drawable.avd_pause_to_play))
        var drawable:Drawable = fab_play.getDrawable();

        if(drawable is AnimatedVectorDrawableCompat){

            avd = drawable as AnimatedVectorDrawableCompat;
            avd.start();

        }else if(drawable is AnimatedVectorDrawable){

            avd2 = drawable as AnimatedVectorDrawable;
            avd2.start();

        }
        isPlaying = false
        mediaPlayer!!.pause()
        if (repeat_one){
            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                R.drawable.ic_play_arrow_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_one_24)
        } else {
            CreateNotification.createNotification(this@PlayerActivity, musicListPlayer[songPosition],
                R.drawable.ic_play_arrow_black_24dp, songPosition, musicListPlayer.size-1, R.drawable.ic_baseline_repeat_24)
        }
        updateWidget(R.drawable.play)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll()
        }

        unregisterReceiver(broadcastReceiver)
    }

}