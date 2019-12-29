package f;

import java.awt.FlowLayout;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.DenseOpticalFlow;
import org.opencv.video.DualTVL1OpticalFlow;
import org.opencv.videoio.VideoCapture;

import d.Analysis;
import d.Pipeline;
import d.gap;
import org.opencv.videoio.Videoio;
public class Start {
	static int x=0;//have the windows line up
	static int y=0;//have the windows stack
	
	static double blackCutOff = 0;
		
	public static final boolean doVideo = true;
	public static final boolean displayImage = false;
	public static final boolean write = false;
	
	public static void main(String[] args) {
		System.out.println();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				
		//DenseOpticalFlow flow = DualTVL1OpticalFlow.create();
		
		//("C:\\Users\\xchen\\Downloads\\CVCinematic\\FTC Skystone Circuit Breakers 111 Point Match SOLO_"+num);
		
		go("C:\\users\\xchen\\desktop\\shet\\t4",0);
//		for(int i=1;i<19;i++) {
//			if(i==6)continue;
//			go("C:\\users\\xchen\\desktop\\shet\\"+i,i);
//
//		}
		
		/*
		for(int i=1;i<501;i++) {
			//String num = String.format("%03d" , i);
			String num = String.valueOf(i);
			System.out.println("Read from: C:\\Users\\xchen\\Downloads\\CVCinematic\\"+num);
			go("C:\\Users\\xchen\\Downloads\\CVCinematic\\pic ("+num+")",i);
			
		}*/
	}
	
	public static void go(String uri,int index) {
		Mat img = Imgcodecs.imread(uri+".jpg");
		//Mat img = Imgcodecs.imread("C:\\Users\\xchen\\Pictures\\am-4059B.jpg");
		//Mat img = Imgcodecs.imread("D:\\Media\\Robotics\\Minerals\\dither.jpg");
		if(img==null)System.exit(69);
		
		Long startTime = System.nanoTime();
		Mat img2 = d.Pipeline.process(img);
		Long endTime = System.nanoTime();
		System.out.println(1000000000f/(endTime-startTime));
		
		display(getHistogram(d.Pipeline.resizedImage),1,"histogram: intensity vs occurence");
		display(Pipeline.blue,1,"fds");
		display(img2,1,"Final with bounds");
		
		if(write)System.out.println("Success? "+Imgcodecs.imwrite("C:\\users\\xchen\\desktop\\shet\\"+index+"-ann.JPEG",img2));
		System.out.println("Write to: "+uri+"-ann.JPEG");
		System.out.println();
		
		if(!doVideo)return;
		
		Mat frame = new Mat();
	    //0; default video device id
	    VideoCapture camera = new VideoCapture(0);
	    
	    JFrame jframe = new JFrame("Title");
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JLabel vidpanel = new JLabel();
	    jframe.setContentPane(vidpanel);
	    jframe.setVisible(true);
	    
	    JFrame jframe2 = new JFrame("Title");
	    jframe2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JLabel vidpanel2 = new JLabel();
	    jframe2.setContentPane(vidpanel2);
	    jframe2.setVisible(true);
	    jframe2.setLocation(1000,0);
	    
	    JFrame jframe3 = new JFrame("Title");
	    jframe3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JLabel vidpanel3 = new JLabel();
	    jframe3.setContentPane(vidpanel3);
	    jframe3.setVisible(true);
	    jframe3.setLocation(1000,400);
	    
	    JFrame jframe4 = new JFrame("Title");
	    jframe4.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JLabel vidpanel4 = new JLabel();
	    jframe4.setContentPane(vidpanel4);
	    jframe4.setVisible(true);
	    jframe4.setLocation(1000,400);
	    
	    while (true) {
	        if (camera.read(frame)) {
	        	Core.flip(frame,frame,1);
	        	
	        	Mat upper = new Mat();
	        	Mat lower = new Mat();
	        	Mat display = new Mat();
	        	
	        	List<Mat> upimages = new ArrayList<Mat>();
	        	//images.add(getHistogram(img));
	        	//images.add(
	        	Mat out = Pipeline.process(frame);//); 
	        	upimages.add(Pipeline.red);
	        	upimages.add(Pipeline.blue);
	        	
	        	List<Mat> downimages = new ArrayList<Mat>();
	        	downimages.add(Pipeline.yellow);
	        	downimages.add(Pipeline.black);
	        	
	        	List<Mat> im = new ArrayList<Mat>();
	        	im.add(upper);
	        	im.add(lower);
	        	
	        	Core.hconcat(upimages,upper);
	        	Core.hconcat(downimages,lower);
	        	Core.vconcat(im,display);
	        	
	        	//masks
	        	Size size = frame.size();
	            flipHoro(frame);
	        	ImageIcon image = new ImageIcon(Mat2BufferedImage(display,1));
	            vidpanel.setIcon(image);
	            vidpanel.repaint();
	            jframe.setSize(image.getIconWidth()+40,image.getIconHeight()+40);
	            
	            //annotations
	            ImageIcon image2 = new ImageIcon(Mat2BufferedImage(out,1));
	            vidpanel2.setIcon(image2);
	            vidpanel2.repaint();
	            jframe2.setSize(image2.getIconWidth()+40,image2.getIconHeight()+40);
	            
	            //histo
	            ImageIcon image3= new ImageIcon(Mat2BufferedImage(getHistogram(d.Pipeline.resizedImage),1));
	            vidpanel3.setIcon(image3);
	            vidpanel3.repaint();
	            jframe3.setSize(image3.getIconWidth()+40,image3.getIconHeight()+40);
	            
	          //shell
	            ImageIcon image4= new ImageIcon(Mat2BufferedImage(d.Pipeline.yellowKeep,1));
	            vidpanel4.setIcon(image4);
	            vidpanel4.repaint();
	            jframe4.setSize(image4.getIconWidth()+40,image4.getIconHeight()+40);
	            
	        }
	    }
	    
	}
		
	public static void flipHoro(Mat inp) {
		Core.flip(inp,inp,1); 
	}
	
	/**
     * Spits out a new mat scaled by factor
     * @param factor multiplier to scale by
     */
	public static Mat resize(Mat tochange, double factor) {
		Mat out = new Mat();
		Imgproc.resize(tochange,out,new Size(tochange.cols()*factor,tochange.rows()*factor));
		return out;
	}
	
	/**
     * convert Mat to AWT bufferedimage
     */
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
	/**
     * create a graph (in mat form) with the value of each RGB color on the xaxis and 
     * the frequency it occurs in the image on the y axis
     */
	public static Mat getHistogram(Mat in) {
		int histSize=255;//# bins
		int histogramHeight = 512;//physcical size
		
		Scalar[] colorsRgb = new Scalar[]{
				new Scalar(200, 0, 0, 255), 
				new Scalar(0, 200, 0, 255), 
				new Scalar(0, 0, 200, 255)};
		
    	Mat[] histData = new Mat[] {new Mat(),new Mat(),new Mat()};
    	
    	Mat output = new Mat(new Size(512,512), in.type());
    	
    	//memory leaks and phased out data add static to the image
    	Imgproc.rectangle(output,
    			new Point(0,0),
    			new Point(1000,1000),
    			new Scalar(new double[] {0,0,0}),
    			-1);
    	
    	for (int i = 0; i < 3; i++) {
        	getHistdata(in,i,histData[i],histSize);
        	
        	//stretch vertically
    	    Core.normalize(histData[i], histData[i], histogramHeight, 0, Core.NORM_INF);
    	    
    	    for (int j = 0; j < histSize; j++) {
    	        Point p1 = new Point((1000/histSize) * (j - 1), histogramHeight - Math.round(histData[i].get(j - 1, 0)[0]));
    	        Point p2 = new Point((1000/histSize) * j, histogramHeight - Math.round(histData[i].get(j, 0)[0]));
    	        
    	        Imgproc.line(output, p1, p2, colorsRgb[i], 2, 8, 0);
    	    }
    	}
    	//analysis
    	//sum of all white colors
    	double whiteSum=0;
    	double whiteTot=0;
    	for (int j = 0; j < histSize; j++) {
    		//avg
    		double Val = (histData[0].get(j, 0)[0] +
    				histData[1].get(j, 0)[0] + 
    				histData[2].get(j, 0)[0] )/3;
    		//min
    		double minVal = min(histData[0].get(j, 0)[0],
    				histData[1].get(j, 0)[0],
    				histData[2].get(j, 0)[0]);
    		
    		whiteSum+=minVal * j;
    		whiteTot+=Val*j;
    		
    		Point p1 = new Point((1000/histSize) * j, histogramHeight);
	        Point p2 = new Point((1000/histSize) * j, histogramHeight - minVal);
	        
	        Imgproc.line(output, p1, p2, new Scalar(new double[] {140,70,70}), 2, 8, 0);
    	}
    	double whiteSumAvg = whiteSum / whiteTot * 500;//width of the thing
    	blackCutOff = whiteSumAvg * 1/6;
    	
    	
    	Point p1 = new Point(0,histogramHeight - whiteSumAvg);
	    Point p2 = new Point(1000,histogramHeight - whiteSumAvg);
	        
	    Imgproc.line(output, p1, p2, new Scalar(new double[] {120,120,255}), 2, 8, 0);
    	
    	histData[0].release();
    	histData[1].release();
    	histData[2].release();
    	
    	return output;
    }
	
	static void getHistdata(Mat in,int channel,Mat out, int histsize) {
		
		Imgproc.calcHist(Arrays.asList(in), 
    			new MatOfInt(channel),//color channel
    			new Mat(),
    			out,
    			new MatOfInt(histsize), //size
    			new MatOfFloat(1,256));//ranges we do not count zeros because they skew it
	
		Imgproc.blur(out,out,new Size(90,90));
	}
	
	static double min(double ... vals) {
		double min=vals[0];
		
		for(int i = 1 ; i<vals.length;i++) {
			if(vals[i]<min)min=vals[i];
		}
		
		return min;
	}
}
