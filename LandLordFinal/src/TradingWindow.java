import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// The window which allows players to trade money and properties
public class TradingWindow extends JPanel{
	
	private Object[] p1Props;
	private Object[] p2Props;
	private int p1Money;
	private int p2Money;
	private boolean traded;
	
	public TradingWindow(Player p1, Player p2) {
		traded = false;
        
		final PlayerTradePanel p1Panel = new PlayerTradePanel(p1);
		final PlayerTradePanel p2Panel = new PlayerTradePanel(p2);
		
		JButton tradeButton = new JButton("Confirm");

		tradeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if(p1Panel.getMoney() < p1.getMoney() && p2Panel.getMoney() < p2.getMoney()) {
					p1Props = p1Panel.getSelectedValues();
					p2Props = p2Panel.getSelectedValues();
					p1Money = p1Panel.getMoney();
					p2Money = p2Panel.getMoney();
					traded = true;
					MonopolyGame.println("Insufficient money for trade to occur!");
				}
			}          
		});
		
		setLayout(new GridLayout(1,3));
		add(p1Panel);
		add(p2Panel);
		add(tradeButton);
	}
	
    // returns true if the trade is complete
	public boolean isTraded() {
		return traded;
	}
	
    // returns a list of the traded properties
	public Object[] getTradedProperties(int pNum) {
		if(pNum == 1) {
			return p1Props;
		} else {
			return p2Props;
		}
	}
	
    //returns the corresponding players money they traded
	public int getMoneyTraded(int player) {
		if(player == 1) {
			return p1Money;
		} else {
			return p2Money;
		}
	}
}
