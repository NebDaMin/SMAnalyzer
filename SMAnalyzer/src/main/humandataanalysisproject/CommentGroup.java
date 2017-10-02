/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.humandataanalysisproject;

import java.util.ArrayList;

/**
 *
 * @author matte
 */
public class CommentGroup 
{
    private ArrayList<CommentInstance> Comments;
    private String Keyword;
    
    public CommentGroup(String s)
    {
       Keyword = s;
       Comments = new ArrayList();
    }
    
    public void addComment(CommentInstance c)
    {
        Comments.add(c);
    }
    
    public String getKeyword()
    {
        return Keyword;
    }
    
    public ArrayList getComments()
    {
        return Comments;
    }
    
    @Override
    public String toString()
    {
        String returnVal = "Keyword: "+Keyword;
        returnVal = returnVal.format("%-20s", returnVal);
        returnVal += "Size: "+Comments.size()+"\n";
        return returnVal;
    }
}
