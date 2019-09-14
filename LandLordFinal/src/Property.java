import java.util.ArrayList;
import java.util.Scanner;

//class to store information about a property including: name, price, mortgaged, houses, rent, owner
public class Property {
	
	private String info; //stores the entire line of information from a file
	private String name;
	private String color;
	private Player owner;
	private int price;
	private boolean mortgaged;
	private boolean isOwned;
	private int houses;
	private int housePrice;
	private int rent;
	private String effect;
	
	public Property(String info) {
		Scanner scanInfo = new Scanner(info);
		this.info = info;
		this.color = scanInfo.next();
		if(color.equals("Effect")) { //for Go, Income Tax, Jail, and Luxury Tax
			this.name = scanInfo.next();
			while(!scanInfo.hasNextInt() && scanInfo.hasNext()) {
				name = name + " " + scanInfo.next();
			}
			this.price = scanInfo.nextInt();
			this.effect = scanInfo.next();
		} else if(color.equals("Card")) { //for community chest and chance cards
			this.name = scanInfo.next();
			while(!scanInfo.hasNextInt() && scanInfo.hasNext()) {
				name = name + " " + scanInfo.next();
			}
			this.effect = "Card";
		} else if(color.equals("Misc")) { //for free parking and visiting jail
			this.name = scanInfo.next();
			while(!scanInfo.hasNextInt() && scanInfo.hasNext()) {
				name = name + " " + scanInfo.next();
			}
			this.effect = "None";
		} else if(color.equals("Black")) { //for railroads
			this.name = scanInfo.next();
			while(!scanInfo.hasNextInt() && scanInfo.hasNext()) {
				name = name + " " + scanInfo.next();
			}
			this.price = scanInfo.nextInt();
			this.effect = "Railroad";
		} else if(color.equals("White")) { //for utilities
			this.name = scanInfo.next();
			while(!scanInfo.hasNextInt() && scanInfo.hasNext()) {
				name = name + " " + scanInfo.next();
			}
			this.price = scanInfo.nextInt();
			this.effect = "Utility";
		} else { //for regular properties
			this.name = scanInfo.next();
			while(!scanInfo.hasNextInt() && scanInfo.hasNext()) {
				name = name + " " + scanInfo.next();
			}
			this.price = scanInfo.nextInt();
			this.housePrice = scanInfo.nextInt();
			this.rent = scanInfo.nextInt();
			this.mortgaged = false;
			this.houses = 0;
			this.effect = "Property";
			this.isOwned = false;
		}
	}
	
	public Property() {
		
	}
	
	public String getEffect() {
		return effect;
	}
	
	public int getRent(int roll) {
		if(effect.equals("Utility")) {
			if(owner.numUtility() >= 2) {
				return roll * 10;
			} else {
				return roll * 4;
			}
		} else if(effect.equals("Railroad")) {
			int numRail = owner.numRailroad();
			if(numRail >= 4) return 200;
			else if(numRail == 3) return 100;
			else if(numRail == 2) return 50;
			else return 25;
		} else {
			return rent;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void addHouses(int num) {
		houses += num;
		adjustRent(owner);
	}
	
	//the following will change the rent according to the number of houses on the property
	public void adjustRent(Player p) {
		Scanner scanInfo = new Scanner(info);
		//skips all the info about the name and color until it gets to the numbers
		while(!scanInfo.hasNextInt() && scanInfo.hasNext()) {
			scanInfo.next();
		}
		scanInfo.next(); //skips price of property
		scanInfo.next(); //skips price of house
		
		for(int i = 0; i < houses; i++) {
			scanInfo.next(); //skips rent prices for # of houses until it gets to the correct one
		}
		
		rent = scanInfo.nextInt();
		
		//the following will find if the property color they are on has a monopoly, then will double the base rent (they have to have no houses)
		if(p != null && houses == 0) {
			ArrayList<Property> props = MonopolyGame.getMonopolies(p);
			int index = 0;
			while(index < props.size() && props.get(index).getColor() != this.color) {
				index++;
			}
			
			if(index < props.size()) {
				rent = rent * 2;
			}
		}
	}
	
	public String getColor() {
		return color;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public int getPrice() {
		return price;
	}
	
	public boolean isOwned() {
		return isOwned;
	}
	
	public void setOwner(Player p) {
		isOwned = true;
		owner = p;
	}
	
	public int mortgageProp() {
		mortgaged = true;
		return price/2;
	}
	
	public int buyBack() {
		mortgaged = false;
		return -price /2;
	}
	
	public boolean isMortgaged() {
		return mortgaged;
	}
	
	public int getHouses() {
		return houses;
	}
	
	public void resetProperty() {
		isOwned = false;
		owner = null;
		houses = 0;
		mortgaged = false;
		adjustRent(owner);
	}
	
	public int getHousePrice() {
		return housePrice;
	}
	
	public String getInfo() {
		return "Name: " + name + " Color: " + color + " Price: " + price + " House Price: " + housePrice + " Rent: " + rent + " Effect: " + effect;

	}
	
	public String toString() {
		return name;
	}	
}
