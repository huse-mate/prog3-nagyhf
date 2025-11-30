package render;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

import game.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private HashMap<Scene, JPanel> scenes;


    public enum Scene{
        MAIN_MENU("MainMenu"),
        GAME("Game"),
        WIN("Win");

        private String name;
        Scene(String n){
            name = n;
        }
    }

    public MainFrame(Game game) {
        super("Prog3 - NHF");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);

        // Set up CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create panels
        MenuPanel menuPanel = new MenuPanel(this);
        GameScene gamePanel = new GameScene(this, game);
        WinPanel winPanel = new WinPanel();
        scenes = new HashMap<>();
        scenes.put(Scene.GAME, gamePanel);
        scenes.put(Scene.MAIN_MENU, menuPanel);
        scenes.put(Scene.WIN, winPanel);

        // Add them to the mainPanel
        addScene(menuPanel, Scene.MAIN_MENU);
        addScene(gamePanel, Scene.GAME);
        addScene(winPanel, Scene.WIN);

        // Add mainPanel to frame
        add(mainPanel);

        setVisible(true);

        // Show main menu first
        showScene(Scene.MAIN_MENU);
    }

    public void refresh() {
        GameScene gameScene = (GameScene) scenes.get(Scene.GAME);
        gameScene.updateStatus();
    }

    public GameScene getGameScene(){
        return (GameScene) scenes.get(Scene.GAME);
    }

    public void loadGameScene(){
        getGameScene().loadPreviousGame();
        showScene(Scene.GAME);
    }

    public void newGameScene(){
        getGameScene().startNewGame();
        showScene(Scene.GAME);
    }

    public void showScene(Scene scene) {
        cardLayout.show(mainPanel, scene.name);
    }

    public void setWinner(Player winner){
        WinPanel winPanel = (WinPanel) scenes.get(Scene.WIN);
        winPanel.setWinner(winner);
    }

    public void addScene(JPanel panel, Scene scene){
        mainPanel.add(panel, scene.name);
    }
}
