package com.decision_t.ui.vote;

import com.decision_t.manager.DBConnector;

import org.json.JSONArray;
import org.json.JSONException;

public class VoteFunction {
    public static boolean canVote(String table_id, String user_id) {
        try {
            //顯示決策桌的項目
            String sql = "SELECT *" +
                    "              FROM `Tables_item` `a`, `V_item_score` `b`" +
                    "          WHERE `a`.`ID` = `b`.`Item_ID`" +
                    "                 AND `a`.`Decision_tables_ID` = '" + table_id + "'" +
                    "                 AND `b`.`Account_ID` = '" + user_id + "'";
            String result = DBConnector.executeQuery(sql);
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray.length() > 0) {//投過票了
                return false;
            } else {//還沒投過
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
