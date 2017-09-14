/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package humandataanalysisproject;

import java.util.ArrayList;

/**
 *
 * @author aluben
 */
public class CommentInstance 
{
    private String _CommentRaw;
    private String _CommentNoPunct = "";
    private ArrayList<Character> _CharList;
    private char[] _PunctWhitelist;
    
    public CommentInstance(String inputString)
    {
        _PunctWhitelist = new char[] {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',' '
                                     ,'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        _CommentRaw = inputString;
        removePunctuation();
    }
    
    private void removePunctuation()
    {
        Character currentChar;
        _CharList = new ArrayList<>();
        
        //Iterate through comment and put into ArrayList of chars
        for(int z=0;z<_CommentRaw.length();z++)
        {
            _CharList.add(_CommentRaw.charAt(z));
        }
       
        for(int y=0;y<_CharList.size();y++)
        {
            currentChar = _CharList.get(y);
            
            for(int x=0; x<_PunctWhitelist.length;x++)
            {
                if(currentChar.equals(_PunctWhitelist[x]))
                {
                    _CommentNoPunct += _CharList.get(y);
                }
            }
        }
        System.out.print(_CommentNoPunct);
    }
}
