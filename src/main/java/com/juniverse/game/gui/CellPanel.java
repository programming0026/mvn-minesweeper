package com.juniverse.game.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.Serial;

import javax.swing.JPanel;

/**
 * The Class CellPanel.
 *
 * @author ajuar
 */
public class CellPanel extends JPanel {

    /**
     * The Constant serialVersionUID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The row.
     */
    private final int row;

    /**
     * The col.
     */
    private final int col;

    /**
     * The is mine.
     */
    private boolean isMine = false;

    /**
     * The is revealed.
     */
    private boolean isRevealed = false;

    /**
     * The is flagged.
     */
    private boolean isFlagged = false;

    /**
     * The adjacent mines.
     */
    private int adjacentMines = 0;

    /**
     * The exploded.
     */
    private boolean exploded = false;

    /**
     * Instantiates a new cell panel.
     *
     * @param row the row
     * @param col the col
     */
    public CellPanel(int row, int col) {
        this.row = row;
        this.col = col;
        setPreferredSize(new Dimension(30, 30));
        setFocusable(false);
    }

    /**
     * Sets the mine.
     *
     * @param isMine the new mine
     */
    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    /**
     * Checks if is mine.
     *
     * @return true, if is mine
     */
    public boolean isMine() {
        return isMine;
    }

    /**
     * Increment adjacent mines.
     */
    public void incrementAdjacentMines() {
        adjacentMines++;
    }

    /**
     * Gets the adjacent mines.
     *
     * @return the adjacent mines
     */
    public int getAdjacentMines() {
        return adjacentMines;
    }

    /**
     * Gets the row.
     *
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the col.
     *
     * @return the col
     */
    public int getCol() {
        return col;
    }

    /**
     * Checks if is revealed.
     *
     * @return true, if is revealed
     */
    public boolean isRevealed() {
        return isRevealed;
    }

    /**
     * Checks if is flagged.
     *
     * @return true, if is flagged
     */
    public boolean isFlagged() {
        return isFlagged;
    }

    /**
     * Reveal.
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
     * Sets the flag.
     */
    public void setFlag() {
        isFlagged = true;
        repaint();
    }

    /**
     * Removes the flag.
     */
    public void removeFlag() {
        isFlagged = false;
        repaint();
    }

    /**
     * Paint component.
     *
     * @param g the g
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
     * Draw mine.
     *
     * @param g the g
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
     * Draw flag.
     *
     * @param g the g
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
     * Draw number.
     *
     * @param g the g
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
