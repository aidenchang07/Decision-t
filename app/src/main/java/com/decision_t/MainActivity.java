package com.decision_t;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabaseUsers;
    private Button logoutButton;
    Toast tos;
    MyAdapter myAdapter;
    ArrayList<String[]> data;
    ListView table_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_activity_main);


        //若在主畫面登出，就會跳轉到登入畫面
        //若無登入(User==null)也會跳到登入畫面
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //若無使用者資料則跳回登入畫面
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                }
            }
        };

        //Toolbar 初始化
        Toolbar toolbar = (Toolbar) findViewById(R.id.table_toolbar);
        setSupportActionBar(toolbar);

        //  + 按鈕觸發點
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //跳轉到新增決策桌的畫面
                Intent tablecreateIntent = new Intent(MainActivity.this, TableCreateActivity.class);
                tablecreateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(tablecreateIntent);
            }
        });
        //左側滑欄初始化
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //JSON取得資料前置動作
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        //JSON取得資料前置動作END

        //決策桌列表宣告
        data = new ArrayList<String[]>();
        table_list = (ListView) findViewById(R.id.table_list);
        table_list.setOnItemClickListener(click_table_list);
        //決策桌列表宣告END


        TextView table_nav_name = (TextView)findViewById(R.id.table_nav_name);
        //table_nav_name.setText("");
        TextView table_nav_email = (TextView)findViewById(R.id.table_nav_email);
        //table_nav_email.setText("");
        tos = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        String user_email = load_user_email();
        tos.setText(user_email);
        tos.show();
        getTableList(user_email);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //新增 FirebaseAuth 的監聽事件
        mAuth.addAuthStateListener(mAuthListener);
    }

    //離開程式
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    System.exit(0);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                }
                            }).show();
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_archive) {
            // 執行已封存的動作
        }else if (id == R.id.nav_setting) {
            // 執行設定的動作
        }else if (id == R.id.nav_logout) {
            // 執行登出的動作
            mAuth.signOut();
        }
        //按完之後關起來
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //取得使用者EMAIL
    public String load_user_email()
    {
        try {
            FileInputStream inStream=this.openFileInput("uu.txt");
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024];
            int length=-1;
            while((length=inStream.read(buffer))!=-1) {
                stream.write(buffer,0,length);
            }
            stream.close();
            inStream.close();
            return stream.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            return null;
        }
        return null;
    }
    //取得決策桌列表
    public void getTableList(String user_email){
        try {
            String result = DBConnector.executeQuery("SELECT a.*\n" +
                    "  FROM `Decision_tables` `a` left join `Decision_tables_member` `b`\n" +
                    "    ON `a`.`ID` = `b`.`Decision_tables_ID`\n" +
                    " WHERE `a`.`Account_ID` = \"" + user_email + "\"\n" +
                    "    OR `b`.`Account_ID` = \"" + user_email + "\"");
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                data.add(new String[] {
                        jsonData.getString("ID"),
                        jsonData.getString("Name"),
                        jsonData.getString("Type"),
                        jsonData.getString("Info"),
                        jsonData.getString("Private"),
                        jsonData.getString("Complete"),
                        jsonData.getString("Account_ID")});
            }

            myAdapter = new MyAdapter(this);
            table_list.setAdapter(myAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater myInflater;
        public MyAdapter(Context c) {
            myInflater = LayoutInflater.from(c);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data.get(position)[1];
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        //重點：產生每一列的view
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //產生一個table_list_view的view
            // TODO Auto-generated method stub
            convertView = myInflater.inflate(R.layout.table_list_view, null);

            //設定元件內容
            ImageView logo = (ImageView) convertView.findViewById(R.id.imglogo);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView list = (TextView) convertView.findViewById(R.id.txtengname);
            ImageView table_status = (ImageView) convertView.findViewById(R.id.img_table_status);


            //塞資料
            String[] dd = data.get(position);
            if(dd[5].equals("Y")){//已完成未完成圖片
                logo.setImageResource(R.drawable.table_list_ok);
            }else{
                logo.setImageResource(R.drawable.table_list_ing);
            }
            //避免過長
            if(dd[1].length()>11)dd[1] = dd[1].substring(0,10) + "...";
            name.setText(dd[1]);//決策桌名
            if(dd[3].length()>14)dd[3] = dd[3].substring(0,14) + "...";
            list.setText(dd[3]);//Info
            //若是成員而不是主持人則顯示圖片6v
            if(!dd[6].equals("sdjsddsd@gmail.com")){
                table_status.setImageResource(R.drawable.table_list_shared);
            }
            return convertView;
        }
    }
    //決策桌表按下事件
    private AdapterView.OnItemClickListener click_table_list
            = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView tt = (TextView) view.findViewById(R.id.name);
            tos.setText(tt.getText());
            tos.show();
        }
    };
}
