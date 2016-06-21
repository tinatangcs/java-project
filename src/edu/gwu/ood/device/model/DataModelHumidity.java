package edu.gwu.ood.device.model;

import edu.gwu.ood.device.DeviceProvider;

public class DataModelHumidity extends DataModel{

	public DataModelHumidity(String modelName, DeviceProvider provider){
		this.modelName = modelName;
		this.unit = "%";
		this.bestValue = 40;
		this.data = this.bestValue;
		this.minSafeValue = 30;
		this.maxSafeValue = 60;
		this.vibeRate = 0.6;
		this.howManyPercentageToAdd = 0.7;
		
		this.provider = provider;
	}

}
