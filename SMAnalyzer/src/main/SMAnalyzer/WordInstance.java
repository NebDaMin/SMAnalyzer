package main.SMAnalyzer;

public class WordInstance implements Comparable<WordInstance> {
    
    private final String WORD;
    private int Count;
    private boolean HasCapFirstChar = false;
    
    public WordInstance(String s) {
        char theChar = s.charAt(0);
        if (theChar >= 'A' && theChar <= 'Z')
            HasCapFirstChar = true;
        WORD = s.toLowerCase();
        Count = 1;
    }
    
    public WordInstance(String s, int c) {
        char theChar = s.charAt(0);
        if (theChar >= 'A' && theChar <= 'Z')
            HasCapFirstChar = true;
        WORD = s.toLowerCase();
        Count = c;
    }
    
    public void increment() {
        Count++;
    }
    
    public String getWord() {
        return WORD;
    }
    
    public int getCount() {
        return Count;
    }
    
    public boolean getHasCapFirstChar() {
        return HasCapFirstChar;
    }
            
    
    @Override
    public String toString() {
        return "Keyword: " + WORD + " Count: " + Count + "\n";
    }
    
    @Override
    public int compareTo(WordInstance otherInstance) throws UnsupportedOperationException {
        return Integer.compare(this.Count, otherInstance.Count);
    }

}
