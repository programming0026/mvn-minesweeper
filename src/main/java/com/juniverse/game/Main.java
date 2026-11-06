package com.juniverse.game;

import com.juniverse.game.gui.MinesweeperFrame;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import javax.swing.SwingUtilities;

/**
 *
 * @author Java Universe
 */
public class Main {

    /**
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        FlatMacDarkLaf.setup();
        SwingUtilities.invokeLater(MinesweeperFrame::new);
    }

}
