package com.quarrel.glasspole.block.entity;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.quarrel.glasspole.CommonConfigs;
import com.quarrel.glasspole.EnergyStoragePlus;
import com.quarrel.glasspole.menu.GreebleGenMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class GreebleGenBlockEntity extends BlockEntity implements MenuProvider {

	private static final int POWERGEN_CAPACITY = 20000;
    private static final int POWERGEN_RECEIVE = 0;
    private static final int POWERGEN_MAXGEN = CommonConfigs.PEAK_GREEBLE_GEN_RATE.get();
    private static final int POWERGEN_SEND = 2 * POWERGEN_MAXGEN;
    private static final int FOOD_SLOT = 0;
    private static final int HUNGERSPAN = 100;	// ticks between fullness levels falling
    private static final int EATSPAN = 10;		// ticks between attempts to eat more
    private int tickCount = 0;
    public int nutLevel = 0;
    public float satLevel = 0.0f;
    
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

    public GreebleGenBlockEntity(BlockPos pPos, BlockState pState) {
		super(ModBlockEntities.GREEBLE_GEN_BLOCK_ENTITY.get(), pPos, pState);
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
        return new TextComponent("Greeble Generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new GreebleGenMenu(pContainerId, pInventory, this);
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
        infoTag.putInt("nutrition", nutLevel);
        infoTag.putFloat("saturation", satLevel);
        nbt.put("Info", infoTag);
    }

    @Override
    public void load(CompoundTag nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyStorage.deserializeNBT(nbt.get("energy"));
        nutLevel = nbt.getCompound("Info").getInt("nutrition");
        satLevel = nbt.getCompound("Info").getFloat("saturation");
        super.load(nbt);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void tickServer(Level level, BlockPos pos, BlockState state, GreebleGenBlockEntity be) {
		if (tickCount++ % EATSPAN == 0) {
			ItemStack food = itemHandler.getStackInSlot(FOOD_SLOT);
			// FoodProperties foodProps = food.getItem().getFoodProperties();
			FoodProperties foodProps = food.getItem().getFoodProperties(food, null);
	        if (food != null & foodProps != null) {
		        int foodNut = foodProps.getNutrition();
		        float foodSat = (float)foodNut * 2.0f * Math.min(1.5f, foodProps.getSaturationModifier());
		        if ((nutLevel + foodNut <= 100) && (satLevel + foodSat <= 100.0f)) {
		        	nutLevel += foodNut;
		        	satLevel += foodSat;
		        	itemHandler.extractItem(FOOD_SLOT, 1, false);
					setChanged();
		        }
	        }
		}
		energyStorage.createEnergy((Math.min(nutLevel, (int)(satLevel + 0.5f)) * POWERGEN_MAXGEN + 50) / 100);
		sendOutPower();
		if (tickCount >= HUNGERSPAN) {
			tickCount -= HUNGERSPAN;
			if (nutLevel > 0)		--nutLevel;
			if (satLevel > 0.0f)	satLevel = Math.max(satLevel - 1.0f, 0.0f);
			setChanged();
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
                        int canSend = Math.min(stored.get(), GreebleGenBlockEntity.POWERGEN_SEND);
                        int didSend = otherStorage.receiveEnergy(canSend, false);
                        energyStorage.extractEnergy(didSend, false);
                        stored.addAndGet(-didSend);
                    }
                });
            	if (stored.get() <= 0) {
            		break;
            	}
        	}
        }
    }
    
}
