package com.forgeessentials.core.preloader.mixin.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fe.event.world.BlockDestroyedByFireEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFire.class)
public abstract class MixinBlockFire_01 extends Block
{
    // dummy constructor, leave me alone
    public MixinBlockFire_01(Material material)
    {
        super(material);
    }

    @Inject(method = "Lnet/minecraft/block/BlockFire;tryCatchFire(Lnet/minecraft/world/World;IIIILjava/util/Random;ILnet/minecraftforge/common/util/ForgeDirection;)V", at = @At("HEAD"), remap = false)
    public void handleTryCatchFire(World world, int x, int y, int z, int p_149841_5_, Random p_149841_6_, int p_149841_7_, ForgeDirection face, CallbackInfo ci)
    {
        if (MinecraftForge.EVENT_BUS.post(new BlockDestroyedByFireEvent(x, y, z, world, this,p_149841_7_ )))
        {
            ci.cancel();
        }
    }
}
