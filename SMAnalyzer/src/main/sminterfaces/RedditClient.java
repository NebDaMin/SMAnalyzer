package main.sminterfaces;

import smsdk.reddit.*;
import smsdk.*;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Date;
import java.util.List;

public class RedditClient {

    private final String ClientId = "tgM2m86Uy_uwKw";
    private final String ClientSecret = "9PWGdLhnmTE8S0biLTHbKOtGHe0";
    private RedditAPI RedditClient;
    private RedditToken Token;
    private RedditOAuth RedditOAuth;
    private ArrayList<NormalizedComment> PostArrayList;

    public RedditClient() {
        RedditClient = new RedditAPI(ClientId, ClientSecret);
        RedditOAuth = new RedditOAuth(RedditClient);
        Token = RedditOAuth.tokenAppOnly();
        PostArrayList = new ArrayList();
    }

    public void fetchComments(String id) {
        RedditRequest request = new RedditRequest("/comments/" + id + ".json");
        ArrayList<JSONObject> list = parseJSONArrayFromString(RedditClient.get(Token, request));
        addOriginalPostToArrayList(list.get(0));
        addCommentsToArrayList(list.get(1), true, id);
    }

    public ArrayList<JSONObject> parseJSONArrayFromString(String jsonText) {
        JSONArray json = Utility.parseJsonArray(jsonText);
        ArrayList<JSONObject> array = new ArrayList<JSONObject>();
        for (int i = 0; i < json.length(); i++) {
            array.add(json.getJSONObject(i));
        }
        return array;
    }

    public ArrayList<JSONObject> parseJSONArray(JSONArray jsonArray) {
        ArrayList<JSONObject> array = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++) {
            array.add(jsonArray.getJSONObject(i));
        }
        return array;
    }

    public JSONObject parseJSONObject(String jsonText) {
        JSONObject json = Utility.parseJson(jsonText);
        return json;
    }

    public int addCommentsToArrayList(JSONObject obj, boolean includeChildren, String id) {
        if (!obj.has("data")) {
            return 1;
        } else {
            boolean searched = false;
            JSONObject data = obj.getJSONObject("data");
            if(data.has("depth")){
                if(data.getInt("depth") == 10){ return 1; }
            }
            JSONArray comments;
            if (data.has("children")) {
                comments = data.getJSONArray("children");
            } else if (data.has("replies") && !data.get("replies").equals("")) {
                comments = data.getJSONObject("replies").getJSONObject("data").getJSONArray("children");
            } else {
                return 1;
            }
            if (comments.length() == 0) {
                RedditRequest commentRequest = new RedditRequest("/comments/" + id + ".json");
                commentRequest.addQueryParameter("comment", data.getString("id"));
                ArrayList<JSONObject> moreChildrenComments = parseJSONArrayFromString(RedditClient.get(Token, commentRequest));
                for (JSONObject childObj : moreChildrenComments) {
                    System.out.println(childObj);
                }
            } else {
                for (int i = 0; i < comments.length() - 1 || searched == false; i++) {
                    searched = true;
                    JSONObject comment = comments.getJSONObject(i);
                    if(comment.getJSONObject("data").has("replies") && !comment.getJSONObject("data").get("replies").equals("")){
                        if(comment.getJSONObject("data").getJSONObject("replies").getString("kind").equals("more")){
                            System.out.println("MORE COMMENTS");
                            RedditRequest moreChildrenRequest = new RedditRequest("/api/morechildren");
                            moreChildrenRequest.addBodyParameter("api_type", "json");
                            moreChildrenRequest.addBodyParameter("link_id", "t3_" + id);
                            List<Object> children = comment.getJSONObject("data").getJSONArray("children").toList();
                            String childrenIds = "";
                            for (int j = 0; j < children.size(); j++) {
                                System.out.println(children.get(j).toString());
                                childrenIds = childrenIds + children.get(j).toString();
                                if (j < children.size() - 1) {
                                    childrenIds = childrenIds + ",";
                                }
                            }
                            moreChildrenRequest.addBodyParameter("children", childrenIds);
                            moreChildrenRequest.addBodyParameter("limit_children", "false");
                            JSONObject result = parseJSONObject(RedditClient.post(Token, moreChildrenRequest));
                            for (int k = 0; k < children.size(); k++) {
                            }
                        }
                    }
                    NormalizedComment normComment = new NormalizedComment();
                    normComment.setFromReddit(comment.getJSONObject("data"));
                    PostArrayList.add(normComment);
                    
                    if (!comment.getJSONObject("data").has("created")) {
                        System.out.println("STOP");
                        break;
                    }
                    
                    Double timeDouble = comment.getJSONObject("data").getDouble("created");
                    Date time = new Date(timeDouble.longValue() * 1000);
                    String text;
                    if (comment.getJSONObject("data").has("body")) {
                        text = comment.getJSONObject("data").getString("body");
                    } else {
                        text = "[No Text]";
                    }
                    System.out.println("Comment: " + text + "\nCreated on: " + time);
                    if (includeChildren == true) {
                        addCommentsToArrayList(comment, includeChildren, id);
                    }
                }
            }
        }
        return 0;
    }

    public void addOriginalPostToArrayList(JSONObject obj) {
        if (obj.has("data")) {
            JSONObject data = obj.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data");
            NormalizedComment normComment = new NormalizedComment();
            normComment.setFromReddit(data);
            PostArrayList.add(normComment);
            String text;
            if (data.has("title")) {
                text = data.getString("title");
            } else {
                text = "[No Text]";
            }
            Double timeDouble = data.getDouble("created");
            Date time = new Date(timeDouble.longValue() * 1000);
            System.out.println("Comment: " + text + "\nCreated on: " + time);
        }
    }

    public void clearArray() {
        PostArrayList.clear();
    }

    public ArrayList<NormalizedComment> getPostArray() {
        return PostArrayList;
    }
}
