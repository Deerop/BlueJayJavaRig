package test;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import f.Start;

public class Pipeline {

	public static void process(Mat img) {
		Start.display(img,1,"input");
		
		Mat edges = new Mat();
		Imgproc.Canny(img, edges, 200,300);
		
		edges = compute.fillHoro(edges);
		
		compute.rectangle(edges, false);
		
		Start.display(edges, 1, "edges");
		
		List<MatOfPoint> hulls = new ArrayList<MatOfPoint>();
		compute.findContours(edges, true, hulls);
		
		List<MatOfPoint> filterHulls = new ArrayList<MatOfPoint>();
		
		for(MatOfPoint p : hulls) {
			double perimeter = Imgproc.arcLength(new MatOfPoint2f(p.toArray()), true);
			double area = Imgproc.contourArea(p);
			double radius = (perimeter / (2 * Math.PI));
			double circularity =  radius * radius * Math.PI;
			
			if(circularity > 0.5 && area > 1800)filterHulls.add(p);
		}
		
		compute.drawHulls(filterHulls, img, new Scalar(255, 0, 0), -1);
		
		Start.display(img, 1, "out");
	}

}
