package main.SMAnalyzer;

import com.swabunga.spell.engine.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DictionaryInstance {

    private final GenericSpellDictionary TheDictionary;
    private String LanguageName;
    private ArrayList<String> CurrentTempDictionary;
    private boolean HasBeenUpdated;

    public DictionaryInstance(String dictionarySourceFile, String languageName) throws IOException {
        File dictionaryFile = new File(dictionarySourceFile);
        TheDictionary = new GenericSpellDictionary(dictionaryFile);
        CurrentTempDictionary = new ArrayList();
        //we could make use of a flag like this to determine if we need to rerun our english analysis
        HasBeenUpdated = true;
    }

    public GenericSpellDictionary getDictionaryInstance() {
        return TheDictionary;
    }

    public String getLanguageName() {
        return LanguageName;
    }

    //Turns out that the dictionary object was not nearly as complicated as I thought. At least for our v1 implementation
    //Here are some getters and setters to make use of.
    public void addTempWordToDict(String tempWord) {
        TheDictionary.addWord(tempWord);
        CurrentTempDictionary.add(tempWord);
    }

    public void addTempWordListToDict(ArrayList<String> tempWordList) {
        for (int y = 0; y < tempWordList.size(); y++) {
            TheDictionary.addWord(tempWordList.get(y));
            CurrentTempDictionary.add(tempWordList.get(y));
        }
    }

    public ArrayList<String> getCurrentTempDictionary() {
        return CurrentTempDictionary;
    }
}
