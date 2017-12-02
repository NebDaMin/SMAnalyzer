package main.sminterfaces;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import smsdk.*;

public class YTClient {

    private static final String API_KEY = "AIzaSyBsAXJxBUV4VvMFSe8GxXnmxgkwiTU7Yhs";
    static YtAPI YtClient;
    private ArrayList<NormalizedComment> PostArrayList;

    public YTClient() {
        YtClient = new YtAPI(API_KEY);
        PostArrayList = new ArrayList();
    }

    public void fetchComments(String type, String id, boolean children) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (type.equals("user")) {
            params.put("forUsername", id);
            params.put("part", "id");
            JSONObject channel = YtClient.getObject("channels", params);
            JSONArray items = channel.getJSONArray("items");
            id = items.getJSONObject(0).getString("id");
            type = "channel";
            params.clear();
        }
        params.put(type + "Id", id);
        params.put("textFormat", "plainText");
        params.put("part", "snippet,replies");
        JSONObject comments = YtClient.getObject("commentThreads", params);
        if (comments.length() != 0) {
            ArrayList<JSONObject> commentsList = YtClient.convertJsonItemsToList(comments);
            boolean searched = false;
            while (comments.has("nextPageToken") || searched == false) {
                searched = true;
                for (JSONObject comment : commentsList) {
                    NormalizedComment normComment = new NormalizedComment();
                    normComment.setFromYoutube(comment);
                    PostArrayList.add(normComment);
                    if (children == true) {
                        if (comment.has("replies")) {
                            try {
                                ArrayList<JSONObject> replies = YtClient.convertJsonReplyToList(comment.getJSONObject("replies"));
                                for (JSONObject reply : replies) {
                                    NormalizedComment normReply = new NormalizedComment();
                                    normReply.setFromYoutube(reply);
                                    PostArrayList.add(normReply);
                                }

                                System.out.println();
                            } catch (JSONException jex) {
                                System.out.println(jex);
                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                        }
                    }
                }
                if (comments.has("nextPageToken")) {
                    params.put("pageToken", comments.getString("nextPageToken"));
                    comments = YtClient.getObject("commentThreads", params);
                    commentsList = YtClient.convertJsonItemsToList(comments);
                    searched = false;
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Uh....",
                    "I don't know how you got here, but you need to leave", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void clearArray() {
        PostArrayList.clear();
    }

    public ArrayList<NormalizedComment> getPostArray() {
        return PostArrayList;
    }

}
