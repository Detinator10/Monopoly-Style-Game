import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

// window allowing the player to purchase houses on monopolized properties
public class HousesPanel extends JPanel {
	private boolean finished;
	private final Player[] pWorkaround;
	public HousesPanel(Player p) {
		pWorkaround = new Player[1];
		pWorkaround[0] = p;
		finished = false;
		final ArrayList<Property> allMonopolies = MonopolyGame.getMonopolies(p);
		
		ArrayList<Property> eligibleProperties = getEligibleProperties(allMonopolies);
		
		final JList list1 = new JList(eligibleProperties.toArray());
		list1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list1.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list1.setVisibleRowCount(-1);
		JScrollPane listScroller1 = new JScrollPane(list1);
		listScroller1.setPreferredSize(new Dimension(300,300));
		
		JButton purchaseButton = new JButton("Purchase");

		purchaseButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				Object[] purchases = list1.getSelectedValuesList().toArray();
				for(int i = 0; i < purchases.length; i++) {
					if(pWorkaround[0].getMoney() > ((Property)purchases[i]).getPrice()){
						((Property)purchases[i]).addHouses(1);
						pWorkaround[0].receiveMoney(-((Property)purchases[i]).getPrice());
					}
				}
				MonopolyGame.repaint();
				list1.setListData(getEligibleProperties(allMonopolies).toArray());
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
		add(purchaseButton);
		add(doneButton);
		
	}
	
    // returns eligible properties (monopolized properties that have the same number of houses currently on them)
	public ArrayList<Property> getEligibleProperties(ArrayList<Property> allProps) {
		ArrayList<Property> eligibleProps = (ArrayList<Property>) allProps.clone();
		ArrayList<ArrayList<Property>> propsByColor = new ArrayList<ArrayList<Property>>();

		String curColor = "";
		ArrayList<Property> temp = new ArrayList<Property>();
		for(int i = 0; i < eligibleProps.size(); i++) {
			if(curColor.equals(eligibleProps.get(i).getColor())) {
				temp.add(eligibleProps.get(i));
			} else {
				if(temp.size() > 0) 
					propsByColor.add((ArrayList<Property>)temp.clone());
				temp.clear();
				curColor = eligibleProps.get(i).getColor();
				temp.add(eligibleProps.get(i));
			}
		}
		propsByColor.add((ArrayList<Property>)temp.clone());
		eligibleProps.clear();
		for(int i = 0; i < propsByColor.size(); i++) {
			int maxHouses = Integer.MAX_VALUE;
			boolean noMortgages = true;
			for(Property prop : propsByColor.get(i)) {
				if(maxHouses > prop.getHouses()) 
					maxHouses = prop.getHouses();
				if(prop.isMortgaged())
					noMortgages = false;
			}
			
			if(noMortgages) {
				for(Property prop : propsByColor.get(i)) {
					if(prop.getHouses() <= maxHouses && prop.getHouses() < 5) {
						eligibleProps.add(prop);
					}
				}
			}
		}
		return eligibleProps;
	}
	
	public boolean isDone() {
		return finished;
	}
}
