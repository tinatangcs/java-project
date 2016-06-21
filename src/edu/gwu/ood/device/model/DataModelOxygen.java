package edu.gwu.ood.device.model;

import edu.gwu.ood.device.DeviceProvider;

public class DataModelOxygen extends DataModel {

	public DataModelOxygen(String modelName, DeviceProvider provider){
		this.modelName = modelName;
		this.unit = "%";
		this.bestValue = 21;
		this.data = this.bestValue;
		this.minSafeValue = 8;
		this.maxSafeValue = 60;
		this.vibeRate = 0.13;
		this.howManyPercentageToAdd = 0.6;
		
		this.provider = provider;
	}
}
