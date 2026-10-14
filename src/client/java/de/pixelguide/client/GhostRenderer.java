package de.pixelguide.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

import org.joml.Matrix4fc;

/**
 * Renders the pixel-art guide using Minecraft's regular 26.3 submit-node pipeline.
 *
 * IMPORTANT: No GPU uploads, command encoders, or render passes in a level callback!
 * The feature renderer owns those operations after it collects submitted geometry.
 */
public final class GhostRenderer {
    private static final int MAX_GHOSTS = 4500;
    private static final double MAX_DIST_SQ = 96 * 96;

    private record Ghost(int x, int y, int color, boolean wrong, double d2) { }

    // Published once per extraction, consumed by the drawing phase.
    private static volatile List<Ghost> ghosts = List.of();

    private GhostRenderer() { }

    public static void register() {
        LevelExtractionEvents.END_EXTRACTION.register(GhostRenderer::extract);
        LevelRenderEvents.COLLECT_SUBMITS.register(GhostRenderer::submit);
    }

    /** Only access the world during extraction, never from the render callback. */
    private static void extract(LevelExtractionContext context) {
        Minecraft client = Minecraft.getInstance();
        if (!PixelGuideClient.enabled || client.level == null || client.player == null) {
            ghosts = List.of();
            return;
        }

        Vec3 eye = client.player.getEyePosition();
        List<Ghost> output = new ArrayList<>();
        for (int i = 0; i < Plan.TOTAL; i++) {
            BlockPos pos = Plan.position(i);
            double dx = pos.getX() + 0.5 - eye.x;
            double dy = pos.getY() + 0.5 - eye.y;
            double dz = pos.getZ() + 0.5 - eye.z;
            double d2 = dx * dx + dy * dy + dz * dz;
            if (d2 > MAX_DIST_SQ || !client.level.hasChunkAt(pos)) continue;

            var state = client.level.getBlockState(pos);
            int paletteIndex = Plan.colorFor(i);
            if (Palette.accepts(paletteIndex, state)) continue;
            output.add(new Ghost(pos.getX(), pos.getY(), Palette.COLORS[paletteIndex].rgb(), !state.isAir(), d2));
        }

        output.sort(Comparator.comparingDouble(Ghost::d2));
        if (output.size() > MAX_GHOSTS) {
            output.subList(MAX_GHOSTS, output.size()).clear();
        }
        ghosts = List.copyOf(output);
    }

    /**
     * COLLECT_SUBMITS adds geometry to the vanilla feature renderer.  It does not
     * upload anything to the GPU and never creates or closes a RenderPass.
     */
    private static void submit(LevelRenderContext context) {
        List<Ghost> snapshot = ghosts;
        if (snapshot.isEmpty()) return;

        Vec3 camera = context.levelState().cameraRenderState.pos;
        PoseStack poseStack = context.poseStack();
        poseStack.pushPose();
        try {
            poseStack.translate(-camera.x, -camera.y, -camera.z);
            context.submitNodeCollector().submitCustomGeometry(
                poseStack, RenderTypes.debugFilledBox(),
                (pose, vertices) -> {
                    Matrix4fc matrix = pose.pose();
                    for (Ghost ghost : snapshot) {
                        float r = ghost.wrong() ? 1.0f : ((ghost.color() >>> 16) & 255) / 255f;
                        float g = ghost.wrong() ? 0.18f : ((ghost.color() >>> 8) & 255) / 255f;
                        float b = ghost.wrong() ? 0.12f : (ghost.color() & 255) / 255f;
                        renderPlane(matrix, vertices, ghost.x(), ghost.y(), Plan.Z, r, g, b, 0.46f);
                    }
                }
            );
        } finally {
            poseStack.popPose();
        }
    }

    private static void renderPlane(Matrix4fc matrix, VertexConsumer vertices,
                                    float x, float y, float z, float r, float g, float b, float alpha) {
        float minX = x + 0.03f, maxX = x + 0.97f;
        float minY = y + 0.03f, maxY = y + 0.97f;
        float front = z + 1.005f, back = z - 0.005f;

        // Front and back of the one-block-thick wall.
        vertex(vertices, matrix, minX, minY, front, r, g, b, alpha);
        vertex(vertices, matrix, maxX, minY, front, r, g, b, alpha);
        vertex(vertices, matrix, maxX, maxY, front, r, g, b, alpha);
        vertex(vertices, matrix, minX, maxY, front, r, g, b, alpha);

        vertex(vertices, matrix, maxX, minY, back, r, g, b, alpha);
        vertex(vertices, matrix, minX, minY, back, r, g, b, alpha);
        vertex(vertices, matrix, minX, maxY, back, r, g, b, alpha);
        vertex(vertices, matrix, maxX, maxY, back, r, g, b, alpha);
    }

    private static void vertex(VertexConsumer vertices, Matrix4fc matrix,
                               float x, float y, float z, float r, float g, float b, float alpha) {
        vertices.addVertex(matrix, x, y, z).setColor(r, g, b, alpha);
    }
}
