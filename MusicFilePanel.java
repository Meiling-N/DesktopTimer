/*
 * MIT License.
 */
package desktoptimer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

 
/**
 *
 * @author Nonohoo
 */
public class MusicFilePanel extends JPanel{
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc =  new GridBagConstraints();
    JButton stopB;
    JButton startB;
    JTextArea dropText;
    DropTarget dT;
    File musicFile = null;
    MediaPlayer mp = null;
    Thread musicThread = new Thread();
    boolean isStopMusic = false;
    ClickedButton cB = new ClickedButton();
    
    MusicFilePanel(){
        
        setLayout(gbl);
        setBackground(Color.GRAY);
        setPreferredSize(new Dimension(400, 200));
        
        stopB = new JButton("SOUND STOP");
        startB = new JButton("SOUND TEST");
        
        dropText = new JTextArea("タイマー終了時間になると指定したファイルが鳴る\nここにドラッグ&ドロップで音声ファイルを指定する");
        dropText.setPreferredSize(new Dimension(200,190));
        dropText.setEnabled(false);
        dropText.setLineWrap(true);
        dropText.setWrapStyleWord(true);
        dropText.setDisabledTextColor(Color.BLACK);
        gbc.gridheight = 2;
        gbl.setConstraints(dropText, LayoutTool.setXYGBC(gbc, 0, 0));
        //ドロップできるように設定
        dT = new DropTarget(dropText, DnDConstants.ACTION_COPY,new MyDropTargetListener());
        add(dropText);
        
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        //停止ボタン
        stopB.setPreferredSize(new Dimension(180, 160));
        stopB.addActionListener(cB);
        gbl.setConstraints(stopB, LayoutTool.setXYGBC(gbc, 2, 0));
        add(stopB);
        //スタートボタン
        startB.setPreferredSize(new Dimension(180, 30));
        startB.addActionListener(cB);
        gbl.setConstraints(startB, LayoutTool.setXYGBC(gbc, 2, 1));
        add(startB);
        
        //これしないとmediaplayerが動かない
        //なんか初期化がいるらしいよ?
        JFXPanel fxPanel = new JFXPanel();
    }
    
    class ClickedButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(stopB)){
                stopMusic();
                return;
            }
            if(e.getSource().equals(startB)){
                startMusic(false);
            }
        }
    }
    
    public void startMusic(boolean forceStart){
        if(forceStart){
            if(musicThread.isAlive()){
                isStopMusic = true;
                mp.stop();
                try {
                    musicThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MusicFilePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if(musicFile == null || !isSupportedMusicFile(musicFile) || musicThread.isAlive())return;

        isStopMusic = false;
        URI uri = musicFile.toURI();
        mp = new MediaPlayer(new Media(uri.toString()));
        mp.setOnEndOfMedia(new Thread() {
                public void run() {
                    isStopMusic = true;
                    mp.stop();
                }
            });

        musicThread = new Thread() {
                public void run() {
                    mp.play();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MusicFilePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //sleep入れないと止まらん
                    //機能してるか怪しい
                    while(!isStopMusic){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MusicFilePanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        isStopMusic = !mp.getStatus().equals(MediaPlayer.Status.PLAYING);
                    }
                    mp.stop();
                }
            };
        musicThread.start();
    }
    
    public void stopMusic(){
        if(musicFile == null || !isSupportedMusicFile(musicFile))return;
        if(musicThread.isAlive()){
            isStopMusic = true;
            mp.stop();
            try {
                musicThread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(MusicFilePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    //コピペ
    class MyDropTargetListener extends DropTargetAdapter {
        @Override
        public void drop(DropTargetDropEvent dtde) {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            boolean b = false;
            try {
                if (dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    b = true;
                    List<File> list = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    File f = list.get(0);
                    if(isSupportedMusicFile(f)){
                        String s = f.getName()
                                + "\n\nタイマー終了時間になると指定したファイルが鳴る\nここにドラッグ&ドロップで音声ファイルを変更";
                        dropText.setText(s);
                        if(musicFile != null && !musicFile.getName().equals(f.getName())){
                            mp.stop();
                        }
                        musicFile = f;
                    }else{
                        String s = f.getName()
                                + "\n\nそのファイルはサポートしてないんだわ、"
                                + "すまんな\n"
                                + "タイマー終了時間になると指定したファイルが鳴る\nここにドラッグ&ドロップで音声ファイルを変更";
                        dropText.setText(s);
                    }
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                dtde.dropComplete(b);
            }
        }
    }
    
    private boolean isSupportedMusicFile(File f){
        return f.getName().matches(".*(\\.(wav|mp3))$");
    }
}
