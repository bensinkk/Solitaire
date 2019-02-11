import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class Client
{
    public static JFrame frame;
    public static JPanel panel;
    public static void main(String[] args){
        frame = new JFrame("Neatest Game");
        panel = new IntroScreen();
        frame.add(panel);
        frame.setSize(300,160);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    private static class IntroScreen extends JPanel{
        public IntroScreen(){
            JButton klondike = new JButton("KLONDIKE");
            klondike.setSize(150,160);
            klondike.addActionListener(new KlondikeListener());
            add(klondike);

            JButton freecell = new JButton("FREECELL");
            freecell.setSize(150,160);
            freecell.addActionListener(new FreecellListener());
            freecell.setLocation(150,0);
            add(freecell);
        }
    }
    private static class KlondikeListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            frame.setLocation(0,0);
            frame.setSize(700,800);
            frame.remove(panel);
            frame.add(new KlondikeBoard());
        }
    }
    private static class FreecellListener implements ActionListener{
        public void actionPerformed(ActionEvent a){
            frame.setLocation(0,0);
            frame.setSize(800,800);
            frame.remove(panel);
            frame.add(new FreecellBoard());
        }
    }
}
