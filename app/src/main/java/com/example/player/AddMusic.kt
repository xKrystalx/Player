package com.example.player

import android.Manifest
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class AddMusic : Fragment() {

    companion object{
        var myQueue = ArrayList<File>()
    }
    var allMusics = ArrayList<File>()
    lateinit var songs: Array<String?>
    lateinit var arrayAdapter: ArrayAdapter<String?>
    lateinit var listView: ListView
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var currentMusic: View
    var mmr:MediaMetadataRetriever? = null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_music, container, false)

        listView = view.findViewById(R.id.myMusics)
        viewPager = activity!!.findViewById(R.id.viewPager)
        tabLayout = activity!!.findViewById(R.id.tabLayout)
        currentMusic = activity!!.findViewById(R.id.currentMusicFragment)

        Dexter.withActivity(activity).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(object: PermissionListener{
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                allMusics = findAllMusics(Environment.getExternalStorageDirectory())
                songs = arrayOfNulls(allMusics.size)

                mmr = MediaMetadataRetriever()
                for(i in 0 until allMusics.size){
                    mmr!!.setDataSource(context, allMusics[i].toUri())
                    if(mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) == null || mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) == null){
                        songs[i] = allMusics[i].name
                    }
                    else{
                        songs[i] = "${mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)} - ${mmr!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)}"
                    }
                }

                arrayAdapter = ArrayAdapter(activity!!, android.R.layout.simple_list_item_1, songs)
                listView.adapter = arrayAdapter

                listView.onItemLongClickListener =
                    AdapterView.OnItemLongClickListener { _, _, position, _ ->
                        Toast.makeText(context, "add to Queue", Toast.LENGTH_LONG).show()
                        myQueue.add(allMusics[position])
                        true
                    }
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest?,
                token: PermissionToken?
            ){
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
            }

        }).check()

        return view
    }

    fun findAllMusics(file: File): ArrayList<File>{
        val allMusics = ArrayList<File>()
        val files = file.listFiles()
        for(currentFile in files){
            if(currentFile.isDirectory && !currentFile.isHidden){
                allMusics.addAll(findAllMusics(currentFile))
            }else{
                if(currentFile.name.endsWith("mp3")){
                    allMusics.add(currentFile)
                }
            }
        }
        return allMusics
    }

}
