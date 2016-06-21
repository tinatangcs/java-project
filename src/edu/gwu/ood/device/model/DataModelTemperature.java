package edu.gwu.ood.device.model;

import edu.gwu.ood.device.DeviceProvider;

public class DataModelTemperature extends DataModel {

	public DataModelTemperature(String modelName, DeviceProvider provider){
		
		this.modelName = modelName;
		this.unit = "F";
		this.bestValue = 72;
		this.data = this.bestValue;
		this.minSafeValue = 35;
		this.maxSafeValue = 80;
		this.vibeRate = 0.6;
		this.howManyPercentageToAdd = 0.7;
		
		this.provider = provider;
	}


}
