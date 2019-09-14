import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;

// Works as part of the TradingWindow, displays a single players properties and an input method to trade their money
public class PlayerTradePanel extends JPanel {
	
	private JTextArea playerMoneyText;
	private JList list;
	public PlayerTradePanel(Player p) {
		playerMoneyText = new JTextArea("Insert money...");
		playerMoneyText.addFocusListener(new FocusListener(){
	        @Override
	        public void focusGained(FocusEvent e){
	            playerMoneyText.setText("");
	        }

			@Override
			public void focusLost(FocusEvent arg0) {}
	    });
		setLayout(new GridLayout(3,1));
		JLabel playerName = new JLabel(p.getName());
		
		list = new JList(p.getProperties().toArray());
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(250,80));
		
		add(playerName);
		add(listScroller);
		add(playerMoneyText);
	}
	
    // returns the inputted money
	public int getMoney() {
		try{
			return Integer.parseInt(playerMoneyText.getText());
		} catch(NumberFormatException e) {
			return 0;
		}
	}
	
    // returns a list of the selected properties
	public Object[] getSelectedValues() {
		return list.getSelectedValuesList().toArray();
	}
}
