import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;


public class AutoSelfApp1 extends JFrame {


    private static final String APP_TITLE = "AutoSelf";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final String FONT_NAME = "Arial";
    private static final int FONT_SIZE = 30;
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final String BACKGROUND_IMAGE_URL = "https://media.istockphoto.com/id/1580668999/es/foto/hombre-que-agarra-llave-de-coche.jpg?s=612x612&w=0&k=20&c=gOpK-6L1vABdg8yRW3ifdGRrPkJU_Uku2TslKUKyJw4=";


    public AutoSelfApp1() {
        initializeUI();
    }


    private void initializeUI() {
        setTitle(APP_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel backgroundPanel = createBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());


        JButton adminButton = createButton("Administradores", e -> new AdminLogin());
        JButton userButton = createButton("Usuarios", e -> new AlquilerCoches());


        JPanel buttonPanel = createButtonPanel(adminButton, userButton);
        JLabel companyLabel = createCompanyLabel();


        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);
        backgroundPanel.add(companyLabel, BorderLayout.SOUTH);


        add(backgroundPanel);
        setVisible(true);
    }


    private JPanel createBackgroundPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    URL url = new URL(BACKGROUND_IMAGE_URL);
                    Image image = ImageIO.read(url);
                    if (image == null) {
                        JOptionPane.showMessageDialog(this, "Error al cargar la imagen de fondo.");
                        return;
                    }
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error al cargar la imagen desde la URL.");
                }
            }
        };
    }


    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_NAME, Font.BOLD, FONT_SIZE));
        button.addActionListener(action);
        return button;
    }


    private JPanel createButtonPanel(JButton... buttons) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 400));
        for (JButton button : buttons) {
            buttonPanel.add(button);
        }
        return buttonPanel;
    }


    private JLabel createCompanyLabel() {
        JLabel label = new JLabel("AUTOSELF -", SwingConstants.CENTER);
        label.setFont(new Font(FONT_NAME, Font.BOLD, FONT_SIZE));
        label.setForeground(TEXT_COLOR);
        return label;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AutoSelfApp1());
    }
}


