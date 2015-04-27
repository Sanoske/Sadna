import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import javax.swing.*;
 
public class GraphingData extends JPanel {
    final int PAD = 20;
    final int SPAD = 2;
    double [] x_data;
    double [] y_data;
    int width, height;
    String title;
    public GraphingData (double [] x, double [] y, int w, int h, String t) {
    	x_data = x.clone();
    	y_data = y.clone();
    	width = w;
    	height = h;
    	title = t;
    }
    // This method overrides the original paintComponent to draw the graphs
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        // Create a buffer to write the image to a file
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setBackground(Color.WHITE);
        g2.clearRect(0,0,width,height);
        int w = getWidth();
        int h = getHeight();
        // Draw Y axis
        g2.setPaint(Color.BLACK);
        g2.draw(new Line2D.Double(PAD, PAD, PAD, h-PAD));
        // Draw X  axis
        g2.draw(new Line2D.Double(PAD, h-PAD, w-PAD, h-PAD));
        // Draw labels.
        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        float sh = lm.getAscent() + lm.getDescent();
        // Y  axis label.
        float sy = lm.getAscent();
        float sw = (float)font.getStringBounds("Y", frc).getWidth();
        float sx = PAD - sw/2;
        g2.drawString("Y", sx, sy);
        // X axis label.
        sy = h - PAD - sh/2 + lm.getAscent();
        sw = (float)font.getStringBounds("X", frc).getWidth();
        sx = w - sw;
        g2.drawString("X", sx, sy);
        // Draw lines.
        double xInc = (double)(w - 2*PAD)/getMax(true);
        double scale = (double)(h - 2*PAD)/getMax(false);
        g2.setPaint(Color.green.darker());
        Collection<Integer> indx = plotByX(x_data);
        Iterator iterator = indx.iterator(); 
        int i_1 = (Integer)iterator.next();
        while (iterator.hasNext()) {
        	double x1 = PAD + xInc*x_data[i_1];
            double y1 = h - PAD - scale*y_data[i_1];
            int i_2 = (Integer)iterator.next();
            double x2 = PAD + xInc*x_data[i_2];
            double y2 = h - PAD - scale*y_data[i_2];
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
            i_1 = i_2;
        }
        // Mark Y points in graph
        g2.setPaint(Color.black);
        for(int i = 0; i < y_data.length; i++) {
        	String dot = String.valueOf(y_data[i]);
        	float dot_w = (float)font.getStringBounds(dot, frc).getWidth();
        	float dot_x = PAD - dot_w - SPAD;
        	float dot_y = (float)(h - PAD - scale*y_data[i] + lm.getAscent()/2);
        	g2.drawString(dot, dot_x, dot_y);
        }
        // Mark X points in graph
        for(int i = 0; i < x_data.length; i++) {
        	String dot = String.valueOf(x_data[i]);
        	float dot_x = (float)(PAD + xInc*x_data[i]);
        	float dot_y = (float)(h - PAD + (PAD - sh)/2 + lm.getAscent());
        	g2.drawString(dot, dot_x, dot_y);
        }
        // Mark data points.
        g2.setPaint(Color.red);
        for(int i = 0; i < x_data.length; i++) {
            double x = PAD + xInc*x_data[i];
            double y = h - PAD - scale*y_data[i];
            g2.fill(new Ellipse2D.Double(x-2, y-2, 4, 4));
        }
        // Save to file
        try {
        	g2.drawImage(bi, null, 0, 0);
        	ImageIO.write(bi, "JPEG", new File(title + ".jpg"));
        }
        catch (IOException ie) {
     	   ie.printStackTrace();
        }
        g2.dispose();
        //Show finished message
        g.setFont(new Font("TimesRoman", Font.PLAIN, 24));
        int stringLen = (int) g.getFontMetrics().getStringBounds("Finished drawing the graphs", g).getWidth();
        int start = width/2 - stringLen/2;
        g.drawString("Finished drawing the graphs", start, height/2);
        g.dispose();
    }
 
    private Collection<Integer> plotByX(double[] x) {
    	Map<Double, Integer> map = new TreeMap<Double, Integer>();
        for (int i = 0; i < x.length; ++i) {
            map.put(x[i], i);
        }
        return map.values();
	}

	private double getMax(boolean isX) {
        double max = -Double.MAX_VALUE;
        for(int i = 0; i < x_data.length; i++) {
            if (isX && x_data[i] > max)
                max = x_data[i];
            else if (!isX && y_data[i] > max)
            	max = y_data[i];
        }
        return max;
    }
}