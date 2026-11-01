package com.juniverse.game.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.logging.Logger;

import javax.swing.Icon;

/**
 * @author ajuar
 */
public record SmileyIcon(int state) implements Icon {

    private static final Logger log = Logger.getLogger(SmileyIcon.class.getName());
    public static final int SIZE = 40;

    public static final int NORMAL = 0;

    public static final int WIN = 1;

    public static final int DEAD = 2;

    /**
     * Pinta el icono.
     *
     * @param c componente
     * @param g contexto gráfico
     * @param x coordenada x
     * @param y the y
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        int size = Math.min(c.getWidth(), c.getHeight());
        int diameter = size - 10;
        int cx = x + (c.getWidth() - diameter) / 2;
        int cy = y + (c.getHeight() - diameter) / 2;

        g.setColor(Color.YELLOW);
        g.fillOval(cx, cy, diameter, diameter);
        g.setColor(Color.BLACK);
        g.drawOval(cx, cy, diameter, diameter);

        int eyeSize = diameter / 5;
        int eyeY = cy + diameter / 3;
        g.fillOval(cx + diameter / 3 - eyeSize / 2, eyeY, eyeSize, eyeSize);
        g.fillOval(cx + 2 * diameter / 3 - eyeSize / 2, eyeY, eyeSize, eyeSize);

        int mouthY = cy + 2 * diameter / 3;
        int mouthWidth = diameter / 2;
        int mouthHeight = diameter / 4;

        switch (state) {

            case NORMAL ->

                g.drawArc(cx + (diameter - mouthWidth) / 2, mouthY - mouthHeight / 3, mouthWidth, mouthHeight, 190, 160);

            case WIN -> {

                g.drawArc(cx + (diameter - mouthWidth) / 2, mouthY - mouthHeight / 3, mouthWidth, mouthHeight, 190, 160);

                g.setColor(new Color(100, 100, 255));
                g.fillRect(cx + diameter / 4 - 2, eyeY - eyeSize / 2, diameter / 2 + 4, eyeSize * 2);
                g.setColor(Color.BLACK);
                g.drawRect(cx + diameter / 4 - 2, eyeY - eyeSize / 2, diameter / 2 + 4, eyeSize * 2);
            }
            case DEAD -> {

                g.drawArc(cx + (diameter - mouthWidth) / 2, mouthY - mouthHeight / 2, mouthWidth, mouthHeight, 10, 160);

                g.setColor(Color.RED);
                int offset = eyeSize / 2;
                g.drawLine(cx + diameter / 3 - offset, eyeY - offset, cx + diameter / 3 + offset, eyeY + offset);
                g.drawLine(cx + diameter / 3 - offset, eyeY + offset, cx + diameter / 3 + offset, eyeY - offset);

                g.drawLine(cx + 2 * diameter / 3 - offset, eyeY - offset, cx + 2 * diameter / 3 + offset, eyeY + offset);
                g.drawLine(cx + 2 * diameter / 3 - offset, eyeY + offset, cx + 2 * diameter / 3 + offset, eyeY - offset);
            }
            default ->
                log.warning("Invalid option");

        }
    }

    /**
     * Obtiene el ancho del icono.
     *
     * @return ancho del icono
     */
    @Override
    public int getIconWidth() {
        return SIZE;
    }

    /**
     * Obtiene el alto del icono.
     *
     * @return alto del icono
     */
    @Override
    public int getIconHeight() {
        return SIZE;
    }

}
