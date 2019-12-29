package test;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class goo {
	public static void main(String[] args) {
		System.out.println();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		go("C:\\users\\xchen\\desktop\\shet\\t4");
	}
	
	static void go (String uri) {
		Mat img = Imgcodecs.imread(uri+".jpg");

		if(img==null)System.exit(69);
		
		Long startTime = System.nanoTime();
		
		Pipeline.process(img);
		
		Long endTime = System.nanoTime();
		System.out.println(1000000000f/(endTime-startTime));
	}
	
	public static BufferedImage Mat2BufferedImage(Mat inp, double sc) {
	    // Fastest code
	    // output can be assigned either to a BufferedImage or to an Image	
		
		if(inp.type()==5) {
			inp.convertTo(inp,0,3);
		}
		
		sc*=0.7;
		Mat m = resize(inp,sc);
		
	    int type = BufferedImage.TYPE_BYTE_GRAY;
	    if ( m.channels() > 1 ) {
	        type = BufferedImage.TYPE_3BYTE_BGR;
	    }
	    int bufferSize = m.channels()*m.cols()*m.rows();
	    byte [] b = new byte[bufferSize];
	    m.get(0,0,b); // get all the pixels
	    BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
	    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	    System.arraycopy(b, 0, targetPixels, 0, b.length);  
	    return image;
	}
	
	/**
     * Show image in new window
     */
	public static void displayImage(Image img2,String title) {
		if(!displayImage)return;
		
	    //BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
	    ImageIcon icon=new ImageIcon(img2);
	    JFrame frame=new JFrame();
	    frame.setLayout(new FlowLayout());        
	    frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
	    JLabel lbl=new JLabel();
	    lbl.setIcon(icon);
	    frame.add(lbl);
	    frame.setTitle(title);
	    frame.setLocation(x,y);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    x+=frame.getWidth();
	    if(x+frame.getWidth()>Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
	    	y+=frame.getHeight()+20;
	    	x=0;
	    }
	    
	}
	
	public static void display (Mat m, double sc) {
		displayImage(Mat2BufferedImage(m,sc),"");
	}
	
	public static void display (Mat m, double sc, String Windowname) {
		if(!doVideo)displayImage(Mat2BufferedImage(m,sc),Windowname);
	}
}
