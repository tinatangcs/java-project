package edu.gwu.ood.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.gwu.ood.device.model.DataModel;
import edu.gwu.ood.device.model.DataModelCamera;

/**
 * CameraProvider for the camera module.
 * It will load 3 default DataModel for cameras.
 *
 */
public class CameraProvider extends DeviceProvider implements Runnable {
	/**
	 * flag for the thread of running update the image source.
	 */
	protected boolean running = true;

	public CameraProvider() {
		models = new HashMap<String, DataModel>();
		orderByName = new ArrayList<String>();

		for (DataModel model : generateModel()) {
			models.put(model.getModelName(), model);
			orderByName.add(model.getModelName());
		}

		// start running
		new Thread(this).start();
	}

	/**
	 * mock function: generate all DataModel(s)
	 * 
	 * @return
	 */
	protected List<DataModel> generateModel() {
		List<DataModel> list = new ArrayList<DataModel>();

		DataModelCamera camera1 = new DataModelCamera("Camera 1", this);
		camera1.loadSrc("./rain.gif");
		list.add(camera1);

		DataModelCamera camera2 = new DataModelCamera("Camera 2", this);
		camera2.loadSrc("./metro.gif");
		list.add(camera2);

		DataModelCamera camera3 = new DataModelCamera("Camera 3", this);
		camera3.loadSrc("./bar.gif");
		list.add(camera3);
//
//		DataModelCamera camera4 = new DataModelCamera("Camera 4", this);
//		camera4.loadSrc("./light.gif");
//		list.add(camera4);

		return list;
	}

	@Override
	public boolean takeAction(String modelName, int cmd, String... paras) {
		//get the target model
		DataModelCamera model = (DataModelCamera) models.get(modelName);

		//take action according to the action type.
		switch (cmd) {
		case (DataModel.CMD_CAMERA_ZOOM_IN):
			model.zoom(true);
			break;
		case (DataModel.CMD_CAMERA_ZOOM_OUT):
			model.zoom(false);
			break;

		case (DataModel.CMD_CAMERA_LEFT):
			model.moveLeftOrRight(true);
			break;

		case (DataModel.CMD_CAMERA_RIGHT):
			model.moveLeftOrRight(false);
			break;

		case (DataModel.CMD_CAMERA_UP):
			model.moveUpOrDown(true);
			break;
		case (DataModel.CMD_CAMERA_DOWN):
			model.moveUpOrDown(false);
			break;

		case (DataModel.CMD_CAMERA_REMOVE):
			model.removeModel();
			break;

		case (DataModel.CMD_CAMERA_ADD):
			return model.loadSrc(paras[0]);

		case (DataModel.CMD_CAMERA_ADD_EXTRA):
			DataModelCamera camera = new DataModelCamera("Camera "+(1+this.orderByName.size()), this);
			if(camera.loadSrc(paras[0])){
				this.models.put(camera.getModelName(), camera);
				this.orderByName.add(camera.getModelName());
				model.addExtraModel();
			}else{
				return false;
			}
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
		while (this.running) {

			for (DataModel model : models.values()) {
				model.updateValue();
			}

			try {
				Thread.sleep(DeviceProvider.CAMERA_REFRESH_RATE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
