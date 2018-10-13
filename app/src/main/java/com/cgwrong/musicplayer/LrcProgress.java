package com.cgwrong.musicplayer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/3/15.
 * 处理歌词的类
 */

public class LrcProgress {

    private List<LrcContent> lrcList;           //List集合存放歌词内容对象
    private LrcContent mLrcContent;             //声明一个歌词内容对象

    //无参构造函数用来实例化对象
    public LrcProgress(){
        mLrcContent=new LrcContent();
        lrcList=new ArrayList<LrcContent>();
    }

    //读取歌词
    public String readLRC(String path){
            //定义一个StringBuilder对象，用来存放歌词内容
            StringBuilder stringBuilder = new StringBuilder();
            String p=path.replace(".mp3",".lrc");
            p=p.replace(".m4a",".lrc");
            p=p.replace(".flac",".lrc");
            File f = new File(p);

            try {
                //创建一个文件输入流对象
                FileInputStream fis = new FileInputStream(f);
                InputStreamReader isr = new InputStreamReader(fis, "utf-8");
                BufferedReader br = new BufferedReader(isr);

                BufferedInputStream bis=new BufferedInputStream(fis);
                bis.mark(4);
                byte[] first3bytes=new byte[3];
                //找到文件前三个字节并判断文件类型
                bis.read(first3bytes);
                bis.reset();
                if (first3bytes[0]==(byte) 0xEF && first3bytes[1]==(byte) 0xBB && first3bytes[2]==(byte) 0xBF){
                    //utf-8
                    br=new BufferedReader(new InputStreamReader(bis,"utf-8"));
                } else if(first3bytes[0]==(byte) 0xFF && first3bytes[1]==(byte) 0xFE){
                    br=new BufferedReader(new InputStreamReader(bis,"unicode"));
                } else if(first3bytes[0]==(byte) 0xFE && first3bytes[1]==(byte) 0xFF){
                    br=new BufferedReader(new InputStreamReader(bis,"utf-16be"));
                } else if(first3bytes[0]==(byte) 0xFF && first3bytes[1]==(byte) 0xFF){
                    br=new BufferedReader(new InputStreamReader(bis,"utf-16le"));
                } else{
                    br=new BufferedReader(new InputStreamReader(bis,"GBK"));
                }

                String s = "";
                while ((s = br.readLine()) != null) {
                    //替换字符
                    s = s.replace("[", "");
                    s = s.replace("]", "@");

                    //分离"@"字符
                    String splitLrcData[] = s.split("@");
                    if (splitLrcData.length > 1) {
                        mLrcContent.setLrcContent(splitLrcData[1]);

                        //处理歌词获取歌曲时间
                        int lrcTime = time2Str(splitLrcData[0]);
                        mLrcContent.setLrcTime(lrcTime);

                        //添加进数组列表
                        lrcList.add(mLrcContent);

                        //新创建歌词内容对象
                        mLrcContent = new LrcContent();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                stringBuilder.append("没有找到歌词");
            } catch (IOException e) {
                e.printStackTrace();
                stringBuilder.append("没有找到歌词");
            }
            return stringBuilder.toString();
    }

    /**
     * 解析歌词时间
     * 歌词内容格式如下
     * [00:02:32]陈奕迅
     * [00:03:43]好久不见
     */
    public int time2Str(String timeStr){
        timeStr=timeStr.replace(":",".");
        timeStr=timeStr.replace(".","@");

        String timeData[]=timeStr.split("@");       //将时间分割成字符串数组

        //分离出分、秒并转换为整型
        int minute=Integer.parseInt(timeData[0]);
        int second=Integer.parseInt(timeData[1]);
        int millisecond=Integer.parseInt(timeData[2]);

        //计算上一行与下一行的时间转换为毫秒数
        int currentTime=(minute*60+second)*1000+millisecond*10;
        return currentTime;
    }

    public List<LrcContent> getLrcList(){
        return lrcList;
    }
}
