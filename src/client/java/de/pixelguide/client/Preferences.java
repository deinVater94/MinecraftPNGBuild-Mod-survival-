package de.pixelguide.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/** All listed blocks are valid; preference determines only the HUD suggestion. */
public final class Preferences {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("pixelguide.json");
    private static final int MAX_CUSTOM_PER_COLOR = 64;
    private String[] preferred = new String[Palette.COLORS.length];
    // Serialized next to preferred; old config files with no custom field still work.
    private String[][] custom = new String[Palette.COLORS.length][];

    public static Preferences load() {
        try {
            if (Files.isRegularFile(FILE)) {
                Preferences loaded = GSON.fromJson(Files.readString(FILE, StandardCharsets.UTF_8), Preferences.class);
                if (loaded != null) {
                    loaded.sanitize();
                    return loaded;
                }
            }
        } catch (Exception e) {
            System.err.println("[PixelGuide] Einstellungen nicht lesbar: " + e.getMessage());
        }
        Preferences fallback = new Preferences();
        fallback.sanitize();
        return fallback;
    }

    private void sanitize() {
        if (preferred == null || preferred.length != Palette.COLORS.length) {
            preferred = new String[Palette.COLORS.length];
        }
        if (custom == null || custom.length != Palette.COLORS.length) {
            custom = new String[Palette.COLORS.length][];
        }
        for (int i = 0; i < Palette.COLORS.length; i++) {
            LinkedHashSet<String> valid = new LinkedHashSet<>();
            if (custom[i] != null) for (String candidate : custom[i]) {
                String normalized = Palette.validBlockId(candidate);
                if (normalized != null && !isDefault(i, normalized) && valid.size() < MAX_CUSTOM_PER_COLOR) {
                    valid.add(normalized);
                }
            }
            custom[i] = valid.toArray(new String[0]);
            if (!isDefault(i, preferred[i]) && !valid.contains(preferred[i])) {
                preferred[i] = Palette.COLORS[i].first();
            }
        }
    }

    private boolean isDefault(int index, String id) {
        return id != null && Arrays.asList(Palette.COLORS[index].suggestions()).contains(id);
    }

    public String get(int index) { return preferred[index]; }

    public List<String> candidates(int index) {
        ArrayList<String> all = new ArrayList<>(Arrays.asList(Palette.COLORS[index].suggestions()));
        all.addAll(Arrays.asList(custom[index]));
        return all;
    }

    public boolean isCustomCandidate(int index, String blockId) {
        if (index < 0 || index >= Palette.COLORS.length || custom[index] == null) return false;
        return Arrays.asList(custom[index]).contains(blockId);
    }

    public boolean choose(int index, String id) {
        if (index < 0 || index >= Palette.COLORS.length) return false;
        if (!isDefault(index, id) && !isCustomCandidate(index, id)) return false;
        preferred[index] = id;
        save();
        return true;
    }

    /** Add an installed Minecraft/modded block to this color and make it preferred. */
    public boolean addCustom(int index, String enteredId) {
        if (index < 0 || index >= Palette.COLORS.length) return false;
        String id = Palette.validBlockId(enteredId);
        if (id == null) return false;
        if (isDefault(index, id) || isCustomCandidate(index, id)) return choose(index, id);
        if (custom[index].length >= MAX_CUSTOM_PER_COLOR) return false;
        ArrayList<String> list = new ArrayList<>(Arrays.asList(custom[index]));
        list.add(id);
        custom[index] = list.toArray(new String[0]);
        preferred[index] = id;
        save();
        return true;
    }

    public boolean removeCustom(int index, String enteredId) {
        if (index < 0 || index >= Palette.COLORS.length) return false;
        String id = Palette.validBlockId(enteredId);
        if (id == null || !isCustomCandidate(index, id)) return false;
        ArrayList<String> list = new ArrayList<>(Arrays.asList(custom[index]));
        list.remove(id);
        custom[index] = list.toArray(new String[0]);
        if (preferred[index].equals(id)) preferred[index] = Palette.COLORS[index].first();
        save();
        return true;
    }

    private void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[PixelGuide] Einstellungen konnten nicht gespeichert werden: " + e.getMessage());
        }
    }
}
