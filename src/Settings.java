
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import org.jnativehook.keyboard.NativeKeyEvent;

/**
 * @author Kevin
 */
public class Settings {

    public static String os = "Windows";

    public static String settings = "Tray on Start: false\nKeybind: SHIFT+ALT+4\nExit on Close: true\nStart on Startup: true\nSound: false";

    public static String DebugText = "";

    public static String InstalledPath = "";
    
    public static boolean TrayStart = false,
            ExitClose = true,
            Startup = true,
            Sound = false;

    public static int[] keybinds = {
        NativeKeyEvent.VC_SHIFT,
        NativeKeyEvent.VC_ALT,
        NativeKeyEvent.VC_4

    };
    public static boolean[] keyActive = {
        false,
        false,
        false
    };
    public static boolean[] match = {
        true,
        true,
        true
    };

    public static boolean hasSettings() {
        return new File("settings.txt").exists();
    }

    public static void Change(int line, String value) {
        String[] lines = settings.split("\n");
        lines[line] = lines[line].substring(0, lines[line].indexOf(": ")) + ": " + value;
        settings = Arrays.toString(lines).substring(1, Arrays.toString(lines).length() - 1).replaceAll(", ", "\n");
        try {
            WriteSettings();
        } catch (Exception e) {
            Log("Error changing settings and rewriting - doing nothing");
        }
        
        try{
            ReadSettings();
        }catch(Exception e){
            Log("Error reading newly changed settings");
        }
    }

    public static void WriteSettings() throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("settings.txt")));

        for (String s : settings.split("\n")) {
            bw.write(s);
            bw.newLine();
        }

        bw.close();
        bw = null;

        System.out.println("Successfully wrote config!");
    }

    public static void Log(String text) {
        System.out.println(text);
        DebugText += text + "\n";

        File debug = new File("debug.log");

        try {
            debug.createNewFile();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            BufferedWriter bw = new BufferedWriter(new FileWriter(debug, true));           
            
            bw.write(dateFormat.format(date) + " | " + text);
            bw.newLine();
            bw.close();

        } catch (IOException e) {
            System.out.println("Critical error - closing");
            System.exit(1);
        }
    }

    public static int ReadSettings() throws Exception {
        if (!hasSettings()) {
            WriteSettings();
        }

        Scanner file = new Scanner(new File("settings.txt"));
        String lines = "";

        while (file.hasNextLine()) {
            lines += file.nextLine() + "\n";
        }

        file = new Scanner(lines);

        for (int i = 0; i < settings.split("\n").length; i++) {
            String config;
            try {
                config = file.nextLine().split(": ")[1];
            } catch (Exception e) {
                new File("settings.txt").delete();
                Log("Config File Error - Rewriting config...");
                WriteSettings();
                ReadSettings();
                return -1;
            }

            switch (i) {
                case 0:
                    TrayStart = Boolean.parseBoolean(config);
                    break;
                case 1:
                    String[] keys = config.trim().split("\\+");

                    keybinds = null;
                    keybinds = new int[keys.length];

                    for (int j = 0; j < keybinds.length; j++) {
                        try {
                            Field f = NativeKeyEvent.class.getField("VC_" + keys[j].toUpperCase());
                            f.setAccessible(true);

                            keybinds[j] = f.getInt(f);

                        } catch (Exception e) {
                            Log(e.toString() + "\n" + "Unrecognized keybinds - rewriting config...");
                            new File("settings.txt").delete();
                            WriteSettings();
                            ReadSettings();
                            return -1;
                        }
                    }
                    break;
                case 2:
                    ExitClose = Boolean.parseBoolean(config);
                    break;
                case 3:
                    Startup = Boolean.parseBoolean(config);
                    break;
                case 4:
                    Sound = Boolean.parseBoolean(config);
                    break;
            }

        }
        settings = lines;
        keyActive = new boolean[keybinds.length];
        match = new boolean[keybinds.length];
        Arrays.fill(match, true);

        Log("Settings Read!");
        return 0;
    }
    
    
}
