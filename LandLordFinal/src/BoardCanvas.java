import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

public class BoardCanvas extends JPanel {
	private ArrayList<Player> players;
	private Property[] properties;
	private Board b;
	private boolean displayPieces;
	private int[][] globalLocs;
	private int[][] houseLocs;
	private String cardText;
	private int[][] ownerLocs;
	
	public BoardCanvas(ArrayList<Player> players, Board b) {
		cardText = "";
		this.players = players;
		displayPieces = true;
		this.b = b;
		this.properties = b.getProperties();
		this.globalLocs = b.getGlobalLocs();
		this.houseLocs = b.getHouseLocs();
		this.ownerLocs = b.getOwnerLocs();
	}
	
    // paints the background and pieces for the board
	public void paint(Graphics g) {
		// grabs game board image from file
		Toolkit t = Toolkit.getDefaultToolkit();
		Image b = t.getImage("monopoly.jpg");
		Image house = t.getImage("House1.png");
		Image hotel = t.getImage("Hotel.png");
		
		// displays game board image
		g.drawImage(b, 0, 0, 750, 750, this);
		
		if(!cardText.equals("")) {
			g.setColor(Color.WHITE);
			g.drawRoundRect(275, 175, 200, 400, 20, 20);
			g.fillRoundRect(275, 175, 200, 400, 20, 20);
			g.setColor(Color.BLACK);
			String formatted = "";
			for(int i = 0; i < cardText.length(); i++) {
				if(i == 0 || i % 35 != 0) {
					formatted += cardText.charAt(i) + "";
				} else {
					g.drawString(formatted, 285, 175 + 10 + (i/35)*10);
					formatted = "";
				}
			}
			if(cardText.length() % 35 == 0)
				g.drawString(formatted, 285, 175 + 10 + (cardText.length()/35)*10);
			else
				g.drawString(formatted, 285, 175 + 20 + (cardText.length()/35)*10);
		}
		
		for(int i = 0; i < properties.length; i++) {
			if(properties[i].isOwned())
				g.drawImage(properties[i].getOwner().getImage(), ownerLocs[i][0], ownerLocs[i][1], 25,25, this);
		}
		// displays property tokens on each property where they should be
		g.setFont(g.getFont().deriveFont(20.0f));
		for(int i = 0; i < properties.length; i++) {
			int houses = properties[i].getHouses();
			if(houses > 0) {
				if(houses < 5) {
					g.drawImage(house, houseLocs[i][0], houseLocs[i][1], 25,25, this);
					g.drawString(""+houses, houseLocs[i][0] + 5, houseLocs[i][1] + 20);
				} else {
					g.drawImage(hotel, houseLocs[i][0], houseLocs[i][1], 25,25, this);
					g.drawString("H", houseLocs[i][0] + 5, houseLocs[i][1] + 20);
				}
			}
		}
		
		//Displays each player piece at correct location if display pieces is enabled
		for(Player p : players) {
			if(displayPieces)
				g.drawImage(p.getImage(), p.getX(), p.getY(), 50, 50, this);
		}
	}
	
    // toggles whether player pieces are shown
	public void togglePieces() {
		displayPieces = !displayPieces;
	}
	
    // displays a card with the corresponding text on the screen
	public void setCardText(String cardText) {
		this.cardText = cardText;
	}
}
