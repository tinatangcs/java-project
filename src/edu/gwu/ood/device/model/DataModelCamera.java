package edu.gwu.ood.device.model;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

import edu.gwu.ood.device.DeviceProvider;
import edu.gwu.ood.frame.CameraPanel;
import edu.gwu.ood.frame.FrameUtil;

/**
 * this is a specific type of DataModel: Camera.
 * in this DataModel, `data` is not used since all data inside this model should be image resource.
 *
 */
public class DataModelCamera extends DataModel {

	/**
	 * how many pixels should the camera canvas move when user click "LEFT"/"RIGHT"/"UP"/"DOWN"
	 */
	public static final int ONE_MOVEMENT = 15;

	/**
	 * the position and the area of visible view.
	 * in canvas, the area (x,y,w,h) will be presented.
	 */
	protected int x, y, w, h;
	/**
	 * zoom rate
	 */
	protected double zoom = 1;
	/**
	 * max zoom rate
	 */
	protected double zoomMax = 4;
	/**
	 * min zoom rate
	 */
	protected double zoomMin = 0.5;
	protected int oriW, oriH;

	/**
	 * delete flag
	 */
	protected boolean isRemoved = false;

	/**
	 * all images from one GIF
	 */
	protected Image[] images;
	/**
	 * which image to display on screen
	 */
	protected int imageIndex = 0;
	/**
	 * stringify the date
	 */
	protected DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * construction
	 * @param modelName
	 * @param provider
	 */
	public DataModelCamera(String modelName, DeviceProvider provider) {
		this.modelName = modelName;
		this.provider = provider;
	}

	/**
	 * load the GIF resource.
	 * when loading, it will calculate the minimum zoom value and the maximum zoom value.
	 * @param fileName
	 * @return
	 */
	public boolean loadSrc(String fileName) {
		ImageLoader loader = new ImageLoader();
		
		InputStream input = ClassLoader.getSystemResourceAsStream("img/"+fileName);
		if (input == null)
			return false;
		//ImageData from GIF, it need to be converted into Image
		ImageData[] imageDatas = loader.load(input);
		if (imageDatas != null && imageDatas.length > 0) {
			oriW = imageDatas[0].width;
			oriH = imageDatas[0].height;

			zoomMin = Math.max(
					(double) ((double) CameraPanel.VIDEO_WIDTH / oriW),
					(double) ((double) CameraPanel.VIDEO_HEIGHT / oriH));

			if (zoom < zoomMin)
				zoom = zoomMin;

			//default zoom value
			zoom = zoomMin * 1.25;
			
			//make the default view into the center
			x = (int) ((oriW - CameraPanel.VIDEO_WIDTH / zoom) / 2);
			y = (int) ((oriH - CameraPanel.VIDEO_HEIGHT / zoom) / 2);

			this.updateOutput();
			//convert ImageData to Image
			convertImageDatasToImages(imageDatas);
		}

		this.isRemoved = false;
		
		//notify observers
		this.updateValue();

		return true;
	}

	/**
	 * update the display area according to the zoom value
	 */
	private void updateOutput() {
		w = (int) (CameraPanel.VIDEO_WIDTH / zoom);
		h = (int) (CameraPanel.VIDEO_HEIGHT / zoom);
		if (x + w > oriW)
			x = oriW - w;
		if (y + h > oriH)
			y = oriH - h;
		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
	}

	/**
	 * zoom in or zoom out. if overflowed, set to the max or min value.
	 * @param in
	 */
	public void zoom(boolean in) {
		if (in) {
			zoom = 1.25 * zoom;
		} else {
			zoom = zoom / 1.25;
		}
		if (zoom > zoomMax)
			zoom = zoomMax;
		if (zoom < zoomMin)
			zoom = zoomMin;
	}

	/**
	 * move view left or right. if overflowed, set to the most left or most right.
	 * @param toLeft
	 */
	public void moveLeftOrRight(boolean toLeft) {
		x = (int) (x + (toLeft ? -1 : 1) * ONE_MOVEMENT / zoom);
		this.updateOutput();
	}

	/**
	 * move view up or down. if overflowed, set to the most top or the most bottom.
	 * @param toUp
	 */
	public void moveUpOrDown(boolean toUp) {
		y = (int) (y + (toUp ? -1 : 1) * ONE_MOVEMENT / zoom);
		this.updateOutput();
	}

	@Override
	public void updateValue() {
		if (!this.isRemoved && images != null) {
			this.updateOutput();
			imageIndex = (imageIndex + 1) % images.length;
			this.setChanged();
			this.notifyObservers();
		}

	}

	/**
	 * get current display image
	 * @return
	 */
	public Image getImage() {
		if (this.images != null)
			return this.images[this.imageIndex];
		return null;
	}

	/**
	 * get current time (in string)
	 * @return
	 */
	public String getTime() {
		return format.format(new Date());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	/**
	 * remove the model inside this DataModel. it will display an empty camera
	 */
	public void removeModel() {
		this.isRemoved = true;
		this.setChanged();
		this.notifyObservers(FrameUtil.MSG_REMOVE_MODEL);
	}
	
	/**
	 * add an extra camera. It will tell others to make changes.
	 */
	public void addExtraModel() {
		this.setChanged();
		this.notifyObservers(FrameUtil.MSG_ADD_EXTRA_MODEL);
	}

	public boolean isRemoved() {
		return this.isRemoved;
	}

	public String toString() {
		return modelName + " images:" + images.length + " x,y:" + x + ","
				+ y + " w,h:" + w + "," + h;
	}

	/**
	 * read GIF and convert ImageData to Image
	 */
	protected void convertImageDatasToImages(ImageData[] imageDatas) {
		images = new Image[imageDatas.length];

		// Step 1: Determine the size of the resulting images.
		int width = imageDatas[0].width;
		int height = imageDatas[0].height;

		// Step 2: Construct each image.
		int transition = SWT.DM_FILL_BACKGROUND;
		for (int i = 0; i < imageDatas.length; i++) {
			ImageData id = imageDatas[i];
			images[i] = new Image(null, width, height);
			GC gc = new GC(images[i]);

			// Do the transition from the previous image.
			switch (transition) {
			case SWT.DM_FILL_NONE:
			case SWT.DM_UNSPECIFIED:
				// Start from last image.
				gc.drawImage(images[i - 1], 0, 0);
				break;
			case SWT.DM_FILL_PREVIOUS:
				// Start from second last image.
				gc.drawImage(images[i - 2], 0, 0);
				break;
			default:
				// DM_FILL_BACKGROUND or anything else,
				// just fill with default background.
				gc.setBackground(FrameUtil.COLOR_CAMERA_BG);
				gc.fillRectangle(0, 0, width, height);
				break;
			}

			// Draw the current image and clean up.
			Image img = new Image(null, id);
			gc.drawImage(img, 0, 0, id.width, id.height, id.x, id.y, id.width,
					id.height);
			img.dispose();
			gc.dispose();

			// Compute the next transition.
			// Special case: Can't do DM_FILL_PREVIOUS on the
			// second image since there is no "second last"
			// image to use.
			transition = id.disposalMethod;
			if (i == 0 && transition == SWT.DM_FILL_PREVIOUS)
				transition = SWT.DM_FILL_NONE;
		}
	}
}
