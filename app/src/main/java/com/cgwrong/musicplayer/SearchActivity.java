package com.cgwrong.musicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String content;
    private ListView listView;
    private List<Music> musicList=new ArrayList<>();
    private MusicAdapter musicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActivityCollector.addActivity(this);

        Intent intent=getIntent();
        content=intent.getStringExtra("content");

        init();

        musicAdapter=new MusicAdapter(MyApplication.getContext(),R.layout.music_layout,musicList);
        listView=(ListView) findViewById(R.id.search_ListView);
        listView.setAdapter(musicAdapter);

        toolbar=(Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        //设置返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private Document doc;
    Runnable runnable=new Runnable() {
        @Override
        public void run() {

            try {
                Connection conn=Jsoup.connect("http://music.2333.me/?name="+content+"&type="+"netease");
                conn.header("User-Agent","Mozilla/5.0(X11;Linux x86_64;rv:32.0) Gecko/  20100101 Firefox/32.0");
                doc= conn.get();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public void init(){

        new Thread(runnable).start();
        Elements elements=doc.select("body");
        for(Element element:elements){
            String title=element.getElementsByClass("aplayer-list-title").text();
            String artist=element.getElementsByClass("aplayer-list-author").text();
            Music music=new Music(title,artist,artist+" - "+artist,"","");
            musicList.add(music);
        }
    }
}
