package main.humandataanalysisproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class CommentListAnalyzer 
{
    //Declare Class Variables
    private final String DictionaryPath = "externalfiles/BigAssDictionaryFromPrinceton.txt";
    private final String BlackListPath = "externalfiles/BlackList.txt";;
    private ArrayList<CommentInstance> AllComments;
    private final DictionaryInstance Dictionary;
    private ArrayList<String> AllUniqueWords;
    private ArrayList<String> AllUniqueWordsFilteredWithCounts;
    private ArrayList<String> AllUniqueWordsFiltered;
    private ArrayList<String> BlackList;
    private ArrayList<CommentGroup> Groups;
    private final int GROUP_THRESHOLD = 5;
    
    public CommentListAnalyzer() throws IOException 
    {
        //Initialize Class Variables
        Dictionary = new DictionaryInstance(DictionaryPath,"English");
        AllComments = new ArrayList();
        AllUniqueWords = new ArrayList();
        AllUniqueWordsFilteredWithCounts = new ArrayList();
        AllUniqueWordsFiltered = new ArrayList();
        BlackList = new ArrayList();
        Groups = new ArrayList();
        
        //Import words into BlackList
        BufferedReader br;
        br = new BufferedReader(new FileReader(BlackListPath));
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) 
            BlackList.add(sCurrentLine);
    }
    
    public void setComments(ArrayList<String> post) throws IOException
    {   
        //Adding ArrayList of strings from input to ArrayList of CommentInstances
        for(int x =0;x<post.size();x++)
        {
            CommentInstance currentInstance = new CommentInstance(post.get(x),Dictionary.getDictionaryInstance());
            if (currentInstance.getIsEnglish())
            {
                AllComments.add(currentInstance);
            }
        }
        
        //Loading the unique words from all CommentInstances into a single ArrayList that can be sorted
        ArrayList<String> currentList;
        for(int y = 0; y<AllComments.size();y++)
        {
            currentList = AllComments.get(y).getUniqueWordList();
            for(int x = 0; x<currentList.size();x++)
            {
                AllUniqueWords.add(currentList.get(x));
            }    
        }
        Collections.sort(AllUniqueWords);
        
        //Call Method to filter out the crap 
        AllUniqueWordsFiltered = filterMeaninglessWords(AllUniqueWords);
        
        //Loading the sorted list of AllUniqueWordsFiltered into another ArrayList that is formatted to store
        //Only one instance of each word but with the number of unique reoccurances preceeding it
        //Once sorted, the highest frequency words will bubble to the top
        int currentCountForWord = 1;        
        for(int k = 0; k<AllUniqueWordsFiltered.size()-1;k++)
        {
            if(AllUniqueWordsFiltered.get(k).equals(AllUniqueWordsFiltered.get(k+1)))
            {
                currentCountForWord += 1;
            }
            else
            {
                AllUniqueWordsFilteredWithCounts.add(currentCountForWord + AllUniqueWordsFiltered.get(k));
                currentCountForWord = 1;
            } 
        }
        Collections.sort(AllUniqueWordsFilteredWithCounts);
        System.out.print(AllUniqueWordsFilteredWithCounts.toString());
    }
    
    public ArrayList<String> filterMeaninglessWords(ArrayList<String> input)
    {
        for(int x = 0; x<input.size();x++)
        {
            if(BlackList.contains(input.get(x)))
            {
                input.remove(x);
                x--;
            }
        }
        return input;
    }
    
    public void groupComments()
    {
        int wordCount;
        for(String s : AllUniqueWordsFilteredWithCounts)
        {
            //parse leading integer from string
            wordCount = Integer.parseInt(String.valueOf(s.charAt(0)));
            //form a group for each word which appears enough times to
            //meet the threshold
            if(wordCount >= GROUP_THRESHOLD)
            {
                //trim leading integer from string and create group
                s = s.substring(1, s.length());
                Groups.add(new CommentGroup(s));
            }
        }
        //iterate through AllComments and add any that contain a grouped keyword
        //to that group. Might be more efficient method
        String keyword;
        ArrayList<String> wordList;
        for(CommentGroup g : Groups)
        {
            keyword = g.getKeyword();
            for (CommentInstance c : AllComments) 
            {
                wordList = c.getUniqueWordList();
                if(wordList.contains(keyword))
                {
                    g.addComment(c);
                }
            }
        }
        //output groups to console
        System.out.print("Total Groups: "+Groups.size()+
                "\n----------------------------------\n");
        for(CommentGroup g : Groups)
        {
            System.out.print(g);
        }
    }
    
    public void clearArray(){
        AllComments.clear();
        AllUniqueWords.clear();
        AllUniqueWordsFilteredWithCounts.clear();
        AllUniqueWordsFiltered.clear();
        BlackList.clear();
        Groups.clear();
    }
}