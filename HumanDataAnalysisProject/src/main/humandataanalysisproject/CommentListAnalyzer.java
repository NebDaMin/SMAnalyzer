package main.humandataanalysisproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class CommentListAnalyzer 
{
    //Declare Class Variables
    private ArrayList<CommentInstance> AllComments;
    private final DictionaryInstance Dictionary;
    private ArrayList<String> AllUniqueWords;
    private ArrayList<String> AllUniqueWordsWithCounts;
    private ArrayList<String> AllUniqueWordsFiltered;
    
    public CommentListAnalyzer() throws IOException 
    {
        //Initialize Class Variables
        Dictionary = new DictionaryInstance("externalfiles/BigAssDictionaryFromPrinceton.txt","English");
        AllComments = new ArrayList();
        AllUniqueWords = new ArrayList();
        AllUniqueWordsWithCounts = new ArrayList();
        AllUniqueWordsFiltered = new ArrayList();
    }
    
    public void setComments(ArrayList<String> post) throws IOException
    {   
        //Adding ArrayList of strings from input to ArrayList of CommentInstances
        for(int x =0;x<post.size();x++)
        {
            AllComments.add(new CommentInstance(post.get(x),Dictionary.getDictionaryInstance()));
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
        //TODO: rename AllUniqueWordsFiltered as needed
        AllUniqueWordsFiltered = filterMeaninglessWords(AllUniqueWords);
        
        //Loading the sorted list of AllUniqueWords into another ArrayList that is formatted to store
        //Only one instance of each word but with the number of unique reoccurances preceeding it
        //Once sorted, the highest frequency words will bubble to the top
        int currentCountForWord = 1;        
        for(int k = 0; k<AllUniqueWords.size()-1;k++)
        {
            if(AllUniqueWords.get(k).equals(AllUniqueWords.get(k+1)))
            {
                currentCountForWord += 1;
            }
            else
            {
                AllUniqueWordsWithCounts.add(currentCountForWord + AllUniqueWords.get(k));
                currentCountForWord = 1;
            } 
        }
        Collections.sort(AllUniqueWordsWithCounts);
        System.out.print(AllUniqueWordsWithCounts.toString());
    }
    
    public ArrayList<String> filterMeaninglessWords(ArrayList<String> input)
    {
        //TODO: This is where the code for excluding meaningless words will go
        return input;
    }
}