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

    public int createEnergy(int toAdd)
    {
    	int clampedToAdd = Math.max(0, Math.min(toAdd, this.capacity-this.energy));
    	this.energy += clampedToAdd;
    	return clampedToAdd;
    }

    public int setEnergy(int newAmount)
    {
    	int clampedNewAmount = Math.max(0, Math.min(newAmount, this.capacity));
    	this.energy = clampedNewAmount;
    	return clampedNewAmount;
    }
}
