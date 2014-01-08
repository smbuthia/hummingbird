/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hummingbird;
//<editor-fold desc="imports" defaultstate="collapsed">

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
//</editor-fold>

/**
 *
 * @author smbuthia
 */
public class Hummingbird {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }

        Properties props = new Properties();
        try {
            props.load(new FileInputStream("config.properties"));

        } catch (FileNotFoundException ex) {
            props.setProperty("PanelBehaviour", Settings.EVADE_RB_ACTION_COMMAND);
//            props.setProperty("VLCLocation", "C:\\Program Files\\VideoLAN\\VLC");

            try {
                props.store(new FileOutputStream("config.properties"), null);
                props.load(new FileInputStream("config.properties"));

            } catch (IOException e) {
                Logger.getLogger(Hummingbird.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (IOException ex) {
            Logger.getLogger(Hummingbird.class.getName()).log(Level.SEVERE, null, ex);
        }

        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "vlc");
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

        createTrayIcon();
        new MediaPlayer().setVisible(true);
    }

    protected static Image createImage(String path, String description) {
        URL imageURL = Hummingbird.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    protected static void createTrayIcon() {
        final Playlist playListWindow = new Playlist();
        final Settings sett = new Settings();

        if (!SystemTray.isSupported()) {
            return;
        }

        PopupMenu popUp = new PopupMenu();
        TrayIcon trayIcon = new TrayIcon(createImage("images/humming-icon.jpg", "icon"), "Hummingbird player");

        MenuItem play = new MenuItem("Play");
        MenuItem pause = new MenuItem("Pause");
        MenuItem stop = new MenuItem("Stop");
        MenuItem playList = new MenuItem("Playlist");
        MenuItem settings = new MenuItem("Settings");
        MenuItem exit = new MenuItem("Exit");

        popUp.add(play);
        popUp.add(pause);
        popUp.add(stop);
        popUp.addSeparator();
        popUp.add(playList);
        popUp.addSeparator();
        popUp.add(settings);
        popUp.add(exit);

        trayIcon.setPopupMenu(popUp);
        final SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException ex) {
            Logger.getLogger(Hummingbird.class.getName()).log(Level.SEVERE, null, ex);
        }

        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playListWindow.playVideoList(0, Playlist.listModel);
            }
        });
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MediaPlayer.mediaListPlayer.isPlaying()) {
                    MediaPlayer.mediaListPlayer.stop();
                }
            }
        });
        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MediaPlayer.mediaListPlayer.isPlaying()) {
                    MediaPlayer.mediaListPlayer.pause();
                }
            }
        });

        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sett.setVisible(true);
            }
        });

        playList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playListWindow.setVisible(true);
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}
