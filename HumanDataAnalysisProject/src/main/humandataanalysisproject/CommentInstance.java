package main.humandataanalysisproject;

import java.util.ArrayList;
import java.io.*;
import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;

public class CommentInstance 
{
    //class level vars
    //Gettable vars
    private String _CommentRaw = "";                    //Raw comment when first initialized
    private String _CommentNoPunct = "";                //Comment Version without punctuation
    private ArrayList<String> _UniqueWordList;          //List of unique words
    private ArrayList<Integer> _UniqueWordListCounts;   //Count of each words in word list
    private ArrayList<String> _GeneralizedWordList;     //TODO: make a string of all word variances generalized
    private boolean _IsEnglish;                         //Is comment english
    
    
    
    //Other Vars
    
    private char[] _PunctWhitelist;                 //Array to store whitelist of english characters
    private ArrayList<Character> _CommentCharList;  //List of Chars while removing punctuation
    
    //Constructor for CommentInstance
    public CommentInstance(String inputString, GenericSpellDictionary dictionary) throws IOException
    {
        //Initialize Variables
        _CommentRaw = inputString;
        _CommentCharList = new ArrayList<>();
        _UniqueWordList = new ArrayList<>();
        _UniqueWordListCounts = new ArrayList<>();
        _PunctWhitelist = new char[] {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',' '
                                     ,'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        
        //Iterate through comment and put into ArrayList of chars
        for(int z=0;z<_CommentRaw.length();z++)
        {
            _CommentCharList.add(_CommentRaw.charAt(z));
        }
        
        removePunctuation();
        populateWordList();
        identifyLanguage(dictionary);
    }
    
    //This method takes the comment and saves a version of it that doesnt include punctuation
    private void removePunctuation()
    {
        Character currentChar;
        
        for(int y=0;y<_CommentCharList.size();y++)
        {
            currentChar = _CommentCharList.get(y);
            
            for(int x=0; x<_PunctWhitelist.length;x++)
            {
                if(currentChar.equals(_PunctWhitelist[x]))
                {
                    _CommentNoPunct += _CommentCharList.get(y);
                }
            }
        }
    }
    
    private void populateWordList() 
    {
        //Iterate through comment and put into ArrayList of unique words,
        //with parallell ArrayList of counts for each word
        String comment = _CommentNoPunct;
        String currentWord = "";
        
        //eliminate leading whitespace, then append one space to catch last word
        comment.trim();
        comment += " ";
        
        //Iterate by character. If not a space, store character in currentWord
        //If space, check for duplicates, 
        //then store currentWord in _UniqueWordList and iterate count
        for(int z=0;z<comment.length();z++)
        {
            if(comment.charAt(z)==' ') 
            {
              int searchResults = _UniqueWordList.indexOf(currentWord);
              if(searchResults == -1)
              {
                 _UniqueWordList.add(currentWord);
                 _UniqueWordListCounts.add(1);
                 currentWord = "";
              }
              else
              {
                 int currentCount = _UniqueWordListCounts.get(searchResults);
                 _UniqueWordListCounts.set(searchResults, (currentCount +1));
                 currentWord = "";
              }
            }
            else
            {
              currentWord += comment.charAt(z);
            }
        }
      /* output in columns to file for testing larger data sets
        try {
           PrintWriter out = new PrintWriter(new FileWriter("alice_output.txt")); 
           int offsetFlag = 0;
           int offsetCount = 0;
           for(int k =0;k<_WordList.size();k++) {
               if(offsetFlag == 0) {
                 out.print(_UniqueWordList.get(k)+": "+_UniqueWordListCounts.get(k)); 
                 offsetCount = _UniqueWordList.get(k).length() + 
                         _UniqueWordListCounts.get(k).toString().length() + 2;
                 offsetFlag = 1;
               }
               else if(offsetFlag == 1) {
                  for(int j = 0;j<(35-offsetCount);j++) {
                      out.print(" ");
                  } 
                  out.print(_UniqueWordList.get(k)+": "+_UniqueWordListCounts.get(k));
                  offsetCount = _UniqueWordList.get(k).length() + 
                         _UniqueWordListCounts.get(k).toString().length() + 2;
                  offsetFlag = 3;
               }
               else if(offsetFlag == 3) {
                  for(int j = 0;j<(35-offsetCount);j++) {
                      out.print(" ");
                  } 
                  out.print(_UniqueWordList.get(k)+": "+_UniqueWordListCounts.get(k));
                  offsetCount = _UniqueWordList.get(k).length() + 
                         _UniqueWordListCounts.get(k).toString().length() + 2;
                  offsetFlag = 4;
               }
               else if(offsetFlag == 4) {
                  for(int j = 0;j<(35-offsetCount);j++) {
                      out.print(" ");
                  } 
                  out.println(_UniqueWordList.get(k)+": "+_UniqueWordListCounts.get(k));
                  offsetCount = 0;
                  offsetFlag = 0;   
               }
           }
       }
       catch(IOException e) {System.out.println(e);}
        
       print _UniqueWordList and _UniqueWordListCounts for testing
       for(int k =0;k<_WordList.size();k++) {
           System.out.println(_UniqueWordList.get(k)+": "+_UniqueWordListCounts.get(k));
       }  */  
    }
    
    private void generalizeWordList()
    {
        //TODO: Create generalization code here
        
    }
    
    private void identifyLanguage(GenericSpellDictionary dictionary)
    {
        StringWordTokenizer commentTokenizer = new StringWordTokenizer(_CommentNoPunct);
        SpellChecker checker = new SpellChecker(dictionary);
        double results = checker.checkSpelling(commentTokenizer);
        double resultThreshold = results / getCommentNoPunctStringArray().length;
        
        _IsEnglish = resultThreshold < .25;
    }
    
    //Getters
    public String getCommentRaw()
    {
        return this._CommentRaw;
    }
    
    public String getCommentNoPunctString()
    {
        return this._CommentNoPunct;
    }
    
    public String[] getCommentNoPunctStringArray()
    {
        return this._CommentNoPunct.split(" ");
    }
    
    public boolean getIsEnglish()
    {
        return _IsEnglish;
    }
    
    public ArrayList<String> getUniqueWordList()
    {
        return _UniqueWordList;
    }
    
    public ArrayList<Integer> getUniqueWordListCounts()
    {
        return _UniqueWordListCounts;
    }
}