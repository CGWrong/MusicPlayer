package com.cgwrong.musicplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/4/15.
 */

public class AddToListDialog extends Dialog{

    public AddToListDialog(@NonNull Context context) {
        super(context);
    }

    public static class Builder{

        private Context context;

        private View layout;
        private AddToListDialog dialog;

        public Builder(Context context){
            this.context=context;
            dialog=new AddToListDialog(context);
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout=inflater.inflate(R.layout.add_to_list_dialog,null);
            dialog.addContentView(layout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public AddToListDialog setListView(){

            List<String> listName=new ArrayList<>();
            MusicListAdapter adapter;
            ListView listView=(ListView) layout.findViewById(R.id.list);
            TextView textView=(TextView) layout.findViewById(R.id.text2);
            textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            String str;
            SharedPreferences pref=MyApplication.getContext().getSharedPreferences("ListName", Context.MODE_PRIVATE);
            listView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            int i=1;
            if(!pref.contains(Integer.toString(i))){
                listView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
            while(pref.contains(Integer.toString(i))){
                str=pref.getString(Integer.toString(i),"");
                listName.add(str);
                i++;
            }
            adapter=new MusicListAdapter(MyApplication.getContext(),R.layout.music_list_item,listName);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView1=(TextView) view.findViewById(R.id.list_name);
                    Intent intent=new Intent("MusicChange");
                    intent.putExtra("Music","list");
                    intent.putExtra("listName",textView1.getText());
                    context.sendBroadcast(intent);
                    dialog.dismiss();
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = View.inflate(context, R.layout.new_list_dialog, null);
                    final EditText editText=(EditText) view.findViewById(R.id.new_list_edit);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("新建歌单");
                    builder.setView(view);
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int which) {
                            if(editText.getText()!=null) {
                                SharedPreferences.Editor editor = context.getSharedPreferences("ListName", Context.MODE_PRIVATE).edit();
                                int i = 1;
                                while (context.getSharedPreferences("ListName", Context.MODE_PRIVATE).contains(Integer.toString(i))) {
                                    i++;
                                }
                                editor.putString(Integer.toString(i), editText.getText().toString());
                                editor.apply();
                                Toast.makeText(context, "创建成功", Toast.LENGTH_SHORT);
                            }else{
                                Toast.makeText(context, "名字不能为空", Toast.LENGTH_SHORT);
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            });

            return dialog;
        }

        public AddToListDialog create(){

            dialog.setContentView(layout);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            return dialog;

        }
    }
}
