package humandataanalysisproject;

import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Ben Ciummo
 */
public class HumanDataAnalysisProject 
{
    public static void main(String[] args) throws IOException 
    {
        File thefile = new File("externalfiles/BigAssDictionaryFromPrinceton.txt");
        GenericSpellDictionary dictionary = new GenericSpellDictionary(thefile);
        
        
        String teststring = new String("Hello world crap");
        StringWordTokenizer finder = new StringWordTokenizer(teststring);
        SpellChecker checker = new SpellChecker(dictionary);
        int results = checker.checkSpelling(finder);
        System.out.print(results);
        checker.getSuggestions("thestring", 1);
        
               
        
    }
    
}
