package com.juniverse.game;

import com.juniverse.game.gui.MinesweeperFrame;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import javax.swing.SwingUtilities;

/**
 *
 * @author ajuar
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FlatMacDarkLaf.setup();
        SwingUtilities.invokeLater(MinesweeperFrame::new);
    }

}
