package edu.gwu.ood.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.gwu.ood.device.DeviceProvider;
import edu.gwu.ood.device.model.DataModel;

/**
 * Console for common use
 *
 */
public class Console extends Composite implements Observer{

	
	public static final int TARGET_INDICATOR = 1;
	public static final int TARGET_CAMERA = 2;
	
	/**
	 * all indicators for Sensor Data
	 */
	private List<IndicatorPanel> indicators;
	
	/**
	 * all cameras for Camera Data
	 */
	private List<CameraPanel> cameras;
	
	protected DeviceProvider sensorProvider;
	
	public Console(Composite parent, int mode) {
		super(parent, mode);
		this.setLayout(null);
		indicators = new ArrayList<IndicatorPanel>();
		cameras = new ArrayList<CameraPanel>();
	}
	
	public void setSensorProvider(DeviceProvider provider){
		this.sensorProvider = provider;
	}
	
	/**
	 * add one DataModel. The type of DataModel should be told 
	 * @param model
	 * @param target
	 */
	public void addDataModel(DataModel model, int target){
		if(target == TARGET_CAMERA){
			CameraPanel camera = new CameraPanel(this, SWT.NONE, model);
			cameras.add(camera);
			camera.setBounds(IndicatorPanel.WIDTH + 20 + ((cameras.size()-1)%2)*(5+CameraPanel.WIDTH), 10 + (5+CameraPanel.HEIGHT)*((int)((cameras.size()-1)/2)), CameraPanel.WIDTH, CameraPanel.HEIGHT);
			//let camera observe this model
			model.addObserver(camera);
			
		}else if(target == TARGET_INDICATOR){
			IndicatorPanel indicator = new IndicatorPanel(this,SWT.NONE,model);
			indicators.add(indicator);
			indicator.setBgColor(FrameUtil.INDICATOR_BGS[indicators.size()%FrameUtil.INDICATOR_BGS.length]);
			indicator.setFgColor(FrameUtil.INDICATOR_FGS[indicators.size()%FrameUtil.INDICATOR_FGS.length]);
			indicator.setSubTitleColor(FrameUtil.INDICATOR_TITLECOLORS[indicators.size()%FrameUtil.INDICATOR_TITLECOLORS.length]);
			indicator.setBounds(10, 10 + (5+IndicatorPanel.HEIGHT) * (indicators.size()-1), IndicatorPanel.WIDTH, IndicatorPanel.HEIGHT);
			//let indicator observe this model
			model.addObserver(indicator);
		}
		//this console need to observe this model too.
		model.addObserver(this);
	}

	@Override
	public void update(Observable o, Object msg) {
		
		if(msg!=null){
			if(msg.equals(FrameUtil.MSG_REMOVE_MODEL)){
				for(int i=0;i<cameras.size();i++){
					if(cameras.get(i).hasModel((DataModel)o)){
						cameras.get(i).removeModel();
					}
				}
			}
		}
		
		
		if(this.isDisposed()||this.getDisplay().isDisposed())
			return;
		this.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
//				pack();
			}
		});
		
	}

}
