package de.pixelguide.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public final class PixelGuideClient implements ClientModInitializer {
    public static Preferences preferences;
    public static boolean enabled = false; // bewusst pro Spielstart aus
    public static int correctlyBuilt = 0;
    public static int incorrectlyBuilt = 0;
    public static int loaded = 0;

    private static int ticks = 0;
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath("pixelguide", "main")
    );
    private static KeyMapping toggleKey;
    private static KeyMapping paletteKey;
    private static KeyMapping materialsKey;

    @Override
    public void onInitializeClient() {
        preferences = Preferences.load();
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.pixelguide.toggle", InputConstants.Type.KEYBOARD, InputConstants.KEY_H, CATEGORY));
        paletteKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.pixelguide.palette", InputConstants.Type.KEYBOARD, InputConstants.KEY_J, CATEGORY));

        materialsKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.pixelguide.materials", InputConstants.Type.KEYBOARD, InputConstants.KEY_K, CATEGORY));

        GhostRenderer.register();
        ClientTickEvents.END_CLIENT_TICK.register(PixelGuideClient::onTick);
    }

    private static void onTick(Minecraft client) {
        if (client.player == null || client.level == null) return;
        while (toggleKey.consumeClick()) {
            enabled = !enabled;
            client.player.sendSystemMessage(Component.literal(
                "§d[PixelGuide] §fHologramm: " + (enabled ? "§aAN" : "§cAUS") + " §7| J: Blöcke | K: Bedarf"));
        }
        while (paletteKey.consumeClick()) {
            if (client.gui.screen() == null) client.gui.setScreen(new PaletteScreen());
        }
        while (materialsKey.consumeClick()) {
            if (client.gui.screen() == null) client.gui.setScreen(new MaterialScreen());
        }
        if (!enabled || client.gui.screen() != null) return;

        ticks++;
        if (ticks % 40 == 0) {
            MaterialProgress.Snapshot progress = MaterialProgress.scan(client);
            correctlyBuilt = progress.sum(progress.correct());
            incorrectlyBuilt = progress.sum(progress.wrong());
            loaded = Plan.TOTAL - progress.sum(progress.unknown());
        }

        if (ticks % 10 == 0) {
            int index = aimedAt(client);
            String info;
            if (index >= 0) {
                BlockPos pos = Plan.position(index);
                int color = Plan.colorFor(index);
                var col = Palette.COLORS[color];
                var chosen = Palette.block(preferences.get(color));
                info = "§d" + pos.getX() + " " + pos.getY() + " " + pos.getZ()
                    + " §f| " + col.name() + " §7→ §e" + chosen.getName().getString();
            } else {
                info = "§dPixelGuide §f" + correctlyBuilt + "/" + Plan.TOTAL
                    + " §7richtig | §c" + incorrectlyBuilt + " §7falsch | §fJ: Palette | K: Bedarf";
            }
            client.player.sendOverlayMessage(Component.literal(info));
        }
    }

    /** Ray/vertical Z-plane intersection, works even if target position contains air. */
    public static int aimedAt(Minecraft client) {
        if (client.player == null) return -1;
        Vec3 eye = client.player.getEyePosition();
        Vec3 dir = client.player.getLookAngle();
        if (Math.abs(dir.z) < 0.000001) return -1;
        double surface = dir.z > 0 ? Plan.Z : Plan.Z + 1.0;
        double distance = (surface - eye.z) / dir.z;
        if (distance < 0 || distance > 96) return -1;
        double atX = eye.x + dir.x * distance;
        double atY = eye.y + dir.y * distance;
        return Plan.indexAt((int)Math.floor(atX), (int)Math.floor(atY), Plan.Z);
    }
}
