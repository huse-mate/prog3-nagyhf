import game.TileMap;
import render.GameRender;
import render.MainMenu;

public class Main {
    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("Prog3 - Game");
            frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            frame.setResizable(true);

            java.awt.CardLayout layout = new java.awt.CardLayout();
            javax.swing.JPanel root = new javax.swing.JPanel(layout);

            MainMenu menu = new MainMenu();
            TileMap map = new TileMap();
            GameRender game = new GameRender(map, layout, root);

            root.add(menu, "menu");
            root.add(game, "game");

            menu.setupButtonActions(frame, layout, root, "game", game);

            frame.setContentPane(root);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            layout.show(root, "menu"); 
        });
    }
}
