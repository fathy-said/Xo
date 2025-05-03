package XOXO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class GameGUI extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private String playerSymbol;
    private String opponentSymbol;
    private boolean myTurn;
    private PrintWriter out;
    private BufferedReader in;
    private JLabel turnLabel;

    public GameGUI(String title) {
        super(title);
        setLayout(new BorderLayout());

        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(3, 3));

        try {
            Socket socket = new Socket("localhost", 55555);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            playerSymbol = in.readLine();
            opponentSymbol = playerSymbol.equals("X") ? "O" : "X";
            myTurn = playerSymbol.equals("X");

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    final int row = i;
                    final int col = j;
                    buttons[i][j] = new JButton("");
                    buttons[i][j].setFont(new Font("Arial", Font.BOLD, 40));
                    buttons[i][j].setFocusPainted(false);
                    buttons[i][j].addActionListener(e -> handleMove(row, col));
                    gamePanel.add(buttons[i][j]);
                }
            }

            add(gamePanel, BorderLayout.CENTER);

            JPanel bottomBar = new JPanel(new BorderLayout());

            turnLabel = new JLabel();
            turnLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            turnLabel.setHorizontalAlignment(SwingConstants.LEFT);
            updateTurnLabel();
            bottomBar.add(turnLabel, BorderLayout.WEST);

            JButton resetButton = new JButton("Reset");
            resetButton.addActionListener(e -> sendResetRequest("X"));  // افتراضياً X
            bottomBar.add(resetButton, BorderLayout.EAST);

            add(bottomBar, BorderLayout.SOUTH);

            listenForOpponentMove();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Connection error: " + e.getMessage());
            System.exit(1);
        }
    }

    private void handleMove(int row, int col) {
        if (!myTurn || !buttons[row][col].getText().equals("")) return;

        buttons[row][col].setText(playerSymbol);
        out.println(row + "," + col);
        myTurn = false;

        updateTurnLabel();
        checkWin();
    }

    private void listenForOpponentMove() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("RESET")) {
                        String[] parts = line.split(":");
                        String winnerSymbol = parts.length > 1 ? parts[1] : "X";
                        resetGameLocal(winnerSymbol);
                        continue;
                    }

                    if (line.startsWith("START")) {
                        myTurn = playerSymbol.equals("X");
                        updateTurnLabel();
                        continue;
                    }

                    if (line.startsWith("WAIT")) {
                        myTurn = false;
                        updateTurnLabel();
                        continue;
                    }

                    String[] parts = line.split(",");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    buttons[row][col].setText(opponentSymbol);
                    myTurn = true;
                    updateTurnLabel();
                    checkWin();
                }
            } catch (IOException e) {
                System.out.println("Disconnected");
            }
        }).start();
    }

    private void checkWin() {
        String winner = null;
        for (int i = 0; i < 3; i++) {
            if (same(buttons[i][0], buttons[i][1], buttons[i][2]))
                winner = buttons[i][0].getText();
            if (same(buttons[0][i], buttons[1][i], buttons[2][i]))
                winner = buttons[0][i].getText();
        }
        if (same(buttons[0][0], buttons[1][1], buttons[2][2]))
            winner = buttons[0][0].getText();
        if (same(buttons[0][2], buttons[1][1], buttons[2][0]))
            winner = buttons[0][2].getText();

        if (winner != null && !winner.equals("")) {
            highlightWinner(winner);
            int choice = JOptionPane.showOptionDialog(this, winner + " wins! Would you like to play again?",
                    "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    new Object[] {"OK"}, "OK");

            if (choice == JOptionPane.OK_OPTION) {
                sendResetRequest(winner);  // إرسال طلب إعادة ضبط
                resetGameLocal(winner);    // إعادة ضبط اللعبة
            } else {
                System.exit(0);
            }
        }
    }

    private boolean same(JButton b1, JButton b2, JButton b3) {
        return !b1.getText().equals("") && b1.getText().equals(b2.getText()) && b2.getText().equals(b3.getText());
    }

    private void highlightWinner(String symbol) {
        for (int i = 0; i < 3; i++) {
            if (same(buttons[i][0], buttons[i][1], buttons[i][2]) && buttons[i][0].getText().equals(symbol)) {
                buttons[i][0].setBackground(Color.GREEN);
                buttons[i][1].setBackground(Color.GREEN);
                buttons[i][2].setBackground(Color.GREEN);
            }
            if (same(buttons[0][i], buttons[1][i], buttons[2][i]) && buttons[0][i].getText().equals(symbol)) {
                buttons[0][i].setBackground(Color.GREEN);
                buttons[1][i].setBackground(Color.GREEN);
                buttons[2][i].setBackground(Color.GREEN);
            }
        }
        if (same(buttons[0][0], buttons[1][1], buttons[2][2]) && buttons[0][0].getText().equals(symbol)) {
            buttons[0][0].setBackground(Color.GREEN);
            buttons[1][1].setBackground(Color.GREEN);
            buttons[2][2].setBackground(Color.GREEN);
        }
        if (same(buttons[0][2], buttons[1][1], buttons[2][0]) && buttons[0][2].getText().equals(symbol)) {
            buttons[0][2].setBackground(Color.GREEN);
            buttons[1][1].setBackground(Color.GREEN);
            buttons[2][0].setBackground(Color.GREEN);
        }
    }

    private void sendResetRequest(String winner) {
        out.println("RESET:" + winner);
    }

    private void resetGameLocal(String winner) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setBackground(null);
            }
        }
        myTurn = playerSymbol.equals("X");
        updateTurnLabel();
    }

    private void updateTurnLabel() {
        if (myTurn) {
            turnLabel.setText("Your Turn (" + playerSymbol + ")");
        } else {
            turnLabel.setText("Opponent's Turn");
        }
    }
}
