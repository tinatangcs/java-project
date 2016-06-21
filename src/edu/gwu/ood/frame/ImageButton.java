package edu.gwu.ood.frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class ImageButton extends Canvas
{
    private Color   textColor;
    private Image   image;
    private String  text = "";
    private int     width;
    private int     height;

    private boolean isHovering = false;
    
    public ImageButton(Composite parent, int style)
    {
        super(parent, SWT.NO_BACKGROUND | SWT.TRANSPARENT);

        textColor = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

        /* Add dispose listener for the image */
        addListener(SWT.Dispose, new Listener()
        {
            @Override
            public void handleEvent(Event arg0)
            {
                if (image != null)
                    image.dispose();
            }
        });

        this.setBackgroundMode(SWT.INHERIT_FORCE);
        
        /* Add custom paint listener that paints the stars */
        addListener(SWT.Paint, new Listener()
        {
            @Override
            public void handleEvent(Event e)
            {
                paintControl(e);
            }
        });

        /* Listen for click events */
        addListener(SWT.MouseDown, new Listener()
        {
            @Override
            public void handleEvent(Event arg0)
            {
                redraw();
            }
        });
        
        this.addMouseMoveListener(new MouseMoveListener(){

			@Override
			public void mouseMove(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        this.setCursor(new Cursor(this.getDisplay(),SWT.CURSOR_HAND));
        
        this.addMouseTrackListener(new MouseTrackListener(){

			@Override
			public void mouseEnter(MouseEvent e) {
				isHovering = true;
			}

			@Override
			public void mouseExit(MouseEvent e) {
				isHovering = false;
			}

			@Override
			public void mouseHover(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
    }

    private void paintControl(Event event)
    {
        GC gc = event.gc;

        if (image != null)
        {
//        	gc.setAlpha(10);
        	if(isEnabled()){
        		gc.setAlpha(255);
        	}else{
        		gc.setAlpha(120);
        	}
        	int extra = isHovering?1:0;
            gc.drawImage(image, 0,0,image.getBounds().width,image.getBounds().height,1-extra,1-extra,width+2*extra,height+2*extra);
            Point textSize = gc.textExtent(text);
            
            gc.setForeground(textColor);
            gc.drawText(text, (width - textSize.x) / 2 + 1, (height - textSize.y) / 2 + 1, true);
        }
    }

    public void setImage(Image image)
    {
        this.image = new Image(Display.getDefault(), image, SWT.IMAGE_COPY);
        width = image.getBounds().width;
        height = image.getBounds().height;
        redraw();
    }
    
    

    @Override
   	public void setSize(int w, int h) {
   		this.width = w;
   		this.height = h;
   		super.setSize(w, h);
   		redraw();
//   		pack();
   	}
    
    @Override
   	public void setBounds(int x, int y, int w, int h) {
   		this.width = w;
   		this.height = h;
   		super.setBounds(x, y, w, h);
   		redraw();
//   		pack();
   	}

	public void setText(String text)
    {
        this.text = text;
        redraw();
    }
	
	

    @Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);
		redraw();
	}

	@Override
    public Point computeSize(int wHint, int hHint, boolean changed)
    {
        int overallWidth = width;
        int overallHeight = height;

        /* Consider hints */
        if (wHint != SWT.DEFAULT && wHint < overallWidth)
            overallWidth = wHint;

        if (hHint != SWT.DEFAULT && hHint < overallHeight)
            overallHeight = hHint;

        /* Return computed dimensions plus border */
        return new Point(overallWidth + 2, overallHeight + 2);
    }

    public static void main(String[] args)
    {
        Display display = Display.getDefault();
        Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));

        shell.setSize(30, 30);
        ImageButton button = new ImageButton(shell, SWT.NO_BACKGROUND|SWT.TRANSPARENT);
        
        
        button.setImage(new Image(display, "./fix_icon.png"));
        button.setText("");
//        button.setSize(30,30);
        button.setBounds(0, 0, 30, 30);
        button.setBackground(shell.getBackground());
        shell.pack();
        shell.open();

        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}