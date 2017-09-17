package humandataanalysisproject;

import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;
import java.io.File;
import java.io.IOException;
//import java.util.Scanner;

/**
 *
 * @author Ben Ciummo
 */
public class HumanDataAnalysisProject 
{
    public static void main(String[] args) throws IOException 
    {
        //File thefile = new File("externalfiles/BigAssDictionaryFromPrinceton.txt");
       // GenericSpellDictionary dictionary = new GenericSpellDictionary(thefile);
        
        //String teststring = new Scanner(new File("externalfiles/alice.txt")).useDelimiter("\\Z").next();
        String teststring = "Hello world crap, now with with punctuation and world shit!";
        
        CommentInstance testCI = new CommentInstance(teststring);
        
        //StringWordTokenizer finder = new StringWordTokenizer(teststring);
        
        //SpellChecker checker = new SpellChecker(dictionary);
        //int results = checker.checkSpelling(finder);
        //System.out.print(results);
        //checker.getSuggestions("thestring", 1);   
    }
}

