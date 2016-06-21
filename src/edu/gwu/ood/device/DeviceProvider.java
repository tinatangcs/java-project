package edu.gwu.ood.device;

import java.util.List;
import java.util.Map;

import edu.gwu.ood.device.model.DataModel;

/**
 * abstract class for all Providers.
 *
 */
public abstract class DeviceProvider {
	/**
	 * the time interval for update the data in SensorProvider
	 */
	public static int REFRESH_RATE = 1000;
	/**
	 * the time interval for update CAMERA view
	 */
	public static int CAMERA_REFRESH_RATE = 100;

	/**
	 * Map for DataModel.
	 * ModelName => DataModel
	 */
	protected Map<String,DataModel> models;
	/**
	 * The list for storing the DataModel names in order.
	 */
	protected List<String> orderByName;
	
	public List<String> getDataModelNames(){
		return orderByName;
	}
	
	public DataModel getDataModel(String modelName){
		if(models!=null)
			return models.get(modelName);
		return null;
	}

	/**
	 * Provider can use a specific DataModel to carry out a specific Action with Parameters
	 * @param modelName
	 * @param cmd
	 * @param paras
	 * @return
	 */
	public abstract boolean takeAction (String modelName, int cmd,String...paras);
	
	public abstract boolean destory ();

	public DataModel getLastModel() {
		if(orderByName!=null && orderByName.size()>0){
			return this.models.get(this.orderByName.get(this.orderByName.size()-1));
		}
		return null;
	}
	
}
