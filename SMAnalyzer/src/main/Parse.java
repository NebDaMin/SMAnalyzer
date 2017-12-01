package main;

import java.util.HashMap;
import javax.swing.JOptionPane;

public class Parse {
    private String Site;
    
    public HashMap<String, String> parseUrl(String s) 
    {
        if (s.contains("facebook.com")) {
            setSite("facebook");
            return parseFacebookUrl(s);
        } else if (s.contains("youtube.com") || s.contains("youtu.be")) {
            setSite("youtube");
            return parseYoutubeUrl(s);
        } else if (s.contains("twitter.com")) {
            setSite("twitter");
            return parseTwitterUrl(s);
        } else if (s.contains("reddit.com") || s.contains("redd.it")) {
            setSite("reddit");
            return parseRedditUrl(s);
        } else {
            JOptionPane.showMessageDialog(null, "Url not recognized",
                    "We only do facebook, youtube, or twitter", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }

    HashMap<String, String> parseFacebookUrl(String s) 
    {
        int last = s.lastIndexOf("facebook.com/");
        int fbLength = "facebook.com/".length();

        String sub = s.substring(last + fbLength, s.length());
        String[] array = sub.split("/");
        HashMap<String, String> map = new HashMap<String, String>();
        if (array.length == 1) {
            map.put("Page Name", array[0]);
        } else if (array.length == 3) {
            map.put("Page Name", array[0]);
            map.put("Post Type", array[1]);
            map.put("Post Id", array[2]);
        } else if (array.length == 5 && array[1].equals("photos")) {
            map.put("Page Name", array[0]);
            map.put("Post Type", array[1]);
            map.put("Post Id", array[3]);
        } else {
            JOptionPane.showMessageDialog(null, "Url not recognized",
                    "Uh...", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        return map;
    }

    HashMap<String, String> parseYoutubeUrl(String s) 
    {
        HashMap<String, String> map = new HashMap<String, String>();
        if (s.contains("youtube.com/")) {
            int last = s.lastIndexOf("youtube.com/");
            int ytLength = "youtube.com/".length();

            String sub = s.substring(last + ytLength, s.length());
            String[] array = sub.split("/");
            if (array.length == 1) {
                map.put("Page Type", "video");
                array[0] = array[0].replace("watch?v=", "");
                map.put("Id", array[0]);
            } else if (array.length == 2) {
                map.put("Page Type", array[0]);
                map.put("Id", array[1]);
            } else if (array.length == 3) {
                map.put("Page Type", array[0]);
                map.put("Id", array[1]);
            }
        } else if (s.contains("youtu.be/")) {
            int last = s.lastIndexOf("youtu.be/");
            int ytLength = "youtu.be/".length();

            String sub = s.substring(last + ytLength, s.length());
            String[] array = sub.split("/");
            if (array.length == 1) {
                map.put("Page Type", "video");
                map.put("Id", array[0]);
            }
        } else {
                JOptionPane.showMessageDialog(null, "Url not recognized",
                        "Uh...", JOptionPane.INFORMATION_MESSAGE);
                return null;
        }
        return map;
    }

    HashMap<String, String> parseTwitterUrl(String s) 
    {
        int last = s.lastIndexOf("twitter.com/");
        int twtLength = "twitter.com/".length();

        String sub = s.substring(last + twtLength, s.length());
        String[] array = sub.split("/");
        HashMap<String, String> map = new HashMap<String, String>();

        if (array.length == 3) {
            map.put("Username", array[0]);
            map.put("Status", array[1]);
            map.put("Post Id", array[2]);
        } else {
            JOptionPane.showMessageDialog(null, "Url not recognized",
                    "Uh...", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return map;
    }

    HashMap<String, String> parseRedditUrl(String s) 
    {
        HashMap<String, String> map = new HashMap<String, String>();

        if (s.contains("reddit.com")) {
            int last = s.lastIndexOf("reddit.com/");
            int redLength = "reddit.com/".length();

            String sub = s.substring(last + redLength, s.length());
            String[] array = sub.split("/");

            if (array.length == 5) {
                map.put("Subreddit", array[1]);
                map.put("Post Id", array[3]);
            } else {
                JOptionPane.showMessageDialog(null, "Url not recognized",
                        "Uh...", JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
        } else if (s.contains("redd.it")) {
            int last = s.lastIndexOf("redd.it/");
            int redLength = "redd.it/".length();

            String sub = s.substring(last + redLength, s.length());
            String[] array = sub.split("/");

            map.put("Post Id", array[0]);
        } else {
                JOptionPane.showMessageDialog(null, "Url not recognized",
                        "Uh...", JOptionPane.INFORMATION_MESSAGE);
                return null;
        }
        return map;
    }
    
    public void setSite(String site) 
    {
       this.Site = site;
    }
     
    public String getSite()
    {
        return Site;
    }
}
