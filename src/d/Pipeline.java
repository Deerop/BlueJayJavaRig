package d;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import f.Start;

import java.util.ArrayList;
import java.util.List;

public class Pipeline {
	
	private static final int regionSideClipExtensionLength = 120; //120
	
    public static Mat resizedImage = new Mat();

    public static List<Foundation> foundations = new ArrayList<Foundation>();
    public static List<Stone>      stones      = new ArrayList<Stone>();
    public static List<SkyStone>   skyStones   = new ArrayList<SkyStone>();

    //debug steps
    public static Mat red    = new Mat();
    public static Mat blue   = new Mat();
    public static Mat yellow = new Mat();
    public static Mat black  = new Mat();
    public static Mat yellowKeep  = new Mat();
    public static int blackcut=0;

    public static boolean doFoundations = true;
    public static boolean doStones = true;
    public static boolean doSkyStones = true;

    public static Mat process(Mat source0){
        return process(source0, 640, 480);
        //return process(source0, 3264/4, 2448/4);
    }

    /**
     * Give it the raw image and it will update the Foundations arraylist
     *
     * @return source image with annotations on it
     */
    public static Mat process(Mat source0, double width, double height) {
        System.gc();
        System.runFinalization();

        //default image size: 3264 x 2448
        Imgproc.resize(source0, resizedImage, new Size(width*1, height*1), 0.0, 0.0, Imgproc.INTER_LINEAR);
        
        Start.display(resizedImage,1,"input");
        //compute.whiteBalance(resizedImage, 1.15,0.9);

        Mat equalizedImage = compute.equalize(resizedImage);

        Mat original = resizedImage.clone();

        //set ranges
        double blackCutOff = compute.getHistogramfast(resizedImage);
        blackcut= (int)blackCutOff;

        Constants.updateColors(resizedImage, equalizedImage, blackCutOff);

        Mat redOutput   = Constants.redOutput;
        Mat blueOutput  = Constants.blueOutput;
        Mat blackOutput = Constants.blackOutput;
        Mat yellowOutput= Constants.yellowOutput;
        Mat yellowTags  = Constants.yellowTags;
        
        //Start.display(yellowOutput, 1, "yellow");
        //Start.display(blackOutput, 1, "black");
        //Start.display(blueOutput, 1, "blue");
        //Start.display(redOutput, 1, "red");
        
        //For Video debug display
        red = redOutput.clone();
        blue = blueOutput.clone();
        yellow = yellowOutput.clone();
        yellow=equalizedImage;
        black = blackOutput.clone();

        skyStones.clear();
        stones.clear();
        foundations.clear();

        if(doSkyStones) skyStones = computeSkyStones(yellowTags);
        if(doStones) stones = computeStones(yellowOutput, original);
        if(doFoundations) foundations = computeFoundations(redOutput, blueOutput, yellowOutput, blackOutput, original);

        for (Stone s : stones) {
            s.draw(original);
        }for (Foundation f : foundations) {
            f.draw(original);
        }for (SkyStone s : skyStones) {
            s.draw(original);
        }

        redOutput.release();
        blueOutput.release();
        yellowOutput.release();
        blackOutput.release();

        return original;
    }

    static List<SkyStone> computeSkyStones(Mat yellowTags){
    	//morph
    	yellowTags = compute.fillHoro(yellowTags);
    	
    	List<SkyStone> skyStones = new ArrayList<SkyStone>();
    	//yellowKeep = yellowTags.clone();
    	//hulls
				//Start.display(yellowTags,1,"yellowTAG");
        List<MatOfPoint> hulls = compute.findHulls(yellowTags);
       
        Mat mask = yellowTags.clone();
        compute.rectangle(mask,true);
        
        compute.drawHulls(hulls,mask, new Scalar(255,255,255),-1);
        compute.drawHulls(hulls,mask, new Scalar(0,0,0),2);//fuckin' aliased edges
        
        mask = compute.flip(mask);
        yellowTags = compute.add(yellowTags,mask);
        		
        		//Start.display(yellowTags,1,"yellowTAG");
        		
        //setup Mat
        Mat drawInternalHulls = new Mat(yellowTags.rows(), yellowTags.cols(), CvType.CV_8UC3); 
        yellowKeep = yellowTags.clone();
        
        Imgproc.rectangle(drawInternalHulls,
        		new Point(0,0), 
        		new Point(drawInternalHulls.width(),drawInternalHulls.height()),
        		new Scalar(0,0,0),
        		-1);
        
        yellowTags = compute.flip(yellowTags); 
        
        	Start.display(yellowTags,1,"flipTags");
        List<MatOfPoint> internalHulls = compute.findHulls(yellowTags);
        internalHulls = compute.filterContours(internalHulls,1500);//1700
        
        compute.drawHulls(internalHulls,drawInternalHulls);
        		Start.display(drawInternalHulls,1,"hulls");
        
        for(MatOfPoint h : internalHulls) {
        	SkyStone ss = new SkyStone(h);
        	if(!ss.isBastard) {
        		skyStones.add(ss);
        	}
        }

        return skyStones;
    }

    /*
     * Takes in yellow masks, and image to annotate on
     * spits out list of Stones
     */
	static List<Stone> computeStones(Mat yellowOutput, Mat canvas){
		Start.display(yellowOutput,1,"stone in");
    	Mat dTrans = compute.distanceTransform(yellowOutput,12);
        Start.display(dTrans,1,"expl aftemat");
    	
    	List<MatOfPoint> stonesContour = compute.findHulls(dTrans);
        
        List<Stone> stones = new ArrayList<Stone>();
        
        for(MatOfPoint con : stonesContour) {
        	Stone d = new Stone(con);
        	if(!d.isBastard) {
        		stones.add(d);
        	}
        }

        return stones;
    }

    /*
     * Takes in red, blue, yellow, black masks, and image to annotate on
     * spits out list of Foundations
     */
    static List<Foundation> computeFoundations(Mat redOutput, Mat blueOutput, Mat yellowOutput, Mat blackOutput, Mat canvas){
        //Find Contours
        List<MatOfPoint> hullsRed = compute.findHulls(redOutput);
        List<MatOfPoint> hullsBlue = compute.findHulls(blueOutput);
        List<MatOfPoint> hullsYellow = compute.findHulls(yellowOutput);

        //populate array of detected (color only)
        List<Detected> detected = new ArrayList<Detected>();
        List<MatOfPoint> detectedHulls = new ArrayList<MatOfPoint>();

        //we will segregate the blacks
        List<Detected> blacks = new ArrayList<Detected>();

        for (MatOfPoint p : hullsRed) {
            Detected toadd = new Detected(p, Detected.Color.RED);
            if (!toadd.isBastard) {
                detected.add(toadd);
                detectedHulls.add(p);
            }
        }
        for (MatOfPoint p : hullsBlue) {
            Detected toadd = new Detected(p, Detected.Color.BLUE);
            if (!toadd.isBastard) {
                detected.add(toadd);
                detectedHulls.add(p);
            }
        }
        /*
        for (MatOfPoint p : hullsYellow) {
            Detected toadd = new Detected(p, Detected.Color.YELLOW);
            if (!toadd.isBastard) {
                detected.add(toadd);
            }
        }*/
        Mat detectedAll = new Mat(redOutput.rows(),redOutput.cols(),redOutput.type());
        Imgproc.rectangle(detectedAll,
        		new Point(0,0),
        		new Point(detectedAll.width(),detectedAll.height()),
        		new Scalar(0,0,0),
        		-1);
        compute.drawHulls(detectedHulls, detectedAll, new Scalar(255,255,255),-1);

        //limit black to regions underneath
        Imgproc.dilate(detectedAll,detectedAll,
    			Imgproc.getStructuringElement(
    					Imgproc.MORPH_RECT,
    					new Size(1,80),
    					new Point(0,0)
    				));
        detectedAll = compute.flip(detectedAll);
        blackOutput = compute.subtract(blackOutput,detectedAll);

        //cut sides of color contours. Field walls are bad.
        for(Detected d:detected) {
        	Point one = new Point(d.bounds.x,d.bounds.y+d.bounds.height*0.1);
        	Point two = new Point(d.bounds.x,d.bounds.y+d.bounds.height*0.1+regionSideClipExtensionLength);
        	Imgproc.line(blackOutput,one, two,new Scalar(new double[] {0,0,0}),1);

        	one = new Point(d.bounds.x+d.bounds.width,d.bounds.y+d.bounds.height*0.1);
        	two = new Point(d.bounds.x+d.bounds.width,d.bounds.y+d.bounds.height*0.1+regionSideClipExtensionLength);
        	Imgproc.line(blackOutput,one, two,new Scalar(new double[] {0,0,0}),1);
        }

        ArrayList<MatOfPoint> hullsBlack = compute.findHulls(blackOutput);

        for (MatOfPoint p : hullsBlack) {
            Detected toadd = new Detected(p, Detected.Color.BLACK);
            if (!toadd.isBastard) {
                blacks.add(toadd);
            }
        }

        for (Detected d : detected) {
            d.draw(canvas);
        }for (Detected d : blacks) {
            d.draw(canvas);
        }

        //process sandwiches, populate foundation ArrayList
        List<Foundation> foundations = new ArrayList<Foundation>();

        Imgproc.putText(canvas, String.valueOf(blackcut), new Point(20,20), Core.FONT_HERSHEY_SIMPLEX, 0.6, new Scalar(0,0,0), 7);
        Imgproc.putText(canvas, String.valueOf(blackcut), new Point(20,20), Core.FONT_HERSHEY_SIMPLEX, 0.6, new Scalar(255,255,0), 2);

        for (Detected black : blacks) {
            for (Detected col : detected) {
                if (black.x > col.x - 40 && //one above the other, within 40 pix
                	black.bounds.y < col.bounds.y+col.bounds.height+30 &&//touching, within 30 pix
                    Math.abs(black.bounds.width*1.0/col.bounds.width-1)  <  0.6)   {//widths match, within 0.6
                    	foundations.add(Foundation.createFoundation(black, col));
                }
            }
        }

        return foundations;
    }


}

