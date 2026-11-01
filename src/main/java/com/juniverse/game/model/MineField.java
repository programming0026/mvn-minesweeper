package com.juniverse.game.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * Estado y reglas del buscaminas, sin ninguna dependencia de la interfaz gráfica: tablero de minas, números de minas
 * vecinas, celdas descubiertas, banderas y estado de la partida. Las minas se colocan en el primer clic, de modo que
 * la primera celda descubierta nunca es una mina.
 */
public class MineField {

    /** Estado de la partida. */
    public enum State {
        /** Aún no se descubrió ninguna celda. */
        READY,
        /** Partida en curso. */
        PLAYING,
        /** Se descubrieron todas las celdas sin mina. */
        WON,
        /** Se descubrió una mina. */
        LOST
    }

    private final int rows;
    private final int cols;
    private final int totalMines;
    private final Random random;
    private final boolean[][] mines;
    private final int[][] adjacent;
    private final boolean[][] revealed;
    private final boolean[][] flagged;
    private int revealedCount;
    private int flagCount;
    private State state = State.READY;

    /**
     * Crea un tablero vacío.
     *
     * @param rows filas
     * @param cols columnas
     * @param totalMines cantidad de minas (debe caber en el tablero dejando libre la primera celda)
     * @param random generador de números aleatorios (se inyecta para poder probar con una semilla fija)
     */
    public MineField(int rows, int cols, int totalMines, Random random) {
        if (rows < 1 || cols < 1 || totalMines < 0 || totalMines > rows * cols - 1) {
            throw new IllegalArgumentException("Tablero inválido: " + rows + "x" + cols + " con " + totalMines + " minas");
        }
        this.rows = rows;
        this.cols = cols;
        this.totalMines = totalMines;
        this.random = random;
        this.mines = new boolean[rows][cols];
        this.adjacent = new int[rows][cols];
        this.revealed = new boolean[rows][cols];
        this.flagged = new boolean[rows][cols];
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public int totalMines() {
        return totalMines;
    }

    public State state() {
        return state;
    }

    /** Indica si la partida terminó (ganada o perdida). */
    public boolean isFinished() {
        return state == State.WON || state == State.LOST;
    }

    public boolean isMine(int row, int col) {
        return mines[row][col];
    }

    public boolean isRevealed(int row, int col) {
        return revealed[row][col];
    }

    public boolean isFlagged(int row, int col) {
        return flagged[row][col];
    }

    /** Minas en las ocho celdas vecinas. */
    public int adjacentMines(int row, int col) {
        return adjacent[row][col];
    }

    public int flagCount() {
        return flagCount;
    }

    public int revealedCount() {
        return revealedCount;
    }

    /**
     * Descubre una celda. En el primer clic coloca las minas; si la celda no tiene minas vecinas, descubre en cadena
     * las vecinas. No hace nada si la partida terminó o la celda ya está descubierta o tiene bandera.
     *
     * @param row fila
     * @param col columna
     */
    public void reveal(int row, int col) {
        if (isFinished() || revealed[row][col] || flagged[row][col]) {
            return;
        }
        if (state == State.READY) {
            placeMines(row, col);
            state = State.PLAYING;
        }
        Deque<int[]> pending = new ArrayDeque<>();
        pending.push(new int[] {row, col});
        while (!pending.isEmpty()) {
            int[] cell = pending.pop();
            int r = cell[0];
            int c = cell[1];
            if (revealed[r][c] || flagged[r][c]) {
                continue;
            }
            revealed[r][c] = true;
            revealedCount++;
            if (mines[r][c]) {
                lose();
                return;
            }
            if (revealedCount == rows * cols - totalMines) {
                win();
                return;
            }
            if (adjacent[r][c] == 0) {
                forEachNeighbor(r, c, (nr, nc) -> {
                    if (!revealed[nr][nc] && !flagged[nr][nc]) {
                        pending.push(new int[] {nr, nc});
                    }
                });
            }
        }
    }

    /**
     * Alterna la bandera de una celda. No hace nada si la celda está descubierta, la partida terminó, o ya se usaron
     * todas las banderas.
     *
     * @param row fila
     * @param col columna
     */
    public void toggleFlag(int row, int col) {
        if (revealed[row][col] || isFinished()) {
            return;
        }
        if (flagged[row][col]) {
            flagged[row][col] = false;
            flagCount--;
        } else if (flagCount < totalMines) {
            flagged[row][col] = true;
            flagCount++;
        }
    }

    private void placeMines(int firstRow, int firstCol) {
        int placed = 0;
        while (placed < totalMines) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);
            if ((r == firstRow && c == firstCol) || mines[r][c]) {
                continue;
            }
            mines[r][c] = true;
            placed++;
            forEachNeighbor(r, c, (nr, nc) -> adjacent[nr][nc]++);
        }
    }

    /** Pierde la partida: se muestran las minas que no tenían bandera. */
    private void lose() {
        state = State.LOST;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (mines[r][c] && !flagged[r][c]) {
                    revealed[r][c] = true;
                }
            }
        }
    }

    /** Gana la partida: se ponen banderas en todas las minas. */
    private void win() {
        state = State.WON;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (mines[r][c] && !flagged[r][c]) {
                    flagged[r][c] = true;
                    flagCount++;
                }
            }
        }
    }

    private void forEachNeighbor(int row, int col, CellAction action) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                int nr = row + dr;
                int nc = col + dc;
                if ((dr != 0 || dc != 0) && nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                    action.apply(nr, nc);
                }
            }
        }
    }

    @FunctionalInterface
    private interface CellAction {
        void apply(int row, int col);
    }
}
