package com.quarrel.glasspole.block.entity;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.quarrel.glasspole.EnergyStoragePlus;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class SparkChamberGenBlockEntity extends BlockEntity {

    private static final int SPARK_ENERGY = 2500;
	private static final int POWERGEN_CAPACITY = SPARK_ENERGY * 10;
    private static final int POWERGEN_RECEIVE = 0;
    private static final int POWERGEN_MAXGEN = POWERGEN_CAPACITY;
    private static final int POWERGEN_SEND = POWERGEN_CAPACITY;
    
    private final EnergyStoragePlus energyStorage =
    		new EnergyStoragePlus(POWERGEN_CAPACITY, POWERGEN_RECEIVE, POWERGEN_SEND);
    private final LazyOptional<IEnergyStorage> energyLazy = LazyOptional.of(() -> energyStorage);

    public SparkChamberGenBlockEntity(BlockPos pPos, BlockState pState) {
		super(ModBlockEntities.SPARK_CHAMBER_GEN_BLOCK_ENTITY.get(), pPos, pState);
	}
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
      if (cap == CapabilityEnergy.ENERGY) {
        return energyLazy.cast();
      }
      return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        energyLazy.invalidate();
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("energy", energyStorage.serializeNBT());
    }

    @Override
    public void load(CompoundTag nbt) {
        energyStorage.deserializeNBT(nbt.get("energy"));
        super.load(nbt);
    }

    public void doRandomTick() {
    	energyStorage.createEnergy(SPARK_ENERGY);
	}

    public void tickServer(Level level, BlockPos pos, BlockState state, SparkChamberGenBlockEntity be) {
        sendOutPower();
    }

    private void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        if (capacity.get() > 0) {
            for (Direction direction : Direction.values()) {
                BlockEntity be = this.level.getBlockEntity(this.worldPosition.relative(direction));
                if (be == null) {
                    continue;
                }
                be.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).ifPresent(otherStorage -> {
                    if (be != this && otherStorage.canReceive()) {
                        int canSend = energyStorage.extractEnergy(POWERGEN_SEND, true);
                        int didSend = otherStorage.receiveEnergy(canSend, false);
                        energyStorage.extractEnergy(didSend, false);
                    }
                });
            }
        }
    }
    
}
