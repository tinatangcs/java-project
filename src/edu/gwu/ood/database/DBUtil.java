package edu.gwu.ood.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

/**
 * the util to communicate with database:
 * including:
 * create, read, delete
 */
public class DBUtil {

	/**
	 * the only instance
	 */
	private static DBUtil _instance;
	Connection conn = null;
	
	
    String url = "jdbc:mysql://paytogether.me:3306/mars_monitor";
    String user = "ubuntu";
    String password = "mission";
    
	
	public static DBUtil getInstance(){
		if(_instance==null)
			_instance = new DBUtil();
		return _instance;
	}
	
	/**
	 * get connection 
	 * @return
	 */
	public static Connection getConnection(){
		return DBUtil.getInstance().conn;
	}
	
	/**
	 * construction, initialize jdbc
	 */
	public DBUtil(){
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
	}
	
	/**
	 * insert event,
	 * insert record in `event`, then `event_item`
	 * @param event
	 * @return
	 */
	public boolean insertEvent(SimulatorEvent event){
		
		if(event==null)
			return false;
		
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		PreparedStatement st2 = null;
		
		try {
			String sqlInsertEvent = "INSERT INTO `event` (name) values (?);";
			st = conn.prepareStatement(sqlInsertEvent, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, event.name);
			st.execute();
			rs = st.getGeneratedKeys();
			int key = -1;
			if(rs.next()){
				key = rs.getInt(1);
			}
			
			if(key<0)
				return false;
			
			String sqlInsertEventItem = "INSERT INTO `event_item` (event_id,indicator,value,time) values (?,?,?,?);";
			st2 = conn.prepareStatement(sqlInsertEventItem);
			
			for(SimulatorEvent.EventInfo info: event.events){
				st2.setInt(1, key);
				st2.setString(2, info.indicator);
				st2.setDouble(3, info.value);
				st2.setDouble(4, info.time);
				st2.addBatch();
			}
			
			st2.executeBatch();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally{
			try {
				if(rs!=null)
					rs.close();
				if(st!=null)
					st.close();
				if(st2!=null)
					st2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	/**
	 * remove event according to the id.
	 * do not delete record in database. change the delete_flag to TRUE only
	 * @param id
	 * @return
	 */
	public boolean removeEvent(int id){
		if(id<= 0)
			return false;
		
		PreparedStatement st = null;
		try {
			String sqlInsertEvent = "UPDATE `event` SET delete_flag = TRUE WHERE id=?;";
			st = conn.prepareStatement(sqlInsertEvent);
			st.setInt(1, id);
			st.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally{
			try {
				if(st!=null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * get the event list
	 * @return
	 */
	public List<SimulatorEvent> getEventList(){
		
		List<SimulatorEvent>events = new ArrayList<SimulatorEvent>();
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			String selectSQL = "SELECT e.id,e.name,i.indicator,i.value,i.time FROM `event` e LEFT JOIN event_item i on e.id=i.event_id WHERE e.id IS NOT NULL AND i.event_id IS NOT NULL AND e.delete_flag IS FALSE AND i.delete_flag IS FALSE;";
			st = conn.prepareStatement(selectSQL);
			rs = st.executeQuery();
			
			int lastKey = -1;
			SimulatorEvent lastEvent = null;
			
			while(rs.next()){
				int key = rs.getInt("id");
				if(key!=lastKey){
					lastEvent = new SimulatorEvent(rs.getString("name"));
					lastKey = key;
					events.add(lastEvent);
				}
				lastEvent.addEvent(new SimulatorEvent.EventInfo(
						rs.getString("indicator"),
						rs.getDouble("value"),
						rs.getInt("time")));
			}
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return events;
		} finally{
			try {
				if(rs!=null)
					rs.close();
				if(st!=null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		return events;
	}
	
//	public static void main(String args[]){
////		Connection con = DBUtil.getConnection();
//		
//		SimulatorEvent event = new SimulatorEvent();
//		event.addEvent(new SimulatorEvent.EventInfo("Oxygen",20,10));
//		event.addEvent(new SimulatorEvent.EventInfo("Oxygen",30,4));
//		event.addEvent(new SimulatorEvent.EventInfo("Oxygen",40,5));
//		
//		DBUtil.getInstance().insertEvent(event);
////		DBUtil.getInstance().removeEvent(4);
//		System.out.println(DBUtil.getInstance().getEventList().size());
//	}
	
	/**
	 * remove event according to the name.
	 * do not delete record in database. change the delete_flag to TRUE only
	 * @param name
	 * @return
	 */
	public boolean removeEvent(String name) {
		if(name == null)
			return false;
		PreparedStatement st = null;
		try {
			String sqlInsertEvent = "UPDATE `event` SET delete_flag = TRUE WHERE name=?;";
			st = conn.prepareStatement(sqlInsertEvent);
			st.setString(1, name);
			st.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally{
			try {
				if(st!=null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	

	
	
}
