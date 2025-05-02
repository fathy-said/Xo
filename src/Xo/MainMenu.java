package XOXO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Main Menu");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // زر Single Player
        JButton singlePlayerButton = new JButton("Single Player");
        singlePlayerButton.addActionListener(e -> {
            new SinglePlayer().setVisible(true);  // عرض واجهة SinglePlayer
            dispose();  // إغلاق MainMenu
        });

        // زر Multiplayer
        JButton multiplayerButton = new JButton("Multiplayer");
        multiplayerButton.addActionListener(e -> {
            new LaunchTwoClients().start();  // تشغيل Multiplayer
            dispose();  // إغلاق MainMenu
        });

        add(singlePlayerButton);
        add(multiplayerButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        new MainMenu();
    }
}
