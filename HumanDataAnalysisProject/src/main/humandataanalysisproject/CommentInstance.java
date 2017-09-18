package main.humandataanalysisproject;

import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;

public class CommentInstance 
{
    
    private String _CommentRaw = "";            //Raw comment when first initialized
    private String _CommentNoPunct = "";        //Comment Version without punctuation
    private ArrayList<Character> _CharList;     //List of Chars while removing punctuation
    private ArrayList<String> _WordList;        //List of unique words
    private ArrayList<Integer> _WordListCounts; //Count of words in word list
    private char[] _PunctWhitelist;             //Array to store whitelist of english characters
    private final File _Dictionaryfile;         //File where the dictionary is stored
    private GenericSpellDictionary _Dictionary; //Dictionary object that will do the spellcheck to identify non english words
    private boolean _IsEnglish;                 //Is comment english
    
    //Constructor for CommentInstance
    public CommentInstance(String inputString) throws IOException
    {
        _Dictionaryfile = new File("externalfiles/BigAssDictionaryFromPrinceton.txt");
        _Dictionary = new GenericSpellDictionary(_Dictionaryfile);
        _PunctWhitelist = new char[] {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',' '
                                     ,'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        _CommentRaw = inputString;
        _CharList = new ArrayList<>();
        _WordList = new ArrayList<>();
        _WordListCounts = new ArrayList<>();
        
        //Iterate through comment and put into ArrayList of chars
        for(int z=0;z<_CommentRaw.length();z++)
        {
            _CharList.add(_CommentRaw.charAt(z));
        }
        
        
        
        removePunctuation();
        populateWordList();
        identifyLanguage();
    }
    
    //This method takes the comment and saves a version of it that doesnt include punctuation
    private void removePunctuation()
    {
        Character currentChar;
        
        for(int y=0;y<_CharList.size();y++)
        {
            currentChar = _CharList.get(y);
            
            for(int x=0; x<_PunctWhitelist.length;x++)
            {
                if(currentChar.equals(_PunctWhitelist[x]))
                {
                    _CommentNoPunct += _CharList.get(y);
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
        //then store currentWord in _WordList and iterate count
        for(int z=0;z<comment.length();z++)
        {
            if(comment.charAt(z)==' ') 
            {
              int searchResults = _WordList.indexOf(currentWord);
              if(searchResults == -1)
              {
                 _WordList.add(currentWord);
                 _WordListCounts.add(1);
                 currentWord = "";
              }
              else
              {
                 int currentCount = _WordListCounts.get(searchResults);
                 _WordListCounts.set(searchResults, (currentCount +1));
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
                 out.print(_WordList.get(k)+": "+_WordListCounts.get(k)); 
                 offsetCount = _WordList.get(k).length() + 
                         _WordListCounts.get(k).toString().length() + 2;
                 offsetFlag = 1;
               }
               else if(offsetFlag == 1) {
                  for(int j = 0;j<(35-offsetCount);j++) {
                      out.print(" ");
                  } 
                  out.print(_WordList.get(k)+": "+_WordListCounts.get(k));
                  offsetCount = _WordList.get(k).length() + 
                         _WordListCounts.get(k).toString().length() + 2;
                  offsetFlag = 3;
               }
               else if(offsetFlag == 3) {
                  for(int j = 0;j<(35-offsetCount);j++) {
                      out.print(" ");
                  } 
                  out.print(_WordList.get(k)+": "+_WordListCounts.get(k));
                  offsetCount = _WordList.get(k).length() + 
                         _WordListCounts.get(k).toString().length() + 2;
                  offsetFlag = 4;
               }
               else if(offsetFlag == 4) {
                  for(int j = 0;j<(35-offsetCount);j++) {
                      out.print(" ");
                  } 
                  out.println(_WordList.get(k)+": "+_WordListCounts.get(k));
                  offsetCount = 0;
                  offsetFlag = 0;   
               }
           }
       }
       catch(IOException e) {System.out.println(e);}
        
       print _WordList and _WordListCounts for testing
       for(int k =0;k<_WordList.size();k++) {
           System.out.println(_WordList.get(k)+": "+_WordListCounts.get(k));
       }  */  
    }
    
    private void identifyLanguage()
    {
        StringWordTokenizer commentTokenizer = new StringWordTokenizer(_CommentNoPunct);
        SpellChecker checker = new SpellChecker(_Dictionary);
        double results = checker.checkSpelling(commentTokenizer);
        double resultThreshold = results / getCommentNoPunctStringArray().length;
        
        if(resultThreshold < .25)
            _IsEnglish = true;
        else
            _IsEnglish = false;
        System.out.println(_IsEnglish);
        //checker.getSuggestions("thestring", 1);  
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
}