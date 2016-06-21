package edu.gwu.ood.system;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.gwu.ood.device.CameraProvider;
import edu.gwu.ood.device.DeviceProvider;
import edu.gwu.ood.device.SensorProvider;
import edu.gwu.ood.device.model.DataModel;
import edu.gwu.ood.frame.*;

/**
 * the start point of our application.
 * It initializes Providers and the frame.
 *
 */
public class MonitorSystem {

	
	public Display display;
	
	public DeviceProvider sensorProvider,cameraProvider;
	
	protected Shell shell;
	
	public MonitorSystem(Display display, String type) {

		shell = new Shell(display);
		shell.setText("Mars Ice House 2");
		shell.setSize(910, 470);

		this.display = display;
		
		shell.setLayout(new FillLayout());
		
		sensorProvider = new SensorProvider();
		
		Console console;
		
		if(type!=null &&type.equals("tester")){
			/**
			 * one type of console
			 */
			console = new ConsoleTester(shell,SWT.NONE);
		}else{
			/**
			 * another type of console
			 */
			console = new ConsoleOperator(shell,SWT.NONE);
		}

		console.setSensorProvider(sensorProvider);
		
		
		for(String name: sensorProvider.getDataModelNames()){
			DataModel model = sensorProvider.getDataModel(name);
			console.addDataModel(model, Console.TARGET_INDICATOR);
		}
		
		cameraProvider = new CameraProvider();
		for(String name: cameraProvider.getDataModelNames()){
			DataModel model = cameraProvider.getDataModel(name);
			console.addDataModel(model, Console.TARGET_CAMERA);
		}
		
		shell.open();


		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		sensorProvider.destory();
		cameraProvider.destory();
		
		
	}
	
	
	public static void main(String args[]) {
		Display display = Display.getDefault();
		String type = "tester";
		if(args!=null && args.length>0){
			type = args[0];
		}
		new MonitorSystem(display,type);
	}
}
