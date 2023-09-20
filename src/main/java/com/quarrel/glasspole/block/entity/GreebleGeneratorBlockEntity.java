package com.quarrel.glasspole.block.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.quarrel.glasspole.EnergyStoragePlus;
import com.quarrel.glasspole.GreebleGeneratorMenu;

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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class GreebleGeneratorBlockEntity extends BlockEntity implements BlockEntityTicker<GreebleGeneratorBlockEntity>, MenuProvider {

	private static final int POWERGEN_CAPACITY = 10000;
    private static final int POWERGEN_RECEIVE = 0;
    private static final int POWERGEN_SEND = 100;
    private static final int GREEBLE_SLOT = 0;
    private static final int FOOD_SLOT = 1;
    
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
    	@Override
    	protected void onContentsChanged(int slot) {
    		setChanged();
    	}
    };
    
    private LazyOptional<IItemHandler> itemsLazy = LazyOptional.empty();
    
    private final EnergyStoragePlus energyStorage =
    		new EnergyStoragePlus(POWERGEN_CAPACITY, POWERGEN_RECEIVE, POWERGEN_SEND);
    private final LazyOptional<IEnergyStorage> energyLazy = LazyOptional.of(() -> energyStorage);

    public GreebleGeneratorBlockEntity(BlockPos pPos, BlockState pState) {
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
        return new GreebleGeneratorMenu(pContainerId, pInventory, this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        itemsLazy = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        itemsLazy.invalidate();
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

	public void tick() {
		// TODO Auto-generated method stub
	}

	@Override
    public void tick(Level level, BlockPos pos, BlockState state, GreebleGeneratorBlockEntity be) {
        be.tick();
    }

}
