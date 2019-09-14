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

// Window which allows the player to mortgage properties
public class MortgagePanel extends JPanel {
	private boolean finished;
	private final Player[] pWorkaround;
	
	public MortgagePanel(Player p) {
		pWorkaround = new Player[1];
		pWorkaround[0] = p;
		finished = false;
		final ArrayList<Property> propsNoHouses = eligibleProperties(p.getProperties());
		
		final JList list1 = new JList(propsNoHouses.toArray());
		list1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list1.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list1.setVisibleRowCount(-1);
		JScrollPane listScroller1 = new JScrollPane(list1);
		listScroller1.setPreferredSize(new Dimension(300,300));
		
		JButton sellButton = new JButton("Sell");

		sellButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				Object[] mortgagedProps = list1.getSelectedValuesList().toArray();
				for(int i = 0; i < mortgagedProps.length; i++) {
					pWorkaround[0].receiveMoney(((Property)mortgagedProps[i]).mortgageProp());
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
		add(sellButton);
		add(doneButton);
		
	}
	
    // returns a list of the eligible properties (properties where no property in the color has a house on it)
	public ArrayList<Property> eligibleProperties(ArrayList<Property> allProps) {
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
			boolean noHouses = true;
			for(Property prop : propsByColor.get(i)) {
				if(prop.getHouses() > 0) 
					noHouses = false;
			}
			if(noHouses) {
				for(Property prop : propsByColor.get(i)) {
					if(!prop.isMortgaged())
						eligibleProps.add(prop);
				}
			}
		}
		return eligibleProps;
	}
    
	public boolean isDone() {
		return finished;
	}
}
