package com.juniverse.game.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

/** Pruebas de las reglas del buscaminas, sin interfaz gráfica. */
class MineFieldTest {

    private static MineField field(int rows, int cols, int mines) {
        return new MineField(rows, cols, mines, new Random(42));
    }

    private static int countMines(MineField f) {
        int n = 0;
        for (int r = 0; r < f.rows(); r++) {
            for (int c = 0; c < f.cols(); c++) {
                if (f.isMine(r, c)) {
                    n++;
                }
            }
        }
        return n;
    }

    @Test
    void alInicioNoHayMinasNiCeldasDescubiertas() {
        MineField f = field(8, 8, 10);
        assertEquals(MineField.State.READY, f.state());
        assertEquals(0, countMines(f));
        assertEquals(0, f.revealedCount());
    }

    @Test
    void elPrimerClicNuncaEsUnaMinaYSeColocaLaCantidadPedida() {
        for (long seed = 0; seed < 50; seed++) {
            MineField f = new MineField(8, 8, 10, new Random(seed));
            f.reveal(3, 4);
            assertFalse(f.isMine(3, 4), "semilla " + seed);
            assertEquals(10, countMines(f));
            assertTrue(f.isRevealed(3, 4));
            assertTrue(f.state() == MineField.State.PLAYING || f.state() == MineField.State.WON);
        }
    }

    @Test
    void losNumerosVecinosCoincidenConLasMinas() {
        MineField f = field(8, 8, 10);
        f.reveal(0, 0);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int expected = 0;
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = r + dr;
                        int nc = c + dc;
                        if ((dr != 0 || dc != 0) && nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && f.isMine(nr, nc)) {
                            expected++;
                        }
                    }
                }
                assertEquals(expected, f.adjacentMines(r, c), "celda " + r + "," + c);
            }
        }
    }

    @Test
    void sinMinasUnClicDescubreTodoYGana() {
        MineField f = field(5, 5, 0);
        f.reveal(2, 2);
        assertEquals(MineField.State.WON, f.state());
        assertEquals(25, f.revealedCount());
    }

    @Test
    void descubrirUnaMinaPierdeYMuestraLasMinasSinBandera() {
        MineField f = field(6, 6, 8);
        f.reveal(0, 0);
        int[] mine = firstMine(f);
        if (f.state() == MineField.State.PLAYING) {
            f.reveal(mine[0], mine[1]);
            assertEquals(MineField.State.LOST, f.state());
            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 6; c++) {
                    if (f.isMine(r, c)) {
                        assertTrue(f.isRevealed(r, c));
                    }
                }
            }
            assertTrue(f.isFinished());
        }
    }

    private static int[] firstMine(MineField f) {
        for (int r = 0; r < f.rows(); r++) {
            for (int c = 0; c < f.cols(); c++) {
                if (f.isMine(r, c)) {
                    return new int[] {r, c};
                }
            }
        }
        return new int[] {-1, -1};
    }

    @Test
    void lasBanderasSeAlternanYNoSuperanElTotalDeMinas() {
        MineField f = field(4, 4, 2);
        f.toggleFlag(0, 0);
        f.toggleFlag(0, 1);
        f.toggleFlag(0, 2);
        assertEquals(2, f.flagCount());
        assertFalse(f.isFlagged(0, 2));
        f.toggleFlag(0, 0);
        assertEquals(1, f.flagCount());
    }

    @Test
    void unaCeldaConBanderaNoSeDescubre() {
        MineField f = field(4, 4, 2);
        f.toggleFlag(1, 1);
        f.reveal(1, 1);
        assertFalse(f.isRevealed(1, 1));
        assertEquals(MineField.State.READY, f.state());
    }

    @Test
    void alGanarSePonenBanderasEnTodasLasMinas() {
        MineField f = field(3, 3, 1);
        f.reveal(0, 0);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (!f.isMine(r, c)) {
                    f.reveal(r, c);
                }
            }
        }
        assertEquals(MineField.State.WON, f.state());
        assertEquals(1, f.flagCount());
    }

    @Test
    void rechazaTablerosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new MineField(0, 5, 1, new Random()));
        assertThrows(IllegalArgumentException.class, () -> new MineField(3, 3, 9, new Random()));
        assertThrows(IllegalArgumentException.class, () -> new MineField(3, 3, -1, new Random()));
    }
}
