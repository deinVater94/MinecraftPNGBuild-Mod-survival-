package de.pixelguide.client;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Sixteen picture colors, each with many interchangeable material suggestions. */
public final class Palette {
    public record ColorGroup(String name, int rgb, String[] suggestions) {
        public String first() { return suggestions[0]; }
    }

    public static final ColorGroup[] COLORS = new ColorGroup[] {
        new ColorGroup("Schwarz", 0x090B10, new String[] { "minecraft:black_wool", "minecraft:black_concrete", "minecraft:black_concrete_powder", "minecraft:black_terracotta", "minecraft:black_glazed_terracotta", "minecraft:obsidian", "minecraft:crying_obsidian", "minecraft:coal_block", "minecraft:netherite_block", "minecraft:blackstone", "minecraft:polished_blackstone", "minecraft:polished_blackstone_bricks", "minecraft:chiseled_polished_blackstone", "minecraft:deepslate_tiles" }),
        new ColorGroup("Weiß", 0xCFD5D6, new String[] { "minecraft:white_concrete", "minecraft:white_wool", "minecraft:white_concrete_powder", "minecraft:white_terracotta", "minecraft:white_glazed_terracotta", "minecraft:quartz_block", "minecraft:smooth_quartz", "minecraft:quartz_bricks", "minecraft:chiseled_quartz_block", "minecraft:calcite", "minecraft:snow_block", "minecraft:diorite", "minecraft:polished_diorite", "minecraft:bone_block", "minecraft:iron_block" }),
        new ColorGroup("Hellgrau", 0x7D7D73, new String[] { "minecraft:light_gray_concrete", "minecraft:light_gray_wool", "minecraft:light_gray_concrete_powder", "minecraft:light_gray_terracotta", "minecraft:light_gray_glazed_terracotta", "minecraft:stone", "minecraft:smooth_stone", "minecraft:stone_bricks", "minecraft:cobblestone", "minecraft:andesite", "minecraft:polished_andesite", "minecraft:tuff", "minecraft:polished_tuff", "minecraft:tuff_bricks", "minecraft:gravel" }),
        new ColorGroup("Braun", 0x603B1F, new String[] { "minecraft:brown_wool", "minecraft:brown_concrete", "minecraft:brown_concrete_powder", "minecraft:brown_terracotta", "minecraft:brown_glazed_terracotta", "minecraft:spruce_planks", "minecraft:dark_oak_planks", "minecraft:dark_oak_wood", "minecraft:spruce_wood", "minecraft:stripped_spruce_wood", "minecraft:stripped_dark_oak_wood", "minecraft:mud_bricks", "minecraft:packed_mud", "minecraft:brown_mushroom_block", "minecraft:soul_soil", "minecraft:coarse_dirt" }),
        new ColorGroup("Dunkelgrau", 0x36393D, new String[] { "minecraft:gray_concrete", "minecraft:gray_wool", "minecraft:gray_concrete_powder", "minecraft:gray_terracotta", "minecraft:gray_glazed_terracotta", "minecraft:deepslate", "minecraft:cobbled_deepslate", "minecraft:polished_deepslate", "minecraft:deepslate_bricks", "minecraft:deepslate_tiles", "minecraft:cracked_deepslate_bricks", "minecraft:basalt", "minecraft:polished_basalt", "minecraft:smooth_basalt", "minecraft:tuff" }),
        new ColorGroup("Rosa", 0xD6658F, new String[] { "minecraft:pink_concrete", "minecraft:pink_wool", "minecraft:pink_concrete_powder", "minecraft:pink_terracotta", "minecraft:pink_glazed_terracotta", "minecraft:cherry_planks", "minecraft:stripped_cherry_wood", "minecraft:cherry_wood", "minecraft:magenta_terracotta", "minecraft:brain_coral_block", "minecraft:purpur_block" }),
        new ColorGroup("Dunkelrot", 0x8F2121, new String[] { "minecraft:red_nether_bricks", "minecraft:red_terracotta", "minecraft:red_wool", "minecraft:red_concrete", "minecraft:red_concrete_powder", "minecraft:red_glazed_terracotta", "minecraft:nether_wart_block", "minecraft:nether_bricks", "minecraft:netherrack", "minecraft:crimson_planks", "minecraft:crimson_hyphae", "minecraft:stripped_crimson_hyphae", "minecraft:mangrove_planks", "minecraft:red_mushroom_block" }),
        new ColorGroup("Olivgrün", 0x495B24, new String[] { "minecraft:green_terracotta", "minecraft:green_wool", "minecraft:green_concrete", "minecraft:green_concrete_powder", "minecraft:green_glazed_terracotta", "minecraft:moss_block", "minecraft:mossy_cobblestone", "minecraft:mossy_stone_bricks", "minecraft:dried_kelp_block", "minecraft:melon", "minecraft:pale_moss_block", "minecraft:oak_leaves", "minecraft:azalea_leaves", "minecraft:lime_terracotta" }),
        new ColorGroup("Magenta", 0xA9309F, new String[] { "minecraft:magenta_concrete", "minecraft:magenta_wool", "minecraft:magenta_concrete_powder", "minecraft:magenta_terracotta", "minecraft:magenta_glazed_terracotta", "minecraft:purple_concrete", "minecraft:purple_wool", "minecraft:purple_concrete_powder", "minecraft:purple_terracotta", "minecraft:pink_terracotta", "minecraft:purpur_block", "minecraft:brain_coral_block" }),
        new ColorGroup("Dunkelblau", 0x2C2E8F, new String[] { "minecraft:blue_concrete", "minecraft:blue_wool", "minecraft:blue_concrete_powder", "minecraft:blue_terracotta", "minecraft:blue_glazed_terracotta", "minecraft:lapis_block", "minecraft:purple_terracotta", "minecraft:purple_concrete", "minecraft:dark_prismarine", "minecraft:sculk", "minecraft:warped_wart_block", "minecraft:black_glazed_terracotta" }),
        new ColorGroup("Violett", 0x64209C, new String[] { "minecraft:purple_concrete", "minecraft:purple_wool", "minecraft:purple_concrete_powder", "minecraft:purple_terracotta", "minecraft:purple_glazed_terracotta", "minecraft:purpur_block", "minecraft:purpur_pillar", "minecraft:magenta_concrete", "minecraft:magenta_terracotta", "minecraft:crying_obsidian", "minecraft:amethyst_block", "minecraft:budding_amethyst" }),
        new ColorGroup("Hellblau", 0x2389C7, new String[] { "minecraft:light_blue_concrete", "minecraft:light_blue_wool", "minecraft:light_blue_concrete_powder", "minecraft:light_blue_terracotta", "minecraft:light_blue_glazed_terracotta", "minecraft:cyan_concrete", "minecraft:cyan_wool", "minecraft:cyan_concrete_powder", "minecraft:blue_ice", "minecraft:packed_ice", "minecraft:ice", "minecraft:diamond_block", "minecraft:prismarine" }),
        new ColorGroup("Hellgrün", 0x5EA818, new String[] { "minecraft:lime_concrete", "minecraft:lime_wool", "minecraft:lime_concrete_powder", "minecraft:lime_terracotta", "minecraft:lime_glazed_terracotta", "minecraft:green_concrete", "minecraft:green_concrete_powder", "minecraft:green_wool", "minecraft:moss_block", "minecraft:emerald_block", "minecraft:slime_block", "minecraft:green_glazed_terracotta", "minecraft:melon" }),
        new ColorGroup("Gelb", 0xF1AF15, new String[] { "minecraft:yellow_concrete", "minecraft:yellow_wool", "minecraft:yellow_concrete_powder", "minecraft:yellow_terracotta", "minecraft:yellow_glazed_terracotta", "minecraft:gold_block", "minecraft:raw_gold_block", "minecraft:honeycomb_block", "minecraft:hay_block", "minecraft:end_stone", "minecraft:end_stone_bricks", "minecraft:ochre_froglight", "minecraft:sponge" }),
        new ColorGroup("Orange", 0xE06101, new String[] { "minecraft:orange_concrete", "minecraft:orange_wool", "minecraft:orange_concrete_powder", "minecraft:orange_terracotta", "minecraft:orange_glazed_terracotta", "minecraft:red_sandstone", "minecraft:smooth_red_sandstone", "minecraft:cut_red_sandstone", "minecraft:acacia_planks", "minecraft:stripped_acacia_wood", "minecraft:pumpkin", "minecraft:honeycomb_block", "minecraft:magma_block", "minecraft:copper_block", "minecraft:waxed_copper_block" }),
        new ColorGroup("Türkis", 0x157788, new String[] { "minecraft:cyan_concrete", "minecraft:cyan_wool", "minecraft:cyan_concrete_powder", "minecraft:cyan_terracotta", "minecraft:cyan_glazed_terracotta", "minecraft:warped_planks", "minecraft:warped_wood", "minecraft:stripped_warped_wood", "minecraft:warped_wart_block", "minecraft:prismarine", "minecraft:prismarine_bricks", "minecraft:dark_prismarine", "minecraft:oxidized_copper", "minecraft:waxed_oxidized_copper", "minecraft:diamond_block" }),
    };

    private Palette() { }

    public static Block block(String blockId) {
        return BuiltInRegistries.BLOCK.getValue(ResourceLocation.parse(blockId));
    }

    /** Input may omit the minecraft: namespace; reject invalid/nonexistent blocks. */
    public static String validBlockId(String entered) {
        if (entered == null || entered.isBlank()) return null;
        try {
            ResourceLocation id = ResourceLocation.parse(entered.trim().toLowerCase(java.util.Locale.ROOT));
            if (!BuiltInRegistries.BLOCK.containsKey(id)) return null;
            String key = id.toString();
            if (key.equals("minecraft:air") || key.equals("minecraft:cave_air")
                || key.equals("minecraft:void_air")) return null;
            return key;
        } catch (RuntimeException e) {
            return null;
        }
    }

    /** A block is accepted if in the built-in OR user's own list for this color. */
    public static boolean accepts(int index, BlockState state) {
        if (index < 0 || index >= COLORS.length) return false;
        String placedId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        for (String id : COLORS[index].suggestions()) {
            if (id.equals(placedId)) return true;
        }
        Preferences prefs = PixelGuideClient.preferences;
        return prefs != null && prefs.isCustomCandidate(index, placedId);
    }
}
