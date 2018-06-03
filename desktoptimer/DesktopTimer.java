/*
 * MIT License.
 */
package desktoptimer;
import javax.swing.JFrame;
import java.awt.Container;
import java.awt.FlowLayout;
/**
 *
 * @author Nonohoo
 */
public class DesktopTimer extends JFrame{

    public static void main(String args[]) {
        DesktopTimer timer = new DesktopTimer();
        timer.setVisible(true);
    }
    
    DesktopTimer(){
        setTitle("Timer");//Frameのタイトルの設定
        setSize(450, 450);//Frameの幅,高さの設定
        setLocationRelativeTo(null);//画面の中央に行くようにする
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//☓ボタンが押された時にプログラムを終了する
        setLayout(new FlowLayout());
        Container contentPane = getContentPane();
        
        //循環参照と便利さを犠牲に挙動不審を召喚!
        //リスナー作れ
        MusicFilePanel msc = new MusicFilePanel();
        TimerPanel pst = new TimerPanel(msc);
        ActionPanel act  = new ActionPanel(pst);
        contentPane.add(pst);
        contentPane.add(act);
        contentPane.add(msc);
    }
    
}
