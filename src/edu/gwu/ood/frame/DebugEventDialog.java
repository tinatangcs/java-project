package edu.gwu.ood.frame;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.gwu.ood.database.DBUtil;
import edu.gwu.ood.database.SimulatorEvent;
import edu.gwu.ood.device.DeviceProvider;
import edu.gwu.ood.device.model.DataModel;

/**
 * this is the pop up dialog for editing event and run event for testing users.
 * user can: 1. add a specific item 2. run the whole event 3. store the whole
 * event into DB
 *
 */
public class DebugEventDialog extends Dialog {

	/*
	 * basic size information for layout
	 */
	public static int INDICATOR_WIDTH = 150;
	public static int LINE_HEIGHT = 30;
	public static int NUMBER_WIDTH = 70;
	public static int BUTTON_WIDTH = 80;

	/**
	 * the interval (in second) for refresh page when running simulator
	 */
	public static double TIME_SEP = 0.1;

	private DeviceProvider provider;
	private Shell shell;

	/**
	 * the data for event
	 */
	private SimulatorEvent event;

	/*
	 * list of those elements for every event item
	 */
	private List<Combo> indicators;
	private List<Text> values;
	private List<Text> times;
	private List<Button> removeBtns;

	/**
	 * warning and waiting label, default invisible
	 */
	private Label warning, waiting;

	/**
	 * buttons
	 */
	private Button addEventItemBtn, runSimulatorBtn, insertIntoDB;

	/**
	 * date format
	 */
	private Format format = new DecimalFormat("0.0");

	/**
	 * composite for hold all elements of event
	 */
	private Composite tables;

	/**
	 * this is a Runnable for executing the simulator action.
	 */
	protected Runnable runnable = null;

	/**
	 * Construction, we need to know the provider
	 * 
	 * @param parent
	 * @param mode
	 * @param provider
	 */
	public DebugEventDialog(Shell parent, int mode, DeviceProvider provider) {
		super(parent, mode);
		this.provider = provider;

		event = new SimulatorEvent();
		event.addEvent();

		indicators = new ArrayList<Combo>();
		values = new ArrayList<Text>();
		times = new ArrayList<Text>();
		removeBtns = new ArrayList<Button>();

		shell = new Shell(getParent());
		shell.setText("Debug Tools");
		draw(); // Contents of Dialog

	}

	/**
	 * present the dialog on the screen
	 */
	public void open() {
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
	 * draw every component
	 */
	private void draw() {

		if (shell == null)
			return;

		shell.setLayout(null);
		shell.setSize(410, 400);

		tables = new Composite(shell, SWT.NONE);
		tables.setLayout(null);
		tables.setBounds(5, 5, 400, 200);

		/*
		 * labels
		 */
		Label titleIndicator = new Label(tables, SWT.HORIZONTAL
				| SWT.SHADOW_OUT);
		titleIndicator.setText("Indicator");
		titleIndicator.setBounds(0, 0, INDICATOR_WIDTH, LINE_HEIGHT);

		Label titleSetValue = new Label(tables, SWT.HORIZONTAL | SWT.SHADOW_OUT);
		titleSetValue.setText("Set Value");
		titleSetValue.setBounds(INDICATOR_WIDTH + 5, 0, NUMBER_WIDTH,
				LINE_HEIGHT);

		Label titleWhen = new Label(tables, SWT.HORIZONTAL | SWT.SHADOW_OUT);
		titleWhen.setText("When");
		titleWhen.setBounds(INDICATOR_WIDTH + NUMBER_WIDTH + 10, 0,
				NUMBER_WIDTH, LINE_HEIGHT);

		Label titleRemove = new Label(tables, SWT.HORIZONTAL | SWT.SHADOW_OUT);
		titleRemove.setText("  ");

		warning = new Label(tables, SWT.NONE);
		warning.setForeground(FrameUtil.COLOR_RED);
		warning.setText("Form not valid!");
		warning.setVisible(false);

		waiting = new Label(tables, SWT.NONE);
		waiting.setForeground(FrameUtil.COLOR_RED);
		waiting.setText("Saving...");
		waiting.setVisible(false);

		/**
		 * add event item into screen
		 */
		for (SimulatorEvent.EventInfo eventInfo : event.getEvents()) {
			this.addOneRecord(eventInfo);
		}

		/**
		 * initial addEventItem button and register the listener
		 */
		addEventItemBtn = new Button(tables, SWT.NONE);
		addEventItemBtn.setText("Insert Event");
		addEventItemBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// create an empty event and add it to both event model and the
				// screen
				event.addEvent(SimulatorEvent.EventInfo.create());
				addOneRecord(event.getLastEvent());
				tables.redraw();
				repackLocation();
				shell.pack();
			}
		});

		/**
		 * initialize insertIntoDB button and register the listener
		 */
		insertIntoDB = new Button(tables, SWT.NONE);
		insertIntoDB.setText("Save into DB");
		insertIntoDB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// get the event data and check whether it is valid. if not,
				// show WARNING
				SimulatorEvent event = getEvent();
				if (event != null) {

					// display WAITING information first, then save data async
					waiting.setText("Saving ....");
					waiting.setVisible(true);
					setEnableAll(false);
					tables.redraw();
					// save data
					getParent().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							// display result according to the DB result
							if (DBUtil.getInstance().insertEvent(getEvent())) {
								waiting.setText("Saved!!");
							} else {
								waiting.setText("Saving failed!");
							}
							setEnableAll(true);
							// hide the information in 2 seconds
							getParent().getDisplay().timerExec(2000,
									new Runnable() {
										@Override
										public void run() {
											waiting.setVisible(false);
										}
									});
						}
					});
				} else {
					// form not valid, show warning, then hide in 2 seconds
					warning.setVisible(true);
					getParent().getDisplay().timerExec(2000, new Runnable() {
						@Override
						public void run() {
							warning.setVisible(false);
						}
					});
				}
			}
		});

		/**
		 * initialize runSimulator button and register the listener
		 */
		runSimulatorBtn = new Button(tables, SWT.NONE);
		runSimulatorBtn.setText("Run!!!!!");
		runSimulatorBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				SimulatorEvent event = getEvent();
				// check whether form is valid
				if (event == null) {
					warning.setVisible(true);
				} else {
					warning.setVisible(false);
					// start running ~
					runSimulator();
				}

			}
		});

		repackLocation();
	}

	/**
	 * repack: the position of every component and the size of popup dialog
	 * screen
	 */
	private void repackLocation() {
		for (int i = 0; i < indicators.size(); i++) {
			indicators.get(i).setBounds(0, (LINE_HEIGHT + 5) * (i + 1),
					INDICATOR_WIDTH, LINE_HEIGHT);
			values.get(i).setBounds(INDICATOR_WIDTH + 5,
					(LINE_HEIGHT + 5) * (i + 1), NUMBER_WIDTH, LINE_HEIGHT - 5);
			times.get(i).setBounds(INDICATOR_WIDTH + NUMBER_WIDTH + 10,
					(LINE_HEIGHT + 5) * (i + 1), NUMBER_WIDTH, LINE_HEIGHT - 5);
			removeBtns.get(i).setBounds(
					INDICATOR_WIDTH + NUMBER_WIDTH * 2 + 15,
					(LINE_HEIGHT + 5) * (i + 1), BUTTON_WIDTH, LINE_HEIGHT - 5);
		}
		// tables.setBounds(5, 5, 400, (LINE_HEIGHT+5)*(indicators.size()+1));
		addEventItemBtn.setBounds(INDICATOR_WIDTH + NUMBER_WIDTH * 2 - 10,
				(LINE_HEIGHT + 5) * (indicators.size() + 1), BUTTON_WIDTH + 25,
				LINE_HEIGHT - 5);
		this.insertIntoDB.setBounds(INDICATOR_WIDTH + 5, (LINE_HEIGHT + 5)
				* (indicators.size() + 1), BUTTON_WIDTH + 25, LINE_HEIGHT - 5);
		warning.setBounds(0, (LINE_HEIGHT + 5) * (indicators.size() + 1),
				INDICATOR_WIDTH, LINE_HEIGHT - 5);
		waiting.setBounds(0, (LINE_HEIGHT + 5) * (indicators.size() + 1),
				INDICATOR_WIDTH, LINE_HEIGHT - 5);
		runSimulatorBtn.setBounds(0, (LINE_HEIGHT + 5)
				* (indicators.size() + 2), INDICATOR_WIDTH + NUMBER_WIDTH * 2
				+ BUTTON_WIDTH + 15, LINE_HEIGHT - 5);
		tables.setBounds(10, 10, INDICATOR_WIDTH + NUMBER_WIDTH * 2
				+ BUTTON_WIDTH + 25, (LINE_HEIGHT + 5)
				* (indicators.size() + 3));
	}

	/**
	 * add one Event record into the dialog 1. add every component for that
	 * event item 2. register Button Click listener
	 * 
	 * @param eventInfo
	 */
	private void addOneRecord(SimulatorEvent.EventInfo eventInfo) {
		Combo targets = new Combo(tables, SWT.DROP_DOWN | SWT.BORDER
				| SWT.READ_ONLY);
		for (String key : provider.getDataModelNames()) {
			targets.add(key);
		}
		indicators.add(targets);

		Text value = new Text(tables, SWT.SINGLE | SWT.BORDER);
		values.add(value);

		Text time = new Text(tables, SWT.SINGLE | SWT.BORDER);
		times.add(time);

		if (eventInfo != null && eventInfo.indicator != null
				&& !eventInfo.indicator.equals("")) {
			targets.setText(eventInfo.indicator);
			value.setText(eventInfo.value + "");
			time.setText(eventInfo.time + "");
		}

		Button removeBtn = new Button(tables, SWT.NONE);
		removeBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				int index = removeBtns.indexOf(e.getSource());
				if (index >= 0) {
					indicators.remove(index).dispose();
					values.remove(index).dispose();
					times.remove(index).dispose();
					removeBtns.remove(index).dispose();
					repackLocation();
					shell.pack();
				}
			}
		});
		// the first one can not be removed!!
		if (removeBtns.size() == 0)
			removeBtn.setEnabled(false);
		removeBtn.setText("Remove");
		removeBtns.add(removeBtn);

	}

	/**
	 * create the data model according to current form value, if not valid,
	 * return null
	 * 
	 * @return
	 */
	public SimulatorEvent getEvent() {
		SimulatorEvent event = new SimulatorEvent(this.event.getName());
		for (int i = 0; i < indicators.size(); i++) {
			if (indicators.get(i).getText().equals("")) {
				return null;
			}
			double value = 0;
			try {
				value = Double.parseDouble(values.get(i).getText());
			} catch (NumberFormatException e) {
				return null;
			}
			int time = 0;
			try {
				time = Integer.parseInt(times.get(i).getText());
			} catch (NumberFormatException e) {
			}
			event.addEvent(new SimulatorEvent.EventInfo(indicators.get(i)
					.getText(), value, time));
		}
		return event;
	}

	/**
	 * disable/enable all elements in pop up window.
	 * 
	 * @param enable
	 */
	protected void setEnableAll(boolean enable) {
		for (int i = 0; i < indicators.size(); i++) {
			indicators.get(i).setEnabled(enable);
			values.get(i).setEnabled(enable);
			times.get(i).setEnabled(enable);
			if (i != 0)
				removeBtns.get(i).setEnabled(enable);
		}
		this.insertIntoDB.setEnabled(enable);
		this.runSimulatorBtn.setEnabled(enable);
		this.addEventItemBtn.setEnabled(enable);
	}

	/**
	 * when running simulator, the event data on screen may be lost. Store in
	 * before running
	 */
	protected SimulatorEvent lastEvent = null;

	/**
	 * store the last event
	 */
	protected void storeLastEvent() {
		lastEvent = this.getEvent();
	}

	/**
	 * restore the last event into page
	 */
	protected void restoreLastEvent() {
		for (SimulatorEvent.EventInfo info : lastEvent.getEvents()) {
			this.addOneRecord(info);
		}
		tables.redraw();
		repackLocation();
		shell.pack();
	}

	/**
	 * run the simulator!!
	 */
	protected void runSimulator() {
		
		//disable element on screen
		setEnableAll(false);
		//save the form in temporary event variable
		storeLastEvent();
		
		/**
		 * this runnable is executed every 0.1 second (TIME_SEP),
		 * every time: 
		 *  1. check the time value of the first event item
		 *     if time left is less than 0, 
		 *         execute:update the indicator;
		 *         remove this record if it is not the first one 
		 *     else, do nothing but update the time text input
		 *         
		 *  when finished, restore the events       
		 */
		this.runnable = new Runnable() {
			public void run() {
				if(times.get(0).isDisposed())
					return;
				
				double timeLeft = Double.parseDouble(times.get(0).getText());
				timeLeft -= TIME_SEP;

				if (timeLeft <= 0) {
					// execute that event
					provider.getDataModel(indicators.get(0).getText())
							.takeAction(DataModel.CMD_DEBUG_SET_VALUE,
									values.get(0).getText());
					times.get(0).setText("0");
					// remove that label if that is not the last one
					indicators.remove(0).dispose();
					values.remove(0).dispose();
					times.remove(0).dispose();
					removeBtns.remove(0).dispose();
					repackLocation();
					shell.pack();

					if (indicators.size() == 0) {
						// last one finished!, end loop
						setEnableAll(true);
						restoreLastEvent();
						return;
					}
				} else {
					times.get(0).setText(format.format(timeLeft));
				}

				getParent().getDisplay().timerExec((int) (TIME_SEP * 1000),
						this);
			}
		};
		
		getParent().getDisplay().timerExec(10, runnable);
	}

	public void setEvent(SimulatorEvent event) {
		// remove all items first
		for (int index = indicators.size() - 1; index >= 0; index--) {
			indicators.remove(index).dispose();
			values.remove(index).dispose();
			times.remove(index).dispose();
			removeBtns.remove(index).dispose();
		}
		this.lastEvent = event;

		this.restoreLastEvent();
	}
}
