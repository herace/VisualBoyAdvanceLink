import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Canvas;

import sdljava.SDLMain;
import sdljava.SDLException;
import sdljava.event.SDLEvent;
import sdljava.event.SDLEventState;
import sdljava.event.SDLJoyAxisEvent;
import sdljava.event.SDLJoyButtonEvent;
import sdljava.event.SDLKeyboardEvent;
import sdljava.image.SDLImage;
import sdljava.joystick.SDLJoystick;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

public class main {

	protected Shell shlVisualboyadvanceLink;
	private Text RKey;
	public String dir = System.getProperty("user.home");
	public String dir2 = System.getProperty("user.home");
	public String dir3 = System.getProperty("user.home");
	public String dir4 = System.getProperty("user.home");
	public String dir5 = System.getProperty("user.home");
	public String dir6 = System.getProperty("user.home");
	private Label lblGamename;
	private Text SpeedKey;
	private Text CaptureKey;
	private Text DownKey;
	private Text UpKey;
	private Text RightKey;
	private Text LeftKey;
	private Text AKey;
	private Text BKey;
	private Text StartKey;
	private Text SelectKey;
	private Text LKey;
	private Text DIR2;
	private Text DIR3;
	private Text FILEB;
	private Canvas ScreenShotImg;
	private Combo SaveTypeValue;
	private Combo FlashSizeValue;
	private Button Fullscreen;
	private Button Interframe1;
	private Button Interframe2;
	private Button Interframe3;
	private Combo Video;
	private Combo FrameSkip;
	private Combo GBFrameSkip;
	private Button AutoSkip;
	private Combo GfxFilter;
	private Button SoundOn;
	private Button Misc1;
	private Button Misc2;
	private Button Misc3;
	private Button GP;
	private Button PauseIfInactive;
	private Button ColorOption;
	private Button ShowSpeed1;
	private Button ShowSpeed2;
	private Button ShowSpeed3;
	private Button Mmx;
	private Button RTC;
	private Button NoLink;
	private Button Server;
	private Button Client;
	private Combo IP1;
	private Combo IP2;
	private Combo IP3;
	private Combo IP4;
	private Combo NumPlayers;
	private Text Timeout;
	private Button DSM;
	private Button BorderAuto;
	private Button Khz1;
	private Button Khz2;
	private Button Khz3;
	private Combo Volume;
	public String game;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//SWTResourceManager.getImage(main.class, "/Images/NS.png")
			main window = new main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlVisualboyadvanceLink.open();
		shlVisualboyadvanceLink.layout();
		while (!shlVisualboyadvanceLink.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public String GetValue()
		{
		String hex="";
		
		try {
			SDLSurface framebuffer;
			SDLMain.init(SDLMain.SDL_INIT_JOYSTICK | SDLMain.SDL_INIT_VIDEO);
			framebuffer = SDLVideo.setVideoMode(200, 100, 32, SDLVideo.SDL_HWSURFACE|SDLVideo.SDL_DOUBLEBUF);
			
			URL rsrcUrl = Thread.currentThread().getContextClassLoader().getResource("Images/Key.png");
			SDLSurface img = SDLImage.load(rsrcUrl);
			SDLRect imgPos = new SDLRect(0, 0);
			
			img.blitSurface(framebuffer, imgPos);
			framebuffer.flip();
			
			SDLJoystick joystick;			
			SDLEvent.joystickEventState(SDLEventState.ENABLE);
		    int count = SDLJoystick.numJoysticks();
		    if (count < 1)
		    	System.out.println("No joystick devices available");
		    else
		    	joystick = SDLJoystick.joystickOpen(0);
		    	    
		    while (true) {
				SDLEvent event = SDLEvent.waitEvent();
	            
				img.blitSurface(framebuffer, imgPos);
				framebuffer.flip();
				
				if (event instanceof SDLKeyboardEvent) {
				    SDLKeyboardEvent keyboardEvent = (SDLKeyboardEvent) event;
				    int value = keyboardEvent.getSym();
				    hex = Integer.toHexString(value);
				    break;
				}

				else if (event instanceof SDLJoyAxisEvent) {
				    int num_axis = ((SDLJoyAxisEvent) event).getAxis();
				    int value = ((SDLJoyAxisEvent) event).getValue();
				    if (value < 0)
				    	value = 0;
				    else
				    	value = 1;
				    hex = "100"+Integer.toHexString(2*num_axis+value);
				    break;
				}

				else if (event instanceof SDLJoyButtonEvent) {
				    int value = ((SDLJoyButtonEvent) event).getButton();
				    hex = "108"+Integer.toHexString(value);
				    break;
				}

		    }
		    img.freeSurface();
		    framebuffer.freeSurface();
		    SDLMain.quit();
		    
		    return hex;
		} catch (SDLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hex;
		
		}
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlVisualboyadvanceLink = new Shell(SWT.SHELL_TRIM & (~SWT.RESIZE));
		shlVisualboyadvanceLink.setImage(SWTResourceManager.getImage(main.class, "/Images/vbal.png"));
		shlVisualboyadvanceLink.setSize(450, 300);
		shlVisualboyadvanceLink.setText("VisualBoyAdvance Link");
		
		TabFolder tabFolder = new TabFolder(shlVisualboyadvanceLink, SWT.NONE);
		tabFolder.setBounds(10, 10, 426, 254);
		
		TabItem tbtmGame = new TabItem(tabFolder, SWT.NONE);
		tbtmGame.setText("Game");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmGame.setControl(composite);
		
		Button btnSelectAGame = new Button(composite, SWT.NONE);
		btnSelectAGame.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shlVisualboyadvanceLink, SWT.OPEN);
		        fd.setText("Select a game");
		        fd.setFilterPath(dir);
		        String[] filterExt = { "*.gba", "*.zip", "*.*" };
		        fd.setFilterExtensions(filterExt);
		        String selected = fd.open();
		        if (selected == null)
		        	lblGamename.setText(lblGamename.getText());
		        else
		        	{
		        	lblGamename.setText(fd.getFileName());
		        	game = selected;
		        	}
		        dir=fd.getFilterPath();
		        
		        File fichier = new File(game + ".png");
		        if (fichier.exists())
		        	ScreenShotImg.setBackgroundImage(SWTResourceManager.getImage(game + ".png"));
		        else
		        	ScreenShotImg.setBackgroundImage(SWTResourceManager.getImage(main.class, "/Images/NS.png"));
		        
		        FileInputStream fstream;
				try {
					fstream = new FileInputStream("/opt/visualboyadvancelink/etc/" + lblGamename.getText() + "cfg");
					DataInputStream in = new DataInputStream(fstream);
					BufferedReader lire_ligne = new BufferedReader(new InputStreamReader(in));
		        
					String texte;
					int value;
					texte = lire_ligne.readLine();
					value = Integer.parseInt(texte);
					SaveTypeValue.select(value);
					texte = lire_ligne.readLine();
					FlashSizeValue.select(value);
					
					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				
				
			}
		});
		btnSelectAGame.setBounds(10, 10, 191, 27);
		btnSelectAGame.setText("Select a game");
		
		Button btnPlay = new Button(composite, SWT.NONE);
		btnPlay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					boolean test;
					int val;
					
					File fichier = new File("/opt/visualboyadvancelink/etc/VisualBoyAdvance.cfg");
					fichier.createNewFile();
					FileWriter fstream = new FileWriter("/opt/visualboyadvancelink/etc/VisualBoyAdvance.cfg");
					
					BufferedWriter ecrire_fichier = new BufferedWriter(fstream);

					ecrire_fichier.write("Joy0_Left=" + LeftKey.getText() + "\n");
					ecrire_fichier.write("Joy0_Right=" + RightKey.getText() + "\n");
					ecrire_fichier.write("Joy0_Up=" + UpKey.getText() + "\n");
					ecrire_fichier.write("Joy0_Down=" + DownKey.getText() + "\n");
					ecrire_fichier.write("Joy0_A=" + AKey.getText() + "\n");
					ecrire_fichier.write("Joy0_B=" + BKey.getText() + "\n");
					ecrire_fichier.write("Joy0_L=" + LKey.getText() + "\n");
					ecrire_fichier.write("Joy0_R=" + RKey.getText() + "\n");
					ecrire_fichier.write("Joy0_Start=" + StartKey.getText() + "\n");
					ecrire_fichier.write("Joy0_Select=" + SelectKey.getText() + "\n");
					ecrire_fichier.write("Joy0_Speed=" + SpeedKey.getText() + "\n");
					ecrire_fichier.write("Joy0_Capture=" + CaptureKey.getText() + "\n");
					
					ecrire_fichier.write("frameSkip=" + FrameSkip.getSelectionIndex() + "\n");
					ecrire_fichier.write("gbFrameSkip=" + GBFrameSkip.getSelectionIndex() + "\n");
					ecrire_fichier.write("video=" + Video.getSelectionIndex() + "\n");
					
					test = Fullscreen.getSelection();
					val = test?1:0;
					ecrire_fichier.write("fullScreen=" + Integer.toString(val) + "\n");
					
					test = Mmx.getEnabled();
					val = test?0:1;
					ecrire_fichier.write("disableMMX=" + Integer.toString(val) + "\n");
	
					ecrire_fichier.write("filter=" + Integer.toHexString(GfxFilter.getSelectionIndex()) + "\n");
					
					test = DSM.getSelection();
					val = test?1:0;
					ecrire_fichier.write("disableStatus=" + Integer.toString(val) + "\n");

					test = BorderAuto.getSelection();
					val = test?1:0;
					ecrire_fichier.write("borderOn=" + Integer.toString(val) + "\n");
					
					test = ColorOption.getSelection();
					val = test?1:0;
					ecrire_fichier.write("colorOption=" + Integer.toString(val) + "\n");
					
					ecrire_fichier.write("captureFormat=0\n");			
					
					if(Khz1.getSelection())
						ecrire_fichier.write("soundQuality=1\n");
					else if(Khz2.getSelection())
						ecrire_fichier.write("soundQuality=2\n");
					else
						ecrire_fichier.write("soundQuality=4\n");

					test = Misc3.getSelection();
					val = test?1:0;
					ecrire_fichier.write("soundEcho=" + Integer.toString(val) + "\n");
					
					test = Misc2.getSelection();
					val = test?1:0;
					ecrire_fichier.write("soundLowPass=" + Integer.toString(val) + "\n");
					
					test = Misc1.getSelection();
					val = test?1:0;
					ecrire_fichier.write("soundReverse=" + Integer.toString(val) + "\n");
					
					ecrire_fichier.write("soundVolume=" + Volume.getSelectionIndex() + "\n");
					
					if(Interframe1.getSelection())
						ecrire_fichier.write("ifbType=0\n");
					else if(Interframe2.getSelection())
						ecrire_fichier.write("ifbType=1\n");
					else
						ecrire_fichier.write("ifbType=2\n");
					
					if(ShowSpeed1.getSelection())
						ecrire_fichier.write("showSpeed=0\n");
					else if(ShowSpeed2.getSelection())
						ecrire_fichier.write("showSpeed=1\n");
					else
						ecrire_fichier.write("showSpeed=2\n");

					test = AutoSkip.getSelection();
					val = test?1:0;
					ecrire_fichier.write("autoFrameSkip=" + Integer.toString(val) + "\n");
					
					test = PauseIfInactive.getSelection();
					val = test?1:0;
					ecrire_fichier.write("pauseWhenInactive=" + Integer.toString(val) + "\n");
					
					test = GP.getSelection();
					val = test?1:0;
					ecrire_fichier.write("agbPrint=" + Integer.toString(val) + "\n");
					
					test = RTC.getSelection();
					val = test?1:0;
					ecrire_fichier.write("rtcEnabled=" + Integer.toString(val) + "\n");
					
					test = SoundOn.getSelection();
					val = test?0:1;
					ecrire_fichier.write("soundOff=" + Integer.toString(val) + "\n");
					
					test = BorderAuto.getSelection();
					val = test?1:0;
					ecrire_fichier.write("borderAutomatic=" + Integer.toString(val) + "\n");			
					
					if(FILEB.getText().isEmpty())
						{
						ecrire_fichier.write("useBios=0\n");
						ecrire_fichier.write("biosFile=none\n");
						}
					else
						{
						ecrire_fichier.write("useBios=1\n");
						ecrire_fichier.write("biosFile=" + FILEB.getText() + "\n");
						}
				
					ecrire_fichier.write("captureDir=" + DIR3.getText() + "\n");
					ecrire_fichier.write("batteryDir=" + DIR2.getText() + "\n");
				
					
					ecrire_fichier.close();
					fstream.close();	
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					boolean test;
					int val;
					
					File fichier = new File("/opt/visualboyadvancelink/etc/" + lblGamename.getText() + "cfg");
					fichier.createNewFile();
					FileWriter fstream = new FileWriter("/opt/visualboyadvancelink/etc/" + lblGamename.getText() + "cfg");
					
					BufferedWriter ecrire_fichier = new BufferedWriter(fstream);
					ecrire_fichier.write(SaveTypeValue.getSelectionIndex()+"\n");
					ecrire_fichier.write(FlashSizeValue.getSelectionIndex()+"\n");
					
					ecrire_fichier.close();
					fstream.close();	
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				Process GBA_exe;
				
				try {
					if(NoLink.getSelection()==true)
						GBA_exe = Runtime.getRuntime().exec("xterm -e /opt/visualboyadvancelink/bin/VisualBoyAdvance -c /opt/visualboyadvancelink/etc/VisualBoyAdvance.cfg --save-type=" + SaveTypeValue.getSelectionIndex() + " --flash-size=" + FlashSizeValue.getSelectionIndex() + " " + game );
					else if(Server.getSelection()==true)
						GBA_exe = Runtime.getRuntime().exec("xterm -e /opt/visualboyadvancelink/bin/VisualBoyAdvance -c /opt/visualboyadvancelink/etc/VisualBoyAdvance.cfg --save-type=" + SaveTypeValue.getSelectionIndex() + " --flash-size=" + FlashSizeValue.getSelectionIndex() + " --link 0 --linknum " + (NumPlayers.getSelectionIndex()+2) + " --linktime " + Timeout.getText() + " " + game );
					else
						GBA_exe = Runtime.getRuntime().exec("xterm -e /opt/visualboyadvancelink/bin/VisualBoyAdvance -c /opt/visualboyadvancelink/etc/VisualBoyAdvance.cfg --save-type=" + SaveTypeValue.getSelectionIndex() + " --flash-size=" + FlashSizeValue.getSelectionIndex() + " --link 1 --linkaddr " + IP1.getSelectionIndex() + "." + IP2.getSelectionIndex() + "." + IP3.getSelectionIndex() + "." + IP4.getSelectionIndex() + " --linktime " + Timeout.getText() + " " + game );			
				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		btnPlay.setText("Play !");
		btnPlay.setBounds(219, 10, 191, 27);
		
		ScreenShotImg = new Canvas(composite, SWT.NONE);
		ScreenShotImg.setBackgroundImage(SWTResourceManager.getImage(main.class, "/Images/NS.png"));
		ScreenShotImg.setBounds(10, 66, 191, 143);
		
		Label lblGame = new Label(composite, SWT.NONE);
		lblGame.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblGame.setBounds(10, 43, 57, 17);
		lblGame.setText("Game :");
		
		lblGamename = new Label(composite, SWT.NONE);
		lblGamename.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.NORMAL));
		lblGamename.setBounds(73, 43, 337, 17);
		
		Label lblSaveType = new Label(composite, SWT.NONE);
		lblSaveType.setText("Save type :");
		lblSaveType.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblSaveType.setBounds(207, 66, 75, 17);
		
		SaveTypeValue = new Combo(composite, SWT.NONE);
		SaveTypeValue.setItems(new String[] {"Automatic", "EEPROM", "SRAM", "Flash", "EEPROM+Sensor", "NONE"});
		SaveTypeValue.setBounds(207, 89, 203, 27);
		SaveTypeValue.select(0);
		
		Label lblFlashSize = new Label(composite, SWT.NONE);
		lblFlashSize.setText("Flash size :");
		lblFlashSize.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblFlashSize.setBounds(207, 122, 75, 17);
		
		FlashSizeValue = new Combo(composite, SWT.NONE);
		FlashSizeValue.setItems(new String[] {"64K Flash", "128K Flash"});
		FlashSizeValue.setBounds(207, 145, 203, 27);
		FlashSizeValue.select(0);
		
		TabItem tbtmGraphic = new TabItem(tabFolder, SWT.NONE);
		tbtmGraphic.setText("Graphic");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmGraphic.setControl(composite_1);
		
		Fullscreen = new Button(composite_1, SWT.CHECK);
		Fullscreen.setText("Fullscreen");
		Fullscreen.setBounds(10, 10, 104, 21);
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setText("Interframe :");
		label.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		label.setBounds(10, 43, 79, 17);
		
		Interframe1 = new Button(composite_1, SWT.RADIO);
		Interframe1.setSelection(true);
		Interframe1.setText("None");
		Interframe1.setBounds(10, 66, 102, 21);
		
		Interframe2 = new Button(composite_1, SWT.RADIO);
		Interframe2.setText("Motion Blur");
		Interframe2.setBounds(10, 90, 102, 21);
		
		Interframe3 = new Button(composite_1, SWT.RADIO);
		Interframe3.setText("Smart");
		Interframe3.setBounds(10, 114, 102, 21);
		
		Label lblVideo = new Label(composite_1, SWT.NONE);
		lblVideo.setText("Video :");
		lblVideo.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblVideo.setBounds(10, 153, 79, 17);
		
		Video = new Combo(composite_1, SWT.NONE);
		Video.setVisibleItemCount(3);
		Video.setItems(new String[] {"x1", "x2", "x3", "x4"});
		Video.setBounds(10, 176, 104, 27);
		Video.select(1);
		
		Label lblFrameSkip = new Label(composite_1, SWT.NONE);
		lblFrameSkip.setText("Frame Skip :");
		lblFrameSkip.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblFrameSkip.setBounds(142, 10, 79, 17);
		
		FrameSkip = new Combo(composite_1, SWT.NONE);
		FrameSkip.setItems(new String[] {"0", "1", "2", "3", "4", "5"});
		FrameSkip.setBounds(140, 33, 104, 27);
		FrameSkip.select(0);
		
		Label lblGbFrameSkip = new Label(composite_1, SWT.NONE);
		lblGbFrameSkip.setText("GB Frame Skip :");
		lblGbFrameSkip.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblGbFrameSkip.setBounds(142, 70, 102, 17);
		
		GBFrameSkip = new Combo(composite_1, SWT.NONE);
		GBFrameSkip.setItems(new String[] {"0", "1", "2", "3", "4", "5"});
		GBFrameSkip.setBounds(140, 92, 104, 27);
		GBFrameSkip.select(0);
		
		AutoSkip = new Button(composite_1, SWT.CHECK);
		AutoSkip.setText("Auto Skip");
		AutoSkip.setBounds(140, 130, 104, 21);
		
		Label lblGfxFilter = new Label(composite_1, SWT.NONE);
		lblGfxFilter.setText("Gfx Filter :");
		lblGfxFilter.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblGfxFilter.setBounds(266, 10, 79, 17);
		
		GfxFilter = new Combo(composite_1, SWT.NONE);
		GfxFilter.setVisibleItemCount(5);
		GfxFilter.setItems(new String[] {"None", "TV Mode", "2xSai", "Super 2xSai", "Super Eagle", "Pixelate", "Motion Blur", "AMAME2x", "Simple2x", "Bilinear", "Bilinear+", "hq2x", "lq2x"});
		GfxFilter.setBounds(263, 33, 147, 27);
		GfxFilter.select(0);
		
		TabItem tbtmSound = new TabItem(tabFolder, SWT.NONE);
		tbtmSound.setText("Sound");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmSound.setControl(composite_2);
		
		SoundOn = new Button(composite_2, SWT.CHECK);
		SoundOn.setSelection(true);
		SoundOn.setText("Sound On");
		SoundOn.setBounds(10, 10, 104, 21);
		
		Label lblQuality = new Label(composite_2, SWT.NONE);
		lblQuality.setText("Quality :");
		lblQuality.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblQuality.setBounds(10, 43, 79, 17);
		
		Khz1 = new Button(composite_2, SWT.RADIO);
		Khz1.setText("44 KHz");
		Khz1.setSelection(true);
		Khz1.setBounds(10, 66, 102, 21);
		
		Khz2 = new Button(composite_2, SWT.RADIO);
		Khz2.setText("22 KHz");
		Khz2.setBounds(10, 90, 102, 21);
		
		Khz3 = new Button(composite_2, SWT.RADIO);
		Khz3.setText("11 KHz");
		Khz3.setBounds(10, 114, 102, 21);
		
		Label lblMisc = new Label(composite_2, SWT.NONE);
		lblMisc.setText("Misc :");
		lblMisc.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblMisc.setBounds(122, 10, 79, 17);
		
		Misc1 = new Button(composite_2, SWT.CHECK);
		Misc1.setBounds(118, 33, 126, 21);
		Misc1.setText("Reverse Stereo");
		
		Misc2 = new Button(composite_2, SWT.CHECK);
		Misc2.setText("Low Pass Filter");
		Misc2.setBounds(118, 57, 126, 21);
		
		Misc3 = new Button(composite_2, SWT.CHECK);
		Misc3.setText("Echo");
		Misc3.setBounds(118, 81, 126, 21);
		
		Label lblVolume = new Label(composite_2, SWT.NONE);
		lblVolume.setText("Volume :");
		lblVolume.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblVolume.setBounds(122, 114, 79, 17);
		
		Volume = new Combo(composite_2, SWT.NONE);
		Volume.setVisibleItemCount(3);
		Volume.setItems(new String[] {"x1", "x2", "x3", "x4"});
		Volume.setBounds(122, 137, 104, 27);
		Volume.select(0);
		
		TabItem tbtmKeys = new TabItem(tabFolder, SWT.NONE);
		tbtmKeys.setText("Keys");
		
		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tbtmKeys.setControl(composite_3);
		
		LeftKey = new Text(composite_3, SWT.BORDER);
		LeftKey.setText("114");
		LeftKey.setBounds(64, 12, 48, 27);
		
		RightKey = new Text(composite_3, SWT.BORDER);
		RightKey.setText("113");
		RightKey.setBounds(64, 45, 48, 27);
		
		UpKey = new Text(composite_3, SWT.BORDER);
		UpKey.setText("111");
		UpKey.setBounds(64, 78, 48, 27);
		
		DownKey = new Text(composite_3, SWT.BORDER);
		DownKey.setText("112");
		DownKey.setBounds(64, 111, 48, 27);
		
		SpeedKey = new Text(composite_3, SWT.BORDER);
		SpeedKey.setText("20");
		SpeedKey.setBounds(64, 144, 48, 27);
		
		CaptureKey = new Text(composite_3, SWT.BORDER);
		CaptureKey.setText("125");
		CaptureKey.setBounds(64, 177, 48, 27);
		
		AKey = new Text(composite_3, SWT.BORDER);
		AKey.setText("7a");
		AKey.setBounds(362, 12, 48, 27);
		
		BKey = new Text(composite_3, SWT.BORDER);
		BKey.setText("78");
		BKey.setBounds(362, 45, 48, 27);
		
		StartKey = new Text(composite_3, SWT.BORDER);
		StartKey.setText("d");
		StartKey.setBounds(362, 78, 48, 27);
		
		SelectKey = new Text(composite_3, SWT.BORDER);
		SelectKey.setText("8");
		SelectKey.setBounds(362, 111, 48, 27);
		
		LKey = new Text(composite_3, SWT.BORDER);
		LKey.setText("61");
		LKey.setBounds(362, 144, 48, 27);
		
		RKey = new Text(composite_3, SWT.BORDER);
		RKey.setText("73");
		RKey.setBounds(362, 177, 48, 27);
		
		Label lblNewLabel = new Label(composite_3, SWT.NONE);
		lblNewLabel.setOrientation(SWT.RIGHT_TO_LEFT);
		lblNewLabel.setBounds(10, 16, 48, 17);
		lblNewLabel.setText("Left");
		
		Label lblRight = new Label(composite_3, SWT.NONE);
		lblRight.setText("Right");
		lblRight.setOrientation(SWT.RIGHT_TO_LEFT);
		lblRight.setBounds(10, 49, 48, 17);
		
		Label lblUp = new Label(composite_3, SWT.NONE);
		lblUp.setText("Up");
		lblUp.setOrientation(SWT.RIGHT_TO_LEFT);
		lblUp.setBounds(10, 82, 48, 17);
		
		Label lblDown = new Label(composite_3, SWT.NONE);
		lblDown.setText("Down");
		lblDown.setOrientation(SWT.RIGHT_TO_LEFT);
		lblDown.setBounds(10, 115, 48, 17);
		
		Label lblSpeed = new Label(composite_3, SWT.NONE);
		lblSpeed.setText("Speed");
		lblSpeed.setOrientation(SWT.RIGHT_TO_LEFT);
		lblSpeed.setBounds(10, 148, 48, 17);
		
		Label lblCapture = new Label(composite_3, SWT.NONE);
		lblCapture.setText("Capture");
		lblCapture.setOrientation(SWT.RIGHT_TO_LEFT);
		lblCapture.setBounds(10, 181, 48, 17);
		
		Label lblA = new Label(composite_3, SWT.NONE);
		lblA.setText("A");
		lblA.setOrientation(SWT.RIGHT_TO_LEFT);
		lblA.setBounds(333, 16, 23, 17);
		
		Label lblB = new Label(composite_3, SWT.NONE);
		lblB.setText("B");
		lblB.setOrientation(SWT.RIGHT_TO_LEFT);
		lblB.setBounds(333, 49, 23, 17);
		
		Label lblStart = new Label(composite_3, SWT.NONE);
		lblStart.setText("Start");
		lblStart.setOrientation(SWT.RIGHT_TO_LEFT);
		lblStart.setBounds(308, 82, 48, 17);
		
		Label lblSelect = new Label(composite_3, SWT.NONE);
		lblSelect.setText("Select");
		lblSelect.setOrientation(SWT.RIGHT_TO_LEFT);
		lblSelect.setBounds(321, 115, 35, 17);
		
		Label lblL = new Label(composite_3, SWT.NONE);
		lblL.setText("L");
		lblL.setOrientation(SWT.RIGHT_TO_LEFT);
		lblL.setBounds(333, 148, 23, 17);
		
		Label lblR = new Label(composite_3, SWT.NONE);
		lblR.setText("R");
		lblR.setOrientation(SWT.RIGHT_TO_LEFT);
		lblR.setBounds(333, 181, 23, 17);
		
		Button btnSpeed = new Button(composite_3, SWT.NONE);
		btnSpeed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SpeedKey.setText(GetValue());
			}
		});
		btnSpeed.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		btnSpeed.setBounds(118, 12, 86, 27);
		btnSpeed.setText("Speed");
		
		Button btnCapture = new Button(composite_3, SWT.NONE);
		btnCapture.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CaptureKey.setText(GetValue());
			}
		});
		btnCapture.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		btnCapture.setText("Capture");
		btnCapture.setBounds(227, 12, 86, 27);
		
		Button btnSelect = new Button(composite_3, SWT.NONE);
		btnSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectKey.setText(GetValue());
			}
		});
		btnSelect.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		btnSelect.setText("Select");
		btnSelect.setBounds(118, 177, 86, 27);
		
		Button btnStart = new Button(composite_3, SWT.NONE);
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StartKey.setText(GetValue());
			}
		});
		btnStart.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		btnStart.setText("Start");
		btnStart.setBounds(227, 177, 86, 27);
		
		Button btnL = new Button(composite_3, SWT.NONE);
		btnL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LKey.setText(GetValue());
			}
		});
		btnL.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		btnL.setText("L");
		btnL.setBounds(227, 45, 23, 27);
		
		Button btnR = new Button(composite_3, SWT.NONE);
		btnR.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RKey.setText(GetValue());
			}
		});
		btnR.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		btnR.setText("R");
		btnR.setBounds(290, 45, 23, 27);
		
		Button btnA = new Button(composite_3, SWT.NONE);
		btnA.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AKey.setText(GetValue());
			}
		});
		btnA.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		btnA.setText("A");
		btnA.setBounds(292, 111, 23, 27);
		
		Button btnB = new Button(composite_3, SWT.NONE);
		btnB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BKey.setText(GetValue());
			}
		});
		btnB.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		btnB.setText("B");
		btnB.setBounds(227, 144, 23, 27);
		
		Button btnU = new Button(composite_3, SWT.NONE);
		btnU.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UpKey.setText(GetValue());
			}
		});
		btnU.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		btnU.setText("Up");
		btnU.setBounds(140, 59, 42, 27);
		
		Button btnLe = new Button(composite_3, SWT.NONE);
		btnLe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LeftKey.setText(GetValue());
			}
		});
		btnLe.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		btnLe.setText("Left");
		btnLe.setBounds(118, 94, 42, 27);
		
		Button btnD = new Button(composite_3, SWT.NONE);
		btnD.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DownKey.setText(GetValue());
			}
		});
		btnD.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		btnD.setText("Down");
		btnD.setBounds(140, 127, 42, 27);
		
		Button btnRi = new Button(composite_3, SWT.NONE);
		btnRi.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RightKey.setText(GetValue());
			}
		});
		btnRi.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		btnRi.setText("Right");
		btnRi.setBounds(162, 94, 42, 27);
		
		TabItem tbtmOther = new TabItem(tabFolder, SWT.NONE);
		tbtmOther.setText("Other");
		
		Composite composite_4 = new Composite(tabFolder, SWT.NONE);
		tbtmOther.setControl(composite_4);
		
		ColorOption = new Button(composite_4, SWT.CHECK);
		ColorOption.setSelection(true);
		ColorOption.setBounds(10, 10, 104, 21);
		ColorOption.setText("Color Option");
		
		PauseIfInactive = new Button(composite_4, SWT.CHECK);
		PauseIfInactive.setText("Pause if inactive");
		PauseIfInactive.setBounds(10, 37, 125, 21);
		
		GP = new Button(composite_4, SWT.CHECK);
		GP.setText("GameBoy Printer");
		GP.setBounds(10, 64, 125, 21);
		
		DSM = new Button(composite_4, SWT.CHECK);
		DSM.setText("Disable status messages");
		DSM.setBounds(10, 91, 177, 21);
		
		Label lblShowSpeed = new Label(composite_4, SWT.NONE);
		lblShowSpeed.setText("Show Speed :");
		lblShowSpeed.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblShowSpeed.setBounds(193, 10, 88, 17);
		
		ShowSpeed1 = new Button(composite_4, SWT.RADIO);
		ShowSpeed1.setBounds(193, 33, 102, 21);
		ShowSpeed1.setText("None");
		
		ShowSpeed2 = new Button(composite_4, SWT.RADIO);
		ShowSpeed2.setSelection(true);
		ShowSpeed2.setText("Percentage");
		ShowSpeed2.setBounds(193, 57, 102, 21);
		
		ShowSpeed3 = new Button(composite_4, SWT.RADIO);
		ShowSpeed3.setText("None");
		ShowSpeed3.setBounds(193, 81, 102, 21);
		
		Mmx = new Button(composite_4, SWT.CHECK);
		Mmx.setBounds(191, 113, 104, 21);
		Mmx.setText("MMX");
		
		RTC = new Button(composite_4, SWT.CHECK);
		RTC.setText("RTC");
		RTC.setBounds(306, 113, 104, 21);
		
		BorderAuto = new Button(composite_4, SWT.CHECK);
		BorderAuto.setText("Border Auto");
		BorderAuto.setBounds(193, 140, 104, 21);
		
		TabItem tbtmPaths = new TabItem(tabFolder, SWT.NONE);
		tbtmPaths.setText("Paths");
		
		Composite composite_6 = new Composite(tabFolder, SWT.NONE);
		tbtmPaths.setControl(composite_6);
		
		Label lblGbaSavesbattery = new Label(composite_6, SWT.NONE);
		lblGbaSavesbattery.setText("GBA Saves (battery) :");
		lblGbaSavesbattery.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblGbaSavesbattery.setBounds(10, 10, 190, 17);
		
		Label lblScreenshotsDirectory = new Label(composite_6, SWT.NONE);
		lblScreenshotsDirectory.setText("Screenshots directory :");
		lblScreenshotsDirectory.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblScreenshotsDirectory.setBounds(10, 59, 190, 17);
		
		Label lblBiosFile = new Label(composite_6, SWT.NONE);
		lblBiosFile.setText("Bios file :");
		lblBiosFile.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblBiosFile.setBounds(10, 108, 190, 17);
		
		DIR2 = new Text(composite_6, SWT.BORDER);
		DIR2.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		DIR2.setBounds(10, 33, 400, 20);
		
		DIR3 = new Text(composite_6, SWT.BORDER);
		DIR3.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		DIR3.setBounds(10, 82, 400, 20);
		
		FILEB = new Text(composite_6, SWT.BORDER);
		FILEB.setText("none");
		FILEB.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		FILEB.setBounds(10, 131, 400, 20);
		
		Button button_5 = new Button(composite_6, SWT.NONE);
		button_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirdlg = new DirectoryDialog(shlVisualboyadvanceLink);
				dirdlg.setText("Select a directory for GBA saves");
				dirdlg.setFilterPath(dir3);
		        String selected = dirdlg.open();
		        if (selected == null)
		        	DIR2.setText(DIR2.getText());
		        else
		        	DIR2.setText(selected);
		        dir3=dirdlg.getFilterPath();
			}
		});
		button_5.setText("Change");
		button_5.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		button_5.setBounds(328, 10, 82, 22);
		
		Button button_6 = new Button(composite_6, SWT.NONE);
		button_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirdlg = new DirectoryDialog(shlVisualboyadvanceLink);
				dirdlg.setText("Select a directory for the screenshots");
				dirdlg.setFilterPath(dir6);
		        String selected = dirdlg.open();
		        if (selected == null)
		        	DIR3.setText(DIR3.getText());
		        else
		        	DIR3.setText(selected);
				dir6=dirdlg.getFilterPath();
			}
		});
		button_6.setText("Change");
		button_6.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		button_6.setBounds(328, 59, 82, 22);
		
		Button button_7 = new Button(composite_6, SWT.NONE);
		button_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shlVisualboyadvanceLink, SWT.OPEN);
		        fd.setText("Select a game");
		        fd.setFilterPath(dir2);
		        String[] filterExt = { "*.bin", "*.BIN" };
		        fd.setFilterExtensions(filterExt);
		        String selected = fd.open();
		        if (selected == null)
		        	FILEB.setText(FILEB.getText());
		        else
		        	FILEB.setText(selected);
		        System.out.println(selected);
		        dir2=fd.getFilterPath();
			}
		});
		button_7.setText("Change");
		button_7.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		button_7.setBounds(328, 108, 82, 22);
		
		TabItem tbtmLink = new TabItem(tabFolder, SWT.NONE);
		tbtmLink.setText("Link");
		
		Composite composite_5 = new Composite(tabFolder, SWT.NONE);
		tbtmLink.setControl(composite_5);
		
		NoLink = new Button(composite_5, SWT.RADIO);
		NoLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IP1.setEnabled(false);
				IP2.setEnabled(false);
				IP3.setEnabled(false);
				IP4.setEnabled(false);
				NumPlayers.setEnabled(false);
				Timeout.setEnabled(false);
			}
		});
		NoLink.setSelection(true);
		NoLink.setBounds(10, 10, 138, 21);
		NoLink.setText("No Link (1P Mode)");
		
		Server = new Button(composite_5, SWT.RADIO);
		Server.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IP1.setEnabled(false);
				IP2.setEnabled(false);
				IP3.setEnabled(false);
				IP4.setEnabled(false);
				NumPlayers.setEnabled(true);
				Timeout.setEnabled(true);
			}
		});
		Server.setText("Server");
		Server.setBounds(218, 10, 72, 21);
		
		Client = new Button(composite_5, SWT.RADIO);
		Client.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IP1.setEnabled(true);
				IP2.setEnabled(true);
				IP3.setEnabled(true);
				IP4.setEnabled(true);
				NumPlayers.setEnabled(false);
				Timeout.setEnabled(true);
			}
		});
		Client.setText("Client");
		Client.setBounds(346, 10, 64, 21);
		
		Label lblNewLabel_2 = new Label(composite_5, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblNewLabel_2.setBounds(10, 53, 72, 17);
		lblNewLabel_2.setText("IP Adress :");
		
		IP1 = new Combo(composite_5, SWT.NONE);
		IP1.setEnabled(false);
		IP1.setItems(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127", "128", "129", "130", "131", "132", "133", "134", "135", "136", "137", "138", "139", "140", "141", "142", "143", "144", "145", "146", "147", "148", "149", "150", "151", "152", "153", "154", "155", "156", "157", "158", "159", "160", "161", "162", "163", "164", "165", "166", "167", "168", "169", "170", "171", "172", "173", "174", "175", "176", "177", "178", "179", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189", "190", "191", "192", "193", "194", "195", "196", "197", "198", "199", "200", "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216", "217", "218", "219", "220", "221", "222", "223", "224", "225", "226", "227", "228", "229", "230", "231", "232", "233", "234", "235", "236", "237", "238", "239", "240", "241", "242", "243", "244", "245", "246", "247", "248", "249", "250", "251", "252", "253", "254", "255"});
		IP1.setBounds(86, 48, 72, 27);
		IP1.select(127);
		
		IP2 = new Combo(composite_5, SWT.NONE);
		IP2.setEnabled(false);
		IP2.setItems(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127", "128", "129", "130", "131", "132", "133", "134", "135", "136", "137", "138", "139", "140", "141", "142", "143", "144", "145", "146", "147", "148", "149", "150", "151", "152", "153", "154", "155", "156", "157", "158", "159", "160", "161", "162", "163", "164", "165", "166", "167", "168", "169", "170", "171", "172", "173", "174", "175", "176", "177", "178", "179", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189", "190", "191", "192", "193", "194", "195", "196", "197", "198", "199", "200", "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216", "217", "218", "219", "220", "221", "222", "223", "224", "225", "226", "227", "228", "229", "230", "231", "232", "233", "234", "235", "236", "237", "238", "239", "240", "241", "242", "243", "244", "245", "246", "247", "248", "249", "250", "251", "252", "253", "254", "255"});
		IP2.setBounds(170, 48, 72, 27);
		IP2.select(0);
		
		IP3 = new Combo(composite_5, SWT.NONE);
		IP3.setEnabled(false);
		IP3.setItems(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127", "128", "129", "130", "131", "132", "133", "134", "135", "136", "137", "138", "139", "140", "141", "142", "143", "144", "145", "146", "147", "148", "149", "150", "151", "152", "153", "154", "155", "156", "157", "158", "159", "160", "161", "162", "163", "164", "165", "166", "167", "168", "169", "170", "171", "172", "173", "174", "175", "176", "177", "178", "179", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189", "190", "191", "192", "193", "194", "195", "196", "197", "198", "199", "200", "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216", "217", "218", "219", "220", "221", "222", "223", "224", "225", "226", "227", "228", "229", "230", "231", "232", "233", "234", "235", "236", "237", "238", "239", "240", "241", "242", "243", "244", "245", "246", "247", "248", "249", "250", "251", "252", "253", "254", "255"});
		IP3.setBounds(255, 48, 72, 27);
		IP3.select(0);
		
		IP4 = new Combo(composite_5, SWT.NONE);
		IP4.setEnabled(false);
		IP4.setItems(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127", "128", "129", "130", "131", "132", "133", "134", "135", "136", "137", "138", "139", "140", "141", "142", "143", "144", "145", "146", "147", "148", "149", "150", "151", "152", "153", "154", "155", "156", "157", "158", "159", "160", "161", "162", "163", "164", "165", "166", "167", "168", "169", "170", "171", "172", "173", "174", "175", "176", "177", "178", "179", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189", "190", "191", "192", "193", "194", "195", "196", "197", "198", "199", "200", "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216", "217", "218", "219", "220", "221", "222", "223", "224", "225", "226", "227", "228", "229", "230", "231", "232", "233", "234", "235", "236", "237", "238", "239", "240", "241", "242", "243", "244", "245", "246", "247", "248", "249", "250", "251", "252", "253", "254", "255"});
		IP4.setBounds(338, 48, 72, 27);
		IP4.select(1);
		
		Label label_6 = new Label(composite_5, SWT.NONE);
		label_6.setText(".");
		label_6.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		label_6.setBounds(162, 58, 8, 17);
		
		Label label_7 = new Label(composite_5, SWT.NONE);
		label_7.setText(".");
		label_7.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		label_7.setBounds(246, 58, 8, 17);
		
		Label label_8 = new Label(composite_5, SWT.NONE);
		label_8.setText(".");
		label_8.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		label_8.setBounds(331, 58, 6, 17);
		
		Label lblNumberOfPlayers = new Label(composite_5, SWT.NONE);
		lblNumberOfPlayers.setText("Number of players :");
		lblNumberOfPlayers.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblNumberOfPlayers.setBounds(10, 87, 138, 17);
		
		NumPlayers = new Combo(composite_5, SWT.NONE);
		NumPlayers.setEnabled(false);
		NumPlayers.setItems(new String[] {"2 players (1P, 2P)", "3 players (1P, 2P, 3P)", "4 players (1P, 2P, 3P, 4P)"});
		NumPlayers.setBounds(170, 81, 240, 27);
		NumPlayers.select(0);
		
		Label lblTimeoutms = new Label(composite_5, SWT.NONE);
		lblTimeoutms.setText("Timeout (ms) * :");
		lblTimeoutms.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblTimeoutms.setBounds(10, 117, 138, 17);
		
		Timeout = new Text(composite_5, SWT.BORDER);
		Timeout.setEnabled(false);
		Timeout.setText("1000");
		Timeout.setBounds(170, 112, 240, 27);
		
		Label lblRecommendedValues = new Label(composite_5, SWT.NONE);
		lblRecommendedValues.setText("* Recommended values  :");
		lblRecommendedValues.setFont(SWTResourceManager.getFont("Droid Sans", 10, SWT.BOLD));
		lblRecommendedValues.setBounds(10, 192, 171, 17);
		
		Label lblForPcs = new Label(composite_5, SWT.NONE);
		lblForPcs.setBounds(188, 192, 222, 17);
		lblForPcs.setText("1000 for PCs, 500 for notebooks");
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("About");
		
		Composite composite_7 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite_7);
		
		Canvas canvas = new Canvas(composite_7, SWT.NONE);
		canvas.setBackgroundImage(SWTResourceManager.getImage(main.class, "/Images/Logo.png"));
		canvas.setBounds(10, 10, 400, 100);
		
		Label lblNewLabel_1 = new Label(composite_7, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("Droid Sans", 9, SWT.BOLD));
		lblNewLabel_1.setBounds(10, 116, 251, 17);
		lblNewLabel_1.setText("Created by Jacques-Olivier IMBERT - V. 5.0");
		
		Label lblHttpjaimegemelofreefr = new Label(composite_7, SWT.NONE);
		lblHttpjaimegemelofreefr.setText("http://jaime.gemelo.free.fr");
		lblHttpjaimegemelofreefr.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		lblHttpjaimegemelofreefr.setAlignment(SWT.RIGHT);
		lblHttpjaimegemelofreefr.setBounds(269, 116, 141, 17);
		
		Label lblBasedOnThe = new Label(composite_7, SWT.NONE);
		lblBasedOnThe.setText("Based on the GUI of VBA Express project of Asher256");
		lblBasedOnThe.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		lblBasedOnThe.setAlignment(SWT.CENTER);
		lblBasedOnThe.setBounds(10, 139, 400, 17);
		
		Label lblLinkSupportBased = new Label(composite_7, SWT.NONE);
		lblLinkSupportBased.setText("Link support based on denopqrihg works (Windows version)");
		lblLinkSupportBased.setFont(SWTResourceManager.getFont("Droid Sans", 8, SWT.NORMAL));
		lblLinkSupportBased.setAlignment(SWT.CENTER);
		lblLinkSupportBased.setBounds(10, 156, 400, 17);
		
		Label lblThanksToYou = new Label(composite_7, SWT.NONE);
		lblThanksToYou.setText("Thank you guys for sharing your works \nthat helped me to create this Linux version ;) !");
		lblThanksToYou.setFont(SWTResourceManager.getFont("Droid Sans", 9, SWT.NORMAL));
		lblThanksToYou.setAlignment(SWT.CENTER);
		lblThanksToYou.setBounds(10, 179, 400, 30);
		
		try {
			File test = new File("/opt/visualboyadvancelink/etc/VisualBoyAdvance.cfg");
			if (test.exists())
			{
			FileInputStream fstream = new FileInputStream("/opt/visualboyadvancelink/etc/VisualBoyAdvance.cfg");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader lire_ligne = new BufferedReader(new InputStreamReader(in));
        
			String texte[];
			int value;
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			LeftKey.setText(texte[0]);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			RightKey.setText(texte[0]);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			UpKey.setText(texte[0]);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			DownKey.setText(texte[0]);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			AKey.setText(texte[0]);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			BKey.setText(texte[0]);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			LKey.setText(texte[0]);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			RKey.setText(texte[0]);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			StartKey.setText(texte[0]);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			SelectKey.setText(texte[0]);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			SpeedKey.setText(texte[0]);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			CaptureKey.setText(texte[0]);
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			FrameSkip.select(Integer.parseInt(texte[0]));
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			GBFrameSkip.select(Integer.parseInt(texte[0]));
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			Video.select(Integer.parseInt(texte[0]));

			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				Fullscreen.setSelection(false);
			else
				Fullscreen.setSelection(true);
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				Mmx.setSelection(false);
			else
				Mmx.setSelection(true);
					
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			GfxFilter.select(Long.decode(texte[0]).intValue());
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				DSM.setSelection(false);
			else
				DSM.setSelection(true);
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				BorderAuto.setSelection(false);
			else
				BorderAuto.setSelection(true);
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				ColorOption.setSelection(false);
			else
				ColorOption.setSelection(true);
			
			texte = lire_ligne.readLine().split("="); System.out.println(texte[0]);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if(value==1)
				{
				Khz1.setSelection(true);
				Khz2.setSelection(false);
				Khz3.setSelection(false);
				}
			else if(value==2)
				{
				Khz1.setSelection(false);
				Khz2.setSelection(true);
				Khz3.setSelection(false);
				}
			else
				{
				Khz1.setSelection(false);
				Khz2.setSelection(false);
				Khz3.setSelection(true);
				}
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				Misc3.setSelection(false);
			else
				Misc3.setSelection(true);

			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				Misc2.setSelection(false);
			else
				Misc2.setSelection(true);

			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				Misc1.setSelection(false);
			else
				Misc1.setSelection(true);
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if(value==0)
				{
				Interframe1.setSelection(true);
				Interframe2.setSelection(false);
				Interframe3.setSelection(false);
				}
			else if(value==1)
				{
				Interframe1.setSelection(false);
				Interframe2.setSelection(true);
				Interframe3.setSelection(false);
				}
			else
				{
				Interframe1.setSelection(false);
				Interframe2.setSelection(false);
				Interframe3.setSelection(true);
				}
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if(value==0)
				{
				ShowSpeed1.setSelection(true);
				ShowSpeed2.setSelection(false);
				ShowSpeed3.setSelection(false);
				}
			else if(value==1)
				{
				ShowSpeed1.setSelection(false);
				ShowSpeed2.setSelection(true);
				ShowSpeed3.setSelection(false);
				}
			else
				{
				ShowSpeed1.setSelection(false);
				ShowSpeed2.setSelection(false);
				ShowSpeed3.setSelection(true);
				}
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				AutoSkip.setSelection(false);
			else
				AutoSkip.setSelection(true);
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				PauseIfInactive.setSelection(false);
			else
				PauseIfInactive.setSelection(true);
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				GP.setSelection(false);
			else
				GP.setSelection(true);
	
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				RTC.setSelection(false);
			else
				RTC.setSelection(true);
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0); System.out.println(texte[0]);
			value = Integer.parseInt(texte[0]);
			if (value == 0)
				SoundOn.setSelection(true);
			else
				SoundOn.setSelection(false);
			
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			texte = lire_ligne.readLine().split("=")[1].split(",", 0);
			
			String[] file = lire_ligne.readLine().split("=");
			System.out.println(file[0]);
			if(file.length>1)
				FILEB.setText(file[1]);
			
			file = lire_ligne.readLine().split("=");
			if(file.length>1)
				DIR3.setText(file[1]);
			
			file = lire_ligne.readLine().split("="); 
			if(file.length>1)
				DIR2.setText(file[1]);
			
			lire_ligne.close();
			in.close();
			fstream.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	}
}
