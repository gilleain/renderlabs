package chalky;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel {

    public Main() {
        this.setSize(400, 400);
        this.setBackground(Color.BLACK);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        Main m = new Main();
        f.add(m);
        f.pack();
        f.setVisible(true);
    }

}
