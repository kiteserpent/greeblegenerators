package com.quarrel.glasspole.block.entity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import com.quarrel.glasspole.EnergyStoragePlus;

import java.util.concurrent.atomic.AtomicInteger;


public class StaticGlassGenBlockEntity extends BlockEntity {

	private static final int POWERGEN_CAPACITY = 5;
    private static final int POWERGEN_RECEIVE = 0;
    private static final int POWERGEN_SEND = 1;

    private final EnergyStoragePlus energyStorage =
    		new EnergyStoragePlus(POWERGEN_CAPACITY, POWERGEN_RECEIVE, POWERGEN_SEND);
    private final LazyOptional<IEnergyStorage> energyLazy = LazyOptional.of(() -> energyStorage);


    public StaticGlassGenBlockEntity(BlockPos pPos, BlockState pState) {
		super(ModBlockEntities.STATIC_GLASS_GEN_BLOCK_ENTITY.get(), pPos, pState);
	}
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
      if ((cap == CapabilityEnergy.ENERGY) & (side != Direction.UP)) {
        return energyLazy.cast();
      }
      return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
      super.invalidateCaps();
      energyLazy.invalidate();
    }

    private void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        if (capacity.get() > 0) {
            for (Direction direction : Direction.values()) {
            	if (direction != Direction.UP) {
		            BlockEntity otherBE = this.level.getBlockEntity(this.worldPosition.relative(direction));
		            if (otherBE == null) {
		                continue;
		            }
		            otherBE.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).ifPresent(otherStorage -> {
		                if (otherBE != this && otherStorage.canReceive()) {
		                    otherStorage.receiveEnergy(POWERGEN_SEND, false);
		                }
		            });
            	}
            }
        }
    }

	public void rub() {
		energyStorage.createEnergy(POWERGEN_CAPACITY);
		sendOutPower();
		energyStorage.setEnergy(0);
	}

}