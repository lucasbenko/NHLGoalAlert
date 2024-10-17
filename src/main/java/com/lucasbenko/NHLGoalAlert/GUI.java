package com.lucasbenko.NHLGoalAlert;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUI implements ActionListener {
    JLabel label;
    JPanel panel;
    static JFrame frame;
    JButton submitButton;
    JButton testButton;
    JButton resetButton;
    JButton exitButton;
    JComboBox teamDropdown;
    static JTextArea consoleBox;
    JScrollPane scrollPane;
    static TeamNames[] choices;
    static String[] formattedNames;

    public GUI() {
        frame = new JFrame();
        submitButton = new RoundedButton("Start");
        submitButton.addActionListener(this);
        label = new JLabel("Select your team:");

        teamDropdown = new JComboBox<>(formattedNames);

        consoleBox = new RoundedTextArea(10, 30);
        consoleBox.setBackground(Color.LIGHT_GRAY);
        consoleBox.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(consoleBox);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.setIconImage(new ImageIcon("res/icon.png").getImage());

        BufferedImage logo = null;

        try {
            logo = ImageIO.read(new File("res/nhlgoalalert.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int fixedWidth = 150;

        int originalWidth = logo.getWidth();
        int originalHeight = logo.getHeight();
        int scaledHeight = (int) ((fixedWidth / (double) originalWidth) * originalHeight);

        BufferedImage scaledImage = new BufferedImage(fixedWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        g2d.drawImage(logo, 0, 0, fixedWidth, scaledHeight, null);
        g2d.dispose();

        JLabel picLabel = new JLabel(new ImageIcon(scaledImage));

        testButton = new RoundedButton("Send Test Alert");
        testButton.addActionListener(this);

        exitButton = new RoundedButton("Exit");
        exitButton.addActionListener(this);

        resetButton = new RoundedButton("Reset");
        resetButton.addActionListener(this);

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        panel.add(label);
        panel.add(teamDropdown);
        panel.add(submitButton);
        panel.add(scrollPane);
        panel.add(testButton);
        panel.add(resetButton);
        panel.add(exitButton);
        panel.add(picLabel);

        layout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, label, 0, SpringLayout.NORTH, panel);

        layout.putConstraint(SpringLayout.WEST, teamDropdown, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, teamDropdown, 10, SpringLayout.SOUTH, label);

        layout.putConstraint(SpringLayout.WEST, submitButton, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, submitButton, 10, SpringLayout.SOUTH, teamDropdown);

        layout.putConstraint(SpringLayout.WEST, picLabel, 85, SpringLayout.EAST, label);
        layout.putConstraint(SpringLayout.NORTH, picLabel, 0, SpringLayout.NORTH, label);

        layout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.SOUTH, submitButton);

        layout.putConstraint(SpringLayout.WEST, testButton, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, testButton, 10, SpringLayout.SOUTH, scrollPane);

        layout.putConstraint(SpringLayout.WEST, exitButton, 10, SpringLayout.EAST, testButton);
        layout.putConstraint(SpringLayout.NORTH, exitButton, 0, SpringLayout.NORTH, testButton);

        layout.putConstraint(SpringLayout.WEST, resetButton, 10, SpringLayout.EAST, exitButton);
        layout.putConstraint(SpringLayout.NORTH, resetButton, 0, SpringLayout.NORTH, exitButton);

        setStyle();
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("NHL Goal Alert");
        frame.setSize(375,375);
        frame.setVisible(true);
        frame.setResizable(false);
    }


    public static void main(String[] args) {
        getTeamsDropdown();
        new GUI();
        Main.readProps();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            submitButton.setEnabled(false);
            int selectedIndex = teamDropdown.getSelectedIndex();
            new Thread(() -> Main.initialize(choices[selectedIndex])).start();
        }else if(e.getSource() == testButton){
            Team t = new Team(choices[teamDropdown.getSelectedIndex()].getTeamName());
            new Thread(() -> GameMonitor.testAlert(t)).start();
        }else if(e.getSource() == resetButton){
            restartApplication();
        }else if(e.getSource() == exitButton){
            System.exit(0);
        }
    }

    public static void restartApplication() {
        try {
            String javaBin = System.getProperty("java.home") + "/bin/java";
            String classPath = System.getProperty("java.class.path");
            String className = GUI.class.getName();
            ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classPath, className);
            builder.start();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getTeamsDropdown() {
        int i = 0;
        choices = new TeamNames[32];
        formattedNames = new String[32];
        for (TeamNames name : TeamNames.values()) {
            choices[i] = name;
            formattedNames[i] = name.getTeamName();
            i++;
        }
    }

    public static void printToConsole(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (consoleBox != null) {
                    consoleBox.append(message + "\n");
                    consoleBox.setCaretPosition(consoleBox.getDocument().getLength());
                }
            }
        });
    }

    public void setStyle(){
        Color primaryDark = new Color(31, 28, 28, 255);
        Color primaryRed = new Color(255, 42, 42, 255);
        Color primaryLight = new Color(255, 255, 255, 255);
        Color secondaryGrey = new Color(239, 239, 239, 255);
        label.setForeground(primaryLight);
        submitButton.setBackground(primaryRed);
        submitButton.setForeground(primaryLight);
        resetButton.setBackground(primaryRed);
        resetButton.setForeground(primaryLight);
        exitButton.setBackground(primaryRed);
        exitButton.setForeground(primaryLight);

        consoleBox.setBackground(secondaryGrey);
        Font customFont = null;

        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("res\\Franko-E5SA.ttf")).deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        label.setFont(customFont);
        consoleBox.setFont(customFont);
        submitButton.setFont(customFont);
        resetButton.setFont(customFont);
        testButton.setFont(customFont);
        exitButton.setFont(customFont);
        teamDropdown.setFont(customFont);
        teamDropdown.setBackground(primaryLight);

        panel.setBackground(primaryDark);
    }

}

class RoundedButton extends JButton {

    public RoundedButton(String label) {
        super(label);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setBorder(new EmptyBorder(5, 15, 5, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        Shape shape = new Rectangle(0, 0, getWidth(), getHeight());
        return shape.contains(x, y);
    }
}

class RoundedTextArea extends JTextArea {

    public RoundedTextArea(int rows, int cols) {
        super(rows, cols);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        Shape shape = new Rectangle(0, 0, getWidth(), getHeight());
        return shape.contains(x, y);
    }

}



