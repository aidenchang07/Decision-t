package com.decision_t;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Cloud on 2016/12/30.
 */

public class UpdateScreenThead {
    private static UpdateScreenThead updateScreenThead = new UpdateScreenThead();
    private Thread thread;
    private Handler handler = new Handler();
    private Handler copyHandler;
    private boolean run;

    private UpdateScreenThead(){
        run = false;
        //避免直接抽換，額外取出
        copyHandler = this.handler;
        thread = new Thread(new Runnable(){
            public void run(){
                try {
                    while(run){
                        Thread.sleep(3000);
                        Message msg = new Message();
                        handler.sendMessage(msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    public void execute(final Handler handler){
        run = false;
        this.handler = handler;
        run = true;
    }

    public static UpdateScreenThead getInstance(){
        return updateScreenThead;
    }
}
