package main.SMAnalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONObject;

public class CommentListAnalyzer {

    //Declare Class Variables
    //for now I have decided to go the same import route for our positivity word list as all our other external files.
    private final String POSITIVITYWORDSPATH = "externalfiles/PositivityWords.txt";
    private final String DICTIONARYPATH = "externalfiles/BigAssDictionaryFromPrinceton.txt";
    private final String BLACKLISTPATH = "externalfiles/BlackList.txt";
    private ArrayList<CommentInstance> AllComments;
    private final DictionaryInstance Dictionary;
    private ArrayList<WordInstance> AllUniqueWords;
    private ArrayList<WordInstance> AllUniqueWordsFiltered;
    private ArrayList<WordInstance> BlackList;
    //I am leveraging our WordInstance class to associate a number value with each word. 
    //This number will be encoded into the text file from which we read all words we want to define as positive or negative.
    private ArrayList<WordInstance> PositivityWords;
    private ArrayList<CommentGroup> Groups;
    private final int NUMBER_OF_GROUPS = 10;

    public CommentListAnalyzer() throws IOException {
        //Initialize Class Variables
        Dictionary = new DictionaryInstance(DICTIONARYPATH, "English");
        AllComments = new ArrayList();
        AllUniqueWords = new ArrayList();
        AllUniqueWordsFiltered = new ArrayList();
        BlackList = new ArrayList();
        PositivityWords = new ArrayList();
        Groups = new ArrayList();

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
            PositivityWords.add(new WordInstance(sCurrentLine,Integer.parseInt(br.readLine())));
        }
    }

    public void setComments(ArrayList<JSONObject> post, boolean noBlacklist) throws IOException {
        //Adding ArrayList of strings from input to ArrayList of CommentInstances
        for (int x = 0; x < post.size(); x++) {
            CommentInstance currentInstance = new CommentInstance(post.get(x).getString("message"), post.get(x).getString("created_time"), Dictionary.getDictionaryInstance(),PositivityWords);
            //Filtering out non english words and instances of Only Names in comments.
            if (currentInstance.getIsEnglish() == true && currentInstance.getIsOnlyName() == false) {
                AllComments.add(currentInstance);
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
                if (wasIncremented) {  }
                else {
                    allUniqueWords.add(AllComments.get(x).getUniqueWordList().get(y));
                }
            }
        }
        AllUniqueWords = allUniqueWords;
        Collections.sort(AllUniqueWords);

        //Call Method to filter out the crap if ignore blacklist is not selected
        AllUniqueWordsFiltered = noBlacklist ? 
                AllUniqueWords : filterMeaninglessWords(AllUniqueWords);
        System.out.println(AllUniqueWordsFiltered);
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
            for (CommentInstance c : AllComments) {
                wordList = c.getUniqueWordList();
                for (WordInstance w : wordList) {
                    if (w.getWord().equals(keyword)) {
                        g.addComment(c);
                    }
                }
            }
        }
        Collections.sort(Groups);
        //output groups to console
        /*System.out.print("Total Groups: " + Groups.size()
                + "\n----------------------------------\n");
        for (CommentGroup g : Groups) {
            System.out.print(g);
        }*/
        return Groups;
    }

    public void clearArray() {
        AllComments.clear();
        AllUniqueWords.clear();
        AllUniqueWordsFiltered.clear();
        Groups.clear();
    }
}
