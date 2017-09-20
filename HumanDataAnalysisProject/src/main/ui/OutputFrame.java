import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Arrays;
import java.awt.datatransfer.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

public class OutputFrame extends JFrame 
{
    private JButton newLookupButton;
    private JButton saveButton;
    private JButton exitButton;
    private JButton updateButton;
    private JLabel outputLabel;
    private JLabel optionsLabel;
    private JTextArea outputText;
    private JPanel outputPanel;
    private JPanel optionsPanel;
    private JCheckBox fullText;
    private JCheckBox wordCount; 
    private String s;
    
    public OutputFrame(String s) 
    {
        //init
        ActionHandler ah = new ActionHandler();
        outputLabel = new JLabel("Output");
        outputText = new JTextArea(100,50); //outputText may be better as a Pane so we can make it all stylized, but they are more work, so it's an Area for now
        outputText.setEditable(false);
        optionsPanel = new JPanel();
        optionsLabel = new JLabel("Options");
        newLookupButton = new JButton("New Lookup");
        saveButton = new JButton("Save");
        exitButton = new JButton("Exit");
        updateButton = new JButton("Update");
        fullText = new JCheckBox("Full Text");
        wordCount = new JCheckBox("Word Count");
        outputPanel = new JPanel();
        this.s =s;
        this.setLayout(new BorderLayout());
        
        
        //output panel has the output and other buttons
        //TODO: make the outputText uneditable
        this.add(outputPanel, BorderLayout.CENTER);
        outputPanel.add(outputText);
        outputPanel.add(newLookupButton);
        outputPanel.add(saveButton);
        outputPanel.add(exitButton);
        
        //options panel will have options that connect to our other code but I only have default so far
        this.add(optionsPanel, BorderLayout.WEST);
        optionsPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        optionsPanel.setLayout(new GridLayout(1,5));
        optionsLabel.setFont(new Font("Tahoma", 1, 12)); // NOI18N
        optionsPanel.add(optionsLabel);
        optionsPanel.add(fullText);
        optionsPanel.add(wordCount);
        optionsPanel.add(updateButton);
        
        newLookupButton.addActionListener(ah);
        saveButton.addActionListener(ah);
        exitButton.addActionListener(ah);
        updateButton.addActionListener(ah);
        
        OutputFormat();
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Output");
        pack();
    }
    private class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
           if (e.getSource()==newLookupButton)
           {
                setVisible(false);
                Main_UI main = new Main_UI();
                main.setVisible(true);
           }
           else if (e.getSource()==saveButton)
           {
               //TODO: give the option to overwrite already existing files before peeps actually do it by accident
                JFileChooser saveFile = new JFileChooser();
                saveFile.showSaveDialog(null);  
           }
           else if (e.getSource()==exitButton)
           {   
               //not sure if I want this or a system.exit(0)
               setVisible(false);
               Main_UI main = new Main_UI();
               main.setVisible(true);
           }
           else if (e.getSource()==updateButton)
           {
             OutputFormat();   
           }
        }
    }
    
    //this method will format our output text
    public void OutputFormat()
          
    {
        outputText.setText("");
        
        try 
        {
          FileReader fr = new FileReader(s);
          BufferedReader br = new BufferedReader(fr);
        
        
            //these parsing methods do not belong here
            if(fullText.isSelected())
            {
                    outputText.append(s +"/n");
            }   
            if(wordCount.isSelected())
            {
                String line = br.readLine ();
                int count = 0;
                while (line != null) 
                {
                    String []parts = line.split(" ");
                    for( String w : parts)
                    {
                        count++;        
                    }
                line = br.readLine();
                }         
                outputText.append("Word Count: "+count + "\n");
            }
            
        }    
       catch (IOException ex) 
        {
            JOptionPane.showMessageDialog(OutputFrame.this, "File could not be opened.",
                             "Get a file that works.", JOptionPane.INFORMATION_MESSAGE);;
        }
    }  
    //This method will eventually be streamlined to do pull data from both urls and files
    
}
   

