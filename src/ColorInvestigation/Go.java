package ColorInvestigation;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import f.Start;

public class Go {
	public static void main(String[] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		Mat m = new Mat(500,180*10,CvType.CV_8UC3);
		
		for(int x = 0;x<180*10;x+=1) {
			int y = x/10;
			if(x<0)y=x+180;
			Imgproc.rectangle(m,new Point(x,0), new Point(x+10,200),new Scalar(y,255,255),-1);
		}
		
		for(int x = 0;x<180*10;x+=1) {
			double y = x/10.0 + 67;
			if(y<0)y+=180;
			System.out.println(y);
			Imgproc.rectangle(m,new Point(x,300), new Point(x+10,500),new Scalar(y,255,255),-1);
		}
				
		Imgproc.cvtColor(m,m,Imgproc.COLOR_HSV2RGB);
		
		Start.display(m,1);
	}
}
