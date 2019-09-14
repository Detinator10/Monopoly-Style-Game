import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

// Right-hand panel with buttons for rolling and hiding pieces
public class ButtonPanel extends JPanel {
	
	private JTextArea t1;
	private JTextField t2;
	private String input;
	public ButtonPanel(final BoardCanvas board) {
		input = "";
		setLayout(new GridLayout(4,1));
		JButton roll = new JButton("Roll");
		add(roll);
		t1 = new JTextArea();
		t1.setBounds(0,0, 100,100);
		t1.setLineWrap(true);
		t1.setWrapStyleWord(true);
		
		t2 = new JTextField("Input text here...");
		t2.setBounds(0,0,100,50);
		Action enterPress = new AbstractAction()
		{
		    @Override
		    public void actionPerformed(ActionEvent e)
		    {
		        input = t2.getText();
		        t2.setText("");
		    }
		};
		t2.addFocusListener(new FocusListener(){
	        @Override
	        public void focusGained(FocusEvent e){
	            t2.setText("");
	        }
	        
	        public void focusLost(FocusEvent e) {
	        	t2.setText("Input text here...");
	        }
	    });
		t2.addActionListener(enterPress);
		
		
		JScrollPane p1 = new JScrollPane(t1);
		DefaultCaret caret = (DefaultCaret)t1.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		add(p1);
		add(t2);

		JButton hideButton = new JButton("Hide/Unhide Pieces");
		add(hideButton);
		hideButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				board.togglePieces();
				board.repaint();
			}          
	 });
		roll.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				MonopolyGame.roll();
			}          
	 });
	}
	
    // prints a line to the graphical console
	public void println(String line) {
		t1.setText(t1.getText() + "\n" + line);
	}
	
	public void print(String line) {
		t1.setText(t1.getText() + line);
	}
	
    // returns input from the input box and resets its contents to blank
	public String getInput() {
		String temp = input;
		input = "";
		return temp;
	}
	
    // returns the input from the input box without resetting its contents, intended to check if input has been changed
	public String checkInput() {
		return input;
	}
}
