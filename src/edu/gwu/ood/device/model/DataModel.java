package edu.gwu.ood.device.model;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Observable;

import edu.gwu.ood.device.DeviceProvider;

public abstract class DataModel extends Observable{

	/*
	 * action CMD types
	 */
	public static final int CMD_REPAIR = 0x1;
	public static final int CMD_DEBUG_SET_VALUE = 0x2;
	public static final int CMD_CAMERA_UP = 0x11;
	public static final int CMD_CAMERA_DOWN = 0x12;
	public static final int CMD_CAMERA_LEFT = 0x13;
	public static final int CMD_CAMERA_RIGHT = 0x14;
	public static final int CMD_CAMERA_ZOOM_IN = 0x15;
	public static final int CMD_CAMERA_ZOOM_OUT = 0x16;
	public static final int CMD_CAMERA_REMOVE = 0x17;
	public static final int CMD_CAMERA_ADD = 0x18;
	public static final int CMD_CAMERA_ADD_EXTRA = 0x19;
	
	/**
	 * the basic double-valued data for DataModel.
	 * 
	 */
	protected double data = 0;
	
	/**
	 * the name of model
	 */
	protected String modelName;
	
	/**
	 * whether this DataModel should show alarm when value is not proper.
	 */
	protected boolean shouldAlarm = true;
	
	/**
	 * 
	 */
	protected boolean enableAlarm = true;
	
	protected DeviceProvider provider;
	
	/**
	 * the best value for this indicator
	 */
	protected double bestValue = 0;
	/**
	 * the minimum safe value for this indicator.
	 */
	protected double minSafeValue = Double.MIN_VALUE;
	/**
	 * the maximum safe value for this indicator
	 */
	protected double maxSafeValue = Double.MAX_VALUE;
	/**
	 * every time generating a new value, it will randomly plus or minus vibeRate to current data
	 */
	protected double vibeRate = 0.01;
	/**
	 * there is 50%(default) possibility that next second the data will be added by vibeRate
	 */
	protected double howManyPercentageToAdd = 0.5;
	
	/**
	 * double to string
	 */
	protected Format fortmat = new DecimalFormat("0.00");
	/**
	 * units
	 */
	protected String unit = "";
	
	
	
	/**
	 * check the alarming status
	 * @return
	 */
	public boolean isAlarming(){
		if(this.shouldAlarm && this.enableAlarm){
			if(this.data>this.maxSafeValue || this.data<this.minSafeValue)
				return true;
		}
		return false;
	}
	
	/**
	 * send robot to fix, it need provider to do something
	 * @return
	 */
	public boolean sendRobotToFix(){
		return provider.takeAction(modelName, DataModel.CMD_REPAIR);
	}
	
	public String getModelName(){
		return this.modelName;
	}
	
	public double getData(){
		return this.data;
	}
	
	public String getDataString() {
		return this.fortmat.format(this.data);
	}
	/**
	 * generate fake data
	 */
	public void generateFakeData(){
		if(this.isAlarming()){
			//if alarming, stop generating new data
			return;
		}
		if(Math.random()<this.howManyPercentageToAdd)
			this.data += this.vibeRate;
		else
			this.data -= this.vibeRate;
	}
	
	
	public void updateValue(){
		this.generateFakeData();
		this.setChanged();
		this.notifyObservers();
	}
	
	
	
	public void setToBestValue(){
		this.data = this.bestValue;
		this.setChanged();
		this.notifyObservers();
	}


	public String getUnit() {
		return this.unit;
	}


	public double getBestValue() {
		return bestValue;
	}


	public double getMinSafeValue() {
		return minSafeValue;
	}


	public double getMaxSafeValue() {
		return maxSafeValue;
	}


	public void setShouldAlarm(boolean shouldAlarm) {
		this.shouldAlarm = shouldAlarm;
	}

	public boolean isAlarmEnabled(){
		return this.shouldAlarm;
	}

	public void setMinSafeValue(double minSafeValue) {
		this.minSafeValue = minSafeValue;
	}


	public void setMaxSafeValue(double maxSafeValue) {
		this.maxSafeValue = maxSafeValue;
	}

	

	public void setVibeRate(double vibeRate) {
		this.vibeRate = vibeRate;
	}


	public void setHowManyPercentageToAdd(double howManyPercentageToAdd) {
		this.howManyPercentageToAdd = howManyPercentageToAdd;
	}
	
	/**
	 * response to a CMD request
	 * it may be adjusting camera, send a robot, and so on
	 * @param cmd
	 * @return
	 */
	public boolean takeAction(int cmd, String...paras){
		if(this.provider==null)
			return false;
		return this.provider.takeAction(this.modelName, cmd, paras);
	}


	public DeviceProvider getProvider() {
		return provider;
	}


	public void updateValue(double value) {
		this.data = value;
		this.setChanged();
		this.notifyObservers();
	}
	
	
//	protected 
}
