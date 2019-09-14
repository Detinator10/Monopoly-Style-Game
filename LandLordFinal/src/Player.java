import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.ListModel;

// Player class which stores all the relevant information and functions of each respective player
public class Player {
	
	private final int OFFSET = 10;
	
	private String name;
	private int num;
	private int location;
	private int money;
	private ArrayList<Property> properties;
	private boolean inJail;
	private int turnsInJail;
	private ArrayList<Card> cards;
	private int[][] coords;
	private Image icon;
	private Property curProperty;
	
	public Player(String name, int num, int[][] globalCoords) {
		curProperty = new Property();
		this.name = name;
		this.num = num;
		location = 0;
		money = 1500;
		properties = new ArrayList<Property>();
		cards = new ArrayList<Card>();
		inJail = false;
		turnsInJail = 0;
		
		coords = new int[globalCoords.length][2];
		
		coords = globalCoords;
		
		Toolkit t = Toolkit.getDefaultToolkit();
		icon = t.getImage("player" + num + ".jpg");
		
		//coordinates = new int[1][1];
	}
	
	//method that will roll dice and continue their movement while they roll doubles or until they are in jail
	public void move(Board b) {
		int numDoubles = 0;
		int amount1 = 0;
		int amount2 = 0;
		while(amount1 == amount2 && numDoubles < 3) {
			MonopolyGame.println("Waiting for roll...");
			MonopolyGame.waitForRoll();
			amount1 = (int)(Math.random() * 6 + 1);
			amount2 = (int)(Math.random() * 6 + 1);
			int amount = amount1 + amount2;
			MonopolyGame.println("You rolled a " + amount1 + " and a " + amount2 + "!");
			if(!inJail) {
				if(amount1 == amount2) {
					numDoubles++;
				} 
				
				if(numDoubles < 3) {
					move(b, amount);
				} else {
					jail();
				}
			} else if(amount1 == amount2) { //free from jail with doubles
				inJail = false;
				MonopolyGame.println("You rolled doubles and got out of jail!");
				move(b, amount);
			} else {
				inJailTurn();
			}
			
		}
		
		if(!inJail) {
			afterMove(b);
		}
	}
	
	//method that will move the player a given amount of spaces and performs the action of the property in which they land on
	public void move(Board b, int amount) {
		if((location + amount)/b.getLength() > 0) {
			receiveMoney(200);
			MonopolyGame.println("Passed Go, collect $200");
		}
		
		location = (location + amount)%b.getLength();
		
		Property p = b.getProperty(location);
		curProperty = p;
		
		MonopolyGame.updateInfoPanel(this);
		MonopolyGame.repaint(); //refreshes display to show updated location
		
		if(p.getEffect().equals("Property")) { //what happens when they land on a regular colored property
			moveProperty(p, amount);
		} else if(p.getEffect().equals("Money")) {
			moveMoney(p);
		} else if(p.getEffect().equals("Card")) {
			if(p.getName().equals("Chance")) {
				//draws a chance card object (to be created later)
				Card c = b.drawChance();
				MonopolyGame.drawCard(c.getName());
				cardEffect(c, b);
				MonopolyGame.println(c.getName());
			} else if(p.getName().equals("Community Chest")) {
				//draws a community chest card object (to be created later)
				Card c = b.drawCommunity();
				MonopolyGame.drawCard(c.getName());
				cardEffect(c, b);
				MonopolyGame.println(c.getName());
			}
		} else if(p.getEffect().equals("Utility")) {
			moveProperty(p, amount);
		} else if(p.getEffect().equals("Railroad")) {
			moveProperty(p, amount);
		} else if(p.getEffect().equals("Jail")) {
			location = 10; //moves to the location of the jail
			inJail = true;
		}
	}
	
	//method that runs after a players move is complete
	//gives the user a set of options to choose from that will do various things
	public void afterMove(Board b) { 
		int choice = -1;
		while(choice != 1) {
			choice = -1;
			while(choice > 6 || choice < 1) {
				MonopolyGame.println("What would you like to do next?");
				MonopolyGame.println("1. End turn");
				MonopolyGame.println("2. Trade");
				MonopolyGame.println("3. Mortage");
				MonopolyGame.println("4. Redeem Property");
				MonopolyGame.println("5. Buy Houses");
				MonopolyGame.println("6. Sell houses");
				choice = MonopolyGame.inputInt();
				
				//to stop their money from being below 0 when ending their turn if they are able to mortgage properties to get above 0
				if(choice == 1 && money < 0 && !isBankrupt()) {
					MonopolyGame.println("You need to mortgage properties or sell houses before ending your turn!");
					choice = -1;
				}
			}
			//implement ending turn, mortgage/unmortgage, buying houses, maybe trading
			
			if(choice == 1) { //ending their turn
				MonopolyGame.println("End of turn");
			} else if(choice == 2) { //trading with another player of their choice
				ArrayList<Player> players = MonopolyGame.getPlayers();
				MonopolyGame.println("Which player would you like to trade with?");
				int tradeChoice = -1;
				while(tradeChoice < 1 || tradeChoice > players.size()) {
					for(int i = 0; i < players.size(); i++) {
						if(!players.get(i).equals(this))
							MonopolyGame.println(i + 1 + ") " + players.get(i).getName());
					}
					tradeChoice = MonopolyGame.inputInt();
				}
				
				//now allows the player to choose properties and amounts of money from either player on a GUI
				MonopolyGame.trade(this, players.get(tradeChoice - 1));
			} else if(choice == 3) { //mortgaging one of their owned properties
				MonopolyGame.mortgageHouses(this);
			} else if(choice == 4) { //unmortgaging one of their mortgaged properties (redeeming it)
				MonopolyGame.buyBackHouses(this);
			} else if(choice == 5) { //buying houses for one of their owned properties
				MonopolyGame.buyHouses(this);
			} else if(choice == 6) { //selling houses on one of their properties with houses on it
				MonopolyGame.sellHouses(this);
			}
		}
	}
	
	//the method that will occur if a player lands on a regular property
	//if they own it: does nothing
	//if someone else owns it, they pay rent
	//if nobody owns it, they have the option to buy it
	public void moveProperty(Property p, int roll) {
		if(p.isOwned()) {
			if(p.getOwner() != this) {
				if(!p.isMortgaged()) {
					MonopolyGame.println(p.getName() + " is owned by " + p.getOwner().getName() + " pay them $" + p.getRent(roll));
					payRent(p, p.getOwner(), roll);
				} else {
					MonopolyGame.println("This property is mortgaged. No rent paid.");
				}
			} else {
				MonopolyGame.println("You already own this space");
			}
		} else {
			if(money > p.getPrice()) { //if the player has enough money to purchase the property, they are given the option to
				MonopolyGame.println("Property is unowned, would you like to purchase it? ($" + p.getPrice() + ")");
				String ans = MonopolyGame.inputString();
				if(ans.charAt(0) == 'y') {
					receiveMoney(-p.getPrice());
					p.setOwner(this);
					properties.add(p);
					MonopolyGame.repaint();
					MonopolyGame.println(name + " purchased " + p.getName());
				} 
			} else {
				MonopolyGame.println("You don't have enough money to buy this property!");
			}
		}
	}
	
	//what happens when a player lands on a property that has an effect on the players money
	public void moveMoney(Property p) {
		MonopolyGame.println("You lost $" + Math.abs(p.getPrice()) + "!");
		receiveMoney(p.getPrice()); //adds or subtracts the money (if negative)
	}
	
	//method that runs when the player is in jail
	public void inJailTurn() {
		if(turnsInJail < 3) {
			int indexCard = hasGetOutOfJail(); //checks if the player has a get out of jail free card or not
			if(indexCard == -1) { //if the player doesnt have a get out of jail free card, they have the option to pay
				MonopolyGame.println("You're in jail! Would you like to spend $50 to get out of jail?");
				String ans = MonopolyGame.inputString();
				if(ans.charAt(0) == 'y') {
					receiveMoney(-50);
					inJail = false;
					turnsInJail = 0;
				} else {
					turnsInJail++;
				}
			} else { //if the player has a get out of jail free card
				MonopolyGame.println("You're in jail! Would you like to use your get out of jail free card?");
				String ans = MonopolyGame.inputString();
				if(ans.charAt(0) == 'y') {
					cards.remove(indexCard);
					inJail = false;
					turnsInJail = 0;
				} else {
					turnsInJail++;
				}
			}
		} else { //forces the user to get out of jail after 3 turns in it
			MonopolyGame.println("You have to pay $50 to get out of jail.");
			receiveMoney(-50);
			inJail = false;
			turnsInJail = 0;
		}
	}
	
	//method to pay rent to another player
	public void payRent(Property p, Player p1, int roll) {
		int amount = p.getRent(roll);
		p1.receiveMoney(amount);
		receiveMoney(-amount);
	}
	
	//method that gives a player money, and then updates the graphics with it
	public void receiveMoney(int amount) {
		money = money + amount;
		MonopolyGame.updateInfoPanel(this);
	}
	
	//method that gets the number of railroads a person has in their properties
	public int numRailroad() {
		int num = 0;
		for(int i = 0; i < properties.size(); i++) {
			if(properties.get(i).getEffect().equals("Railroad")) {
				num++;
			}
		}
		
		return num;
	}
	
	//method that gets the number of utility properties that a person has in their properties
	public int numUtility() {
		int num = 0;
		for(int i = 0; i < properties.size(); i++) {
			if(properties.get(i).getEffect().equals("Utility")) {
				num++;
			}
		}
		
		return num;
	}
	
	//gets the x location of the player
	public int getX() {
		return coords[location][0];
	}
	
	//gets the y location of the player
	public int getY() {
		return coords[location][1];
	}
	
	//gets the player's image
	public Image getImage() {
		return icon;
	}
	
	//gets the amount of money a player has
	public int getMoney() {
		return money;
	}
	
	//gets the players name
	public String getName() {
		return this.name;
	}
	
	//method that will move the player to jail and set them to be in jail
	public void jail() {
		location = 10;
		inJail = true;
	}
	
	//adds a card to the players hand (not properties)
	public void addCard(Card card) {
		cards.add(card);
	}
	
	//gets the effect of a card and applies it to the player
	public void cardEffect(Card c, Board b) {
		if(c.getEffect().equalsIgnoreCase("Jail")) {
			if(c.getAmount() == 0) { //going to jail
				jail();
			} else if(c.getAmount() == 1) {
				cards.add(c);
			}
		} else if(c.getEffect().equalsIgnoreCase("Go")){
			location = 0;
			receiveMoney(200);
		} else if(c.getEffect().equalsIgnoreCase("Money")) {
			receiveMoney(c.getAmount());
		} else if(c.getEffect().equalsIgnoreCase("Move")) {
			move(b, c.getAmount());
		}
	}
	
	//removes properties from the player if they are in the arraylist of objects
	public void removeProperties(Object[] objects) {
		for(int i = 0; i < properties.size(); i++) {
			for(int j = 0; j < objects.length; j++) {
				if(objects[j] == properties.get(i)) {
					properties.remove(i);
				}
			}
		}
	}
	
	//adds properties to the players properties ArrayList
	public void addProperties(Object[] properties2) {
		for(int i = 0; i < properties2.length; i++) {
			Property prop = (Property) properties2[i];
			properties.add(prop);
			prop.setOwner(this);
		}
	}
	
	//resets the properties a player has (for when they go bankrupt)
	public void resetPlayerProps() {
		for(int i = 0; i < properties.size(); i++) {
			properties.get(i).resetProperty();
		}
	}
	
	//gets the current property a player is on
	public String currentProperty() {
		return curProperty.toString();
	}
	
	//gets the ArrayList of properties that the player has
	public ArrayList<Property> getProperties() {
		return properties;
	}
	
	//returns true or false based on if the player has a get out of jail free card in their hand
	public int hasGetOutOfJail() {
		int ans = -1;
		int i = 0;
		while(ans == -1 && i < cards.size()) {
			if(cards.get(i).getEffect().equals("Jail") && cards.get(i).getAmount() != 0) {
				ans = i;
			}
		}
		return ans;
	}
	
	//returns true or false if the player is bankrupt from the game or not
	public boolean isBankrupt() {
		int numMortgaged = 0;
		int worthMortgaged = 0;
		
		//checks if the total worth of all their properties and houses will make them not be bankrupt, otherwise they are bankrupt
		for(int i = 0; i < properties.size(); i++) {
			if(properties.get(i).isMortgaged()) {
				numMortgaged++;
			} else {
				if(properties.get(i).getHouses() != 0) {
					for(int j = 0; j < properties.get(i).getHouses(); j++)
						worthMortgaged = worthMortgaged + properties.get(i).getHousePrice();
				} 
				worthMortgaged = worthMortgaged + properties.get(i).getPrice();
			}
		}
		
		if(money + worthMortgaged < 0) {
			return true;
		}
		return false;
		
	}
	
	//static method return true if there is only one player in the ArrayList of players
	public static boolean gameDone(ArrayList<Player> players) {
		if(players.size() <= 1) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return "Player: " + name + " Money: $" + money;
	}
}
