package com.juniverse.game.gui;

import com.juniverse.game.model.MineField;
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
 * @author Java Universe
 */
public class MinesweeperFrame extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int BEGINNER_ROWS = 8;

    private static final int BEGINNER_COLS = 8;

    private static final int BEGINNER_MINES = 10;

    private static final int INTERMEDIATE_ROWS = 16;

    private static final int INTERMEDIATE_COLS = 16;

    private static final int INTERMEDIATE_MINES = 40;

    private static final int EXPERT_ROWS = 16;

    private static final int EXPERT_COLS = 30;

    private static final int EXPERT_MINES = 99;

    /**
     * Cantidad de filas.
     */
    private int rows = BEGINNER_ROWS;

    /**
     * Cantidad de columnas.
     */
    private int cols = BEGINNER_COLS;

    /**
     * Total de minas del tablero.
     */
    private int totalMines = BEGINNER_MINES;

    /**
     * Reglas y estado de la partida (sin dependencias de la interfaz).
     */
    private MineField field;

    /**
     * Indica si ya se mostró el mensaje de fin de partida.
     */
    private boolean endShown = false;

    /**
     * Panel del tablero.
     */
    private final JPanel boardPanel = new JPanel();

    /**
     * Etiqueta de estado.
     */
    private final JLabel statusLabel = new JLabel("Flags: 0/" + totalMines);

    /**
     * Botón para reiniciar la partida.
     */
    private final JButton resetButton = new JButton();

    /**
     * Temporizador.
     */
    private final Timer timer = new Timer(1000, null);

    /**
     * Segundos transcurridos.
     */
    private int secondsElapsed = 0;

    /**
     * Celdas del tablero.
     */
    private CellPanel[][] cells;

    /**
     * Generador de números aleatorios.
     */
    private final Random random = new Random();

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
     * Prepara el tablero.
     */
    private void setupBoard() {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(rows, cols));

        cells = new CellPanel[rows][cols];
        field = new MineField(rows, cols, totalMines, random);
        endShown = false;
        secondsElapsed = 0;
        timer.stop();

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (field.isFinished()) {
                    return;
                }

                CellPanel cell = (CellPanel) e.getSource();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (!cell.isFlagged() && !cell.isRevealed()) {
                        boolean firstClick = field.state() == MineField.State.READY;
                        field.reveal(cell.getRow(), cell.getCol());
                        if (firstClick) {
                            timer.start();
                        }
                        refresh();
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    field.toggleFlag(cell.getRow(), cell.getCol());
                    refresh();
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
     * Sincroniza las celdas con el modelo y atiende el fin de la partida.
     */
    private void refresh() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                CellPanel cell = cells[r][c];
                cell.setMine(field.isMine(r, c));
                cell.setAdjacentMines(field.adjacentMines(r, c));
                if (field.isFlagged(r, c) && !cell.isFlagged()) {
                    cell.setFlag();
                } else if (!field.isFlagged(r, c) && cell.isFlagged()) {
                    cell.removeFlag();
                }
                if (field.isRevealed(r, c) && !cell.isRevealed()) {
                    cell.reveal();
                }
            }
        }
        updateStatus();
        if (field.isFinished() && !endShown) {
            endShown = true;
            timer.stop();
            if (field.state() == MineField.State.LOST) {
                resetButton.setIcon(new SmileyIcon(SmileyIcon.DEAD));
                MessageUtil.showInfo("You've lost!", "Game Over");
            } else {
                resetButton.setIcon(new SmileyIcon(SmileyIcon.WIN));
                MessageUtil.showInfo("You've won!", "Congratulations");
            }
        }
    }

    /**
     * Actualiza el estado.
     */
    private void updateStatus() {
        statusLabel.setText("Flags: " + field.flagCount() + "/" + totalMines + " | Time: " + secondsElapsed + "s");
    }

    /**
     * Reinicia la partida.
     */
    private void resetGame() {
        setupBoard();
        resetButton.setIcon(new SmileyIcon(SmileyIcon.NORMAL));
    }

    /**
     * Establece la dificultad.
     *
     * @param rows filas
     * @param cols columnas
     * @param mines cantidad de minas
     */
    private void setDifficulty(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.totalMines = mines;
        resetGame();
    }

    /**
     * Muestra el cuadro «Acerca de».
     */
    private void about() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.setVisible(true);
    }

}
