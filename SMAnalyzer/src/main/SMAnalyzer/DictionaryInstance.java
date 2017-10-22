package main.SMAnalyzer;

import com.swabunga.spell.engine.*;
import java.io.File;
import java.io.IOException;
        
public class DictionaryInstance
{
    private final GenericSpellDictionary TheDictionary;
    private String LanguageName;
    
    public DictionaryInstance(String dictionarySourceFile, String languageName) throws IOException
    {
        File dictionaryFile = new File(dictionarySourceFile);
        TheDictionary = new GenericSpellDictionary(dictionaryFile);
    }
    
    public GenericSpellDictionary getDictionaryInstance()
    {
        return TheDictionary;
    }
    
    public String getLanguageName()
    {
        return LanguageName;
    }
}