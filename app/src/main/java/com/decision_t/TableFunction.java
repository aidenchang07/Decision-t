package com.decision_t;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class TableFunction {
    public static void archive(String table_id, String user_id){
        try {
            //先檢查是否已經是封存狀態
            String sql = "SELECT * FROM `Decision_tables_archive` WHERE `Decision_tables_ID`='"+table_id+"' AND `Account_ID`='"+user_id+"';";
            String result = DBConnector.executeQuery(sql);
            JSONArray jsonArray = new JSONArray(result);
            if(jsonArray.length() > 0) {
                return;
            }
            sql = "INSERT INTO `Decision_tables_archive` (`Decision_tables_ID`, `Account_ID`)" +
                    "VALUES ('"+table_id+"', '"+user_id+"')";
            DBConnector.executeQuery(sql);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        return;
    }
    public static Boolean delete(String table_id, String user_id) {
        try {
            //先檢查是否為主持人
            String sql = "SELECT * FROM `Decision_tables` WHERE `ID`='"+table_id+"' AND `Account_ID`='"+user_id+"';";
            String result = DBConnector.executeQuery(sql);
            JSONArray jsonArray = new JSONArray(result);
            if(jsonArray.length() == 0) {
                return false;
            }
            sql = "DELETE FROM `Decision_tables`" +
                    "WHERE `ID` = '"+table_id+"';";
            DBConnector.executeQuery(sql);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
