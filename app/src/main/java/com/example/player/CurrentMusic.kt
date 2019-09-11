package com.example.player

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.fragment_current_music.*
import kotlinx.android.synthetic.main.fragment_current_music.view.*
import java.io.File
import android.widget.SeekBar


class CurrentMusic : Fragment() {

    companion object{
        var myQueue = ArrayList<File>()
        var flag = 1
        var musicCurrentLength: Int = 0
    }
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    var mediaPlayer: MediaPlayer? = null
    var mmr: MediaMetadataRetriever? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_current_music, container, false)

        myQueue = AddMusic.myQueue

        view.playPause.setOnClickListener {
            if(mediaPlayer != null && mediaPlayer!!.isPlaying){
                pauseMusic()
            }else if(mediaPlayer != null && !mediaPlayer!!.isPlaying){
                resumeMusic()
            }else if(mediaPlayer == null){
                initMusicPlayer()
            }
        }

        view.previousMusic.setOnClickListener {
            if(mediaPlayer != null){
                musicCurrentLength = 0
                mediaPlayer!!.seekTo(0)
            }
        }

        view.nextMusic.setOnClickListener{
            if(mediaPlayer != null){
                mediaPlayer!!.stop()
                initMusicPlayer()
            }
        }

        view.musicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    mediaPlayer!!.seekTo(progress*1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        return view
    }

    private fun initMusicPlayer(){
        mmr = MediaMetadataRetriever()
        if(myQueue.size != 0){
            mmr!!.setDataSource(context, myQueue[0].toUri())
            playPause.setImageResource(R.drawable.stop)
            mediaPlayer = MediaPlayer.create(context, myQueue[0].toUri())
            titleMusic.text = mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            if(mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) == null || mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) == null){
                titleMusic.text = myQueue[0].name
            }
            else{
                titleMusic.text = "${mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)} - ${mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)}"
            }
            mediaPlayer!!.start()
            initializeSeekBar()
            myQueue.removeAt(0)

            mediaPlayer!!.setOnCompletionListener {
                initMusicPlayer()
            }
        }else{
            playPause.setImageResource(R.drawable.play)
        }
    }

    private fun pauseMusic(){
        playPause.setImageResource(R.drawable.play)
        mediaPlayer!!.pause()
        musicCurrentLength = mediaPlayer!!.currentPosition
    }

    private fun resumeMusic() {
        playPause.setImageResource(R.drawable.stop)
        mediaPlayer!!.seekTo(musicCurrentLength)
        mediaPlayer!!.start()
    }

    private val MediaPlayer.seconds:Int
        get() {
            return this.duration / 1000
        }

    private val MediaPlayer.currentSeconds:Int
        get() {
            musicCurrentLength = currentPosition
            return this.currentPosition/1000
        }

    private fun initializeSeekBar() {
        view!!.musicSeekBar.max = mediaPlayer!!.seconds

        runnable = Runnable {
            view!!.musicSeekBar.progress = mediaPlayer!!.currentSeconds
            handler.postDelayed(runnable, 50)
        }
        handler.postDelayed(runnable, 50)
    }
}