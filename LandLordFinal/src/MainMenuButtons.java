import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;

// JPanel containing the start game and numPlayers fields on the main menu window
public class MainMenuButtons extends JPanel {
	private JTextArea numPlayers;
	private JButton startButton;
	private boolean done;
	
	public MainMenuButtons() {
		
		done = false;
		
		numPlayers = new JTextArea("Insert # of players (2-4)");
		numPlayers.addFocusListener(new FocusListener(){
	        @Override
	        public void focusGained(FocusEvent e){
	            numPlayers.setText("");
	        }

			@Override
			public void focusLost(FocusEvent arg0) {}
	    });
		setLayout(new GridLayout(2,1));
		
		startButton = new JButton("Start Game");
		startButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				done = true;
			}          
		});
		
		add(startButton);
		add(numPlayers);

	}
	
	public boolean isDone() {
		return done;
	}
	
    // returns the number of players input or returns a default value of 2
	public int getNumPlayers() {
		try{
			return Integer.parseInt(numPlayers.getText());
		} catch(NumberFormatException e) {
			return 2;
		}
	}
}

