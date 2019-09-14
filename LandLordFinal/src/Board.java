import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

//class that deals with where players and properties are
public class Board {

	private Property[] board;
	private int boardLength;
	private int[][] playerLocs;
	private Card[] chance;
	private Card[] community;
	private int[][] houseLocs;
	private int[][] ownerLocs;
	
	public Board() throws FileNotFoundException {
		File properties = new File("Properties.txt");
		File coordFile = new File("Locations.txt");
		File chanceFile = new File("Chance.txt");
		File communityFile = new File("CommunityChest.txt");
		File housesFile = new File("houseLocs.txt");
		Scanner scanProp = new Scanner(properties);
		Scanner scanLoc = new Scanner(coordFile);
		Scanner scanChance = new Scanner(chanceFile);
		Scanner scanCommunity = new Scanner(communityFile);
		Scanner scanHouses = new Scanner(housesFile);
		
		//finds # of properties
		int numProp = 0;
		while(scanProp.hasNextLine()) { //counts how many properties are in the txt doc
			scanProp.nextLine();
			numProp++;
		}
		numProp--; //gets rid of top line (formatting)
		
		//finds # of locations (for placing images)
		int numLoc = 0;
		while(scanLoc.hasNextLine()) { //counts how many properties are in the txt doc
			scanLoc.nextLine();
			numLoc++;
		}
		numLoc--; //gets rid of top line (formatting)
		
		//finds # of chance cards
		int numChance = 0;
		while(scanChance.hasNextLine()) {
			scanChance.nextLine();
			numChance++;
		}
		//no top line on this file
		
		//finds # of chance cards
		int numCommunity = 0;
		while(scanCommunity.hasNextLine()) {
			scanCommunity.nextLine();
			numCommunity++;
		}
		//no top line on this file
		
		//finds the # of house locations there are on the board
		int numHouseLocs = 0;
		while(scanHouses.hasNextLine()) {
			scanHouses.nextLine();
			numHouseLocs++;
		}
		scanHouses = new Scanner(housesFile);
		houseLocs = new int[numHouseLocs][2];
		
		//creates the board array with the correct number of properties (which is gotten before)
		board = new Property[numProp];
		
		//adds all the properties into the board
		scanProp = new Scanner(properties);
		scanProp.nextLine();
		int index = 0;
		while(scanProp.hasNextLine()) {
			board[index] = new Property(scanProp.nextLine());
			index++;
		}
		
		// reads list of player coords from file and stores them
		playerLocs = new int[numLoc][2];
		scanLoc = new Scanner(coordFile);
		scanLoc.nextLine();
		index = 0;
		while(scanLoc.hasNextLine()) {
			Scanner scanLine = new Scanner(scanLoc.nextLine());
			
			scanLine.next();
			int[] temp = {scanLine.nextInt(), scanLine.nextInt()};
			playerLocs[index] = temp;
			index++;
		}
		
		// reads list of coords for icons displaying owner of property
		scanLoc = new Scanner(new File("ownerLocs"));
		ownerLocs = new int[numLoc][2];
		index = 0;
		while(scanLoc.hasNextLine()) {
			Scanner scanLine = new Scanner(scanLoc.nextLine());
			int[] temp = {scanLine.nextInt() / 2, scanLine.nextInt() / 2 - 10};
			ownerLocs[index] = temp;
			index++;
		}
		
		//stores all the chance cards into the array of cards
		chance = new Card[numChance];
		scanChance = new Scanner(chanceFile);
		index = 0;
		while(scanChance.hasNextLine()) {
			Scanner scanLine = new Scanner(scanChance.nextLine());
			
			String cardName = scanLine.next();
			while(!scanLine.hasNextInt()) { //the card's name lasts until it hits a number
				cardName = cardName + " " + scanLine.next();
			}
			
			int amount = scanLine.nextInt();
			String effect = scanLine.next();
			
			chance[index] = new Card(cardName, amount, effect);
			index++;
		}
		
		//stores all the community chest cards into the array of cards
		community = new Card[numCommunity];
		scanCommunity = new Scanner(communityFile);
		index = 0;
		while(scanCommunity.hasNextLine()) {
			Scanner scanLine = new Scanner(scanCommunity.nextLine());
			
			String cardName = scanLine.next();
			while(!scanLine.hasNextInt()) { //the card's name lasts until it hits a number
				cardName = cardName + " " + scanLine.next();
			}
			
			int amount = scanLine.nextInt();
			String effect = scanLine.next();
			
			community[index] = new Card(cardName, amount, effect);
			
			index++;
		}
		
		//stores all the house locations in the array coordinates
		index = 0;
		while(scanHouses.hasNextLine()) {
			Scanner housesLine = new Scanner(scanHouses.nextLine());
			
			houseLocs[index][0] = (int)(housesLine.nextInt() / 2.0);
			houseLocs[index][1] = (int)(housesLine.nextInt() / 2.0) + 5;
			
			index++;
		}		
		
		boardLength = board.length;
	}
	
	public int getLength() {
		return boardLength;
	}
	
	public Property getProperty(int location) {
		return board[location];
	}
	
	public int[][] getGlobalLocs() {
		return playerLocs;
	}
	
	public Card drawChance() {
		int rand = (int)(Math.random() * chance.length);
		return chance[rand];
	}
	
	public Card drawCommunity() {
		int rand = (int)(Math.random() * community.length);
		return community[rand];
	}
	
	public Property[] getProperties() {
		return board;
	}
	
	public int[][] getHouseLocs() {
		return houseLocs;
	}
	
	public int[][] getOwnerLocs() {
		return ownerLocs;
	}
	
    // returns a list of all the properties that the Player p has a monopoly on
	public ArrayList<Property> getMonopolyProps(Player p) {
		String curColor = "";
		boolean streak = true;
		
		// monopolies stores the final list of monopolized properties
		ArrayList<Property> monopolies = new ArrayList<Property>();
		
		// temp stores the owned properties while building to a monopoly. Is added to monopolies if
		// 		those properties are monopolized
		ArrayList<Property> temp = new ArrayList<Property>();
		for(int i = 0; i < board.length; i++) {
			String color = board[i].getColor();
			
			// Checks if the current location is a non-property space
			if(!(color.equals("Card") || color.equals("Misc") || color.equals("Effect") || color.equals("White") || color.equals("Black"))) {
				//if color has changed, checks if the player owned all properties up to this point
				// 		If so, player has achieved a monopoly of the previous colors
				if(!color.equals(curColor)) {
					if(streak) {
						for(int j = 0; j < temp.size(); j++) {
							monopolies.add(temp.get(j));
						}
					}
					temp.clear();
					streak = true;
					curColor = color;
				}
				// adds owned properties to temp if streak is still running or breaks streak if 
				// 		property is unowned
				if(board[i].getOwner() != null && board[i].getOwner().equals(p) && streak) {
					temp.add(board[i]);
				} else {
					streak = false;
				}
			}
		}
		//adds leftover properties if there is a streak
		if(streak) {
			for(int j = 0; j < temp.size(); j++) {
				monopolies.add(temp.get(j));
				System.out.println(temp.get(j).getName() + "  " + temp.get(j).getColor());
			}
		}
		
		return monopolies;
	}
}
