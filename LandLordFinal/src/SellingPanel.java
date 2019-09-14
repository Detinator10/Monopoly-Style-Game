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

// Window that allows for houses to be sold
public class SellingPanel extends JPanel {
	private boolean finished;
	private final Player[] pWorkaround;
	public SellingPanel(Player p) {
		pWorkaround = new Player[1];
		pWorkaround[0] = p;
		finished = false;
		final ArrayList<Property> propsWithHouses = eligibleProperties(p.getProperties());
		
		final JList list1 = new JList(propsWithHouses.toArray());
		list1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list1.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list1.setVisibleRowCount(-1);
		JScrollPane listScroller1 = new JScrollPane(list1);
		listScroller1.setPreferredSize(new Dimension(300,300));
		
		JButton sellButton = new JButton("Sell");

		sellButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				Object[] soldProps = list1.getSelectedValuesList().toArray();
				for(int i = 0; i < soldProps.length; i++) {
					((Property)soldProps[i]).addHouses(-1);
					pWorkaround[0].receiveMoney(((Property)soldProps[i]).getPrice());
				}
				MonopolyGame.repaint();
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
		add(sellButton);
		add(doneButton);
		
	}
	
    // returns a list of the eligible properties (properties with houses on them)
	public ArrayList<Property> eligibleProperties(ArrayList<Property> allProps) {
		ArrayList<Property> eligibleProps = new ArrayList<Property>();
		for(Property p : allProps) {
			if(p.getHouses() > 0) {
				eligibleProps.add(p);
			}
		}
		return eligibleProps;
	}
	
    // returns true when the window has completed and can be closed
	public boolean isDone() {
		return finished;
	}
}
