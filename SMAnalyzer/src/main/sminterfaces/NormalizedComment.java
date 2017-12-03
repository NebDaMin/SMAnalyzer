package main.sminterfaces;

import java.util.Date;
import org.json.*;

public class NormalizedComment {

    String Media;
    String Id;
    String Message;
    String CreatedTime;
    String Shares;
    String Upvotes;

    public void setFromFacebook(JSONObject fb) {
        this.Media = "facebook";
        try {
            this.Id = fb.getString("id");
            this.Message = fb.getString("message");
            this.CreatedTime = fb.getString("created_time").replace("+0000", "").replace("T", " ");
            if (fb.has("shares")) {
                this.Shares = fb.getJSONObject("shares").get("count").toString();
            } else {
                this.Shares = null;
            }
            this.Upvotes = null;
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setFromYoutube(JSONObject yt) {
        this.Media = "youtube";
        try {
            this.Id = yt.getString("id");
            if (yt.has("snippet")) {
                if (yt.getJSONObject("snippet").has("topLevelComment")) {
                    this.Message = yt.getJSONObject("snippet").getJSONObject("topLevelComment").getJSONObject("snippet").getString("textOriginal");
                    this.CreatedTime = yt.getJSONObject("snippet").getJSONObject("topLevelComment").getJSONObject("snippet").getString("publishedAt").replace(".000Z", "").replace("T", " ");
                } else if (yt.getJSONObject("snippet").has("textOriginal")) {
                    this.Message = yt.getJSONObject("snippet").getString("textOriginal");
                    this.CreatedTime = yt.getJSONObject("snippet").getString("publishedAt").replace(".000Z", "").replace("T", " ");
                }
                if (yt.getJSONObject("snippet").has("title")) {
                    this.Message = yt.getJSONObject("snippet").getString("title");
                    this.CreatedTime = yt.getJSONObject("snippet").getString("publishedAt").replace(".000Z", "").replace("T", " ");
                }
            }
            this.Shares = null;
            this.Upvotes = null;
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setFromTwitter(JSONObject twt) {
        this.Media = "twitter";
        try {
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void setFromReddit(JSONObject rdt) {
        this.Media = "reddit";
        this.Id = rdt.getString("id");
        if (rdt.has("body")) {
            this.Message = rdt.getString("body");
        } else if (rdt.has("title")) {
            this.Message = rdt.getString("title");
        } else {
            this.Message = "[No Text]";
        }
        Double timeDouble = rdt.getDouble("created");
        Date time = new Date(timeDouble.longValue() * 1000);
        this.CreatedTime = time.toString();
        this.Shares = null;
        this.Upvotes = rdt.get("ups").toString();
        try {
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String getMedia() {
        return Media;
    }

    public String getId() {
        return Id;
    }

    public String getMessage() {
        return Message;
    }

    public String getTime() {
        return CreatedTime;
    }

    public String getShares() {
        return Shares;
    }

    public String getUpvotes() {
        return Upvotes;
    }

    public void setMedia(String media) {
        this.Media = media;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public void setTime(String time) {
        this.CreatedTime = time;
    }

    public void setShares(String shares) {
        this.Shares = shares;
    }

    public void setUpvotes(String upvotes) {
        this.Upvotes = upvotes;
    }
}
