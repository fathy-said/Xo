package XOXO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SinglePlayer extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private JLabel statusLabel = new JLabel("Player X's turn");
    private char[][] board = new char[3][3];
    private char currentPlayer = 'X';
    private Character lastWinner = null; // null في أول مرة

    public SinglePlayer() {
        setTitle("Tic Tac Toe - Single Player");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel(new GridLayout(3, 3));
        Font font = new Font("Arial", Font.BOLD, 50);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton btn = new JButton("");
                btn.setFont(font);
                buttons[i][j] = btn;
                final int row = i;
                final int col = j;

                btn.addActionListener(e -> handleMove(row, col));
                gamePanel.add(btn);
            }
        }

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGame());

        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        bottomPanel.add(resetButton, BorderLayout.EAST);

        add(gamePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        resetBoard(); // أول تشغيل
        setVisible(true);
    }

    private void handleMove(int row, int col) {
        if (makeMove(row, col)) {
            buttons[row][col].setText(String.valueOf(currentPlayer));
            if (checkWin()) {
                highlightWinningCells(getWinningCells());
                statusLabel.setText("Player " + currentPlayer + " wins!");
                lastWinner = currentPlayer; // نحفظ من فاز
                disableButtons();
            } else if (isFull()) {
                statusLabel.setText("It's a draw!");
                lastWinner = null; // تعادل
                disableButtons();
            } else {
                switchPlayer();
                statusLabel.setText("Player " + currentPlayer + "'s turn");
            }
        }
    }

    private boolean makeMove(int row, int col) {
        if (board[row][col] == ' ') {
            board[row][col] = currentPlayer;
            return true;
        }
        return false;
    }

    private boolean checkWin() {
        char p = currentPlayer;
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == p && board[i][1] == p && board[i][2] == p) ||
                (board[0][i] == p && board[1][i] == p && board[2][i] == p)) {
                return true;
            }
        }
        if ((board[0][0] == p && board[1][1] == p && board[2][2] == p) ||
            (board[0][2] == p && board[1][1] == p && board[2][0] == p)) {
            return true;
        }
        return false;
    }

    private boolean isFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == ' ') return false;
            }
        }
        return true;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private void resetBoard() {
        // نحدد من يبدأ الجيم الجديد
        currentPlayer = (lastWinner != null) ? lastWinner : 'X';
        statusLabel.setText("Player " + currentPlayer + "'s turn");

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = ' ';
    }

    private int[][] getWinningCells() {
        char p = currentPlayer;
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == p && board[i][1] == p && board[i][2] == p) {
                return new int[][]{{i, 0}, {i, 1}, {i, 2}};
            }
            if (board[0][i] == p && board[1][i] == p && board[2][i] == p) {
                return new int[][]{{0, i}, {1, i}, {2, i}};
            }
        }
        if (board[0][0] == p && board[1][1] == p && board[2][2] == p) {
            return new int[][]{{0, 0}, {1, 1}, {2, 2}};
        }
        if (board[0][2] == p && board[1][1] == p && board[2][0] == p) {
            return new int[][]{{0, 2}, {1, 1}, {2, 0}};
        }
        return null;
    }

    private void highlightWinningCells(int[][] winningCells) {
        if (winningCells != null) {
            for (int[] cell : winningCells) {
                buttons[cell[0]][cell[1]].setBackground(Color.GREEN);
            }
        }
    }

    private void disableButtons() {
        for (JButton[] row : buttons)
            for (JButton btn : row)
                btn.setEnabled(false);
    }

    private void resetGame() {
        resetBoard();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                buttons[i][j].setBackground(null);
            }
    }
}
