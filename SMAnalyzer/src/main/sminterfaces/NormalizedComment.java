package main.sminterfaces;

import org.json.*;

public class NormalizedComment {

    String media;
    String id;
    String message;
    String created_time;
    String shares;

    public void setFromFacebook(JSONObject fb) {
        this.media = "facebook";
        try {
            this.id = fb.getString("id");
            this.message = fb.getString("message");
            this.created_time = fb.getString("created_time").replace("+0000", "").replace("T", " ");
            if(fb.has("shares")){ this.shares = fb.getJSONObject("shares").getString("count"); }
            else { this.shares = "0"; }
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setFromYoutube(JSONObject yt) {
        this.media = "youtube";
        try {
            
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setFromTwitter(JSONObject twt) {
        this.media = "twitter";
        try {
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setFromReddit(JSONObject rdt) {
        this.media = "reddit";
        try {
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public String getMedia(){
        return media;
    }
    
    public String getId(){
        return id;
    }
    
    public String getMessage(){
        return message;
    }
    
    public String getTime(){
        return created_time;
    }
}