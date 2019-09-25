package com.example.game;

import android.app.Application;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by YHF at 13:30 on 2019-09-25.
 */

public class SoundPoolManager {

    private SoundPool soundpool;
    private Random random = new Random();
    private int nextSound;
    private Map<Integer, Integer> soundmap = new HashMap<Integer, Integer>();
    private static SoundPoolManager instance = new SoundPoolManager();
    public static SoundPoolManager getInstance(){
        return instance;
    }
    private SoundPoolManager(){

    }

    public void init(){
        //当前系统的SDK版本大于等于21(Android 5.0)时
        if(Build.VERSION.SDK_INT > 21){
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频数量
            builder.setMaxStreams(10);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);//STREAM_MUSIC
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            soundpool = builder.build();
        }else{
            soundpool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 0);
        }

        soundmap.put(1, soundpool.load(BaseApplication.getContext(), R.raw.a1, 1));
        soundmap.put(2, soundpool.load(BaseApplication.getContext(), R.raw.a2, 1));
        soundmap.put(3, soundpool.load(BaseApplication.getContext(), R.raw.a3, 1));
        soundmap.put(4, soundpool.load(BaseApplication.getContext(), R.raw.a4, 1));
        soundmap.put(5, soundpool.load(BaseApplication.getContext(), R.raw.a5, 1));
        soundmap.put(0, soundpool.load(BaseApplication.getContext(), R.raw.a6, 1));
        soundmap.put(7, soundpool.load(BaseApplication.getContext(), R.raw.point_false, 1));
        soundmap.put(8, soundpool.load(BaseApplication.getContext(), R.raw.point_right, 1));
    }

    public void playRight(){
        soundpool.play(soundmap.get(8), 1, 1, 0, 0, 1);
    }

    public void playFalse(){
        soundpool.play(soundmap.get(7), 1, 1, 0, 0, 1);
    }

    public void playNext(){
        nextSound = random.nextInt(6);
        soundpool.play(soundmap.get(nextSound), 1, 1, 0, 0, 1);
    }

}
