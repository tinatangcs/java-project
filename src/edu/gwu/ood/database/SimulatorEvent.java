package edu.gwu.ood.database;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * the structure of a simulator event.
 * this event contains one or more items inside it.
 * each item can update one indicator to a specific value.
 *
 */
public class SimulatorEvent {
	
	protected Format namingFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	protected String name;
	
	/**
	 * event items inside this SimulatorEvent.
	 * each item can update one indicator to a specific value.
	 */
	protected List<EventInfo> events;
	
	/**
	 * construction using a specific name
	 * @param name
	 */
	public SimulatorEvent(String name){
		this.name = name;
		events = new ArrayList<EventInfo>();
	}
	
	/**
	 * construction using a auto-generated name
	 */
	public SimulatorEvent(){
		this.name = "event_"+namingFormat.format(new Date());
		events = new ArrayList<EventInfo>();
	}
	
	/**
	 * add one empty event item
	 */
	public void addEvent(){
		events.add(new EventInfo("",0,0));
	}
	
	/**
	 * get the item list of this SimulatorEvent
	 * @return
	 */
	public List<EventInfo> getEvents(){
		return this.events;
	}
	
	/**
	 * add a specific event item
	 * @param info
	 */
	public void addEvent(EventInfo info){
		events.add(info);
	}
	
	/**
	 * get the last one of items in SimulatorEvent
	 * @return
	 */
	public EventInfo getLastEvent() {
		return this.events.get(this.events.size()-1);
	}
	
	/**
	 * stringify
	 */
	public String toString(){
		String str = "";
		for(EventInfo info: events){
			str+=info+"\n";
		}
		return str;
	}
	
	/**
	 * the structure of event item (EventInfo)
	 *
	 */
	public static class EventInfo {
		public String indicator;
		public double value;
		public int time;
		public EventInfo(String indicator,double value,int time){
			this.indicator = indicator;
			this.value = value;
			this.time = time;
		}
		
		public static EventInfo create(){
			return new EventInfo("",0,0);
		}
		
		public String toString(){
			return indicator+" to "+value+" in "+time+"ms";
		}
	}

	public String getName() {
		return name;
	}
}


