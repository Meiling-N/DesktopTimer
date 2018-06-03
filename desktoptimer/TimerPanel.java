/*
 * MIT License.
 */
package desktoptimer;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.Box;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import java.util.Timer;
import java.util.TimerTask;
/**
 *
 * @author Nonohoo
 */
public class TimerPanel extends JPanel{
    private JTextField hourTextF;
    private JTextField minuteTextF;
    private JTextField secTextF;
    private int hour = 0;
    private int minute = 3;
    private int sec = 0;
    JLabel leftTimeText;
    JLabel cautionText;
    TimeTextListener ttl = new TimeTextListener();
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gbc =  new GridBagConstraints();
    Timer timer = new Timer();
    boolean isEndTime = true;//タイマー終了確認用
    private final Object locker = new Object();//排他制御用
    MusicFilePanel mfp;
    
    //本来ならリスナーを用意すべき
    TimerPanel(MusicFilePanel m){
        mfp = m;
        setLayout(gbl);
        setBackground(Color.white);
        setPreferredSize(new Dimension(400, 150));
        
        //細かい面倒な設定
        hourTextF = new JTextField("00",2);
        minuteTextF = new JTextField("03",2);
        secTextF = new JTextField("00",2);
        
        hourTextF.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 50));
        minuteTextF.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 50));
        secTextF.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 50));
        
        hourTextF.setHorizontalAlignment(JTextField.RIGHT);
        minuteTextF.setHorizontalAlignment(JTextField.RIGHT);
        secTextF.setHorizontalAlignment(JTextField.RIGHT);
        
        hourTextF.getDocument().addDocumentListener(ttl);
        minuteTextF.getDocument().addDocumentListener(ttl);
        secTextF.getDocument().addDocumentListener(ttl);
        
        leftTimeText = new JLabel(getTimeString(hour,minute,sec));
        leftTimeText.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 35));
        cautionText = new JLabel("数字以外が入ってるから消して");
        cautionText.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 15));
        cautionText.setForeground(Color.red);
        cautionText.setVisible(false);
        //配置
        add(new JLabel("[時間の指定]"));
        add(hourTextF);
        add(new JLabel("時"));
        add(minuteTextF);
        add(new JLabel("分"));
        add(secTextF);
        add(new JLabel("秒後に鳴る"));
        
        JLabel kei = new JLabel("残り時間");
        kei.setFont(new Font("ＭＳ ゴシック", Font.PLAIN, 15));
        gbl.setConstraints(kei, LayoutTool.setXYGBC(gbc, 0, 1));
        add(kei);
        
        gbc.gridwidth = 6;
        gbl.setConstraints(leftTimeText, LayoutTool.setXYGBC(gbc, 1, 1));
        add(leftTimeText);
        
        add(Box.createVerticalStrut(15),LayoutTool.setXYGBC(gbc, 0, 2)); 
        gbl.setConstraints(cautionText,LayoutTool.setXYGBC(gbc, 0, 3));
        add(cautionText);
    }
    
    public int getHour(){
        return Integer.valueOf(hourTextF.getText());
    }
    
    private String getTimeString(int h,int m,int s){
        return String.format("%02d時 %02d分 %02d秒", h,m,s); 
    }
    
    private boolean isAllString_Num(String s){
        return s.matches("^[0-9]+$");
    }
    
    //テキストに変化があったら通知するクラス
    private class TimeTextListener implements DocumentListener{
        TimeTextListener () {
        }
        
        //テキストが変わる度にウザイぐらい毎回呼ばれる
        @Override
        public void insertUpdate(DocumentEvent e) {
            try {
                //書いてあるのを取得
                String time = e.getDocument().getText(0,e.getDocument().getLength());
                
                //3文字以上入れさせない
                if(time.length() > 2){
                    shortenTextin_2char(e,time);
                }
                
                //雑な例外処理,数字以外入ってたらreturn
                if(!isAllString_Num(time)){
                    cautionText.setVisible(true);
                    return;
                }else{
                    cautionText.setVisible(false);
                }                
                
                //documentの判定がくそ冗長に見えるけどこれ以外方法がわからんかった
                //テキストの内容を数字に入れる
                if(e.getDocument().equals(hourTextF.getDocument())){
                    hour = Integer.valueOf(time);
                }
                if(e.getDocument().equals(minuteTextF.getDocument())){
                    minute = Integer.valueOf(time);
                }
                if(e.getDocument().equals(secTextF.getDocument())){
                    sec = Integer.valueOf(time);
                }
                
                //文字にも反映
                leftTimeText.setText(getTimeString(hour,minute,sec));
            } catch (BadLocationException b) {
                System.out.println(b.toString());
            }
        }
        
        @Override
        public void removeUpdate(DocumentEvent e) {
             try {
                //変わったテキストからウザイぐらい毎回呼ばれる
                String time = e.getDocument().getText(0,e.getDocument().getLength());
                
                //中身がなんもなくなったら変化させない
                if(time.isEmpty()){
                    //set0inText(e);//なんか鬱陶しいのでやめた
                    return;
                }
                //雑な例外処理,数字以外入ってたらreturn
                if(!isAllString_Num(time)){
                    cautionText.setVisible(true);
                    return;
                }else{
                    cautionText.setVisible(false);
                }
                //テキストの内容を数字に入れる
                if(e.getDocument().equals(hourTextF.getDocument())){
                    hour = Integer.valueOf(time);
                }
                if(e.getDocument().equals(minuteTextF.getDocument())){
                    minute = Integer.valueOf(time);
                }
                if(e.getDocument().equals(secTextF.getDocument())){
                    sec = Integer.valueOf(time);
                }
                
                //文字にも反映
                leftTimeText.setText(getTimeString(hour,minute,sec));
            } catch (BadLocationException b) {
                 System.out.println(b.toString());
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            //使い方不明
        }
        
        private JTextField getTextFieldfrom(DocumentEvent e){
            //documentの判定がくそ冗長に見えるけどこれ以外方法がわからんかった
            if(e.getDocument().equals(hourTextF.getDocument())){
                return  hourTextF;
            }
            if(e.getDocument().equals(minuteTextF.getDocument())){
                return  minuteTextF;
            }
            if(e.getDocument().equals(secTextF.getDocument())){
                return  secTextF;
            }
            return null;
        }
        
        private void set0inText(DocumentEvent e){
            JTextField t = getTextFieldfrom(e);
            //よくわからんけどこうすると動く
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    t.setText("0");
                }
            });
        }
        
        private void shortenTextin_2char(DocumentEvent e,String org){
            JTextField t = getTextFieldfrom(e);
            //よくわからんけどこうすると動く
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    t.setText(org.substring(1,3));
                    int pos = t.getCaretPosition() - 1;
                    t.setCaretPosition(pos < 0 ? 0 : pos);
                }
            });
        }
    }
    
    public boolean startTimer(){
        synchronized(locker){
            if(sec == 0 && minute == 0 && hour == 0){
                return false;
            }
            if(isEndTime){
                hourTextF.setEnabled(false);
                minuteTextF.setEnabled(false);
                secTextF.setEnabled(false);
                timer = new Timer();
                timer.schedule(new ReduceTimeTask(), 0,1000);
                isEndTime=false;
            }
        }
        return true;
    }
    
    public void stopTimer(){
        synchronized(locker){
            if(!isEndTime){
                hourTextF.setEnabled(true);
                minuteTextF.setEnabled(true);
                secTextF.setEnabled(true);
                timer.cancel();
                isEndTime=true;
            }
        }
    }
    
    public void resetTimer(){
        synchronized(locker){
            if(isEndTime && !cautionText.isVisible()){
                try{
                    hour = Integer.valueOf(hourTextF.getDocument().getText(0,hourTextF.getDocument().getLength()));
                    minute = Integer.valueOf(minuteTextF.getDocument().getText(0,minuteTextF.getDocument().getLength()));
                    sec = Integer.valueOf(secTextF.getDocument().getText(0,secTextF.getDocument().getLength()));
                    leftTimeText.setText(getTimeString(hour,minute,sec));
                }catch (BadLocationException b){
                    System.out.println(b.toString());
                }
            }
        }
    }
    
    //時間のカウントダウンを行うクラス
    private class ReduceTimeTask extends TimerTask{
        @Override
        public void run(){
            sec--;
            if(sec == 0 && minute == 0 && hour == 0){
                mfp.startMusic(true);
                timer.cancel();
                //文字に反映
                leftTimeText.setText(getTimeString(hour,minute,sec));
                return;
            }
            //秒以外の変化が起きるまでreturn
            if(sec >= 0){
                leftTimeText.setText(getTimeString(hour,minute,sec));
                return;
            }
            //この時点でsec==-1
            //分が残っているなら減らしてreturn
            sec = 59;
            if(minute > 0){
                minute--;
                leftTimeText.setText(getTimeString(hour,minute,sec));
                return;
            }
            //分が残っていないなら時を減らして終了
            if(minute == 0){
                minute = 59;
                hour--;
            }
            //earlyReturnの弊害
            leftTimeText.setText(getTimeString(hour,minute,sec));
        }
    }
}
