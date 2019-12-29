package d;

import org.opencv.core.Mat;

public class Constants {
	//For Blue
	public static double[] blueColor = {90,140};

    //for Red
	public static double[] redColor1 = {160,180};
	public static double[] redColor2 = {0,3};
	
	//STONES
    public static final double[] yellowColor = {10,37};
    //SKYSTONES
    public static final double[] stressedYellowColor = {3,45};
    
    public static Mat redOutput = new Mat();
    public static Mat blueOutput = new Mat();
    public static Mat blackOutput = new Mat();
    public static Mat yellowOutput = new Mat();
    public static Mat yellowTags = new Mat();

    public static void updateColors(Mat resizedImage, Mat equalizedImage, double blackCutOff) {

        double[] satRange = {60, 255};
        double[] valRange = {blackCutOff*1.0, 255};

        redOutput = compute.combine(
        		compute.threshold(resizedImage, redColor1, satRange, valRange),
        		compute.threshold(resizedImage, redColor2, satRange, valRange));

        blueOutput = compute.threshold(resizedImage, blueColor, satRange, valRange);

        blackOutput = compute.threshold(
                resizedImage,
                new double[]{0, 255},//hue  0, 180
                new double[]{0, 180},//sat  0, 180
                new double[]{0, blackCutOff*0.8});//val
        
        
        yellowOutput = compute.threshold(
        		equalizedImage, 
        		yellowColor, 
        		new double[]{100, 255},//sat
                new double[]{blackCutOff*1, 255}); //val
        
        yellowTags = compute.threshold(
        		resizedImage, 
        		stressedYellowColor, 
        		new double[]{90, 255},//sat
                new double[]{blackCutOff*0.8, 255}); //val
        

    }

}

