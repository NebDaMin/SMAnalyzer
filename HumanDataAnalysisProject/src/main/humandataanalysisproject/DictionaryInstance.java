package main.humandataanalysisproject;

import com.swabunga.spell.engine.*;
import java.io.File;
import java.io.IOException;
        
public class DictionaryInstance
{
    private final GenericSpellDictionary _TheDictionary;
    private String _LanguageName;
    
    public DictionaryInstance(String dictionarySourceFile, String languageName) throws IOException
    {
        File dictionaryFile = new File(dictionarySourceFile);
        _TheDictionary = new GenericSpellDictionary(dictionaryFile);
    }
    
    public GenericSpellDictionary getDictionaryInstance()
    {
        return _TheDictionary;
    }
    
    public String getLanguageName()
    {
        return _LanguageName;
    }
}