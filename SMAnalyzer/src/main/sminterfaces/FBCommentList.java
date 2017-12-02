package main.sminterfaces;

import smsdk.*;
import java.util.*;
import org.json.*;

public class FBCommentList {

    String before;
    String after;
    boolean hasNext;

    JSONArray data;
    int count;

    public FBCommentList(String id, FbAPI fbClient, String newAfter) {
        JSONObject comments;
        if (newAfter != null) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("after", newAfter);
            comments = fbClient.getConnections(id, "comments", params);
        } else {
            comments = fbClient.getConnections(id, "comments", null);
        }

        if (comments.has("paging")) {
            before = comments.getJSONObject("paging").getJSONObject("cursors").getString("before");
            after = comments.getJSONObject("paging").getJSONObject("cursors").getString("after");
            data = comments.getJSONArray("data");
            hasNext = comments.getJSONObject("paging").has("next");
            count = data.length();
        }
    }

    public String getAfter() {
        return after;
    }

    public String getBefore() {
        return before;
    }

    public int getCount() {
        return count;
    }

    public JSONArray getData() {
        return data;
    }

    public boolean getHasNext() {
        return hasNext;
    }
}
