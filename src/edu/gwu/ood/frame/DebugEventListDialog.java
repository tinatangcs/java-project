package edu.gwu.ood.frame;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.gwu.ood.database.DBUtil;
import edu.gwu.ood.database.SimulatorEvent;
import edu.gwu.ood.device.DeviceProvider;

/**
 * this is the popup dialog for tester to view the event list stored in DB.
 * user can choose one event and jump to EventDebug page,
 * user can also remove a selected event.
 *
 */
public class DebugEventListDialog extends Dialog {

	/*
	 * base size information
	 */
	public static int NAME_LABEL_WIDTH = 180;
	public static int LINE_HEIGHT = 30;
	public static int NUMBER_WIDTH = 30;
	public static int BUTTON_RUN_WIDTH = 100;
	public static int BUTTON_REMOVE_WIDTH = 80;
	
	public static double TIME_SEP = 0.1;
	
	private DeviceProvider provider;
	private Shell shell;
	
	/*
	 * list for every component
	 */
	private List<Label> names;
	private List<Label> sizes;
	private List<Button> runBtns;
	private List<Button> removeBtns;
	
	/*
	 * events model
	 */
	private List<SimulatorEvent> events;
	
	private Label warning,waiting;
	
	/*
	 * holding composite
	 */
	private Composite tables;
	

	/**
	 * construction: it need to know the Provider
	 * @param parent
	 * @param mode
	 * @param provider
	 */
	public DebugEventListDialog(Shell parent, int mode, DeviceProvider provider) {
		super(parent, mode);
		this.provider = provider;
		
		names = new ArrayList<Label>();
		sizes = new ArrayList<Label>();
		runBtns = new ArrayList<Button>();
		removeBtns = new ArrayList<Button>();
	}

	/**
	 * present dialog on screen
	 */
	public void open() {
		shell = new Shell(getParent());
		shell.setText("Debug Tools");
		draw(); // Contents of Dialog
		shell.pack();
		shell.open();
 
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
 
	}
 
	/**
	 * draw the content
	 */
	private void draw() {
 
		if(shell==null)
			return;
		
		shell.setLayout(null);
		shell.setSize(410, 400);
//		shell.setBounds(10, 10, 400, 500);
		
		tables = new Composite(shell, SWT.NONE);
		tables.setLayout(null);
		tables.setBounds(5, 5, 400, 200);
		
		/*
		 * title part
		 */
		Label titleIndicator = new Label(tables, SWT.HORIZONTAL | SWT.SHADOW_OUT);
		titleIndicator.setText("Name");
		titleIndicator.setBounds(0, 0, NAME_LABEL_WIDTH, LINE_HEIGHT);
		
		Label titleSetValue = new Label(tables, SWT.HORIZONTAL | SWT.SHADOW_OUT);
		titleSetValue.setText("Event");
		titleSetValue.setBounds(NAME_LABEL_WIDTH+5, 0, NUMBER_WIDTH, LINE_HEIGHT);
		
		Label titleWhen = new Label(tables, SWT.HORIZONTAL | SWT.SHADOW_OUT);
		titleWhen.setText("Action");
		titleWhen.setBounds(NAME_LABEL_WIDTH+NUMBER_WIDTH+10, 0, BUTTON_RUN_WIDTH, LINE_HEIGHT);
		
		Label titleRemove = new Label(tables, SWT.HORIZONTAL | SWT.SHADOW_OUT);
		titleRemove.setText("  ");
 
		/*
		 * warning and waiting information
		 * 
		 * warning label is default hidden
		 * waiting label is default shown, it will hide when DB responses.
		 */
		warning = new Label(tables, SWT.NONE);
		warning.setForeground(FrameUtil.COLOR_RED);
		warning.setText("Form not valid!");
		warning.setVisible(false);
		waiting = new Label(tables, SWT.NONE);
		waiting.setForeground(FrameUtil.COLOR_RED);
		waiting.setText("Loading from Database");
		
		/*
		 * get the event list from DB asyncly.
		 */
		getParent().getDisplay().asyncExec(new Runnable(){
			@Override
			public void run() {
				events = DBUtil.getInstance().getEventList();
				waiting.setVisible(false);
				for(SimulatorEvent event : events){
					addOneRecord(event);
					
				}
				repackLocation();
			}
			
		});
		
		//repack
		repackLocation();
	}

	/**
	 * repack: the position of every component and the size of popup dialog screen
	 */
	private void repackLocation(){
		for(int i=0;i<names.size();i++){
			names.get(i).setBounds(0, (LINE_HEIGHT+5)*(i+1), NAME_LABEL_WIDTH, LINE_HEIGHT);
			sizes.get(i).setBounds(NAME_LABEL_WIDTH+5, (LINE_HEIGHT+5)*(i+1), NUMBER_WIDTH, LINE_HEIGHT-5);
			runBtns.get(i).setBounds(NAME_LABEL_WIDTH+NUMBER_WIDTH+10, (LINE_HEIGHT+5)*(i+1), BUTTON_RUN_WIDTH, LINE_HEIGHT-5);
			removeBtns.get(i).setBounds(NAME_LABEL_WIDTH+NUMBER_WIDTH+BUTTON_RUN_WIDTH+15, (LINE_HEIGHT+5)*(i+1), BUTTON_REMOVE_WIDTH, LINE_HEIGHT-5);
		}
		waiting.setBounds(0, (LINE_HEIGHT+5)*(1), NAME_LABEL_WIDTH, LINE_HEIGHT);
		tables.setBounds(10, 10, NAME_LABEL_WIDTH+NUMBER_WIDTH+BUTTON_RUN_WIDTH+BUTTON_REMOVE_WIDTH+25, (LINE_HEIGHT+5)*(names.size()+3));
		tables.redraw();
		shell.pack();
	}

	/**
	 * add one Event record into the dialog
	 * 1. add every component for that event
	 * 2. register Button Click listener
	 * @param event
	 */
	private void addOneRecord(SimulatorEvent event){
		Label name = new Label(tables,SWT.NONE);
		name.setText(event.getName());
		names.add(name);
		
		Label size = new Label(tables,SWT.NONE);
		size.setText(event.getEvents().size()+"");
		sizes.add(size);
		

		/*
		 * run button:
		 * pop up DebugEvent Dialog with selected event
		 */
		Button runBtn = new Button(tables, SWT.NONE);
		runBtn.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				int index = runBtns.indexOf(e.getSource());
				DebugEventDialog debugger = new DebugEventDialog(shell,SWT.None, provider);
				debugger.setEvent(events.get(index));
				debugger.open();
			}
		});
		runBtn.setText("View/Run");
		runBtns.add(runBtn);
		
		/**
		 * remove button:
		 * 1. remove from screen
		 * 2. remove from DB
		 */
		Button removeBtn = new Button(tables, SWT.NONE);
		removeBtn.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				int index = removeBtns.indexOf(e.getSource());
				if(index>=0){
					names.remove(index).dispose();
					sizes.remove(index).dispose();
					runBtns.remove(index).dispose();
					removeBtns.remove(index).dispose();
					DBUtil.getInstance().removeEvent(events.remove(index).getName());
					repackLocation();
					shell.pack();
				}
			}
		});
		removeBtn.setText("Remove");
		removeBtns.add(removeBtn);
		
	}
}
