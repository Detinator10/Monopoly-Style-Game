import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

// Window allowing the player to purchase properties back from mortgage
public class BuybackPanel extends JPanel {
	private boolean finished;
	private final Player[] pWorkaround;
	
	public BuybackPanel(Player p) {
		pWorkaround = new Player[1];
		pWorkaround[0] = p;
		finished = false;
		final ArrayList<Property> mortgagedProps = eligibleProperties(p.getProperties());
		
		final JList list1 = new JList(mortgagedProps.toArray());
		list1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list1.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list1.setVisibleRowCount(-1);
		JScrollPane listScroller1 = new JScrollPane(list1);
		listScroller1.setPreferredSize(new Dimension(300,300));
		
		JButton buyButton = new JButton("Buy");

		buyButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				Object[] boughtProps = list1.getSelectedValuesList().toArray();
				for(int i = 0; i < boughtProps.length; i++) {
					if(pWorkaround[0].getMoney() >= ((Property)boughtProps[i]).getPrice()/2)
						pWorkaround[0].receiveMoney(((Property)boughtProps[i]).buyBack());
				}
				list1.setListData(eligibleProperties(pWorkaround[0].getProperties()).toArray());
			}          
		});

		JButton doneButton = new JButton("Done");

		doneButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				finished = true;
			}          
		});
		
		setLayout(new GridLayout(1,3));
		add(listScroller1);
		add(buyButton);
		add(doneButton);
		
	}
	
    // returns eligible properties (ALl properties that are mortgaged)
	public ArrayList<Property> eligibleProperties(ArrayList<Property> allProps) {
		ArrayList<Property> eligibleProps = new ArrayList<Property>();
		for(Property p : allProps) {
			if(p.isMortgaged()) {
				eligibleProps.add(p);
			}
		}
		return eligibleProps;
	}
	
	public boolean isDone() {
		return finished;
	}
}
