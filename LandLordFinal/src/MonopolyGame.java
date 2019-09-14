import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

// game client
public class MonopolyGame {
	
	private static volatile boolean roll = false;
	private static BoardCanvas boardPanel;
	private static PlayerInfoPanel infoPanel;
	private static ButtonPanel buttonPanel;
	private static ArrayList<Player> playersInGame;
	private static Board board;
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		MainMenuButtons menuButtons = new MainMenuButtons();
		JFrame menuFrame = new JFrame();
		menuFrame.add(new MainMenu(), BorderLayout.CENTER);
		menuFrame.add(menuButtons, BorderLayout.SOUTH);
		menuFrame.setSize(595/2,500);
		menuFrame.setResizable(false);
		menuFrame.setVisible(true);
		menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		while(!menuButtons.isDone()){
			try {
				Thread.sleep(200);
			} catch(InterruptedException e) {}
		}
		
		int numPlayers = menuButtons.getNumPlayers();
		if(numPlayers > 4) 
			numPlayers = 4;
		else if(numPlayers < 2)
			numPlayers = 2;
		
		menuFrame.setVisible(false);
		menuFrame.dispose();
		
		board = new Board(); // initialize boardPanel object
		
		// Creates list of players
		ArrayList<Player> players = new ArrayList<Player>();
		for(int i = 0; i < numPlayers; i++) {
			players.add(new Player("Player " + (i+1),i+1, board.getGlobalLocs()));
		}
		playersInGame = players;

		boardPanel = new BoardCanvas(players, board); // initialize boardPanel image
		boardPanel.setBounds(0,0,750,750); //set screen dimensions of board
		
		// Initialize and add components to JFrame
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(boardPanel,BorderLayout.CENTER);
		buttonPanel = new ButtonPanel(boardPanel);
		f.add(buttonPanel, BorderLayout.EAST);
		infoPanel = new PlayerInfoPanel(players.get(0));
		f.add(infoPanel, BorderLayout.SOUTH);
		f.setSize(910,800);
		f.setVisible(true);
		JFrame f2 = new JFrame();
		
		//loops until there is only one player left in the ArrayList (meaning the game is done)
		//this is the loop where there actual game occurs, with players making moves until only one person is not bankrupt
		while(!Player.gameDone(players)) {
			int index = 0;
			//loops through all the players
			while(index < players.size() && !Player.gameDone(players)) {
				Player p = players.get(index);
				println("\n" + " -------------------------------");
				println("Current Player: " + p.getName());
				updateInfoPanel(p);
				p.move(board);
				boardPanel.repaint();
				if(p.isBankrupt()) {
					println(p.getName() + " is bankrupt!");
					p.resetPlayerProps();
					players.remove(index);
				} else {
					index++;
				}
			 }
		 }
		
		println(players.get(0).getName() + " is the winner!");
		
	}
	
    // method which indicates the roll button has been clicked and allows the program to move forward in the player move method
	public static void roll() {
		roll = true;
		try {
			Thread.sleep(200);
		} catch(InterruptedException e) {}
		roll = false;
	}
	
    // a relay method to allow instance classes to repaint the graphical elements
	public static void repaint() {
		boardPanel.repaint();
	}
	
	public static void updateInfoPanel(Player p) {
		infoPanel.update(p);
	}
	
    // relay method to print to the graphical console
	public static void println(String line) {
		buttonPanel.println(line);
	}
	
	public static void print(String line) {
		buttonPanel.print(line);
	}
	
    // Method which returns a string input from the console input box
	public static String inputString() {
		while(buttonPanel.checkInput().equals("")){
			try {
				Thread.sleep(200);
			} catch(InterruptedException e) {}
		}
		return buttonPanel.getInput();
	}
	
    // Method which specifically returns an integer from the console input box
	public static int inputInt() {
		while(buttonPanel.checkInput().equals("")){
			try {
				Thread.sleep(200);
			} catch(InterruptedException e) {}
		}
		
		try{
			return Integer.parseInt(buttonPanel.getInput());
		} catch(NumberFormatException e) {
			return -1;
		}
	}
	
    // initiates trade between two players, opens correct windows, and moves money/properties
	public static void trade(Player p1, Player p2) {
		JFrame f2 = new JFrame();
		TradingWindow t1 = new TradingWindow(p1, p2);
		f2.add(t1,BorderLayout.CENTER);
		f2.setBounds(0,0, 400,400);
		f2.setVisible(true);
		
		while(!t1.isTraded() && f2.isVisible()){
			try {
				Thread.sleep(200);
			} catch(InterruptedException e) {}
		}
		if(t1.isTraded()) {
			p1.removeProperties(t1.getTradedProperties(1));
			p2.removeProperties(t1.getTradedProperties(2));
			p1.addProperties(t1.getTradedProperties(2));
			p2.addProperties(t1.getTradedProperties(1));
			p2.receiveMoney(t1.getMoneyTraded(1) - t1.getMoneyTraded(2));
			p1.receiveMoney(t1.getMoneyTraded(2) - t1.getMoneyTraded(1));
		}
		f2.setVisible(false);
		f2.dispose();
	}
	
	public static ArrayList<Player> getPlayers() {
		return playersInGame;
	}
	
	public static ArrayList<Property> getMonopolies(Player p) {
		return board.getMonopolyProps(p);
	}
	
    // opens a window to buy houses for the player
	public static void buyHouses(Player p) {
		JFrame f2 = new JFrame();
		HousesPanel h1 = new HousesPanel(p);
		f2.add(h1,BorderLayout.CENTER);
		f2.setBounds(0,0, 300,300);
		f2.setVisible(true);
		
		while(!h1.isDone() && f2.isVisible()){
			try {
				Thread.sleep(200);
			} catch(InterruptedException e) {}
		}

		f2.setVisible(false);
		f2.dispose();
	}
	
    // opens a window to sell houses for the player
	public static void sellHouses(Player p) {
		JFrame f2 = new JFrame();
		SellingPanel s1 = new SellingPanel(p);
		f2.add(s1,BorderLayout.CENTER);
		f2.setBounds(0,0, 300,300);
		f2.setVisible(true);
		
		while(!s1.isDone() && f2.isVisible()){
			try {
				Thread.sleep(200);
			} catch(InterruptedException e) {}
		}
		f2.setVisible(false);
		f2.dispose();
	}
    
    // opens a window to mortgage houses for the player
	public static void mortgageHouses(Player p) {
		JFrame f2 = new JFrame();
		MortgagePanel m1 = new MortgagePanel(p);
		f2.add(m1,BorderLayout.CENTER);
		f2.setBounds(0,0, 300,300);
		f2.setVisible(true);
		
		while(!m1.isDone() && f2.isVisible()){
			try {
				Thread.sleep(200);
			} catch(InterruptedException e) {}
		}

		f2.setVisible(false);
		f2.dispose();
	}
	
    // opens a window to un-mortgage houses for the player
	public static void buyBackHouses(Player p) {
		JFrame f2 = new JFrame();
		BuybackPanel b1 = new BuybackPanel(p);
		f2.add(b1,BorderLayout.CENTER);
		f2.setBounds(0,0, 300,300);
		f2.setVisible(true);
		
		while(!b1.isDone() && f2.isVisible()){
			try {
				Thread.sleep(200);
			} catch(InterruptedException e) {}
		}

		f2.setVisible(false);
		f2.dispose();
	}
	
    // draws a card on the board with corresponding text for 3 seconds and then disables it
	public static void drawCard(String cardText) {
		boardPanel.setCardText(cardText);
		repaint();
		try {
			Thread.sleep(3000);
		}catch (InterruptedException e) {}
		boardPanel.setCardText("");
		repaint();
	}
	
	// pauses thread while waiting for input from roll button
	public static void waitForRoll() {
		while(!roll) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		roll = false;
	}
}
