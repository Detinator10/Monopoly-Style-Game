import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

// Displays info about the current player at the bottom of the screen
public class PlayerInfoPanel extends JPanel {
	private JTextArea playerText;
	private JTextArea propText;
    
	public PlayerInfoPanel(Player p) {
		
		setLayout(new GridLayout(1,2));
		playerText = new JTextArea(p.toString());
		propText = new JTextArea(p.currentProperty());
		propText.setLineWrap(true);
		add(playerText);
		add(propText);
	}
	
    // refreshes player information and/or changes the displayed player
	public void update(Player p) {
		playerText.setText(p.toString());
		propText.setText(p.currentProperty());
	}
}
