import java.awt.Image;

// object representing the card and containing info about it
public class Card {

	private String name;
	private String effect;
	private int amount;
	
	public Card(String name, int amount, String effect) {
		this.name = name;
		this.effect = effect;
		if(effect.equals("Money")) {
			this.amount = amount;
		} else if(effect.equals("Move")) {
			this.amount = amount;
		} else if(effect.equals("Jail")) {
			this.amount = amount;
			if(amount == 0) { //go into jail
				
			} else { //get out of jail free card
				
			}
		} else if(effect.equals("Pay")) { //if the card is to pay another player(s)
			//maybe implement later
		}
	}
	
	public String getEffect() {
		return this.effect;
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	public String getName() {
		return name;
	}
}
