package de.pixelguide.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

/** Read-only material counts. Does not modify the level or server. */
public final class MaterialProgress {
    public record Snapshot(int[] total, int[] correct, int[] empty, int[] wrong, int[] unknown) {
        public int remainingMaximum(int color) { return total[color] - correct[color]; }
        public int confirmedToDo(int color) { return empty[color] + wrong[color]; }
        public int sum(int[] array) {
            int result = 0;
            for (int value : array) result += value;
            return result;
        }
    }

    private static final int[] TOTALS = totals();

    private MaterialProgress() { }

    private static int[] totals() {
        int[] counts = new int[Palette.COLORS.length];
        for (int i = 0; i < Plan.TOTAL; i++) counts[Plan.colorFor(i)]++;
        return counts;
    }

    public static Snapshot scan(Minecraft client) {
        int n = Palette.COLORS.length;
        int[] correct = new int[n];
        int[] empty = new int[n];
        int[] wrong = new int[n];
        int[] unknown = new int[n];
        if (client.level == null) {
            System.arraycopy(TOTALS, 0, unknown, 0, n);
            return new Snapshot(TOTALS.clone(), correct, empty, wrong, unknown);
        }
        for (int i = 0; i < Plan.TOTAL; i++) {
            int color = Plan.colorFor(i);
            BlockPos position = Plan.position(i);
            if (!client.level.hasChunkAt(position)) {
                unknown[color]++;
            } else {
                var state = client.level.getBlockState(position);
                if (Palette.accepts(color, state)) correct[color]++;
                else if (state.isAir()) empty[color]++;
                else wrong[color]++;
            }
        }
        return new Snapshot(TOTALS.clone(), correct, empty, wrong, unknown);
    }
}
