package com.cgwrong.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/4/15.
 */

public class MusicListFragment extends Fragment{

    private List<String> listName=new ArrayList<>();
    private MusicListAdapter adapter;
    private ListView listView;
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        View view=inflater.inflate(R.layout.music_list_fragment,container,false);
        listView=(ListView) view.findViewById(R.id.list_list_view);
        textView=(TextView) view.findViewById(R.id.tips);
        init();

        adapter=new MusicListAdapter(MyApplication.getContext(),R.layout.music_list_item,listName);
        listView.setAdapter(adapter);

        //listview点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        return view;
    }

    public void init(){
        String str;
        SharedPreferences pref=MyApplication.getContext().getSharedPreferences("ListName", Context.MODE_PRIVATE);
        if(pref==null){
            listView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }else{
            listView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            int i=1;
            while(pref.contains(Integer.toString(i))){
                str=pref.getString(Integer.toString(i),"");
                listName.add(str);
                i++;
            }
        }
    }

}
