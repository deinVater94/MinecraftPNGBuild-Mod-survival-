package de.pixelguide.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Two color pages; the material suggestions are paginated separately. */
public final class PaletteScreen extends Screen {
    private static final int COLORS_PER_PAGE = 8;
    private static final int MATERIALS_PER_PAGE = 5;
    private int selected;
    private int colorPage;
    private int materialPage;
    private int layoutLeft, layoutTop, leftWidth, rightX, rightWidth;
    private String inputText = "";
    private String feedback = "";
    private EditBox customInput;

    public PaletteScreen() {
        this(selectedFromTarget());
    }

    private static int selectedFromTarget() {
        int index = PixelGuideClient.aimedAt(Minecraft.getInstance());
        return index < 0 ? 0 : Plan.colorFor(index);
    }

    public PaletteScreen(int initialColor) {
        super(Component.literal("PixelGuide – Blockpalette"));
        selected = Math.max(0, Math.min(initialColor, Palette.COLORS.length - 1));
        colorPage = selected / COLORS_PER_PAGE;
    }

    @Override
    protected void init() {
        super.init();
        int panelWidth = Math.min(420, width - 16);
        layoutLeft = (width - panelWidth) / 2;
        layoutTop = Math.max(12, (height - 230) / 2);
        leftWidth = Math.min(160, Math.max(100, panelWidth * 38 / 100));
        rightX = layoutLeft + leftWidth + 10;
        rightWidth = panelWidth - leftWidth - 10;

        for (int j = 0; j < COLORS_PER_PAGE; j++) {
            int idx = colorPage * COLORS_PER_PAGE + j;
            if (idx >= Palette.COLORS.length) break;
            String label = (idx == selected ? "▶ " : "") + Palette.COLORS[idx].name();
            addRenderableWidget(Button.builder(Component.literal(label), button -> {
                selected = idx;
                materialPage = 0;
                inputText = "";
                feedback = "";
                rebuildWidgets();
            }).bounds(layoutLeft + 13, layoutTop + 15 + j * 19, leftWidth - 13, 18).build());
        }

        List<String> options = PixelGuideClient.preferences.candidates(selected);
        int pages = Math.max(1, (options.size() + MATERIALS_PER_PAGE - 1) / MATERIALS_PER_PAGE);
        materialPage = Math.max(0, Math.min(materialPage, pages - 1));
        int from = materialPage * MATERIALS_PER_PAGE;
        for (int j = 0; j < MATERIALS_PER_PAGE && from + j < options.size(); j++) {
            String id = options.get(from + j);
            String name = Palette.block(id).getName().getString();
            String title = (PixelGuideClient.preferences.get(selected).equals(id) ? "✔ " : "") + name;
            addRenderableWidget(Button.builder(Component.literal(title), button -> {
                PixelGuideClient.preferences.choose(selected, id);
                feedback = "Bevorzugt: " + id;
                rebuildWidgets();
            }).bounds(rightX, layoutTop + 15 + j * 19, rightWidth, 18).build());
        }

        int btnW = Math.max(42, (rightWidth - 6) / 2);
        addRenderableWidget(Button.builder(Component.literal("◀"), button -> {
            materialPage = Math.max(0, materialPage - 1);
            rebuildWidgets();
        }).bounds(rightX, layoutTop + 115, btnW, 18).build()).active = materialPage > 0;
        addRenderableWidget(Button.builder(Component.literal("▶"), button -> {
            materialPage = Math.min(pages - 1, materialPage + 1);
            rebuildWidgets();
        }).bounds(rightX + rightWidth - btnW, layoutTop + 115, btnW, 18).build()).active = materialPage < pages - 1;

        customInput = new EditBox(font, rightX, layoutTop + 147, rightWidth, 18,
            Component.literal("Block-ID (z. B. minecraft:obsidian)"));
        customInput.setMaxLength(120);
        customInput.setValue(inputText);
        customInput.setResponder(value -> inputText = value);
        addRenderableWidget(customInput);

        addRenderableWidget(Button.builder(Component.literal("+ Hinzufügen"), button -> {
            if (PixelGuideClient.preferences.addCustom(selected, inputText)) {
                feedback = "Hinzugefügt oder ausgewählt!";
                materialPage = (PixelGuideClient.preferences.candidates(selected).size() - 1) / MATERIALS_PER_PAGE;
                inputText = "";
            } else feedback = "Ungültige oder nicht installierte Block-ID!";
            rebuildWidgets();
        }).bounds(rightX, layoutTop + 170, btnW, 18).build());
        addRenderableWidget(Button.builder(Component.literal("- Entfernen"), button -> {
            if (PixelGuideClient.preferences.removeCustom(selected, inputText)) {
                feedback = "Eigener Block entfernt.";
                inputText = "";
            } else feedback = "Nur eigene IDs können entfernt werden.";
            rebuildWidgets();
        }).bounds(rightX + rightWidth - btnW, layoutTop + 170, btnW, 18).build());

        addRenderableWidget(Button.builder(Component.literal(colorPage == 0 ? "Farben 9–16 ▶" : "◀ Farben 1–8"), button -> {
            colorPage = 1 - colorPage;
            selected = colorPage * COLORS_PER_PAGE;
            materialPage = 0;
            feedback = "";
            inputText = "";
            rebuildWidgets();
        }).bounds(layoutLeft, layoutTop + 173, leftWidth, 18).build());
        addRenderableWidget(Button.builder(Component.literal("Zurück"), button -> onClose())
            .bounds(layoutLeft, layoutTop + 197, leftWidth, 18).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(font, title, width / 2, layoutTop - 10, 0xFFFFFFFF);
        for (int j = 0; j < COLORS_PER_PAGE; j++) {
            int idx = colorPage * COLORS_PER_PAGE + j;
            if (idx >= Palette.COLORS.length) break;
            graphics.fill(layoutLeft, layoutTop + 18 + j * 19, layoutLeft + 10,
                layoutTop + 29 + j * 19, 0xFF000000 | Palette.COLORS[idx].rgb());
        }
        int pages = Math.max(1,
            (PixelGuideClient.preferences.candidates(selected).size() + MATERIALS_PER_PAGE - 1) / MATERIALS_PER_PAGE);
        graphics.text(font, "Materialien: " + Palette.COLORS[selected].name(), rightX, layoutTop + 3, 0xFFFFFFFF);
        graphics.text(font, "Seite " + (materialPage + 1) + "/" + pages,
            rightX + rightWidth - 48, layoutTop + 105, 0xFFDDDDDD);
        graphics.text(font, "Eigene Block-ID (auch Mod-Blöcke):", rightX, layoutTop + 136, 0xFFB7B7B7);
        graphics.text(font, feedback.isEmpty() ? "Alle Vorschläge zählen als richtig." : feedback,
            rightX, layoutTop + 194, 0xFFD5E6D7);
        graphics.text(font, PixelGuideClient.correctlyBuilt + "/" + Plan.TOTAL + " richtig gebaut",
            rightX, layoutTop + 210, 0xFF96DC90);
    }
}
