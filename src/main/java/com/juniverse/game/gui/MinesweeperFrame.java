package com.juniverse.game.gui;

import com.juniverse.game.util.MessageUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * The Class MinesweeperFrame.
 *
 * @author ajuar
 */
public class MinesweeperFrame extends JFrame {

    /**
     * The Constant serialVersionUID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The Constant BEGINNER_ROWS.
     */
    private static final int BEGINNER_ROWS = 8;

    /**
     * The Constant BEGINNER_COLS.
     */
    private static final int BEGINNER_COLS = 8;

    /**
     * The Constant BEGINNER_MINES.
     */
    private static final int BEGINNER_MINES = 10;

    /**
     * The Constant INTERMEDIATE_ROWS.
     */
    private static final int INTERMEDIATE_ROWS = 16;

    /**
     * The Constant INTERMEDIATE_COLS.
     */
    private static final int INTERMEDIATE_COLS = 16;

    /**
     * The Constant INTERMEDIATE_MINES.
     */
    private static final int INTERMEDIATE_MINES = 40;

    /**
     * The Constant EXPERT_ROWS.
     */
    private static final int EXPERT_ROWS = 16;

    /**
     * The Constant EXPERT_COLS.
     */
    private static final int EXPERT_COLS = 30;

    /**
     * The Constant EXPERT_MINES.
     */
    private static final int EXPERT_MINES = 99;

    /**
     * The rows.
     */
    private int rows = BEGINNER_ROWS;

    /**
     * The cols.
     */
    private int cols = BEGINNER_COLS;

    /**
     * The total mines.
     */
    private int totalMines = BEGINNER_MINES;

    /**
     * The flags placed.
     */
    private int flagsPlaced = 0;

    /**
     * The cells revealed.
     */
    private int cellsRevealed = 0;

    /**
     * The game over.
     */
    private boolean gameOver = false;

    /**
     * The first click.
     */
    private boolean firstClick = true;

    /**
     * The board panel.
     */
    private final JPanel boardPanel = new JPanel();

    /**
     * The status label.
     */
    private final JLabel statusLabel = new JLabel("Flags: 0/" + totalMines);

    /**
     * The reset button.
     */
    private final JButton resetButton = new JButton();

    /**
     * The timer.
     */
    private final Timer timer = new Timer(1000, null);

    /**
     * The seconds elapsed.
     */
    private int secondsElapsed = 0;

    /**
     * The cells.
     */
    private CellPanel[][] cells;

    /**
     * The random.
     */
    private final Random random = new Random();

    /**
     * Instantiates a new minesweeper frame.
     */
    public MinesweeperFrame() {
        setTitle("Minesweeper");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initMenu();
        initGame();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Inits the menu.
     */
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem newGameItem = new JMenuItem("New game");
        JMenuItem beginnerItem = new JMenuItem("Beginner");
        JMenuItem intermediateItem = new JMenuItem("Intermediate");
        JMenuItem expertItem = new JMenuItem("Expert");
        JMenuItem exitItem = new JMenuItem("Exit");

        JMenuItem aboutItem = new JMenuItem("About");

        newGameItem.addActionListener(e -> resetGame());
        beginnerItem.addActionListener(e -> setDifficulty(BEGINNER_ROWS, BEGINNER_COLS, BEGINNER_MINES));
        intermediateItem.addActionListener(e -> setDifficulty(INTERMEDIATE_ROWS, INTERMEDIATE_COLS, INTERMEDIATE_MINES));
        expertItem.addActionListener(e -> setDifficulty(EXPERT_ROWS, EXPERT_COLS, EXPERT_MINES));
        exitItem.addActionListener(e -> System.exit(0));
        aboutItem.addActionListener(e -> about());

        gameMenu.add(newGameItem);
        gameMenu.addSeparator();
        gameMenu.add(beginnerItem);
        gameMenu.add(intermediateItem);
        gameMenu.add(expertItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        helpMenu.add(aboutItem);

        menuBar.add(gameMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Inits the game.
     */
    private void initGame() {
        JPanel controlPanel = new JPanel(new BorderLayout());

        resetButton.setPreferredSize(new Dimension(40, 40));
        resetButton.setFocusable(false);
        resetButton.setContentAreaFilled(false);
        resetButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        resetButton.addActionListener(e -> resetGame());
        resetButton.setIcon(new SmileyIcon(SmileyIcon.NORMAL));

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        timer.addActionListener(e -> {
            secondsElapsed++;
            updateStatus();
        });

        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalGlue());
        statusPanel.add(resetButton);

        controlPanel.add(statusPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.NORTH);

        setupBoard();
        add(boardPanel, BorderLayout.CENTER);
    }

    /**
     * Setup board.
     */
    private void setupBoard() {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(rows, cols));

        cells = new CellPanel[rows][cols];
        flagsPlaced = 0;
        cellsRevealed = 0;
        gameOver = false;
        firstClick = true;
        secondsElapsed = 0;
        timer.stop();

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameOver) {
                    return;
                }

                CellPanel cell = (CellPanel) e.getSource();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (!cell.isFlagged() && !cell.isRevealed()) {
                        if (firstClick) {
                            placeMines(cell.getRow(), cell.getCol());
                            firstClick = false;
                            timer.start();
                        }
                        revealCell(cell);
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    toggleFlag(cell);
                }
            }
        };

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                CellPanel cell = new CellPanel(r, c);
                cell.addMouseListener(mouseHandler);
                cells[r][c] = cell;
                boardPanel.add(cell);
            }
        }

        updateStatus();
        boardPanel.revalidate();
        pack();
    }

    /**
     * Place mines.
     *
     * @param firstRow the first row
     * @param firstCol the first col
     */
    private void placeMines(int firstRow, int firstCol) {
        int minesPlaced = 0;
        while (minesPlaced < totalMines) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);

            if ((r == firstRow && c == firstCol) || cells[r][c].isMine()) {
                continue;
            }

            cells[r][c].setMine(true);
            minesPlaced++;

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = r + dr;
                    int nc = c + dc;
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                        cells[nr][nc].incrementAdjacentMines();
                    }
                }
            }
        }
    }

    /**
     * Reveal cell.
     *
     * @param cell the cell
     */
    private void revealCell(CellPanel cell) {
        if (cell.isRevealed() || cell.isFlagged()) {
            return;
        }

        cell.reveal();
        cellsRevealed++;

        if (cell.isMine()) {
            gameOver();
            return;
        }

        if (cellsRevealed == rows * cols - totalMines) {
            gameWon();
            return;
        }

        if (cell.getAdjacentMines() == 0) {
            revealAdjacentCells(cell.getRow(), cell.getCol());
        }

        updateStatus();
    }

    /**
     * Reveal adjacent cells.
     *
     * @param row the row
     * @param col the col
     */
    private void revealAdjacentCells(int row, int col) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = row + dr;
                int nc = col + dc;
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                    CellPanel adjCell = cells[nr][nc];
                    if (!adjCell.isRevealed() && !adjCell.isFlagged()) {
                        revealCell(adjCell);
                    }
                }
            }
        }
    }

    /**
     * Toggle flag.
     *
     * @param cell the cell
     */
    private void toggleFlag(CellPanel cell) {
        if (cell.isRevealed() || gameOver) {
            return;
        }

        if (cell.isFlagged()) {
            cell.removeFlag();
            flagsPlaced--;
        } else if (flagsPlaced < totalMines) {
            cell.setFlag();
            flagsPlaced++;
        }

        updateStatus();
    }

    /**
     * Game over.
     */
    private void gameOver() {
        gameOver = true;
        timer.stop();
        resetButton.setIcon(new SmileyIcon(SmileyIcon.DEAD));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                CellPanel cell = cells[r][c];
                if (cell.isMine() && !cell.isFlagged()) {
                    cell.reveal();
                }
            }
        }

        MessageUtil.showInfo("You've lost!", "Game Over");
    }

    /**
     * Game won.
     */
    private void gameWon() {
        gameOver = true;
        timer.stop();
        resetButton.setIcon(new SmileyIcon(SmileyIcon.WIN));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                CellPanel cell = cells[r][c];
                if (cell.isMine() && !cell.isFlagged()) {
                    cell.setFlag();
                    flagsPlaced++;
                }
            }
        }

        updateStatus();
        MessageUtil.showInfo("You've won!", "Congratulations");
    }

    /**
     * Update status.
     */
    private void updateStatus() {
        statusLabel.setText("Flags: " + flagsPlaced + "/" + totalMines + " | Time: " + secondsElapsed + "s");
    }

    /**
     * Reset game.
     */
    private void resetGame() {
        setupBoard();
        resetButton.setIcon(new SmileyIcon(SmileyIcon.NORMAL));
    }

    /**
     * Sets the difficulty.
     *
     * @param rows the rows
     * @param cols the cols
     * @param mines the mines
     */
    private void setDifficulty(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.totalMines = mines;
        resetGame();
    }

    /**
     * About.
     */
    private void about() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.setVisible(true);
    }

}
