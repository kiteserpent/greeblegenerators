package net.quarrel.greeblegens.menu;

import net.quarrel.greeblegens.EnergyStoragePlus;
import net.quarrel.greeblegens.block.ModBlocks;
import net.quarrel.greeblegens.block.entity.DeepKelpGenBlockEntity;

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

	private final DeepKelpGenBlockEntity dkbe;
	private final Level level;
	
	public DeepKelpGenMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
		this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
	}

	public DeepKelpGenMenu(int pContainerId, Inventory inv, BlockEntity be) {
		super(ModMenuTypes.DEEPKELP_GENERATOR_MENU.get(), pContainerId);
		checkContainerSize(inv, 1);
		this.dkbe = (DeepKelpGenBlockEntity)be;
		this.level = inv.player.level;
		this.dkbe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
			addSlot(new DriedKelpSlot(handler, 0, 64, 35));
		});
		addPlayerInventory(inv);
		addPlayerHotbar(inv);
		addDataSlot(new DataSlot() {
			@Override
			public int get() {
				return getDeepEnough() ? 1 : 0;
			}
			@Override
			public void set(int val) {
				dkbe.deepEnough = ( val == 1 );
			}
		});
		addDataSlot(new DataSlot() {
			@Override
			public int get() {
				return getTickCount();
			}
			@Override
			public void set(int val) {
				dkbe.tickCount = val;
			}
		});
		addDataSlot(new DataSlot() {
			@Override
			public int get() {
				return getCurrentRate();
			}
			@Override
			public void set(int val) {
				dkbe.currentRate = val;
			}
		});
		addDataSlot(new DataSlot() {
			@Override
			public int get() {
				return getFullBurnTicks();
			}
			@Override
			public void set(int val) {
				dkbe.fullBurnTicks = val;
			}
		});
		addDataSlot(new DataSlot() {		// stored energy
			@Override
			public int get() {
		        return getEnergy();
		    }
			@Override
			public void set(int val) {
                dkbe.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    ((EnergyStoragePlus)h).setEnergy(val);
                });
			}
		});
	}

	public boolean getDeepEnough() {
    	return dkbe.isDeepEnough();
    }

	public int getTickCount() {
    	return dkbe.tickCount;
    }

	public int getCurrentRate() {
    	return dkbe.currentRate;
    }

	public int getFullBurnTicks() {
    	return dkbe.fullBurnTicks;
    }

    public int getEnergy() {
        return dkbe.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }
	
    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, dkbe.getBlockPos()),
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
