package com.quarrel.glasspole.block.entity;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.quarrel.glasspole.CommonConfigs;
import com.quarrel.glasspole.EnergyStoragePlus;
import com.quarrel.glasspole.block.ModBlocks;
import com.quarrel.glasspole.menu.DeepKelpGenMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class DeepKelpGenBlockEntity extends BlockEntity implements MenuProvider {

	private static final int POWERGEN_CAPACITY = 100000;
    private static final int POWERGEN_RECEIVE = 0;
    private static final int POWERGEN_MAXGEN = CommonConfigs.DEEPKELP_GEN_RATE.get();
    private static final int POWERGEN_LOWGEN = CommonConfigs.DEEPKELP_LOW_GEN_RATE.get();
    private static final int POWERGEN_SEND = 2 * POWERGEN_MAXGEN;
    private static final int KELP_BURN_TIME = CommonConfigs.KELP_BURN_TIME.get();
    private static final int KELP_BLOCK_BURN_TIME = CommonConfigs.KELP_BLOCK_BURN_TIME.get();
    private static final int KELPGEN_MIN_DEPTH = CommonConfigs.KELPGEN_MIN_DEPTH.get();
    private static final int KELP_SLOT = 0;
    public int tickCount = 0;
    public int currentRate = 0;
    public int fullBurnTicks = KELP_BURN_TIME;
    public boolean deepEnough = false;
    private int depthCheckCounter = 0;
    
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
    	@Override
    	protected void onContentsChanged(int slot) {
    		setChanged();
    	}
    };
    private final LazyOptional<IItemHandler> itemsLazy = LazyOptional.of(() -> itemHandler);
    
    private final EnergyStoragePlus energyStorage =
    		new EnergyStoragePlus(POWERGEN_CAPACITY, POWERGEN_RECEIVE, POWERGEN_SEND);
    private final LazyOptional<IEnergyStorage> energyLazy = LazyOptional.of(() -> energyStorage);

    public DeepKelpGenBlockEntity(BlockPos pPos, BlockState pState) {
		super(ModBlockEntities.DEEPKELP_GEN_BLOCK_ENTITY.get(), pPos, pState);
	}
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
      if (cap == CapabilityEnergy.ENERGY) {
        return energyLazy.cast();
      }
      if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
          return itemsLazy.cast();
      }
      return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Deep Kelp Generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new DeepKelpGenMenu(pContainerId, pInventory, this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        itemsLazy.invalidate();
        energyLazy.invalidate();
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.put("energy", energyStorage.serializeNBT());
        CompoundTag infoTag = new CompoundTag();
        infoTag.putInt("currentRate", currentRate);
        infoTag.putInt("tickCount", tickCount);
        infoTag.putInt("fullBurnTicks", fullBurnTicks);
        nbt.put("Scalars", infoTag);
    }
    
    @Override
    public void load(CompoundTag nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyStorage.deserializeNBT(nbt.get("energy"));
        currentRate = nbt.getCompound("Scalars").getInt("currentRate");
        tickCount = nbt.getCompound("Scalars").getInt("tickCount");
        fullBurnTicks = nbt.getCompound("Scalars").getInt("fullBurnTicks");
        super.load(nbt);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void checkDepth() {
    	if ((depthCheckCounter++ % 20) == 0) {	// don't spam this check
            int depth = 0;
            for (BlockPos pos = this.getBlockPos().above();
            		// this.level.getBlockState(pos).is(Blocks.WATER);
            		this.level.getBlockState(pos).getFluidState().is(FluidTags.WATER);
            		pos = pos.above()) {
                depth++;
            }
            setDeepEnough(depth >= KELPGEN_MIN_DEPTH);
    	}
    }
    
    public void tickServer(Level level, BlockPos pos, BlockState state, DeepKelpGenBlockEntity be) {
		if (tickCount++ < fullBurnTicks) {
			energyStorage.createEnergy(currentRate);
		}
		sendOutPower();
		if (tickCount >= fullBurnTicks) {
			checkDepth();
			currentRate = 0;
			tickCount = 0;
			fullBurnTicks = KELP_BURN_TIME;
			ItemStack kelpItem = itemHandler.getStackInSlot(KELP_SLOT);
			if (kelpItem != null &&
					energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored() &&
					isDeepEnough()) {
				BlockState bsUnder = level.getBlockState(pos.below());
				if (bsUnder.is(Blocks.BUBBLE_COLUMN)) {
					currentRate = POWERGEN_LOWGEN;
				} else if (bsUnder.is(ModBlocks.SULFUR_BUBBLE_COLUMN.get())) {
					currentRate = POWERGEN_MAXGEN;
				}
				if (kelpItem.is(Items.DRIED_KELP_BLOCK)) {
					fullBurnTicks = KELP_BLOCK_BURN_TIME;
		        	itemHandler.extractItem(KELP_SLOT, 1, false);
		        	setChanged();
				} else if (kelpItem.is(Items.DRIED_KELP)) { 
		        	itemHandler.extractItem(KELP_SLOT, 1, false);
		        	setChanged();
				} else {	// failsafe in case illegal fuel got into our input
					currentRate = 0;
				}
			}
		}
	}

    private void sendOutPower() {
        AtomicInteger stored = new AtomicInteger(energyStorage.getEnergyStored());
        if (stored.get() > 0) {
            for (Direction direction : Direction.values()) {
                BlockEntity otherBE = this.level.getBlockEntity(this.worldPosition.relative(direction));
                if (otherBE == null) {
                    continue;
                }
                otherBE.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).ifPresent(otherStorage -> {
                    if (otherStorage.canReceive()) {
                        int canSend = Math.min(stored.get(), DeepKelpGenBlockEntity.POWERGEN_SEND);
                        int didSend = otherStorage.receiveEnergy(canSend, false);
                        energyStorage.extractEnergy(didSend, false);
                        if (didSend > 0) {
                        	stored.addAndGet(-didSend);
                        	setChanged();
                        }
                    }
                });
            	if (stored.get() <= 0) {
            		break;
            	}
        	}
        }
    }

	public boolean isDeepEnough() {
		return deepEnough;
	}

	public void setDeepEnough(boolean deepEnough) {
		this.deepEnough = deepEnough;
	}
    
}
