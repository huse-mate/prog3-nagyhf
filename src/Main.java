import render.GameRender;
import render.MainFrame;
import render.MenuPanel;
import game.*;

public class Main {
    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame();

            // javax.swing.JFrame frame = new javax.swing.JFrame("Prog3 - Game");
            // frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            // frame.setResizable(true);

            // java.awt.CardLayout layout = new java.awt.CardLayout();
            // javax.swing.JPanel root = new javax.swing.JPanel(layout);

            // MenuPanel menu = new MenuPanel();
            // TileMap map = new TileMap();
            // Game gamePlay = new Game(map);
            // GameRender gameRender = new GameRender(gamePlay, layout, root);
            

            // root.add(menu, "menu");
            // root.add(gameRender, "game");

            // menu.setupButtonActions(frame, layout, root, "game", gameRender);

            // frame.setContentPane(root);
            // frame.pack();
            // frame.setLocationRelativeTo(null);
            // frame.setVisible(true);
            // layout.show(root, "menu"); 
        });
    }
}
