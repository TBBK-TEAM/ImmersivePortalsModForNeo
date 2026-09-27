package qouteall.imm_ptl.peripheral;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Maps every built-in command stick variant to a dedicated item model / texture.
 * <p>
 * A command stick is a single registered item ({@code immersive_portals:command_stick}) whose
 * variants are distinguished only by the {@code iportal:command_stick_data} component, so they all
 * used to share one texture. This table gives each variant its own icon.
 * <p>
 * The index inside {@link #ENTRIES} is what
 * {@link CommandStickItem#getModelIndex(net.minecraft.world.item.ItemStack)} reports through the
 * {@code immersive_portals:command_stick_model} item property. It must stay in sync with the
 * {@code overrides} array of
 * {@code assets/immersive_portals/models/item/command_stick.json}, which is ordered the same way:
 * a variant resolves to the last override whose threshold is {@code <=} the reported value
 * (the same ascending-threshold scheme vanilla uses for the compass).
 * <p>
 * The model json files are generated from this table by
 * {@code misc/generate_command_stick_models.ps1}, which also verifies that every referenced texture
 * exists in the vanilla client jar. Re-run it after editing this list.
 * <p>
 * Only textures from the {@code minecraft} namespace are referenced, so no third-party or Mojang
 * asset files are redistributed by this mod. Keep it that way when adding entries.
 */
public final class CommandStickModelMapping {
    private CommandStickModelMapping() {
    }

    /**
     * @param command the exact command stored in the {@code command_stick_data} component
     * @param texture the {@code layer0} texture of the generated model, e.g. {@code minecraft:item/barrier}
     */
    public record Entry(String command, String texture) {
    }

    /**
     * Order defines the model index. Append new entries at the end (and re-run the generator)
     * instead of reordering, otherwise existing indices silently point at the wrong texture.
     */
    public static final List<Entry> ENTRIES = List.of(
        // destroying portals
        new Entry("/portal delete_portal", "minecraft:item/barrier"),
        new Entry("/portal remove_connected_portals", "minecraft:item/shears"),
        new Entry("/portal eradicate_portal_cluster", "minecraft:item/fire_charge"),
        new Entry("/portal complete_bi_way_bi_faced_portal", "minecraft:item/ender_eye"),
        new Entry("/portal complete_bi_way_portal", "minecraft:item/ender_pearl"),

        // moving the portal itself
        new Entry("/portal move_portal 0.5", "minecraft:item/arrow"),
        new Entry("/portal move_portal -0.5", "minecraft:item/spectral_arrow"),
        new Entry("/portal move_portal -0.001", "minecraft:item/tipped_arrow_base"),
        new Entry("/portal move_portal_destination 0.5", "minecraft:item/firework_rocket"),
        new Entry("/portal move_portal_destination -0.5", "minecraft:item/wind_charge"),

        // rotating
        new Entry("/portal rotate_portal_rotation_along x 15", "minecraft:item/compass_16"),
        new Entry("/portal rotate_portal_rotation_along y 15", "minecraft:item/compass_08"),
        new Entry("/portal rotate_portal_rotation_along z 15", "minecraft:item/compass_24"),

        // nbt flags
        new Entry("/portal nbt {unbreakable:true}", "minecraft:item/netherite_ingot"),
        new Entry("/portal nbt {fuseView:true}", "minecraft:item/spyglass"),
        new Entry("/portal nbt {adjustPositionAfterTeleport:true}", "minecraft:item/recovery_compass_00"),
        new Entry("/portal nbt {doRenderPlayer:false}", "minecraft:item/armor_stand"),
        new Entry("/portal nbt {teleportChangesGravity:true}", "minecraft:item/rabbit_foot"),
        new Entry("/portal nbt {isVisible:false}", "minecraft:item/structure_void"),
        new Entry("/portal nbt {isVisible:true}", "minecraft:item/light"),
        new Entry("/portal nbt {defaultAnimation:{durationTicks:0}}", "minecraft:item/clock_00"),

        // debug / isometric / rooms
        new Entry("/portal debug isometric_enable 50", "minecraft:item/painting"),
        new Entry("/portal debug isometric_disable", "minecraft:item/item_frame"),
        new Entry("/portal create_connected_rooms roomSize 6 4 6 roomNumber 5", "minecraft:item/oak_door"),
        new Entry("/portal debug accelerate 50", "minecraft:item/sugar"),
        new Entry("/portal debug accelerate 200", "minecraft:item/honey_bottle"),
        new Entry("/portal debug accelerate -50", "minecraft:item/fermented_spider_eye"),

        // animation
        new Entry("/portal animation pause", "minecraft:item/music_disc_stal"),
        new Entry("/portal animation resume", "minecraft:item/music_disc_cat"),
        new Entry("/portal animation rotate_infinitely @s 0 1 0 1.0", "minecraft:item/compass_00"),
        new Entry("/portal animation rotate_infinitely_random", "minecraft:item/compass_31"),
        new Entry(
            "execute positioned 0.0 0.0 0.0 run portal animation rotate_infinitely @p ^0.0 ^0.0 ^1.0 1.7",
            "minecraft:item/recovery_compass_16"
        ),
        new Entry("/portal animation expand_from_center 20", "minecraft:item/slime_ball"),
        new Entry("/portal animation clear", "minecraft:item/glass_bottle"),

        // shape
        new Entry("/portal shape sculpt", "minecraft:item/brush"),
        new Entry("/portal shape reset", "minecraft:item/feather"),

        // player utilities
        new Entry("/attribute @s minecraft:generic.scale modifier remove iportal:scaling", "minecraft:item/bundle"),
        new Entry("/attribute @s minecraft:player.block_interaction_range base set 100", "minecraft:item/fishing_rod"),
        new Entry("/effect give @s minecraft:night_vision 9999 1 true", "minecraft:item/golden_carrot"),
        new Entry("/setblock ~ ~ ~ minecraft:grass_block", "minecraft:item/bone_meal"),

        // misc
        new Entry("/portal goback", "minecraft:item/filled_map"),
        new Entry("/portal wiki", "minecraft:item/knowledge_book")
    );

    /**
     * @return the 1-based model index for a command, or {@code 0} when the command has no dedicated
     * model. 0 never matches an override because the smallest threshold is 1, so unmapped variants
     * keep the default command stick texture.
     */
    public static int getModelIndex(@Nullable String command) {
        if (command == null) {
            return 0;
        }
        for (int i = 0; i < ENTRIES.size(); i++) {
            if (ENTRIES.get(i).command().equals(command)) {
                return i + 1;
            }
        }
        return 0;
    }
}
