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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.gwu.ood.device.model.DataModel;
import edu.gwu.ood.device.model.DataModelCamera;

/**
 * the panel for displaying camera view
 *
 */
public class CameraPanel extends Composite implements Observer {

	/*
	 * size config
	 */
	public static final int HEIGHT = 210;
	public static final int WIDTH = 330;
	public static final int VIDEO_HEIGHT = 200;
	public static final int VIDEO_WIDTH = 200;
	public static final int PADDING = 5;
	public static final int BUTTON_SIZE = 40;
	public static final int MENU_WIDTH = 115;
	public static final int MENU_HEIGHT = 200;
	public static final int CANVAS_TIME_X = 80;

	/**
	 * canvas for the Video part
	 */
	protected Canvas videoCanvas;

	/**
	 * data model
	 */
	protected DataModelCamera model;

	protected String modelName = "";

	/**
	 * two part for the buttons:
	 * ctrlButtons: Up,Down,Left,Right,Zoom In,Zoom Out,Remove
	 * settingButtons: Add
	 */
	protected Composite ctrlButtons, settingButtons;

	protected ImageButton btnUp, btnDown, btnLeft, btnRight, btnZoomIn,
			btnZoomOut, btnRemove;

	protected Button btnAdd, btnAddExtra;
	protected Text linkText;
	protected Text linkExamples;

	public CameraPanel(Composite parent, int mode, DataModel model) {
		this(parent, mode);
		this.model = (DataModelCamera) model;
	}

	private CameraPanel(Composite parent, int mode) {
		super(parent, mode);

		// this.setBackgroundMode(SWT.INHERIT_FORCE);

		this.setBackground(FrameUtil.COLOR_CAMERA_BG);

		videoCanvas = new Canvas(this, SWT.NONE);
		videoCanvas.setBounds(PADDING, PADDING, VIDEO_WIDTH, VIDEO_HEIGHT);
		/*
		 * draw video content according to current setting (x,y,w,h: zoom)
		 */
		videoCanvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {

				e.gc.setBackground(FrameUtil.COLOR_CAMERA_BG);

				e.gc.fillRectangle(0, 0, VIDEO_WIDTH, VIDEO_HEIGHT);

				if (model != null && !model.isRemoved()) {
					Image image = model.getImage();
	
					//draw current image 
					e.gc.drawImage(image, model.getX(), model.getY(),
							model.getW(), model.getH(), 0, 0, VIDEO_WIDTH,
							VIDEO_HEIGHT);
			

					e.gc.setForeground(FrameUtil.COLOR_WHITE);
					e.gc.setFont(FrameUtil.FONT_TIME);
					e.gc.drawString(model.getModelName(), 1, 1, true);
					e.gc.drawString(model.getTime(), CANVAS_TIME_X + 1, 1, true);
					e.gc.drawString(model.getModelName(), -1, -1, true);
					e.gc.drawString(model.getTime(), CANVAS_TIME_X + 1, -1,
							true);
					e.gc.drawString(model.getModelName(), -1, 1, true);
					e.gc.drawString(model.getTime(), CANVAS_TIME_X - 1, -1,
							true);
					e.gc.drawString(model.getModelName(), 1, -1, true);
					e.gc.drawString(model.getTime(), CANVAS_TIME_X - 1, 1, true);
					e.gc.setForeground(FrameUtil.COLOR_BLACK);
					e.gc.setFont(FrameUtil.FONT_TIME);
					e.gc.drawString(model.getModelName(), 0, 0, true);
					e.gc.drawString(model.getTime(), CANVAS_TIME_X, 0, true);

				}

			}

		});

		/*
		 * initialize buttons
		 */
		ctrlButtons = new Composite(this, SWT.None);
		ctrlButtons.setBounds(VIDEO_WIDTH + 10, 5, MENU_WIDTH, MENU_HEIGHT);
		ctrlButtons.setBackground(new Color(null, 79, 43, 120));

		btnDown = new ImageButton(ctrlButtons, SWT.NONE);
		btnDown.setImage(new Image(ctrlButtons.getDisplay(), ClassLoader.getSystemResourceAsStream("img/down.png")));
		btnDown.setBounds(BUTTON_SIZE - 5, (int) (BUTTON_SIZE * 1.5 + 5),
				BUTTON_SIZE, BUTTON_SIZE);

		btnUp = new ImageButton(ctrlButtons, SWT.NONE);
		btnUp.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream("img/up.png")));
		btnUp.setBounds(BUTTON_SIZE - 5, 5, BUTTON_SIZE, BUTTON_SIZE);

		btnLeft = new ImageButton(ctrlButtons, SWT.NONE);
		btnLeft.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream("img/left.png")));
		btnLeft.setBounds(5, 15 + (int) (BUTTON_SIZE / 2), BUTTON_SIZE,
				BUTTON_SIZE);

		btnRight = new ImageButton(ctrlButtons, SWT.NONE);
		btnRight.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream("img/right.png")));
		btnRight.setBounds(BUTTON_SIZE * 3 / 2 + 5,
				15 + (int) (BUTTON_SIZE / 2), BUTTON_SIZE, BUTTON_SIZE);

		btnZoomIn = new ImageButton(ctrlButtons, SWT.NONE);
		btnZoomIn.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream("img/zoomin.png")));
		btnZoomIn.setBounds(5, 10 + BUTTON_SIZE * 5 / 2, BUTTON_SIZE,
				BUTTON_SIZE);

		btnZoomOut = new ImageButton(ctrlButtons, SWT.NONE);
		btnZoomOut.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream("img/zoomout.png")));
		btnZoomOut.setBounds(BUTTON_SIZE * 3 / 2 + 5, 10 + BUTTON_SIZE * 5 / 2,
				BUTTON_SIZE, BUTTON_SIZE);

		btnRemove = new ImageButton(ctrlButtons, SWT.NONE);
		btnRemove.setImage(new Image(this.getDisplay(), ClassLoader.getSystemResourceAsStream("img/close.png")));
		btnRemove.setBounds((MENU_WIDTH - BUTTON_SIZE) / 2,
				10 + BUTTON_SIZE * 7 / 2, BUTTON_SIZE, BUTTON_SIZE);

		settingButtons = new Composite(this, SWT.None);
		settingButtons.setBounds(5, 5, VIDEO_WIDTH + MENU_WIDTH + 5,
				VIDEO_HEIGHT);
		settingButtons.setLayout(null);
		
		
		btnAdd = new Button(settingButtons, SWT.NONE);
		btnAdd.setText("Add");
		btnAdd.setBounds(20, 50, 80, 30);
		
		btnAddExtra = new Button(settingButtons, SWT.NONE);
		btnAddExtra.setText("Add An Extra Camera");
		btnAddExtra.setBounds(110, 50, 160, 30);

		linkText = new Text(settingButtons, SWT.SINGLE | SWT.BORDER);
		linkText.setBounds(20, 20, VIDEO_WIDTH + MENU_WIDTH - 35, 20);

		linkExamples = new Text(settingButtons, SWT.MULTI | SWT.BORDER);
		linkExamples.setBounds(20, 100, VIDEO_WIDTH + MENU_WIDTH - 35, 80);
		linkExamples
				.setText("//Examples:\nbike, bar, metro, rain, waterfall, light");
		linkExamples.setEditable(false);

		this.addListener();
	}

	private void addListener() {
		btnZoomIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				model.takeAction(DataModel.CMD_CAMERA_ZOOM_IN);
			}
		});
		btnZoomOut.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				model.takeAction(DataModel.CMD_CAMERA_ZOOM_OUT);
			}
		});

		btnLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				model.takeAction(DataModel.CMD_CAMERA_LEFT);
			}
		});
		btnRight.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				model.takeAction(DataModel.CMD_CAMERA_RIGHT);
			}
		});

		btnUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				model.takeAction(DataModel.CMD_CAMERA_UP);
			}
		});
		btnDown.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				model.takeAction(DataModel.CMD_CAMERA_DOWN);
			}
		});

		btnRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				model.takeAction(DataModel.CMD_CAMERA_REMOVE);
			}
		});

		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (!model.takeAction(DataModel.CMD_CAMERA_ADD,
						linkText.getText() + ".gif")) {
					MessageBox messageBox = new MessageBox(btnAdd.getShell(),
							SWT.ICON_ERROR | SWT.OK);

					messageBox.setText("Warning");
					messageBox.setMessage("No camera under this link!");
					messageBox.open();
				}
			}
		});
		
		  
		
		btnAddExtra.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (!model.takeAction(DataModel.CMD_CAMERA_ADD_EXTRA,
						"./" + linkText.getText() + ".gif")) {
					MessageBox messageBox = new MessageBox(new Shell(),
							SWT.ICON_ERROR | SWT.OK | SWT.APPLICATION_MODAL);

					messageBox.setText("Warning");
					messageBox.setMessage("No camera under this link!");
					messageBox.getParent().setBounds(0, 0, 100, 100);
					messageBox.open();
					
//					MessageDialog.openWarning(btnAdd.getShell(),"","");
				}
			}
		});
		
	}

	@Override
	public void update(Observable o, Object msg) {

		if (msg != null && msg.equals(FrameUtil.MSG_REMOVE_MODEL)) {
			return;
		}

		if (msg != null && msg.equals(FrameUtil.MSG_ADD_EXTRA_MODEL)) {
			Console parent = (Console)this.getParent();
			DataModel extra = model.getProvider().getLastModel();
			if(extra!=null){
				parent.addDataModel(extra, Console.TARGET_CAMERA);
			}
			
			return;
		}
		
		
		if (this.isDisposed() || this.getDisplay().isDisposed())
			return;

		this.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (model == null || model.isRemoved()) {
					videoCanvas.setVisible(false);
					ctrlButtons.setVisible(false);
					settingButtons.setVisible(true);
					return;
				}
				videoCanvas.setVisible(true);
				ctrlButtons.setVisible(true);
				settingButtons.setVisible(false);
				redraw();
				videoCanvas.redraw();
			}
		});

	}

	/**
	 * check whether this Panel is for the model
	 * @param model
	 * @return
	 */
	public boolean hasModel(DataModel model) {
		return this.model != null
				&& this.model.getModelName().equals(model.getModelName());
	}

	public void removeModel() {
		videoCanvas.setVisible(false);
		ctrlButtons.setVisible(false);
		settingButtons.setVisible(true);

	}

}
