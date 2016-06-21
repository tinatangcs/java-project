package edu.gwu.ood.frame;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.gwu.ood.device.model.DataModel;

/**
 * this panel is used to display data for indicators (from SensorProvider)
 */
public class IndicatorPanel extends Composite implements Observer {

	public static int WIDTH = 220;
	public static int HEIGHT = 90;
	public static int PADDING = 5;
	protected static int GRAPH_WIDTH = 2;

	/**
	 * the background graph data for this indicator.
	 * this INT array has a fixed number of data. it is a loop and graphIndex will tell which one is the first.
	 */
	protected int[] graphData;
	/**
	 * graphIndex will tell which one is the first
	 */
	protected int graphIndex = 0;

	protected DataModel model = null;

	protected Color bgColor = FrameUtil.INDICATOR_BGS[0];
	protected Color fgColor = FrameUtil.INDICATOR_FGS[0];
	protected Color subTitleColor = FrameUtil.INDICATOR_TITLECOLORS[0];

	protected Label nameLabel, valueLabel, alarmLabel, unitLabel;
	protected ImageButton fixButton;

	protected Canvas alarmCanvas;

	protected Runnable runnable = null;
	protected int colorTimming = 0;

	protected IndicatorPanel(Composite parent, int mode) {
		super(parent, mode);

		this.setLayout(null);

		this.initGraphData();

		this.setBackgroundMode(SWT.INHERIT_FORCE);

		// this.setBackground(this.bgColor);

		nameLabel = new Label(this, SWT.NONE);
		nameLabel.setForeground(subTitleColor);
		// nameLabel.setBa
		// nameLabel.setFont(FrameUtil.FONT_VALUE);
		//nameLabel.setBounds(10, 60, 150, 20);

		valueLabel = new Label(this, SWT.NO_BACKGROUND);
		valueLabel.setForeground(FrameUtil.COLOR_WHITE);
		valueLabel.setFont(FrameUtil.FONT_VALUE);
		// valueLabel.setBounds(10, 10, 140, 50);

		unitLabel = new Label(this, SWT.NO_BACKGROUND | SWT.TRANSPARENT);
		unitLabel.setForeground(FrameUtil.COLOR_WHITE);
		// unitLabel.setBounds(130, 10, 20, 20);

		fixButton = new ImageButton(this, SWT.PUSH);
		alarmLabel = new Label(this, SWT.NONE);
		alarmLabel.setForeground(new Color(null, 255, 0, 0));
		// fixButton.setText("Send Robot");
		fixButton.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream("img/fix_icon.png")));
		fixButton.setSize(30, 30);
		fixButton.setBounds(170, 30, 30, 30);
		fixButton.setEnabled(false);

		// fixButton.setBackgroundImage(new
		// Image(this.getDisplay(),"./fix_icon.png"));
		fixButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (model != null) {
					model.sendRobotToFix();
				}
			}
		});

		this.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				// e.gc.drawLine(0, 0, 100, 100);
				// e.gc.drawString(model.getDataString(), 0, 0);

				e.gc.setBackground(fgColor);
				for (int i = 0; i < graphData.length; i++) {
					int height = graphData[(i + graphIndex) % graphData.length];
					e.gc.fillRectangle(i * GRAPH_WIDTH, HEIGHT - PADDING
							- height, GRAPH_WIDTH, height);
				}

				// e.gc.setBackground(null);
				e.gc.setForeground(FrameUtil.COLOR_WHITE);
				e.gc.setFont(FrameUtil.FONT_VALUE);
				e.gc.drawString(model.getDataString(), 10, 10, true);

				e.gc.setFont(FrameUtil.FONT_UNIT);
				e.gc.drawString(model.getUnit(), 140, 10, true);
				
				e.gc.setFont(FrameUtil.FONT_NAME);
				e.gc.setForeground(subTitleColor);
				e.gc.drawString(model.getModelName(), 10, 60, true);
			}
		});

		
		alarmCanvas = new Canvas(this, SWT.NONE);
		alarmCanvas.setBounds(0, HEIGHT - PADDING, WIDTH, PADDING);
		// alarmCanvas.setBackground(new Color(null,255,1,1));
		alarmCanvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (model.isAlarming()) {
					e.gc.setBackground(FrameUtil.RED_FLASHING[colorTimming
							% (FrameUtil.RED_FLASHING.length)]);
					e.gc.fillRectangle(0, 0, alarmCanvas.getSize().x,
							alarmCanvas.getSize().y);
					// System.out.println(alarmCanvas.getBounds());
					// e.gc.drawLine(0, 0, 10, 10);
				}
			}
		});
	}

	/**
	 * initialize the GraphData: the size should be WIDHT/GRAPH_WIDTH. So it can fill the whole panel
	 */
	private void initGraphData() {
		this.graphData = new int[WIDTH / GRAPH_WIDTH];
	}

	/**
	 * insert new data.
	 * By 'insert', it doesn't mean to insert item into GraphData.
	 * It means to replace the data of [graphIndex+1] with the new value.
	 * @param value
	 */
	private void insertNewGraphData(double value) {
		this.graphData[this.graphIndex] = (int) ((HEIGHT - 2 * PADDING)
				* (value - model.getMinSafeValue()) / (model.getMaxSafeValue() - model
				.getMinSafeValue()));
		this.graphIndex = (this.graphIndex + 1) % this.graphData.length;
	}

	public IndicatorPanel(Composite parent, int mode, DataModel model) {
		this(parent, mode);
		this.model = model;
		if(!this.model.isAlarmEnabled()){
			this.alarmCanvas.setVisible(false);
			this.fixButton.setVisible(false);
		}
		this.update();
	}

	private void showAlarm() {

		if (this.runnable != null) {
			return;
		}
		// Set up the timer for the animation
		this.runnable = new Runnable() {
			public void run() {
				// System.out.println("haha");
				getDisplay().timerExec(100, this);
				colorTimming = (colorTimming + 1) % 50;
				alarmCanvas.redraw();
			}
		};
		getDisplay().timerExec(10, runnable);
	}

	private void endAlarm() {
		if (this.runnable != null) {
			getDisplay().timerExec(-1, runnable);
			runnable = null;
		}
	}

	public void update() {
		if (model != null) {
			nameLabel.setText(model.getModelName());
			valueLabel.setText(model.getDataString());
			alarmLabel.setText(model.isAlarming() ? "Alarm!!!" : "");
			unitLabel.setText(model.getUnit());

			if (model.isAlarming()) {
				showAlarm();
			} else {
				endAlarm();
			}

			insertNewGraphData(model.getData());

			fixButton.setEnabled(model.isAlarming());

		} else {
			nameLabel.setText("NA");
		}
		// System.out.println(model.getData());

		// this.pack();
		this.redraw();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (this.isDisposed()||this.getDisplay().isDisposed())
			return;
		this.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				update();
			}
		});
	}

	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
		this.setBackground(this.bgColor);
	}

	public void setFgColor(Color fgColor) {
		this.fgColor = fgColor;
	}

	public void setSubTitleColor(Color color) {
		this.subTitleColor = color;
		nameLabel.setForeground(subTitleColor);
	}

}
