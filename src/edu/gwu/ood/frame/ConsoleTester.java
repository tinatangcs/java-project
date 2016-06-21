package edu.gwu.ood.frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * Console for tester, it has extra:
 * debug tools:
 * 1. event edit&run
 * 2. load event list from DB
 *
 */
public class ConsoleTester extends Console{

	private Shell shell;
	public ConsoleTester(Composite parent, int mode) {
		super(parent, mode);
		
		shell = parent.getShell();
		this.initializeMenus();
		
	}
	
	/**
	 * initialize the menus for testing user
	 */
	private void initializeMenus(){
		Menu menuBar = new Menu(shell, SWT.BAR);
        MenuItem cascadeFileMenu = new MenuItem(menuBar, SWT.CASCADE);
        cascadeFileMenu.setText("&Tools");
        
        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
        cascadeFileMenu.setMenu(fileMenu);

        MenuItem debugItem = new MenuItem(fileMenu, SWT.PUSH);
        debugItem.setText("&Debug");
        shell.setMenuBar(menuBar);
        
        
        debugItem.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event e) {
				DebugEventDialog debugger = new DebugEventDialog(shell,SWT.None, sensorProvider);
				debugger.open();
			}
        	
        });
        
        MenuItem databaseItem = new MenuItem(fileMenu, SWT.PUSH);
        databaseItem.setText("&Load From DB");
        shell.setMenuBar(menuBar);
        
        
        databaseItem.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event e) {
				DebugEventListDialog debugger = new DebugEventListDialog(shell,SWT.None, sensorProvider);
				debugger.open();
			}
        	
        });
	}

}
