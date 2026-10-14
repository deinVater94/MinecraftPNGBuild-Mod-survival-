package de.pixelguide.client;

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.core.BlockPos;

/** Top-left image coordinate (0,0) => block (133,166,-95); bottom right => (228,71,-95). */
public final class Plan {
    public static final int WIDTH = 96;
    public static final int HEIGHT = 96;
    public static final int LEFT_X = 133;
    public static final int BOTTOM_Y = 71;
    public static final int Z = -95;
    public static final int TOTAL = WIDTH * HEIGHT;

    private static final byte[] COLORS = readImage();

    private Plan() { }

    private static byte[] readImage() {
        try (InputStream in = Plan.class.getResourceAsStream("/assets/pixelguide/plan.bin")) {
            if (in == null) throw new IllegalStateException("plan.bin fehlt");
            byte[] bytes = in.readAllBytes();
            if (bytes.length != TOTAL) throw new IllegalStateException("plan.bin: unerwartete Groesse " + bytes.length);
            return bytes;
        } catch (IOException e) {
            throw new IllegalStateException("Pixel-Art-Datei konnte nicht geladen werden", e);
        }
    }

    public static int colorFor(int index) {
        return Byte.toUnsignedInt(COLORS[index]);
    }

    public static BlockPos position(int index) {
        int column = index % WIDTH;
        int row = index / WIDTH;
        return new BlockPos(LEFT_X + column, BOTTOM_Y + HEIGHT - 1 - row, Z);
    }

    public static int indexAt(int x, int y, int z) {
        if (z != Z || x < LEFT_X || x >= LEFT_X + WIDTH || y < BOTTOM_Y || y >= BOTTOM_Y + HEIGHT) return -1;
        int col = x - LEFT_X;
        int row = HEIGHT - 1 - (y - BOTTOM_Y);
        return row * WIDTH + col;
    }
}
