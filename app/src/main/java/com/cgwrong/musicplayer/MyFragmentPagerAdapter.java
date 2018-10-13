package com.cgwrong.musicplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/4/17.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{

    private List<Fragment> list;

    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list){
        super(fm);
        this.list=list;
    }

    @Override
    public Fragment getItem(int arg0){
        return list.get(arg0);
    }

    @Override
    public int getCount(){
        return list.size();
    }

}
