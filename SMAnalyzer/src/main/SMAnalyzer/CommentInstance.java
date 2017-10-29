package main.SMAnalyzer;

import java.util.ArrayList;
import java.io.*;
import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;

public class CommentInstance implements Comparable<CommentInstance> {

    //class level vars
    private String CommentRaw = "";                         //Raw comment when first initialized
    private String CommentTime = "";                        //Placeholder for the time value to be sent from the facebook API
    private String CommentNoPunct = "";                     //Comment Version without punctuation
    private ArrayList<WordInstance> UniqueWordList;         //List of unique words    //Count of each words in word list
    private ArrayList<WordInstance> GeneralizedWordList;    //TODO: make a string of all word variances generalized
    private boolean IsEnglish;                              //Is comment english
    private boolean IsOnlyName;                             //Is a persons name only
    private final char[] PunctWhitelist;                    //Array to store whitelist of english characters
    private ArrayList<Character> CommentCharList;           //List of Chars while removing punctuation
    private int PositivityLevel = 0;                        //This is the int that will determine the positivity level

    //Constructor for CommentInstance
    public CommentInstance(String inputString, String time, GenericSpellDictionary dictionary, ArrayList<WordInstance> positivityWordList) throws IOException {
        //Initialize Variables
        CommentRaw = inputString;
        CommentTime = time.replace("+0000", "").replace("T", " "); //The raw version of time is a little messy. End format is "yyyy-MM-dd HH:mm:ss"
        CommentCharList = new ArrayList<>();
        UniqueWordList = new ArrayList<>();

        PunctWhitelist = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', ' ', '\'',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        //Iterate through comment and put into ArrayList of chars
        for (int z = 0; z < CommentRaw.length(); z++) {
            CommentCharList.add(CommentRaw.charAt(z));
        }

        removePunctuation();
        populateUniqueWordList();
        identifyIsEnglish(dictionary);
        identifyIsName();
        identifyPositivityLevel(positivityWordList);
    }

    //This method takes the comment and saves a version of it that doesnt include punctuation
    private void removePunctuation() {
        Character currentChar;

        for (int y = 0; y < CommentCharList.size(); y++) {
            currentChar = CommentCharList.get(y);

            for (int x = 0; x < PunctWhitelist.length; x++) {
                if (currentChar.equals(PunctWhitelist[x])) {
                    CommentNoPunct += CommentCharList.get(y);
                }
            }
        }
    }

    private void populateUniqueWordList() {
        //Iterate through comment and put into ArrayList of unique WordInstances
        ArrayList<String> currentWordList;
        String comment = CommentNoPunct;
        String currentWord = "";

        //eliminate leading whitespace, then append one space to catch last word
        comment = comment.trim();
        comment += " ";

        //Iterate by character. If not a space, store character in currentWord
        //If space, use currentWordList to check for duplicates, then create new
        currentWordList = new ArrayList();
        for (int z = 0; z < comment.length(); z++) {

            if (comment.charAt(z) == ' ') {
                //search currentWordList to see if currentWord exists 
                
                int searchResults = currentWordList.indexOf(currentWord.toLowerCase());
                //if currentWord doesn't exist
                if (searchResults == -1) {
                    currentWordList.add(currentWord.toLowerCase());
                    if (!currentWord.isEmpty()) {
                        UniqueWordList.add(new WordInstance(currentWord));
                    }
                    currentWord = "";
                } else {
                    currentWord = "";
                }
            } else {
                currentWord += comment.charAt(z);
            }
        }
    }

    private void generalizeWordList() {
        //TODO: Create generalization code here

    }

    private void identifyIsEnglish(GenericSpellDictionary dictionary) {
        StringWordTokenizer commentTokenizer = new StringWordTokenizer(CommentNoPunct);
        SpellChecker checker = new SpellChecker(dictionary);
        double results = checker.checkSpelling(commentTokenizer);
        double resultThreshold = results / getCommentNoPunctStringArray().length;

        if(getCommentNoPunctStringArray().length == 1 && results < 0)
        {
            IsEnglish = true;
        }
        else IsEnglish = resultThreshold < .34;
    }
    
    private void identifyIsName() {
        if (UniqueWordList.size() == 2)
        {
            IsOnlyName = UniqueWordList.get(0).getHasCapFirstChar() == true && UniqueWordList.get(1).getHasCapFirstChar() == true;
        }
        else {
            IsOnlyName = false;
        }
    }       
    
    //Matteo take a look at this
    //Basically I use the "getCount" method from the word instance to add to the overall positivity level in a comment.
    //We still need to add logic for modifiers such as "not" and so on.
    private void identifyPositivityLevel(ArrayList<WordInstance> positivityWordList) {
        PositivityLevel = 0;
        for (int x = 0; x < UniqueWordList.size(); x++) {
            for (int y = 0; y < positivityWordList.size(); y++) {
                if (UniqueWordList.get(x).getWord().equals(positivityWordList.get(y).getWord())) {
                    PositivityLevel += positivityWordList.get(y).getCount();
                }
            }
        }
    }
    
    @Override
    public int compareTo(CommentInstance other) {
        return this.CommentTime.compareTo(other.CommentTime);
    }

    //Getters
    public String getCommentRaw() {
        return this.CommentRaw;
    }

    
    public String getCommentTime() {
        return this.CommentTime;
    }
    
    public String getCommentNoPunctString() {
        return this.CommentNoPunct;
    }

    public String[] getCommentNoPunctStringArray() {
        return this.CommentNoPunct.split(" ");
    }

    public boolean getIsEnglish() {
        return IsEnglish;
    }
    
    public boolean getIsOnlyName() {
        return IsOnlyName;
    }
    
    public int getPositivityLevel() {
        return PositivityLevel;
    }    

    public ArrayList<WordInstance> getUniqueWordList() {
        return UniqueWordList;
    }
}
