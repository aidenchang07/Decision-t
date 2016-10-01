package com.decision_t;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TableCreateActivity extends AppCompatActivity {

    private Button tButton;
    private Button voteButton;
    private Button randomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_create);

        //初始化按鈕
        tButton = (Button) findViewById(R.id.tButton);
        voteButton = (Button) findViewById(R.id.voteButton);
        randomButton = (Button) findViewById(R.id.randomButton);

        //初始化按鈕的監聽器
        tButton.setOnClickListener(new handlerButton());
        voteButton.setOnClickListener(new handlerButton());
        randomButton.setOnClickListener(new handlerButton());
    }

    private class handlerButton implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tButton:
                    //創建T桌
                    break;
                case R.id.voteButton:
                    //創建投票桌
                    break;
                case R.id.randomButton:
                    //創建隨機桌

                    //以下是我測試隨機桌的Layout擺得好不好，你可不於理會，如果最後確定用不到可刪除
                    Intent r_tableIntent = new Intent(TableCreateActivity.this, R_Table_Activity.class);
                    r_tableIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(r_tableIntent);
                    break;
            }
        }
    }

}
