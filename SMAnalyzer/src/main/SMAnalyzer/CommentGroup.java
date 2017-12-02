package main.SMAnalyzer;

import java.util.ArrayList;

//This is a class that assiciates all comment instances into an arraylist
//Per Word
public class CommentGroup implements Comparable<CommentGroup> {

    private ArrayList<CommentInstance> Comments;
    private String Keyword;

    public CommentGroup(String s) {
        Keyword = s.toLowerCase();
        Comments = new ArrayList();
    }

    public void addComment(CommentInstance c) {
        Comments.add(c);
    }

    public String getKeyword() {
        return Keyword;
    }

    public ArrayList getComments() {
        return Comments;
    }

    @Override
    public String toString() {
        String returnVal = "Keyword: " + Keyword;
        returnVal = returnVal.format("%-20s", returnVal);
        returnVal += " Size: " + Comments.size() + "\n";
        return returnVal;
    }

    @Override
    public int compareTo(CommentGroup other) {
        return Integer.compare(other.Comments.size(), this.Comments.size());
    }
}