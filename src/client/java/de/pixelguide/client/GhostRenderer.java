package de.pixelguide.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

/** Translucent quads in the Z = -95 wall plane. This never edits the world. */
public final class GhostRenderer {
    private static final int MAX_GHOSTS = 4500;
    private static final double MAX_DIST_SQ = 96 * 96;

    private static final RenderPipeline PIPELINE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("pixelguide", "pipeline/hologram"))
            .build()
    );
    private static final Vector4f WHITE = new Vector4f(1, 1, 1, 1);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE = new Matrix4f();
    private static final StagedVertexBuffer BUFFER = new StagedVertexBuffer(
        () -> "PixelGuide Hologram", RenderType.SMALL_BUFFER_SIZE);

    private record Ghost(int x, int y, int color, boolean wrong, double d2) { }
    private static List<Ghost> ghosts = List.of();

    private GhostRenderer() { }

    public static void register() {
        LevelExtractionEvents.END_EXTRACTION.register(GhostRenderer::extract);
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(GhostRenderer::draw);
    }

    private static void extract(LevelExtractionContext context) {
        Minecraft client = Minecraft.getInstance();
        if (!PixelGuideClient.enabled || client.level == null || client.player == null) {
            ghosts = List.of();
            return;
        }
        Vec3 eye = client.player.getEyePosition();
        List<Ghost> buffer = new ArrayList<>();
        for (int i = 0; i < Plan.TOTAL; i++) {
            BlockPos pos = Plan.position(i);
            double dx = pos.getX() + .5 - eye.x;
            double dy = pos.getY() + .5 - eye.y;
            double dz = pos.getZ() + .5 - eye.z;
            double d2 = dx * dx + dy * dy + dz * dz;
            if (d2 > MAX_DIST_SQ || !client.level.hasChunkAt(pos)) continue;
            var state = client.level.getBlockState(pos);
            int paletteIndex = Plan.colorFor(i);
            if (Palette.accepts(paletteIndex, state)) continue;
            buffer.add(new Ghost(pos.getX(), pos.getY(), Palette.COLORS[paletteIndex].rgb(), !state.isAir(), d2));
        }
        buffer.sort(Comparator.comparingDouble(Ghost::d2));
        if (buffer.size() > MAX_GHOSTS) buffer.subList(MAX_GHOSTS, buffer.size()).clear();
        ghosts = List.copyOf(buffer); // no live world references in drawing phase
    }

    private static void draw(LevelRenderContext context) {
        if (ghosts.isEmpty()) return;
        VertexFormat format = PIPELINE.getVertexFormatBinding(0);
        if (format == null) return;
        PrimitiveTopology topology = PIPELINE.getPrimitiveTopology();
        StagedVertexBuffer.Draw job = BUFFER.appendDraw(
            format, topology, topology == PrimitiveTopology.QUADS ? RenderSystem.getProjectionType().vertexSorting() : null);

        PoseStack pose = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;
        pose.pushPose();
        pose.translate(-camera.x, -camera.y, -camera.z);
        VertexConsumer vertices = BUFFER.getVertexBuilder(job);
        Matrix4fc matrix = pose.last().pose();

        for (Ghost g : ghosts) {
            float r = g.wrong() ? 1 : ((g.color() >> 16) & 255) / 255f;
            float green = g.wrong() ? .18f : ((g.color() >> 8) & 255) / 255f;
            float b = g.wrong() ? .12f : (g.color() & 255) / 255f;
            renderPlane(matrix, vertices, g.x(), g.y(), Plan.Z, r, green, b, .46f);
        }
        pose.popPose();
        BUFFER.upload();
        StagedVertexBuffer.ExecuteInfo info = BUFFER.getExecuteInfo(job);
        if (info != null) execute(Minecraft.getInstance(), info, PIPELINE);
        BUFFER.endFrame();
    }

    private static void renderPlane(Matrix4fc matrix, VertexConsumer vertices,
                                    float x, float y, float z, float r, float g, float b, float alpha) {
        float minX = x + .03f, maxX = x + .97f;
        float minY = y + .03f, maxY = y + .97f;
        // Both faces (front/back) of the one-block-deep pixel-art wall.
        float front = z + 1.005f, back = z - .005f;
        vertex(vertices, matrix, minX, minY, front, r,g,b,alpha);
        vertex(vertices, matrix, maxX, minY, front, r,g,b,alpha);
        vertex(vertices, matrix, maxX, maxY, front, r,g,b,alpha);
        vertex(vertices, matrix, minX, maxY, front, r,g,b,alpha);
        vertex(vertices, matrix, maxX, minY, back, r,g,b,alpha);
        vertex(vertices, matrix, minX, minY, back, r,g,b,alpha);
        vertex(vertices, matrix, minX, maxY, back, r,g,b,alpha);
        vertex(vertices, matrix, maxX, maxY, back, r,g,b,alpha);
    }

    private static void vertex(VertexConsumer vertices, Matrix4fc m, float x, float y, float z,
                               float r, float g, float b, float a) {
        vertices.addVertex(m, x, y, z).setColor(r, g, b, a);
    }

    private static void execute(Minecraft client, StagedVertexBuffer.ExecuteInfo info, RenderPipeline pipeline) {
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(
            RenderSystem.getModelViewMatrixCopy(), WHITE, MODEL_OFFSET, TEXTURE);
        RenderTarget target = client.gameRenderer.mainRenderTarget();
        GpuTextureView color = target.getColorTextureView();
        if (color == null) return;
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                () -> "PixelGuide hologram", color, Optional.empty(),
                target.getDepthTextureView(), OptionalDouble.empty())) {
            renderPass.setPipeline(RenderSystem.getCompiledPipeline(pipeline));
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
            renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());
            renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
        }
    }
}
