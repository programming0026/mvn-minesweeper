package com.juniverse.game.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.Serial;

import javax.swing.JPanel;

/**
 * @author ajuar
 */
public class CellPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Fila.
     */
    private final int row;

    /**
     * Columna.
     */
    private final int col;

    private boolean isMine = false;

    private boolean isRevealed = false;

    private boolean isFlagged = false;

    /**
     * Cantidad de minas en las celdas vecinas.
     */
    private int adjacentMines = 0;

    private boolean exploded = false;

    public CellPanel(int row, int col) {
        this.row = row;
        this.col = col;
        setPreferredSize(new Dimension(30, 30));
        setFocusable(false);
    }

    /**
     * Marca o desmarca la celda como mina.
     *
     * @param isMine {@code true} si la celda tiene mina
     */
    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isMine() {
        return isMine;
    }

    /**
     * Establece la cantidad de minas vecinas.
     *
     * @param adjacentMines minas en las celdas vecinas
     */
    public void setAdjacentMines(int adjacentMines) {
        this.adjacentMines = adjacentMines;
    }

    /**
     * Obtiene la cantidad de minas vecinas.
     *
     * @return minas en las celdas vecinas
     */
    public int getAdjacentMines() {
        return adjacentMines;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    /**
     * Descubre la celda.
     */
    public void reveal() {
        if (isRevealed) {
            return;
        }

        isRevealed = true;
        if (isMine) {
            exploded = true;
        }
        repaint();
    }

    /**
     * Coloca la bandera.
     */
    public void setFlag() {
        isFlagged = true;
        repaint();
    }

    /**
     * Quita la bandera.
     */
    public void removeFlag() {
        isFlagged = false;
        repaint();
    }

    /**
     * Pinta el componente.
     *
     * @param g contexto gráfico
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isRevealed) {
            if (exploded) {

                g.setColor(Color.RED);
                g.fillRect(0, 0, getWidth(), getHeight());
                drawMine(g);
            } else {

                g.setColor(new Color(220, 220, 220));
                g.fillRect(0, 0, getWidth(), getHeight());

                if (isMine) {
                    drawMine(g);
                } else if (adjacentMines > 0) {
                    drawNumber(g);
                }
            }
        } else {

            g.setColor(new Color(180, 180, 180));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.drawLine(0, 0, getWidth() - 1, 0);
            g.drawLine(0, 0, 0, getHeight() - 1);
            g.setColor(Color.GRAY.darker());
            g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight() - 1);
            g.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1);

            if (isFlagged) {
                drawFlag(g);
            }
        }
    }

    /**
     * Dibuja la mina.
     *
     * @param g contexto gráfico
     */
    private void drawMine(Graphics g) {
        g.setColor(Color.BLACK);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 4;

        g.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        g.setColor(Color.DARK_GRAY);
        int spikeLength = radius + 2;
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int x1 = centerX + (int) (radius * Math.cos(angle));
            int y1 = centerY + (int) (radius * Math.sin(angle));
            int x2 = centerX + (int) (spikeLength * Math.cos(angle));
            int y2 = centerY + (int) (spikeLength * Math.sin(angle));
            g.drawLine(x1, y1, x2, y2);
        }

        g.setColor(Color.WHITE);
        g.fillOval(centerX - radius / 3, centerY - radius / 2, radius / 3, radius / 3);
    }

    /**
     * Dibuja la bandera.
     *
     * @param g contexto gráfico
     */
    private void drawFlag(Graphics g) {
        int poleX = getWidth() / 3;
        int poleTop = getHeight() / 4;
        int poleBottom = getHeight() * 3 / 4;

        g.setColor(Color.BLACK);
        g.drawLine(poleX, poleTop, poleX, poleBottom);

        g.setColor(Color.RED);
        int[] xPoints = {poleX, getWidth() * 2 / 3, poleX};
        int[] yPoints = {poleTop + 5, poleTop + getHeight() / 4, poleTop + getHeight() / 2 - 5};
        g.fillPolygon(xPoints, yPoints, 3);
    }

    /**
     * Dibuja el número de minas vecinas.
     *
     * @param g contexto gráfico
     */
    private void drawNumber(Graphics g) {
        String number = String.valueOf(adjacentMines);
        Font font = new Font("Arial", Font.BOLD, 16);
        g.setFont(font);

        switch (adjacentMines) {
            case 1 ->
                g.setColor(Color.BLUE);
            case 2 ->
                g.setColor(new Color(0, 128, 0));
            case 3 ->
                g.setColor(Color.RED);
            case 4 ->
                g.setColor(new Color(0, 0, 128));
            case 5 ->
                g.setColor(new Color(128, 0, 0));
            case 6 ->
                g.setColor(Color.CYAN);
            case 7 ->
                g.setColor(Color.BLACK);
            case 8 ->
                g.setColor(Color.GRAY);
            default ->
                g.setColor(Color.BLACK);
        }

        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(number)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(number, x, y);
    }
}
