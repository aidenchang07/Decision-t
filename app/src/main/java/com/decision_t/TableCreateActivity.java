package com.decision_t;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TableCreateActivity extends AppCompatActivity {

    private Button registerButton;
    private Button tButton;
    private Button voteButton;
    private Button randomButton;
    private TextView table_id_or_name;
    String[] user_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_create);

        //初始化按鈕
        registerButton = (Button) findViewById(R.id.registerButton);
        tButton = (Button) findViewById(R.id.tButton);
        voteButton = (Button) findViewById(R.id.voteButton);
        randomButton = (Button) findViewById(R.id.randomButton);

        //初始化按鈕的監聽器
        registerButton.setOnClickListener(new handlerButton());
        tButton.setOnClickListener(new handlerButton());
        voteButton.setOnClickListener(new handlerButton());
        randomButton.setOnClickListener(new handlerButton());

        //初始化TextView
        table_id_or_name = (TextView) findViewById(R.id.table_id_or_name);

        //Toolbar 初始化
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("創建決策桌");

        //取得使用者資料
        Intent it = getIntent();
        user_info = it.getStringArrayExtra("user_info");
    }

    private class handlerButton implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.registerButton:
                    //加入決策桌
                    registerTable(table_id_or_name.getText().toString(), user_info[0]);
                    break;
                case R.id.tButton:
                    //創建T桌
                    createTable(table_id_or_name.getText().toString(), "T", user_info[0]);
                    break;
                case R.id.voteButton:
                    //創建投票桌
                    createTable(table_id_or_name.getText().toString(), "V", user_info[0]);
                    break;
                case R.id.randomButton:
                    //創建隨機桌
                    createTable(table_id_or_name.getText().toString(), "R", user_info[0]);
                    break;
            }
        }
    }
    private void createTable(String table_name, String table_type, String user_id){
        table_id_or_name.setText(table_id_or_name.getText().toString().trim());
        if(table_id_or_name.getText().toString().equals("")){
            Toast.makeText(this, "請輸入決策桌名稱", Toast.LENGTH_SHORT).show();
            return;
        }
        String sql = "INSERT INTO `Decision_tables` (\n" +
                "`Name` ,\n" +
                "`Type` ,\n" +
                "`Private` ,\n" +
                "`Complete` ,\n" +
                "`Account_ID`\n" +
                ")\n" +
                "VALUES ('" + table_name + "', '" +table_type + "', 'N', 'N', '" + user_id + "'\n); ";
        DBConnector.executeQuery(sql);
        finish();//未來不只關閉創建畫面還要直接進去桌畫面
    }

    private void registerTable(String table_id, String user_id){
        String sql;
        String result;
        JSONArray jsonArray;
        try {
            //檢查是否有輸入
            table_id_or_name.setText(table_id_or_name.getText().toString().trim());
            if(table_id_or_name.getText().toString().equals("")){
                Toast.makeText(this, "請輸入決策桌ID", Toast.LENGTH_SHORT).show();
                return;
            }
            //是否存在table
            sql = "SELECT * FROM `Decision_tables` WHERE `ID`='"+table_id+"';";
            result = DBConnector.executeQuery(sql);
            jsonArray = new JSONArray(result);
            if(jsonArray.length() == 0){
                Toast.makeText(this, "無此決策桌ID", Toast.LENGTH_SHORT).show();
                return;
            }else{
                JSONObject jsonData = jsonArray.getJSONObject(0);//只有一筆
                //是否為主持人或成員
                sql = "SELECT a.*" +
                        " FROM `Decision_tables` `a` left join `Decision_tables_member` `b`" +
                        "   ON `a`.`ID` = `b`.`Decision_tables_ID`" +
                        "WHERE `ID`='"+table_id+"' "+
                        "       AND(`a`.`Account_ID` = '" + user_id +"'"+
                        "                  OR `b`.`Account_ID` = '" + user_id +"')";
                result = DBConnector.executeQuery(sql);
                jsonArray = new JSONArray(result);
                if(jsonArray.length() > 0){
                    Toast.makeText(this, "您已在此決策桌中", Toast.LENGTH_SHORT).show();
                    return;
                }
                //是否為私密
                if(jsonData.getString("Private").equals("Y")){
                    Toast.makeText(this, "此決策桌不公開\n請找主持人加入", Toast.LENGTH_SHORT).show();
                    return;
                }

                //加入為成員
                sql = "INSERT INTO `Decision_tables_member` (`Decision_tables_ID` , `Account_ID` )\n" +
                        "VALUES ('"+table_id+"', '"+user_id+"');";
                result = DBConnector.executeQuery(sql);
                finish();//未來不只關閉創建畫面還要直接進去桌畫面
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "炸了", Toast.LENGTH_SHORT).show();
        }

    }

}
