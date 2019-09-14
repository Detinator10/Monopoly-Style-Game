import java.awt.*;
import java.awt.Toolkit;

import javax.swing.*;

// Displays the title graphic on the main menu window
public class MainMenu extends JPanel {
	public MainMenu() {}
	
	public void paint(Graphics g) {
        Toolkit t = Toolkit.getDefaultToolkit();
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, 1000, 1000);
        Image title = t.getImage("monopoly-logo.jpg");
        g.drawImage(title, 0, 0, 595/2, 842/2, this);
    }
}