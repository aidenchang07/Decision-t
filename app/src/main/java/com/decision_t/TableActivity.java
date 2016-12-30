package com.decision_t;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import com.github.clans.fab.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class TableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Toast tos;
    private MyAdapter myAdapter;
    private ArrayList<String[]> data;
    private ListView table_list;
    private String[] user_info;
    private UpdateScreenThead updateScreenThead;

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
                    Intent loginIntent = new Intent(TableActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                }
            }
        };

        //Toolbar 初始化
        Toolbar toolbar = (Toolbar) findViewById(R.id.table_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_main);

        //  + 按鈕觸發點
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                //跳轉到新增決策桌的畫面
                Intent tablecreateIntent = new Intent(TableActivity.this, TableCreateActivity.class);
                tablecreateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                tablecreateIntent.putExtra("user_info", user_info);
                startActivityForResult(tablecreateIntent, 0);
            }
        });
        //左側滑欄初始化
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.table_nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //取得使用者資料
        user_info = load_user_info();
        NavigationView nav= (NavigationView) findViewById(R.id.table_nav_view);
        View nav_view = nav.getHeaderView(0);
        TextView table_nav_email = (TextView)nav_view.findViewById(R.id.table_nav_email);
        table_nav_email.setText(user_info[0]);
        TextView table_nav_name = (TextView)nav_view.findViewById(R.id.table_nav_name);
        table_nav_name.setText(user_info[1]);

        //取得圖片測試
        ImageView table_nav_imageView = (ImageView)nav_view.findViewById(R.id.table_nav_imageView);
        //避免初次開啟APP時導致畢卡索載入空値網址失敗，用try catch包覆
        try{
            Picasso.with(this).load(user_info[2]).into(table_nav_imageView);
        }catch (Exception e){}


        tos = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        //取得使用者資料完畢

        //決策桌列表宣告
        table_list = (ListView) findViewById(R.id.table_list);
        table_list.setOnItemClickListener(click_table_list);
        table_list.setOnItemLongClickListener(long_click_table_list);

        //決策桌列表宣告END
        //顯示正在進行中的決策表
        getTableList(user_info[0]);
        //開始使用Timer 每隔3秒更新畫面
        updateScreenThead = UpdateScreenThead.getInstance();
        updateScreenThead.execute(handler);
        //顯示正在進行中的決策表完畢
    }

    //給多執行緒更新畫面的介面
    private Handler handler = new Handler(){
        public  void  handleMessage(Message msg) {
            super.handleMessage(msg);
            getTableList(user_info[0]);
        }
    };
    //離開程式
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(this)
                    .setTitle("確認視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    .setIcon(R.mipmap.logo)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    System.exit(0);
                                }
                            })
                    .setNegativeButton("取消",null).show();
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_archive) {
            // 顯示已封存的桌列表
            Intent archiveIntent = new Intent(TableActivity.this, ArchiveActivity.class);
            archiveIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            archiveIntent.putExtra("user_info", user_info);
            startActivityForResult(archiveIntent, 4);
        }else if (id == R.id.nav_logout) {
            // 執行登出的動作
            mAuth.signOut();
        }

        //按完之後關起來
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //取得使用者資料
    public String[] load_user_info()
    {
        try {
            //先取得預存的資料
            FileInputStream inStream=this.openFileInput("uu.txt");
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024];
            int length=-1;
            while((length=inStream.read(buffer))!=-1) {
                stream.write(buffer,0,length);
            }
            stream.close();
            inStream.close();
            String[] user_data = stream.toString().split(" ");
            String user_email = user_data[0];//取得Email
            String userPhotoUrl =  user_data[1];
            String user_name = "";
            String result = DBConnector.executeQuery("SELECT *" +
                    "                                                                    FROM `Account` " +
                    "                                                                 WHERE `ID` = '" + user_email + "'");
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                user_name = jsonData.getString("Name");
            }
            return new String[] {user_email, user_name, userPhotoUrl};
        } catch (FileNotFoundException e) {
            //找不到檔案就重新登入
            mAuth.signOut();
            e.printStackTrace();
        } catch (IOException e){
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new String[] {"", "", ""};
    }
    //取得決策桌列表
    public void getTableList(String user_id){
        //先清空資料
        data = new ArrayList<String[]>();
        try {
            //不顯示封存的決策桌
            String sql = "SELECT a.*" +
                    "  FROM `Decision_tables` `a` left join `Decision_tables_member` `b`" +
                    "    ON `a`.`ID` = `b`.`Decision_tables_ID`" +
                    " WHERE (`a`.`Account_ID` = '" + user_id + "'" +
                    "                    OR `b`.`Account_ID` = '" + user_id + "')"+
                    "        AND NOT EXISTS (SELECT *" +
                    "                                                 FROM `Decision_tables_archive`" +
                    "                                              WHERE `Decision_tables_ID`=`a`.`ID`" +
                    "                                                    AND `Account_ID`='"+user_id+"')" +
                    "  GROUP BY `a`.`ID`" +
                    "  ORDER BY `ID` DESC; ";
            String result = DBConnector.executeQuery(sql);
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
                        jsonData.getString("Lock"),
                        jsonData.getString("Final_decision"),
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
            TextView name = (TextView) convertView.findViewById(R.id.item_name);
            TextView id = (TextView) convertView.findViewById(R.id.table_id);
            ImageView table_status = (ImageView) convertView.findViewById(R.id.img_table_status);


            //塞資料
            String[] dd = data.get(position);
            name.setText(dd[1]);//決策桌名
            id.setText("ID:" + dd[0]);

            // 依據是否為主持人和桌類型來判斷選擇何種圖片
            if(!dd[8].equals(user_info[0])){
                // 不是主持人，會是黃色
                if(dd[2].equals("R")) {
                    table_status.setImageResource(R.drawable.ic_yellow_r_215dp);
                } else if(dd[2].equals("V")) {
                    table_status.setImageResource(R.drawable.ic_yellow_v_215dp);
                } else {
                    table_status.setImageResource(R.drawable.ic_yellow_t_215dp);
            }
            } else {
                // 是主持人，會是藍色
                if(dd[2].equals("R")) {
                    table_status.setImageResource(R.drawable.ic_blue_r_215dp);
                } else if(dd[2].equals("V")) {
                    table_status.setImageResource(R.drawable.ic_blue_v_215dp);
                } else {
                    table_status.setImageResource(R.drawable.ic_blue_t_215dp);
                }
            }
            return convertView;
        }
    }

    //決策桌表按下事件
    private AdapterView.OnItemClickListener click_table_list
            = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            switch (data.get(position)[2]){
                case "R":
                    Intent rTable = new Intent(TableActivity.this, R_Table_Activity.class);
                    rTable.putExtra("user_info", user_info);
                    rTable.putExtra("table_data", data.get(position));
                    startActivityForResult(rTable, 1);
                    break;
                case "V":
                    Intent vTable = new Intent(TableActivity.this, V_Table_Activity.class);
                    vTable.putExtra("user_info", user_info);
                    vTable.putExtra("table_data", data.get(position));
                    startActivityForResult(vTable, 2);
                    break;
                case "T":
                    Intent tTable = new Intent(TableActivity.this, T_Table_Activity.class);
                    tTable.putExtra("user_info", user_info);
                    tTable.putExtra("table_data", data.get(position));
                    startActivityForResult(tTable, 3);
                    break;
            }
        }
    };
    //決策桌表長按下事件
    private AdapterView.OnItemLongClickListener long_click_table_list = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            String[] function = {"封存", "刪除"};
            AlertDialog.Builder dialog = new AlertDialog.Builder(TableActivity.this);
            dialog.setTitle("操作");
            dialog.setItems(function, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            TableFunction.archive(data.get(position)[0], user_info[0]);
                            Toast.makeText(TableActivity.this, "封存", Toast.LENGTH_SHORT).show();
                            getTableList(user_info[0]);
                            break;
                        case 1:
                            AlertDialog.Builder check = new AlertDialog.Builder(TableActivity.this);
                            check.setTitle("確定刪除?");
                            check.setMessage("決策桌資料刪除後將不可挽回");
                            check.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(TableFunction.delete(data.get(position)[0], user_info[0])){
                                        Toast.makeText(TableActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(TableActivity.this, "您不是主持人", Toast.LENGTH_SHORT).show();
                                    }
                                    getTableList(user_info[0]);
                                }
                            });
                            check.show();
                            break;
                    }
                }
            });
            dialog.show();
            return true;
        }
    };
    @Override // 覆寫 onActivityResult，按下+後傳值回來時會執行此方法。
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //不管如何先更新列表再說
        getTableList(user_info[0]);
        switch (requestCode){
            case 0://按下+按鈕後畫面返回的動作
                break;
            case 1://進入R類型決策桌後返回的動作
                break;
            case 2://進入V類型決策桌後返回的動作
                break;
            case 3://進入T類型決策桌後返回的動作
                break;
        }
    }

}
