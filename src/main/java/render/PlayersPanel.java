package render;

import javax.swing.*;

import game.*;

import java.awt.*;
import java.util.HashMap;


public class PlayersPanel extends JPanel {

    private final MainFrame frame;
    private final Game game; 

    private HashMap<Player, PlayerStatusPanel> playerStatus;

    public PlayersPanel(MainFrame frame, Game game) {
		setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 100, 0));

        this.frame = frame;
		this.game = game;
        playerStatus = new HashMap<>();
        game.getPlayers().forEach( player -> {
            PlayerStatusPanel playerBox = new PlayerStatusPanel(player);
            add(playerBox);
            playerStatus.put(player, playerBox);
        });
    }


    public void updateStatus(Player p){
        playerStatus.get(p).updateStatus(p.getScore(), p.getCardCount(), p.getDevCardCount(), p.getMaxRoadLength(), p.getKnightsPlayed());
    }

    public void highlightCurrentPlayer(Player p){
        for (PlayerStatusPanel panel  : playerStatus.values()){
            panel.unhighlightPlayer();
        }
        playerStatus.get(p).highlightPlayer();
    }
}
