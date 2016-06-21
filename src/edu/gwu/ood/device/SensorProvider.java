package edu.gwu.ood.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.gwu.ood.device.model.DataModel;
import edu.gwu.ood.device.model.DataModelHumidity;
import edu.gwu.ood.device.model.DataModelOxygen;
import edu.gwu.ood.device.model.DataModelTemperature;

/**
 * The Provider for all sensor data: temperature, oxygen and so on.
 *
 */
public class SensorProvider extends DeviceProvider implements Runnable{
	/**
	 * flag for the thread of running update the sensor data.
	 */
	protected boolean running = true;
	
	public SensorProvider(){
		models = new HashMap<String,DataModel>();
		orderByName = new ArrayList<String>();
		
		for(DataModel model : generateModel()){
			models.put(model.getModelName(), model);
			orderByName.add(model.getModelName());
		}
		
		//start running
		new Thread(this).start();
	}
	
	/**
	 * mock function: generate all DataModel(s)
	 * @return
	 */
	protected List<DataModel> generateModel(){
		List<DataModel> list = new ArrayList<DataModel>();
		
		DataModel outTemperature = new DataModelTemperature("Outer Temperature", this);
		outTemperature.setShouldAlarm(false);
		outTemperature.setMaxSafeValue(200);
		outTemperature.setHowManyPercentageToAdd(0.5);
		outTemperature.setVibeRate(2.42);
		list.add(outTemperature);
		
		list.add(new DataModelTemperature("Inner Temperature", this));
		list.add(new DataModelHumidity("Humidity", this));
		list.add(new DataModelOxygen("Oxygen", this));
		
		return list;
	}

	@Override
	public boolean takeAction(String modelName, int cmd,	String...paras) {
		DataModel model = this.models.get(modelName);
		if(model==null){
			return false;
		}
		
		switch (cmd) {
		case (DataModel.CMD_REPAIR):
			model.setToBestValue();
		
			break;
		case (DataModel.CMD_DEBUG_SET_VALUE):
			model.updateValue(Double.parseDouble(paras[0]));
			
			break;
		default:
			return false;
		}
			
		return true;
	}

	@Override
	public boolean destory() {
		this.running = false;
		return false;
	}

	@Override
	public void run() {
		
		while(this.running){
			
			for(DataModel model: models.values()){
				model.updateValue();
			}
			
			try {
				Thread.sleep(DeviceProvider.REFRESH_RATE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
