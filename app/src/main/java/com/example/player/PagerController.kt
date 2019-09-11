package com.example.player

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerController(fm: FragmentManager, private val tabCounts: Int): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when(position){
            0->return AddMusic()
            1->return CurrentMusic()
            2->return QueueMusic()
        }
        return null
    }

    override fun getCount(): Int {
        return tabCounts
    }
}