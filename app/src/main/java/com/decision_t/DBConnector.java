package com.decision_t;

import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DBConnector {
    public static String executeQuery(String query_string) {
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
        String result = "";
        HttpURLConnection urlConnection=null;
        InputStream is =null;
        try {
            URL url=new URL("http://mysql.mis.knjc.edu.tw/~proj1/test.php");  //php的位置
            urlConnection=(HttpURLConnection) url.openConnection();//對資料庫打開連結
            urlConnection.setRequestMethod("POST");
            query_string = "sql=" + query_string;

            DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
            dos.write(query_string.getBytes());
            dos.flush();
            urlConnection.connect();//接通資料庫
            is=urlConnection.getInputStream();//從database 開啟 stream

            BufferedReader bufReader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            is.close();
            result = builder.toString();
        } catch(Exception e) {
            Log.e("log_tag", e.toString());
        }

        return result;
    }
}