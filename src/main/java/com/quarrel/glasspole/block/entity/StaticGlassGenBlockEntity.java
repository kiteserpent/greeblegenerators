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


public class StaticGlassGenBlockEntity extends BlockEntity implements BlockEntityTicker<StaticGlassGenBlockEntity> {

	private static final int POWERGEN_CAPACITY = 10;
    private static final int POWERGEN_RECEIVE = 0;
    private static final int POWERGEN_SEND = 1;

    private final EnergyStoragePlus energyStorage =
    		new EnergyStoragePlus(POWERGEN_CAPACITY, POWERGEN_RECEIVE, POWERGEN_SEND);
    private final LazyOptional<IEnergyStorage> energyLazy = LazyOptional.of(() -> energyStorage);


    public StaticGlassGenBlockEntity(BlockPos pPos, BlockState pState) {
		super(ModBlockEntities.STATIC_GLASS_GEN_BLOCK_ENTITY.get(), pPos, pState);
		// TODO Auto-generated constructor stub
	}
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
      if (cap == CapabilityEnergy.ENERGY) {
        return energyLazy.cast();
      }
      return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
      super.invalidateCaps();
      energyLazy.invalidate();
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("Energy")) {
            energyStorage.deserializeNBT(tag.get("Energy"));
        }
        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("Energy", energyStorage.serializeNBT());
    }
    

	public void tick() {
        sendOutPower();
		// TODO Auto-generated method stub
	}

	@Override
    public void tick(Level level, BlockPos pos, BlockState state, StaticGlassGenBlockEntity be) {
        be.tick();
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
                        setChanged();
                    }
                });
/*
 * 		McJty's code
                BlockEntity be = level.getBlockEntity(worldPosition.relative(direction));
                if (be != null) {
                    boolean doContinue = be.getCapability(CapabilityEnergy.ENERGY, direction);
                    		be.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).map(handler -> {
                                if (handler.canReceive()) {
                                    int received = handler.receiveEnergy(Math.min(capacity.get(), POWERGEN_SEND), false);
                                    capacity.addAndGet(-received);
                                    energyStorage.(received, false);
                                    setChanged();
                                    return capacity.get() > 0;
                                } else {
                                    return true;
                                }
                            }
                    ).orElse(true);
                    if (!doContinue) {
                        return;
                    }
                }
*/
            }
        }
    }

	public void rub() {
		if (energyStorage.createEnergy(1) > 0) {
			setChanged();
		}
	}

}