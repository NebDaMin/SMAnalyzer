package main.humandataanalysisproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class HumanDataAnalysisProject 
{
    private ArrayList<CommentInstance> _AllComments;
    private DictionaryInstance _Dictionary;
    
    public HumanDataAnalysisProject() throws IOException 
    {
        //Initialize Variables
        _Dictionary = new DictionaryInstance("externalfiles/BigAssDictionaryFromPrinceton.txt","English");
        _AllComments = new ArrayList();
        
        
        //Test strings to simulate reading in multiple comments.
        String teststring =     "This is Bob. Bob is an english comment be like bob";
        String teststring1 =    "THIZZZ hereeeeeee comment izzz nottttt speeeeellled cerecterly IZZZ NOOOOOT bob";
        String teststring2 =    "This is a comment that doesn't necessarily agree with bob's ideaology however it still is english";
        String teststring3 =    "The cake is a lie";
        String teststring4 =    "This is a cooooment that has only a few missssspelled words therefore it should still be counted";
        
        
        //Adding all comments to an ArrayList, this is where the file read in stuff happens
        _AllComments.add(new CommentInstance(teststring,_Dictionary.getDictionaryInstance()));
        _AllComments.add(new CommentInstance(teststring1,_Dictionary.getDictionaryInstance()));
        _AllComments.add(new CommentInstance(teststring2,_Dictionary.getDictionaryInstance()));
        _AllComments.add(new CommentInstance(teststring3,_Dictionary.getDictionaryInstance()));
        _AllComments.add(new CommentInstance(teststring4,_Dictionary.getDictionaryInstance()));
        
        
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