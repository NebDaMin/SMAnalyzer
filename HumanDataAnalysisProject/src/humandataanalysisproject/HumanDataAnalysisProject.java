package humandataanalysisproject;

import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//import java.util.Scanner;

/**
 *
 * @author Ben Ciummo
 */
public class HumanDataAnalysisProject 
{
    private static ArrayList<CommentInstance> _AllComments;
    
    public static void main(String[] args) throws IOException 
    {
        //Test strings to simulate reading in multiple comments.
        String teststring =     "This is Bob. Bob is an english comment be like bob";
        String teststring1 =    "THIZZZ hereeeeeee comment izzz nottttt speeeeellled cerecterly IZZZ NOOOOOT bob";
        String teststring2 =    "This is a comment that doesn't necessarily agree with bob's ideaology however it still is english";
        String teststring3 =    "The cake is a lie";
        String teststring4 =    "This is a cooooment that has only a few missssspelled words therefore it should still be counted";
        
        
        //Adding all comments to an ArrayList
        _AllComments = new ArrayList();
        _AllComments.add(new CommentInstance(teststring));
        _AllComments.add(new CommentInstance(teststring1));
        _AllComments.add(new CommentInstance(teststring2));
        _AllComments.add(new CommentInstance(teststring3));
        _AllComments.add(new CommentInstance(teststring4));
        
        
        //this loop gets the total number of unique words for every comment
        //this number will be used to build a very large primative string array with every single word
        int numOfWordsForAllComments = 0;
        for(int z =0 ;z<_AllComments.size();z++)
        {
            numOfWordsForAllComments += _AllComments.get(z).getCommentNoPunctStringArray().length;
        }
        numOfWordsForAllComments--;
        
        
        //This will concatinate all the arrays together into a single array
        String[] allComments = new String[numOfWordsForAllComments+1];
        int currentArrayPoint = 0;
        for(int z =0;z<_AllComments.size();z++)
        {
            CommentInstance currentInstance = _AllComments.get(z);
            System.arraycopy(   currentInstance.getCommentNoPunctStringArray(), 
                                0,
                                allComments, 
                                currentArrayPoint,
                                currentInstance.getCommentNoPunctStringArray().length);
            currentArrayPoint += currentInstance.getCommentNoPunctStringArray().length;
        }
        
        
        //Become one with the all powerful HashMap
        HashMap rankedWords = compileAllCommentInstances(allComments);
        System.out.println(rankedWords.toString());
        
        //Alice test
        //String teststring = new Scanner(new File("externalfiles/alice.txt")).useDelimiter("\\Z").next();
        //CommentInstance testCI = new CommentInstance(teststring);
    }
    
    public static HashMap compileAllCommentInstances(String[] inputArray )
    {
        HashMap<String, Integer> frequencyHashMap = new HashMap<String, Integer>();
        
        for (String s : inputArray)
        {
            if (frequencyHashMap.containsKey(s)) frequencyHashMap.replace(s, frequencyHashMap.get(s) + 1);
            else frequencyHashMap.put(s, 1);
        }
        
        return frequencyHashMap;
    }
}