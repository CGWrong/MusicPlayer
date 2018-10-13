package com.cgwrong.musicplayer;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by admin on 2018/4/1.
 */

public class MusicDialog extends Dialog{

    public MusicDialog(@NonNull Context context) {
        super(context);
    }

    public static class Builder{

        private Context context;

        private String size;
        private String name;
        private String url;

        private View layout;
        private MusicDialog dialog;

        public Builder(Context context){
            dialog=new MusicDialog(context);
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout=inflater.inflate(R.layout.music_dialog_layout,null);
            dialog.addContentView(layout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public Builder setSize(String size){
            this.size=size;
            return this;
        }

        public Builder setName(String name){
            this.name=name;
            return this;
        }

        public Builder setName(int name){
            this.name=(String) context.getText(name);
            return this;
        }

        public Builder setUrl(String url){
            this.url=url;
            return this;
        }

        public MusicDialog create(){
            if(name!=null)
                ((TextView) layout.findViewById(R.id.file_name_text)).setText(name);
            if(url!=null)
                ((TextView) layout.findViewById(R.id.url_text)).setText(url);
            if(size!=null)
                ((TextView) layout.findViewById(R.id.size_text)).setText(size);
            dialog.setContentView(layout);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            return dialog;
        }
    }
}
