package main.humandataanalysisproject;

import java.util.ArrayList;
import java.io.*;
import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;

public class CommentInstance 
{
    //class level vars
    private String CommentRaw = "";                     //Raw comment when first initialized
    private String CommentNoPunct = "";                 //Comment Version without punctuation
    private ArrayList<String> UniqueWordList;           //List of unique words
    private ArrayList<Integer> UniqueWordListCounts;    //Count of each words in word list
    private ArrayList<String> GeneralizedWordList;      //TODO: make a string of all word variances generalized
    private boolean IsEnglish;                          //Is comment english
    private char[] PunctWhitelist;                      //Array to store whitelist of english characters
    private ArrayList<Character> CommentCharList;       //List of Chars while removing punctuation
    
    //Constructor for CommentInstance
    public CommentInstance(String inputString, GenericSpellDictionary dictionary) throws IOException
    {
        //Initialize Variables
        CommentRaw = inputString;
        CommentCharList = new ArrayList<>();
        UniqueWordList = new ArrayList<>();
        UniqueWordListCounts = new ArrayList<>();
        PunctWhitelist = new char[] {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',' ','\''
                                     ,'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        
        //Iterate through comment and put into ArrayList of chars
        for(int z=0;z<CommentRaw.length();z++)
        {
            CommentCharList.add(CommentRaw.charAt(z));
        }
        
        removePunctuation();
        populateUniqueWordList();
        identifyIsEnglish(dictionary);
    }
    
    //This method takes the comment and saves a version of it that doesnt include punctuation
    private void removePunctuation()
    {
        Character currentChar;
        
        for(int y=0;y<CommentCharList.size();y++)
        {
            currentChar = CommentCharList.get(y);
            
            for(int x=0; x<PunctWhitelist.length;x++)
            {
                if(currentChar.equals(PunctWhitelist[x]))
                {
                    CommentNoPunct += CommentCharList.get(y);
                }
            }
        }
    }
    
    private void populateUniqueWordList() 
    {
        //Iterate through comment and put into ArrayList of unique words,
        //with parallell ArrayList of counts for each word
        String comment = CommentNoPunct;
        String currentWord = "";
        
        //eliminate leading whitespace, then append one space to catch last word
        comment.trim();
        comment += " ";
        
        //Iterate by character. If not a space, store character in currentWord
        //If space, check for duplicates, 
        //then store currentWord in UniqueWordList and iterate count
        for(int z=0;z<comment.length();z++)
        {
            if(comment.charAt(z)==' ') 
            {
              int searchResults = UniqueWordList.indexOf(currentWord);
              if(searchResults == -1)
              {
                 UniqueWordList.add(currentWord);
                 UniqueWordListCounts.add(1);
                 currentWord = "";
              }
              else
              {
                 int currentCount = UniqueWordListCounts.get(searchResults);
                 UniqueWordListCounts.set(searchResults, (currentCount +1));
                 currentWord = "";
              }
            }
            else
            {
              currentWord += comment.charAt(z);
            }
        }
    }
    
    private void generalizeWordList()
    {
        //TODO: Create generalization code here
        
    }
    
    private void identifyIsEnglish(GenericSpellDictionary dictionary)
    {
        StringWordTokenizer commentTokenizer = new StringWordTokenizer(CommentNoPunct);
        SpellChecker checker = new SpellChecker(dictionary);
        double results = checker.checkSpelling(commentTokenizer);
        double resultThreshold = results / getCommentNoPunctStringArray().length;
        
        IsEnglish = resultThreshold < .25;
    }
    
    //Getters
    public String getCommentRaw()
    {
        return this.CommentRaw;
    }
    
    public String getCommentNoPunctString()
    {
        return this.CommentNoPunct;
    }
    
    public String[] getCommentNoPunctStringArray()
    {
        return this.CommentNoPunct.split(" ");
    }
    
    public boolean getIsEnglish()
    {
        return IsEnglish;
    }
    
    public ArrayList<String> getUniqueWordList()
    {
        return UniqueWordList;
    }
    
    public ArrayList<Integer> getUniqueWordListCounts()
    {
        return UniqueWordListCounts;
    }
}