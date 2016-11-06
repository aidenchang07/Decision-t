package com.decision_t;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TableFunction {
    public static String[] table_data(String table_id){
        String[] data = null;
        try {
            //不顯示封存的決策桌
            String sql = "SELECT *" +
                    "  FROM `Decision_tables`"+
                    " WHERE `ID` = "+table_id+";";
            String result = DBConnector.executeQuery(sql);
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonData = jsonArray.getJSONObject(0);
            data = new String[] {
                    jsonData.getString("ID"),
                    jsonData.getString("Name"),
                    jsonData.getString("Type"),
                    jsonData.getString("Info"),
                    jsonData.getString("Private"),
                    jsonData.getString("Complete"),
                    jsonData.getString("Lock"),
                    jsonData.getString("Final_decision"),
                    jsonData.getString("Account_ID")};

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
    //封存
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
    //取消封存
    public static void unarchive(String table_id, String user_id){
        String sql = "DELETE FROM `Decision_tables_archive`" +
                "           WHERE `Decision_tables_ID` = "+table_id+"" +
                "                  AND `Account_ID` = '"+user_id+"';";
        DBConnector.executeQuery(sql);
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
    //取得已加入的member列表
    public static ArrayList<String[]> getMember(String table_id){
        ArrayList<String[]> member = new ArrayList<String[]>();
        String[] data;
        try {
            String sql = "SELECT `c`.*" +
                    "  FROM `Decision_tables` `a`, `Decision_tables_member` `b`, `Account` `c`"+
                    " WHERE `a`.`ID` = `b`.`Decision_tables_ID`" +
                    "       AND `b`.`Account_ID` = `c`.`ID`" +
                    "       AND `a`.`ID` = "+table_id+";";
            String result = DBConnector.executeQuery(sql);
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                data = new String[] {
                        jsonData.getString("ID"),
                        jsonData.getString("Name")};
                member.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return member;
    }
    //取得未加入的member列表
    public static ArrayList<String[]> getNotYetMember(String table_id, String filter){
        ArrayList<String[]> member = new ArrayList<String[]>();
        String[] data;
        //把條件拆開並塞入%符號
        String[] ff = filter.split("");
        filter = "%";
        for (String s:ff) {
            filter += s + "%";
        }

        try {
            String sql = "SELECT *" +
                    "              FROM `Account`" +
                    "          WHERE `ID` NOT IN (SELECT `Account_ID`" +
                    "                                                        FROM `Decision_tables_member`" +
                    "                                                     WHERE `Decision_tables_ID` = " + table_id + ")" +
                    "                 AND `ID` NOT IN (SELECT `Account_ID`" +
                    "                                                         FROM `Decision_tables`" +
                    "                                                      WHERE `ID` = " + table_id + ")" +
                    "                 AND (`ID` LIKE '" + filter + "' OR `Name` LIKE '" + filter + "')";
            String result = DBConnector.executeQuery(sql);
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                data = new String[] {
                        jsonData.getString("ID"),
                        jsonData.getString("Name")};
                member.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return member;
    }
}
