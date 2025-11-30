package render;
import javax.swing.*;

import render.MainFrame.Scene;

import java.awt.*;

public class MenuPanel extends JPanel {
    private static final String FONT_NAME = "NerdFont";
    private static final int FONT_STYLE = Font.BOLD;

    private final JButton newGameButton = new JButton("Start New Game");
    private final JButton loadGameButton = new JButton("Load Previous Game");
    private final JButton exitButton = new JButton("Exit");
    private final Dimension btnSize = new Dimension(410, 60);

    private final transient Image bgImage = new ImageIcon(new java.io.File("assets/bg.png").getAbsolutePath()).getImage();
    private static final int BG_WIDTH = 3840;
    private static final int BG_HEIGHT = 2160;

    public MenuPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(1920, 1080));

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        JLabel title = new JLabel("GADÁNY");
        setupText(title, 72, Component.CENTER_ALIGNMENT, Colors.TITLE_COLOR.val);
        box.add(title);

        JLabel subtitle = new JLabel("telepesei");
        setupText(subtitle, 48, Component.CENTER_ALIGNMENT, Colors.SUBTITLE_COLOR.val);
        box.add(subtitle);

        box.add(Box.createVerticalStrut(400));

        for (JButton b : new JButton[]{newGameButton, loadGameButton, exitButton}) {
            setupButton(box, b);
        }
        setupButtonActions(frame);
        

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(box, gbc);
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            int panelW = getWidth();
            int panelH = getHeight();
            int drawW = Math.min(BG_WIDTH, panelW);
            int drawH = Math.min(BG_HEIGHT, panelH);
            int x = (panelW - drawW) / 2;
            int y = (panelH - drawH) / 2;
            g.drawImage(bgImage, x, y, drawW, drawH, this);
        }
    }

    private void setupText(JLabel text, int fontSize, float alignment, Color color){
        text.setAlignmentX(alignment);
        text.setFont(new Font(FONT_NAME, FONT_STYLE, fontSize));
        text.setForeground(color);
    }

    private void setupButton(JPanel box, JButton b){
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setBackground(Colors.BUTTON_COLOR.val);
        b.setMaximumSize(btnSize);
        b.setPreferredSize(btnSize);
        b.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        b.setFocusPainted(false);
        box.add(b);
        box.add(Box.createVerticalStrut(20));
}

    private void setupButtonActions(MainFrame frame) {
        newGameButton.addActionListener(e -> 
            frame.newGameScene()
        );
        loadGameButton.addActionListener(e -> 
            frame.loadGameScene()
        );
        exitButton.addActionListener(e -> {
            frame.dispose();
            System.exit(0);
        });
    }
}
