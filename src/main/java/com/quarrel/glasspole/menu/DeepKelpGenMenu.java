package com.quarrel.glasspole.menu;

import com.quarrel.glasspole.EnergyStoragePlus;
import com.quarrel.glasspole.block.ModBlocks;
import com.quarrel.glasspole.block.entity.DeepKelpGenBlockEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;

public class DeepKelpGenMenu extends AbstractContainerMenu {

	private final DeepKelpGenBlockEntity ggbe;
	private final Level level;
	
	public DeepKelpGenMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
		this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
	}

	public DeepKelpGenMenu(int pContainerId, Inventory inv, BlockEntity be) {
		super(ModMenuTypes.DEEPKELP_GENERATOR_MENU.get(), pContainerId);
		checkContainerSize(inv, 1);
		this.ggbe = (DeepKelpGenBlockEntity)be;
		this.level = inv.player.level;
		this.ggbe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
			addSlot(new DriedKelpSlot(handler, 0, 80, 35));
		});
		addPlayerInventory(inv);
		addPlayerHotbar(inv);
		addDataSlot(new DataSlot() {		// stored energy
			@Override
			public int get() {
		        return getEnergy();
		    }
			@Override
			public void set(int val) {
                ggbe.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    ((EnergyStoragePlus)h).setEnergy(val);
                });
			}
		});
	}

    public int getEnergy() {
        return ggbe.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }
	
    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, ggbe.getBlockPos()),
                pPlayer, ModBlocks.DEEPKELP_GEN_BLOCK.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 144));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int fromIndex) {
        ItemStack remainingStack = ItemStack.EMPTY;
        Slot fromSlot = this.slots.get(fromIndex);
        if (fromSlot != null && fromSlot.hasItem()) {
            ItemStack sourceStack = fromSlot.getItem();
            remainingStack = sourceStack.copy();
            if (fromIndex == 0) {
                if (!this.moveItemStackTo(sourceStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                fromSlot.onQuickCraft(sourceStack, remainingStack);
            } else {
                if (!this.moveItemStackTo(sourceStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                } else if (fromIndex < 28) {
                    if (!this.moveItemStackTo(sourceStack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (fromIndex < 37 && !this.moveItemStackTo(sourceStack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (sourceStack.isEmpty()) {
                fromSlot.set(ItemStack.EMPTY);
            } else {
                fromSlot.setChanged();
            }

            if (sourceStack.getCount() == remainingStack.getCount()) {
                return ItemStack.EMPTY;
            }

            fromSlot.onTake(playerIn, sourceStack);
        }

        return remainingStack;
    }
}
