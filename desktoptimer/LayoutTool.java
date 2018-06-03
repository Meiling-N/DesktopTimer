/*
 * MIT License.
 */
package desktoptimer;

import java.awt.GridBagConstraints;

/**
 *
 * @author Nonohoo
 */
public class LayoutTool {
    /***
     * これ使ってないやろ
     * @param w:幅
     * @param h:高さ
     * @return 
    ***/
    public static GridBagConstraints getGBC(int w, int h){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        return gbc;
    }
    
    public static GridBagConstraints setXYGBC(GridBagConstraints gbc,int x, int y){
        gbc.gridx = x;
        gbc.gridy = y;
        return gbc;
    }
}
