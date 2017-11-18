package main.SMAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import main.sminterfaces.NormalizedComment;

public class CommentListAnalyzer {

    //Declare Class Variables
    //for now I have decided to go the same import route for our positivity word list as all our other external files.
    private final String POSITIVITYWORDSPATH = "externalfiles/PositivityWords.txt";
    private final String DICTIONARYPATH = "externalfiles/BigAssDictionaryFromPrinceton.txt";
    private final String BLACKLISTPATH = "externalfiles/BlackList.txt";
    private ArrayList<CommentInstance> AllComments;
    private ArrayList<CommentInstance> NonEnglishComments;
    private final DictionaryInstance Dictionary;
    private ArrayList<WordInstance> AllUniqueWords;
    private ArrayList<WordInstance> AllUniqueWordsFiltered;
    private ArrayList<WordInstance> BlackList;
    private ArrayList<WordInstance> TempBlacklist;
    //This number will be encoded into the text file from which we read all words we want to define as positive or negative.
    private ArrayList<WordInstance> PositivityWords;
    private ArrayList<CommentGroup> Groups;
    private final int NUMBER_OF_GROUPS = 10;
    private Boolean hasBeenAnalyzed;

    public CommentListAnalyzer() throws IOException {
        //Initialize Class Variables
        Dictionary = new DictionaryInstance(DICTIONARYPATH, "English");
        AllComments = new ArrayList();
        NonEnglishComments = new ArrayList();
        AllUniqueWords = new ArrayList();
        AllUniqueWordsFiltered = new ArrayList();
        BlackList = new ArrayList();
        TempBlacklist = new ArrayList();
        PositivityWords = new ArrayList();
        Groups = new ArrayList();
        hasBeenAnalyzed = false;

        //Import words into BlackList
        BufferedReader br;
        br = new BufferedReader(new FileReader(BLACKLISTPATH));
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) {
            BlackList.add(new WordInstance(sCurrentLine));
        }

        //Import words and values into PositivityWords
        br = new BufferedReader(new FileReader(POSITIVITYWORDSPATH));
        while ((sCurrentLine = br.readLine()) != null) {
            //This might be a cheap solution for now, basically we just need to add a 1 or -1 the line after each word in this file
            PositivityWords.add(new WordInstance(sCurrentLine, Integer.parseInt(br.readLine().trim())));
        }
    }

    public void setComments(ArrayList<NormalizedComment> post, boolean isBlacklistEnabled) throws IOException {
        //Adding ArrayList of strings from input to ArrayList of CommentInstances
        for (int x = 0; x < post.size(); x++) {
            CommentInstance currentInstance = new CommentInstance(post.get(x).getMedia(), post.get(x).getId(), post.get(x).getMessage(), post.get(x).getTime(),
                    post.get(x).getShares(), Dictionary.getDictionaryInstance(), PositivityWords);
            //Filtering out non english words and instances of Only Names in comments.
            //We should somehow rerun this code when we apply words to the TempDictionary
            //Im not sure how the existing rerun functionality is working with the blacklist. Figured I'd mention this to be sure.
            if (currentInstance.getIsEnglish() == true && currentInstance.getIsOnlyName() == false) {
                AllComments.add(currentInstance);
            } else {
                //We can use this to catch non english comments in case we want to investigate them at some point
                //Maybe we can give the user the option to view these "non english" comments to identify potential words to add to the TempDictionary
                NonEnglishComments.add(currentInstance);
            }
        }

        //Loading the unique words from all CommentInstances into a single ArrayList that can be sorted
        ArrayList<WordInstance> allUniqueWords;
        allUniqueWords = AllComments.get(0).getUniqueWordList();
        //This is started at 1 because the UniqueWordList is already seeded with the original post
        for (int x = 1; x < AllComments.size(); x++) { //iterate through All comments
            for (int y = 0; y < AllComments.get(x).getUniqueWordList().size(); y++) { //for each comment in all comments get the unique word list
                boolean wasIncremented = false;
                for (int z = 0; z < allUniqueWords.size(); z++) { //for each uniqe word in each comment's unique word list iterate through the "alluniquewords" list
                    if (AllComments.get(x).getUniqueWordList().get(y).getWord().equals(allUniqueWords.get(z).getWord())) {
                        allUniqueWords.get(z).increment();
                        wasIncremented = true;
                        break;
                    }
                }
                if (wasIncremented) {
                } else {
                    allUniqueWords.add(AllComments.get(x).getUniqueWordList().get(y));
                }
            }
        }
        AllUniqueWords = allUniqueWords;
        Collections.sort(AllUniqueWords);

        //Call Method to filter out the crap if ignore blacklist is not selected
        AllUniqueWordsFiltered = isBlacklistEnabled
                ? filterMeaninglessWords(AllUniqueWords) : AllUniqueWords;
        System.out.println(AllUniqueWordsFiltered);
        hasBeenAnalyzed = true;
    }

    public ArrayList<WordInstance> filterMeaninglessWords(ArrayList<WordInstance> input) {
        for (int x = 0; x < input.size(); x++) {
            for (int y = 0; y < BlackList.size(); y++) {
                if (BlackList.get(y).getWord().equals(input.get(x).getWord())) {
                    input.remove(x);
                    x--;
                    break;
                }
            }
            for (int z = 0; z < TempBlacklist.size(); z++) {
                if (TempBlacklist.get(z).getWord().equals(input.get(x).getWord())) {
                    input.remove(x);
                    x--;
                    break;
                }
            }
        }
        return input;
    }

    public ArrayList<CommentGroup> groupComments() {
        int targetIndex = 0;
        String keyword = "";
        //set targetIndex equal to last element in list
        targetIndex = AllUniqueWordsFiltered.size() - 1;
        //create a group for the last x elements in list, where x is
        //NUMBER_OF_GROUPS
        //Issue: since AllUniqueWordsFiltered is only ever going to be one value why is there an if here?
        //I think the intended functionality would be to group everything in its entirety,
        //but only output as many groups as determined by the final NUMBER_OF_GROUPS
        if (AllUniqueWordsFiltered.size() > NUMBER_OF_GROUPS) {
            for (int k = 0; k < NUMBER_OF_GROUPS; k++) {
                keyword = AllUniqueWordsFiltered.get(targetIndex).getWord();
                Groups.add(new CommentGroup(keyword));
                targetIndex--;
            }
        } else {
            for (int k = 0; k < AllUniqueWordsFiltered.size(); k++) {
                keyword = AllUniqueWordsFiltered.get(targetIndex).getWord();
                Groups.add(new CommentGroup(keyword));
                targetIndex--;
            }
        }
        //iterate through AllComments and add any that contain a grouped keyword
        //to that group. Might be more efficient method
        ArrayList<WordInstance> wordList;
        for (CommentGroup g : Groups) {
            keyword = g.getKeyword();
            for (int k = 1; k < AllComments.size(); k++) {
                wordList = AllComments.get(k).getUniqueWordList();
                for (WordInstance w : wordList) {
                    if (w.getWord().equals(keyword)) {
                        g.addComment(AllComments.get(k));
                    }
                }
            }
        }

        for (int k = 0; k < Groups.size(); k++) {
            if (Groups.get(k).getComments().size() < 1) {
                Groups.remove(k);
                k--;
            }
        }
        Collections.sort(Groups);
        for (CommentGroup g : Groups) {
            Collections.sort(g.getComments());
        }
        //output groups to console
        /*System.out.print("Total Groups: " + Groups.size()
                + "\n----------------------------------\n");
        for (CommentGroup g : Groups) {
            System.out.print(g);
        }*/
        return Groups;
    }

    public void addToBlacklist(String wordToAdd) throws IOException {
        TempBlacklist.add(new WordInstance(wordToAdd));
    }

    public void addToDictionary(String wordToAdd) {
        Dictionary.addTempWordToDict(wordToAdd);
    }

    public boolean getHasBeenAnalyzed() {
        return hasBeenAnalyzed;
    }

    public ArrayList<CommentInstance> getComments() {
        return AllComments;
    }

    public ArrayList<WordInstance> getFilteredWord() {
        return AllUniqueWordsFiltered;
    }

    public void clearArray() {
        AllComments.clear();
        AllUniqueWords.clear();
        AllUniqueWordsFiltered.clear();
        Groups.clear();
    }
}
