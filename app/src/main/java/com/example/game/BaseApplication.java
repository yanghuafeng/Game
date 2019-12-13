package com.example.game;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by YHF at 13:37 on 2019-09-25.
 */

public class BaseApplication extends Application {

    private static Context mContext = null;
    private static Handler mHandler = null;
    private static boolean canJump = false;
    private static boolean canCheat = false;


    public static Context getContext() {
        return mContext;
    }
    public static Handler getHandler() {
        return mHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mHandler = new Handler(mContext.getMainLooper());
        SoundPoolManager.getInstance().init();
    }

    public static boolean getCanJump(){

            return canJump;
    }

    public static void setCanJump(boolean b) {
        canJump = b;
    }

    public static boolean getCanCheat() {
        return canCheat;
    }

    public static void setCanCheat(boolean b) {
        canCheat = b;
    }
}
