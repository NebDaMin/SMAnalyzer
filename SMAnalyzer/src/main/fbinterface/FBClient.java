package main.fbinterface;

import fbapi.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.json.*;

public class FBClient {

    public static final String APP_ID = "1959837484256375";
    public static final String APP_SECRET = "b224ad6a4bae050c34fff51efbce2b60";
    static FbAPI fbClient;
    private ArrayList<JSONObject> PostArrayList;
    private PrintWriter out;
    
    public FBClient() {
        fbClient = new FbAPI(APP_ID, APP_SECRET);
        PostArrayList = new ArrayList();
    }

    public void fetchRandomPagePost(String pageName, Boolean children, Boolean file) {
        if (fbClient.getAccessToken() == null) {
            System.out.println("Access token is null");
        } else {
            int i = 0;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("limit", 1);
            JSONObject pageFeed = fbClient.getConnections(pageName, "feed", params);
            List<JSONObject> postList = fbClient.convertJsonDataToList(pageFeed);
            if (file) 
            {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                int result = chooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f.exists() == true) {
                        int n = JOptionPane.showConfirmDialog(null,
                         "This file already exists. Would you like to overwrite this file?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                        if (n == JOptionPane.YES_OPTION) {
                            try {
                            out = new PrintWriter(new BufferedWriter(new FileWriter(f, false)));
                            } catch (IOException ex) {
                             JOptionPane.showMessageDialog(null, "File could not be opened.",
                             "Get a better file", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                    else{
                        try {
                            out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
                        } 
                        catch(IOException ex)
                        {
                        JOptionPane.showMessageDialog(null, "File could not be opened.",
                        "Get a better file", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } 
            }    
             if(file){
                for (JSONObject post : postList) {
                PostArrayList.add(post);
                out.println("Post id: " + post.getString("id"));
                out.println("Message: " + post.getString("message"));
                out.println("Created on: " + post.getString("created_time"));
                out.println(); 
                
                getComments(post.getString("id"), children);
                }
            out.close();
            }
             else{
             for (JSONObject post : postList) {
                PostArrayList.add(post);
                System.out.println("Post id: " + post.getString("id"));
                System.out.println("Message: " + post.getString("message"));
                System.out.println("Created on: " + post.getString("created_time"));
                System.out.println();

                getComments(post.getString("id"), children);
            }
             }
            System.out.println("PostArrayList size: " + PostArrayList.size());
        }
    }

    public void fetchSpecificPagePost(String pageName, String postId, Boolean children, Boolean file) {
        if (fbClient.getAccessToken() == null) {
            System.out.println("Access token is null");
        } else {
            int i = 0;
            JSONObject page = fbClient.getObject(pageName);
            String pageId = page.getString("id");
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("fields", "id,message,shares,created_time");
            JSONObject post = fbClient.getObject(pageId + "_" + postId, params);
            PostArrayList.add(post);
            System.out.println("Post id: " + post.getString("id"));
            System.out.println("Message: " + post.getString("message"));
            System.out.println("Created on: " + post.getString("created_time"));
            try {
                System.out.println("Shares: " + post.getJSONObject("shares").get("count"));
            } catch (JSONException ex) {
            }
            System.out.println();

            getComments(post.getString("id"), children);
            System.out.println("PostArrayList size: " + PostArrayList.size());
        }
    }

    public ArrayList<JSONObject> getPostArray() {
        return PostArrayList;
    }

    public void clearArray() {
        PostArrayList.clear();
    }

    public int getComments(String id, boolean children) {
        FBCommentList comments = new FBCommentList(id, fbClient, null);
        if (comments.getData() == null) {
            return 1;
        } else {
            if (comments.getHasNext()) {
                boolean searched = false;
                while (comments.getHasNext() || searched == false) {
                    searched = true;
                    for (int i = 0; i < comments.getCount(); i++) {
                        JSONObject comment = comments.getData().getJSONObject(i);
                        PostArrayList.add(comment);
                        String time = comment.getString("created_time").replace("T", " ").replace("+0000", "");
                        System.out.println("Comment: " + comment.getString("message") + "\nCreated on: " + time);
                        if (children == true) {
                            getComments(comment.getString("id"), children);
                        }
                    }
                    if (comments.getHasNext()) {
                        comments = new FBCommentList(id, fbClient, comments.getAfter());
                        searched = false;
                    }
                }
            } else {
                for (int i = 0; i < comments.getCount(); i++) {
                    JSONObject comment = comments.getData().getJSONObject(i);
                    PostArrayList.add(comment);
                    String time = comment.getString("created_time").replace("T", " ").replace("+0000", "");
                    System.out.println("Comment: " + comment.getString("message") + "\nCreated on: " + time);
                    if (children == true) {
                        getComments(comment.getString("id"), children);
                    }
                }
            }
        }
        return 0;
    }
}
