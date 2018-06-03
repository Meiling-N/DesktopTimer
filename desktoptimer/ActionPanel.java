/*
 * MIT License.
 */
package desktoptimer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JButton;

/**
 *
 * @author Nonohoo
 */
public class ActionPanel extends JPanel{
    JButton stopB;
    JButton startB;
    JButton resetB;
    TimerPanel tp;
    ClickedButton cB = new ClickedButton();
    
    //タイマー複数用意できるようにしようかと思ったけど面倒だったのでこうなった
    //やるなら複数のTimerPanelを管理するパネルを作ってこっちに投げればいけるんじゃないかな
    //リスナー作るべき
    ActionPanel(TimerPanel p){
        setBackground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(400, 40));
        
        stopB = new JButton("STOP");
        startB = new JButton("START");
        resetB = new JButton("RESET");
        tp = p;
        stopB.addActionListener(cB);
        stopB.setEnabled(false);
        startB.addActionListener(cB);
        resetB.addActionListener(cB);
        add(startB);
        add(stopB);
        add(resetB);
    }
    
    class ClickedButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(stopB)){
                tp.stopTimer();
                stopB.setEnabled(false);
                startB.setEnabled(true);
                resetB.setEnabled(true);
                return;
            }
            if(e.getSource().equals(startB)){
                if(tp.startTimer()){
                    stopB.setEnabled(true);
                    startB.setEnabled(false);
                    resetB.setEnabled(false);
                }
                return;
            }
            if(e.getSource().equals(resetB)){
                tp.resetTimer();
            }
        }
    }
}
