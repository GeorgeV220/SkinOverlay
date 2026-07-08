package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.providers.*;
import com.georgev22.skinoverlay.refreshers.*;
import com.georgev22.skinoverlay.utilities.BukkitMinecraftUtils.MinecraftVersion;

import java.util.List;

public final class VersionRegistry {

    private VersionRegistry() {
    }

    public static final List<VersionRange> RANGES = List.of(

            new VersionRange(
                    new MinecraftVersion(1, 8, 0),
                    new MinecraftVersion(1, 16, 5),
                    BukkitLegacyGameProfileProvider::new,
                    LegacySkinRefresher::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 17, 0),
                    new MinecraftVersion(1, 17, 1),
                    GameProfileProvider_1_17::new,
                    SkinRefresher_1_17::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 18, 0),
                    new MinecraftVersion(1, 18, 1),
                    GameProfileProvider_1_18::new,
                    SkinRefresher_1_18::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 18, 2),
                    GameProfileProvider_1_18_2::new,
                    SkinRefresher_1_18_2::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 19, 0),
                    new MinecraftVersion(1, 19, 2),
                    GameProfileProvider_1_19::new,
                    SkinRefresher_1_19::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 19, 3),
                    GameProfileProvider_1_19_3::new,
                    SkinRefresher_1_19_3::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 19, 4),
                    GameProfileProvider_1_19_4::new,
                    SkinRefresher_1_19_4::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 20, 0),
                    new MinecraftVersion(1, 20, 1),
                    GameProfileProvider_1_20::new,
                    SkinRefresher_1_20::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 20, 2),
                    GameProfileProvider_1_20_2::new,
                    SkinRefresher_1_20_2::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 20, 3),
                    new MinecraftVersion(1, 20, 4),
                    GameProfileProvider_1_20_3::new,
                    SkinRefresher_1_20_3::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 20, 5),
                    new MinecraftVersion(1, 20, 6),
                    GameProfileProvider_1_20_5::new,
                    SkinRefresher_1_20_5::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 21, 0),
                    new MinecraftVersion(1, 21, 1),
                    GameProfileProvider_1_21::new,
                    SkinRefresher_1_21::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 21, 2),
                    new MinecraftVersion(1, 21, 3),
                    GameProfileProvider_1_21_2::new,
                    SkinRefresher_1_21_2::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 21, 4),
                    GameProfileProvider_1_21_4::new,
                    SkinRefresher_1_21_4::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 21, 5),
                    GameProfileProvider_1_21_5::new,
                    SkinRefresher_1_21_5::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 21, 6),
                    new MinecraftVersion(1, 21, 8),
                    GameProfileProvider_1_21_6::new,
                    SkinRefresher_1_21_6::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 21, 9),
                    new MinecraftVersion(1, 21, 10),
                    GameProfileProvider_1_21_9::new,
                    SkinRefresher_1_21_9::new
            ),

            new VersionRange(
                    new MinecraftVersion(1, 21, 11),
                    GameProfileProvider_1_21_11::new,
                    SkinRefresher_1_21_11::new
            ),

            new VersionRange(
                    new MinecraftVersion(26, 1, 0),
                    new MinecraftVersion(26, 2, 99),
                    GameProfileProvider_26_1::new,
                    SkinRefresher_26_1::new
            )
    );
}