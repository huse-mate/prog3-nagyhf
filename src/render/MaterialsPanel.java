package render;
import javax.swing.*;

import game.Player;
import game.Resource;

import java.awt.*;
import java.util.EnumMap;



public class MaterialsPanel extends JPanel {

    private EnumMap<Resource, JLabel> resourceLabels = new EnumMap<>(Resource.class);

    
    public MaterialsPanel(){
        setPreferredSize(new Dimension(300, 60));
        setLayout(new GridLayout(1, 10));
        setBackground(Colors.MATERIALS_PANEL_BACKGROUND_COLOR.val);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, false));


        for (Resource res : Resource.values()) {
            if (res == Resource.DESERT) continue;
            JLabel label = new JLabel("0");
            resourceLabels.put(res, label);
            add(getMaterialImage(res));
            add(label);
        }
    }

    public void updateStatus(Player currentPlayer){
        resourceLabels.forEach((res, label) -> {
            if (res != Resource.DESERT) 
                label.setText(Integer.toString(currentPlayer.getResourceCount(res)));
        });
    }

    public JLabel getMaterialImage(Resource res){
        Image img = new ImageIcon(new java.io.File("assets/icon" + res.name().substring(0, 1) + res.name().substring(1).toLowerCase() +".png").getAbsolutePath()).getImage();
        return new JLabel(new ImageIcon(img.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
    }
}
