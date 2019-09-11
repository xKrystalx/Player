package com.example.player

import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.net.toUri

class QueueMusic : Fragment() {

    companion object{
        lateinit var queueSongs: Array<String?>
    }
    private lateinit var listView: ListView
    private lateinit var arrayAdapter: ArrayAdapter<String?>
    var mmr:MediaMetadataRetriever? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_queue_music, container, false)
        listView = view.findViewById(R.id.queueMusic)
        refresh()
        listView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { _, _, position, _ ->
                Toast.makeText(context, "delete from queue", Toast.LENGTH_LONG).show()
                CurrentMusic.myQueue.removeAt(position)
                refresh()
                true
            }
        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser && CurrentMusic.flag==1){
            refresh()
        }
    }

    private fun refresh(){
        mmr = MediaMetadataRetriever()
        queueSongs = arrayOfNulls(CurrentMusic.myQueue.size)
        for(i in 0 until CurrentMusic.myQueue.size){
            mmr!!.setDataSource(context, CurrentMusic.myQueue[i].toUri())
            queueSongs[i] = mmr!!.extractMetadata((MediaMetadataRetriever.METADATA_KEY_TITLE))
            if(mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) == null || mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) == null){
                queueSongs[i] = CurrentMusic.myQueue[i].name
            }
            else{
                queueSongs[i] = "${mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)} - ${mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)}"
            }
        }
        arrayAdapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, queueSongs)
        listView.adapter = arrayAdapter
    }

}