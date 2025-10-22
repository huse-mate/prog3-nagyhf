package render;
import javax.swing.*;
import java.awt.*;

/**
 * Simple main menu panel with Play, and Exit buttons.
 */

public class MainMenu extends JPanel {
    private static final String FONT_NAME = "NerdFont";
    private static final int FONT_STYLE = Font.BOLD;

    private final JButton playButton = new JButton("Play");
    private final JButton exitButton = new JButton("Exit");
    private final Dimension btnSize = new Dimension(410, 60);

    private final transient Image BG_IMAGE = new ImageIcon(new java.io.File("assets/bg.png").getAbsolutePath()).getImage();
    private static final int BG_WIDTH = 3840;
    private static final int BG_HEIGHT = 2160;

    public MainMenu() {
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

        for (JButton b : new JButton[]{playButton, exitButton}) {
            setupButton(b);
            box.add(b);
            box.add(Box.createVerticalStrut(20));
        }
        

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(box, gbc);
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (BG_IMAGE != null) {
            int panelW = getWidth();
            int panelH = getHeight();
            int drawW = Math.min(BG_WIDTH, panelW);
            int drawH = Math.min(BG_HEIGHT, panelH);
            int x = (panelW - drawW) / 2;
            int y = (panelH - drawH) / 2;
            g.drawImage(BG_IMAGE, x, y, drawW, drawH, this);
        }
    }

    private void setupText(JLabel text, int fontSize, float alignment, Color color){
        text.setAlignmentX(alignment);
        text.setFont(new Font(FONT_NAME, FONT_STYLE, fontSize));
        text.setForeground(color);
    }

    private void setupButton(JButton b){
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setBackground(Colors.BUTTON_COLOR.val);
        b.setMaximumSize(btnSize);
        b.setPreferredSize(btnSize);
        b.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        b.setFocusPainted(false);
}

    public void setupButtonActions(javax.swing.JFrame frame,java.awt.CardLayout layout, javax.swing.JPanel root, String gameScreenName, java.awt.Component gameComponent) {
        playButton.addActionListener(e -> {
            layout.show(root, gameScreenName);
            gameComponent.requestFocusInWindow();
        });
        exitButton.addActionListener(e -> {
            frame.dispose();
            System.exit(0);
        });
    }
}
