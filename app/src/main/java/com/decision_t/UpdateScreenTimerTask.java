package com.decision_t;

import java.util.TimerTask;

/**
 * Created by Cloud on 2016/12/30.
 */

public class UpdateScreenTimerTask extends TimerTask{
    private UpdateScreen updateScreen;

    public UpdateScreenTimerTask(UpdateScreen updateScreen){
        this.updateScreen = updateScreen;
    }
    @Override
    public void run() {
        updateScreen.update();
    }
}
