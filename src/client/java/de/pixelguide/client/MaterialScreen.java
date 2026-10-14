package de.pixelguide.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** In-game, automatically updating materials list (K). */
public final class MaterialScreen extends Screen {
    private static final int COLORS_PER_PAGE = 8;
    private MaterialProgress.Snapshot progress;
    private int selected;
    private int colorPage;
    private int left, top, leftWidth, rightX, rightWidth;
    private int ticks;

    public MaterialScreen() {
        super(Component.literal("PixelGuide – Materialbedarf"));
        int index = PixelGuideClient.aimedAt(Minecraft.getInstance());
        selected = index < 0 ? 0 : Plan.colorFor(index);
        colorPage = selected / COLORS_PER_PAGE;
        progress = MaterialProgress.scan(Minecraft.getInstance());
    }

    @Override
    protected void init() {
        super.init();
        int panelWidth = Math.min(490, width - 16);
        left = (width - panelWidth) / 2;
        top = Math.max(10, (height - 278) / 2);
        leftWidth = Math.min(158, Math.max(110, panelWidth * 35 / 100));
        rightX = left + leftWidth + 14;
        rightWidth = panelWidth - leftWidth - 14;

        for (int j = 0; j < COLORS_PER_PAGE; j++) {
            int index = colorPage * COLORS_PER_PAGE + j;
            if (index >= Palette.COLORS.length) break;
            String label = (selected == index ? "▶ " : "") + Palette.COLORS[index].name();
            addRenderableWidget(Button.builder(Component.literal(label), b -> {
                selected = index;
                rebuildWidgets();
            }).bounds(left + 14, top + 29 + j * 21, leftWidth - 14, 19).build());
        }
        addRenderableWidget(Button.builder(Component.literal(colorPage == 0 ? "Farben 9–16 ▶" : "◀ Farben 1–8"), b -> {
            colorPage = 1 - colorPage;
            selected = colorPage * COLORS_PER_PAGE;
            rebuildWidgets();
        }).bounds(left, top + 202, leftWidth, 19).build());

        addRenderableWidget(Button.builder(Component.literal("Material ändern (J)"), b ->
            minecraft.gui.setScreen(new PaletteScreen(selected))
        ).bounds(rightX, top + 177, rightWidth, 19).build());
        addRenderableWidget(Button.builder(Component.literal("Neu prüfen"), b -> {
            progress = MaterialProgress.scan(minecraft);
        }).bounds(rightX, top + 202, rightWidth, 19).build());
        addRenderableWidget(Button.builder(Component.literal("Schließen"), b -> onClose())
            .bounds(left, top + 237, leftWidth, 19).build());
    }

    @Override
    public void tick() {
        if (++ticks % 40 == 0) progress = MaterialProgress.scan(minecraft);
    }

    private void line(GuiGraphicsExtractor graphics, String label, int value, int offset, int color) {
        graphics.text(font, label + ": " + value, rightX, top + offset, color);
    }

    private String shortLabel(String s) {
        return font.plainSubstrByWidth(s, Math.max(60, rightWidth - 10));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(font, title, width / 2, top + 5, 0xFFFFFFFF);
        for (int j = 0; j < COLORS_PER_PAGE; j++) {
            int idx = colorPage * COLORS_PER_PAGE + j;
            if (idx >= Palette.COLORS.length) break;
            graphics.fill(left + 1, top + 33 + j * 21, left + 11, top + 43 + j * 21,
                0xFF000000 | Palette.COLORS[idx].rgb());
        }
        var color = Palette.COLORS[selected];
        graphics.text(font, shortLabel(color.name()), rightX, top + 29, 0xFFEEEEEE);
        line(graphics, "Benötigt insgesamt", progress.total()[selected], 47, 0xFFFFFFFF);
        line(graphics, "Bereits richtig", progress.correct()[selected], 62, 0xFF90E5A5);
        line(graphics, "Noch leer", progress.empty()[selected], 77, 0xFFFFEAA0);
        line(graphics, "Falsch belegt", progress.wrong()[selected], 92, 0xFFFF9B92);
        line(graphics, "Nicht prüfbar", progress.unknown()[selected], 107, 0xFFBABABA);
        int max = progress.remainingMaximum(selected);
        line(graphics, "Maximal noch nötig", max, 123, 0xFFB3CFFF);
        graphics.text(font, "(inklusive ungeprüfter Stellen)", rightX, top + 137, 0xFFAAAAAA);
        String preferred = PixelGuideClient.preferences.get(selected);
        graphics.text(font, "Geplantes Material:", rightX, top + 150, 0xFFBBBBBB);
        graphics.text(font, shortLabel(Palette.block(preferred).getName().getString()),
            rightX, top + 161, 0xFFF7DA93);

        int correct = progress.sum(progress.correct());
        int unknown = progress.sum(progress.unknown());
        graphics.text(font, correct + "/" + Plan.TOTAL + " gebaut – " + unknown + " nicht prüfbar",
            left, top + 226, 0xFFE8E8E8);
        graphics.text(font, "Maximalbedarf setzt nur das gewählte Material je Farbe voraus.",
            left, top + 264, 0xFFAAAAAA);
    }
}
