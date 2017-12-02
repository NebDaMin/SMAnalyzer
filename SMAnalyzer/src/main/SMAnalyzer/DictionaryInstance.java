package main.SMAnalyzer;

import com.swabunga.spell.engine.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//This class maintains a dictionary loaded from a file.
//It is used to identify comments as english or not.
public class DictionaryInstance {

    private final GenericSpellDictionary TheDictionary;
    private String LanguageName;
    private ArrayList<String> CurrentTempDictionary;
    private boolean HasBeenUpdated;

    public DictionaryInstance(String dictionarySourceFile, String languageName) throws IOException {
        File dictionaryFile = new File(dictionarySourceFile);
        TheDictionary = new GenericSpellDictionary(dictionaryFile);
        CurrentTempDictionary = new ArrayList();
        HasBeenUpdated = true;
    }

    public GenericSpellDictionary getDictionaryInstance() {
        return TheDictionary;
    }

    public String getLanguageName() {
        return LanguageName;
    }

    public void addCustomWordToDict(String tempWord) {
        TheDictionary.addWord("\n------------------\n" + tempWord);
        CurrentTempDictionary.add(tempWord);
    }

    public void addCustomWordListToDict(ArrayList<String> tempWordList) {
        for (int y = 0; y < tempWordList.size(); y++) {
            TheDictionary.addWord(tempWordList.get(y));
            CurrentTempDictionary.add(tempWordList.get(y));
        }
    }

    public ArrayList<String> getCurrentCustomDictionary() {
        return CurrentTempDictionary;
    }
}