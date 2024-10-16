package com.example.recyclomate.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.recyclomate.UI.MapsFragment
import com.example.recyclomate.UI.HomeFragment
import com.example.recyclomate.UI.ProfileFragment

class ViewPagerAdpater(fragmentActivity: FragmentManager): FragmentPagerAdapter(fragmentActivity) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return MapsFragment()
            }
            1 -> {
                return HomeFragment()
            }
            2 -> {
                return ProfileFragment()
            }
            else -> {
                return HomeFragment()
            }
        }
    }


}
