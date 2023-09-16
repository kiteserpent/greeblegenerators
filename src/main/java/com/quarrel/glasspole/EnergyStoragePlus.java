package com.quarrel.glasspole;

import net.minecraftforge.energy.EnergyStorage;

public class EnergyStoragePlus extends EnergyStorage {

    public EnergyStoragePlus(int capacity)
    {
        super(capacity, capacity, capacity, 0);
    }

    public EnergyStoragePlus(int capacity, int maxTransfer)
    {
    	super(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStoragePlus(int capacity, int maxReceive, int maxExtract)
    {
    	super(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStoragePlus(int capacity, int maxReceive, int maxExtract, int energy)
    {
    	super(capacity, maxReceive, maxExtract, energy);
    }

    public int createEnergy(int newEnergy)
    {
    	int amountToAdd = Math.max(0,  Math.min(newEnergy, this.capacity-this.energy));
    	this.energy += amountToAdd;
    	return amountToAdd;
    }
}
