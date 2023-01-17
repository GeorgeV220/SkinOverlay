package com.georgev22.skinoverlay.mixin.sponge;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SynchedEntityData.class)
public abstract class SynchedEntityData_Mixin {

    @Shadow
    private boolean isDirty;

    @Shadow
    protected abstract <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> dataWatcherObject);

    public <T> void markDirty(EntityDataAccessor<T> dataWatcherObject) {
        getItem(dataWatcherObject).setDirty(true);
        isDirty = true;
    }

}
