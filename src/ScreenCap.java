
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import javax.swing.UIManager;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;

/**
 * @author Kevin
 */
public class ScreenCap extends JFrame implements ActionListener {

    public static void main(String[] args) throws Exception {
        Settings.os = new Scanner(System.getProperty("os.name")).next().toLowerCase();
        GlobalScreen.registerNativeHook();
        
        new ScreenCap();
    }

    JCheckBox tray;
    JCheckBox exit;
    JCheckBox startup;
    JCheckBox sound;
    JButton open;
    JButton website;
    MenuItem exitItem;
    MenuItem openItem;
    TrayIcon icon;

    public ScreenCap() throws Exception {
        Settings.InstalledPath = getClass().getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
        
        Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
        if (Settings.os.equals("windows")) {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }

        Settings.ReadSettings();

        if (Settings.TrayStart) {
            OpenTray();
        } else {
            OpenMenu();
        }
    }

    public void OpenTray() {
        if (SystemTray.isSupported()) {

            PopupMenu popup = new PopupMenu();
            SystemTray st = SystemTray.getSystemTray();

            Image image = null;

            try {
                image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/tray.gif"));
            } catch (NullPointerException e) {
                Settings.Log("/images/tray.gif not found - replacing with blank image");

                BufferedImage bi = new BufferedImage(1, 1, BufferedImage.OPAQUE);
                bi.setRGB(0, 0, 255);

                try {
                    ImageIO.write(bi, "GIF", new File("backup.gif"));
                } catch (IOException excep) {
                    Settings.Log("/images/tray.gif is an invalid path - closing");
                    System.exit(1);
                }

                try {
                    image = Toolkit.getDefaultToolkit().getImage("backup.gif");
                } catch (NullPointerException ee) {
                    Settings.Log("backup.gif not found - endless loop detected - closing");
                    System.exit(1);
                }
            }

            icon = new TrayIcon(image, "Screencapper", popup);

            icon.setImageAutoSize(true);

            exitItem = new MenuItem("Exit");
            openItem = new MenuItem("Open");

            exitItem.addActionListener(this);
            openItem.addActionListener(this);

            popup.add(openItem);
            popup.add(exitItem);

            try {
                st.add(icon);
            } catch (Exception e) {
                System.out.println(e.toString());
            }

        }
    }

    public void OpenMenu() {
        setResizable(false);
        setTitle("Screencapper");
        setVisible(true);
        setSize(250, 115);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        FlowLayout lm = new FlowLayout();
        lm.setAlignment(FlowLayout.LEFT);
        setLayout(lm);

        open = new JButton("Open settings location");
        open.setVisible(true);
        open.setEnabled(Desktop.isDesktopSupported());
        
        website = new JButton("Website");
        website.setVisible(true);
        website.setEnabled(Desktop.isDesktopSupported());
        
        tray = new JCheckBox();
        tray.setText("Send to tray on startup");
        tray.setEnabled(SystemTray.isSupported());
        tray.setSelected(Settings.TrayStart);

        exit = new JCheckBox();
        exit.setText("Exit on close");
        exit.setEnabled(SystemTray.isSupported());
        exit.setSelected(Settings.ExitClose);
        SetExitOnClose(exit);

        startup = new JCheckBox();
        startup.setText("Start application on login");
        startup.setEnabled(Settings.os.equals("windows"));
        startup.setSelected(Settings.Startup);
        
        sound = new JCheckBox();
        sound.setText("Sound");
        sound.setEnabled(true);
        sound.setSelected(Settings.Sound);
        
        add(open);
        add(website);
        add(tray);
        add(exit);
        add(startup);
        add(sound);

        website.addActionListener(this);
        open.addActionListener(this);
        tray.addActionListener(this);
        exit.addActionListener(this);
        startup.addActionListener(this);
        sound.addActionListener(this);
        
        
        addWindowListener(new WindowAdapter(){
           @Override
           public void windowClosing(WindowEvent et) {
               System.out.println("closing");
               
               if(!Settings.ExitClose)
                   OpenTray();
           }
        });
        
        
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                int length = Settings.keybinds.length;

                for (int i = 0; i < length; i++) {
                    if (i > 0 && Settings.keyActive[i - 1] && e.getKeyCode() == Settings.keybinds[i]) {
                        Settings.keyActive[i] = true;
                    }
                    if (e.getKeyCode() == Settings.keybinds[i]) {
                        Settings.keyActive[i] = true;
                    }
                }

                if (Arrays.equals(Settings.keyActive, Settings.match)) {
                    StartCapture(500, 500);
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent e) {
                for (int i = 0; i < Settings.keybinds.length; i++) {
                    if (e.getKeyCode() == Settings.keybinds[i]) {
                        Settings.keyActive[i] = false;
                    }
                }
            }

            @Override
            public void nativeKeyTyped(NativeKeyEvent e) {
            }
        });
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icon.gif")));

    }

    public void StartCapture(int x, int y) {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        FrameArea cap = new FrameArea(500, 500);
        cap.SetFrameSize(1, 1);

        GlobalScreen.addNativeMouseMotionListener(new NativeMouseMotionListener() {
            @Override
            public void nativeMouseDragged(NativeMouseEvent e) {
                if (!cap.isVisible()) {
                    GlobalScreen.removeNativeMouseMotionListener(this);
                }

                cap.SetFrameSize(e.getX() - cap.getX(), e.getY() - cap.getY());
            }

            @Override
            public void nativeMouseMoved(NativeMouseEvent e) {
            }
        });

        GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
            
                @Override
            public void nativeMouseReleased(NativeMouseEvent e) {
                cap.setVisible(false);
                GlobalScreen.removeNativeMouseListener(this);

                Rectangle rect = new Rectangle(cap.getX(), cap.getY(), cap.getWidth(), cap.getHeight());

                TakeScreenshot(rect);
            }

            @Override
            public void nativeMousePressed(NativeMouseEvent e) {
                cap.setVisible(true);
                cap.SetFramePosition(e.getX(), e.getY());
            }

            @Override
            public void nativeMouseClicked(NativeMouseEvent e) {
            }
        });
    }

    public void TakeScreenshot(Rectangle rect) {
        BufferedImage image = null;

        try {
            image = new Robot().createScreenCapture(rect);
        } catch (Exception e) {
            Settings.Log(e.toString() + "\n" + " Image create error - invalid dimensions");
        }

        if (image != null) {
            try {
                ImageIO.write(image, "PNG", new File(Settings.InstalledPath + "\\screenshot.png"));
                UploadImage();
            } catch (Exception e) {
                Settings.Log(e.toString() + "\n" + " Image create error - incorrect path or already exists");
            }
        }
    }
    
    public void UploadImage(){
        
        
        
    }
    
    public void SetExitOnClose(JCheckBox b) {
        setDefaultCloseOperation(b.isSelected() ? JFrame.EXIT_ON_CLOSE : JFrame.HIDE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == open) {
            OpenLocation();
        }

        if (source == exitItem) {
            System.exit(0);
        }

        if (source == openItem) {
            OpenMenu();
            SystemTray.getSystemTray().remove(icon);
        }

        if (source == tray) {
            Settings.Change(0, "" + tray.isSelected());
        }

        if (source == exit) {
            SetExitOnClose(exit);
            Settings.Change(2, "" + exit.isSelected());
        }
        
        if(source == sound){
            Settings.Change(4, ""+ sound.isSelected());
        }
        if(source == website){
            if (Desktop.isDesktopSupported()) {
                try{
                    Desktop.getDesktop().browse(new URI("http://www.solidstudios.us/"));
                }catch(Exception ex){
                    Settings.Log(ex.toString() + "  cannot open web browser");
                }
            }
   
        }
        if (source == startup) {
            Settings.Change(3, "" + startup.isSelected());

            if (startup.isSelected()) {
                AddStartup();
            } else {
                RemoveStartup();
            }

        }

    }

    public void AddStartup() {
        if (Settings.os.startsWith("win")) {

            try {

                String path = System.getProperty("user.home").replaceAll("\\\\", "/") + "/AppData/Roaming/Microsoft/Windows/Start%20Menu/Programs/Startup/Screencapper.bat";

                File f = new File(new URI("file:///" + path));
                f.createNewFile();

                BufferedWriter bw = new BufferedWriter(new FileWriter(f));

                bw.write("start javaw -jar " + Settings.InstalledPath + "Screencapper.jar");
                bw.close();

            } catch (Exception e) {
                Settings.Log("File not found - ignoring");
            }

        }
        if (Settings.os.startsWith("mac")) {

        }
    }

    public void RemoveStartup() {
        if (Settings.os.startsWith("win")) {

            try {
                String path = System.getProperty("user.home").replaceAll("\\\\", "/") + "/AppData/Roaming/Microsoft/Windows/Start%20Menu/Programs/Startup/Screencapper.bat";
                File f = new File(new URI("file:///" + path));
                
                if(f.exists()){
                    f.delete();
                }
                
            } catch (Exception e) {
                Settings.Log("Error removing from startup - ignoring");
            }

        }

        if (Settings.os.startsWith("mac")) {

        }
    }

    public void OpenLocation() {

        try {
            Desktop.getDesktop().open(new File(Settings.InstalledPath));
        } catch (IOException e) {
            Settings.Log(e.toString() + "  Cannot find program location - doing nothing");
        }

    }

    public static boolean arrContains(int[] arr, int val) {
        for (int i : arr) {
            if (i == val) {
                return true;
            }
        }

        return false;
    }
}
