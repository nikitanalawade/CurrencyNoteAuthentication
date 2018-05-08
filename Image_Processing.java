import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;


    public class Image_Processing{
	public static void main(String args[])throws IOException{
    System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
  Mat image1=new Mat();
  Mat grad =new Mat();
  Mat src_gray=new Mat();
  int scale = 1;
  int delta = 0;
  int ddepth=CvType.CV_16S;
		 
  image1 = Imgcodecs.imread("/home/nikita/Desktop/500/1.jpg",  Imgcodecs.CV_LOAD_IMAGE_COLOR);
  if (image1.empty()) {
      System.out.println("image is empty");
   }
   Imgproc.GaussianBlur(image1, image1, new Size(3,3), 0, 0);
   Imgproc.cvtColor(image1, src_gray, Imgproc.COLOR_RGB2GRAY); /// Convert it to gray
   Mat grad_x=new Mat();// Generate grad_x and grad_y
   Mat grad_y=new Mat();
   Mat abs_grad_x=new Mat();
   Mat abs_grad_y= new Mat();
   Imgproc.Sobel(src_gray, grad_x, ddepth, 1, 0, 3, scale, delta);/// Gradient X
   Core.convertScaleAbs(grad_x, abs_grad_x);
   Imgproc.Sobel(src_gray, grad_y, ddepth, 0, 1, 3, scale, delta);  /// Gradient Y
   Core.convertScaleAbs(grad_y, abs_grad_y);
   Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad);
    Imgcodecs.imwrite( "/home/nikita/Desktop/Integration/sobel.jpg", grad );

   
    Mat image2 = Imgcodecs.imread("/home/nikita/Desktop/Integration/sobel.jpg");
   
   Rect roi = new Rect(3,170,60,40);
   Mat roi1 = image2.submat(roi);
   Imgcodecs.imwrite("/home/nikita/Desktop/Integration/uid.jpg", roi1);
   Rect roi4 = new Rect(5,95,75,75);
   Mat roi5 = image2.submat(roi4);
   Imgcodecs.imwrite("/home/nikita/Desktop/Integration/str.jpg", roi5);
   
   
   Mat image3 = Imgcodecs.imread("/home/nikita/Desktop/500/1.jpg");
   Rect roi6 = new Rect(285,95,125,80);
   Mat roi7 = image3.submat(roi6);
   Imgcodecs.imwrite("/home/nikita/Desktop/Integration/ovi.jpg", roi7);
   Rect roi8 = new Rect(625,60,70,150);
   Mat roi9 = image3.submat(roi8);
   Imgcodecs.imwrite("/home/nikita/Desktop/Integration/latent.jpg", roi9);
   
   Mat  image4 = Imgcodecs.imread("/home/nikita/Desktop/Integration/uid.jpg");
   Rect rectangle = new Rect(2,10,50,100);
   Mat result =new Mat();
   Mat bgModel=new Mat();
   Mat fgModel=new Mat();
   Mat source1 = new Mat(1, 1, CvType.CV_8U, new Scalar(3.0));
   Imgproc.grabCut(image4, result, rectangle, bgModel, fgModel, 1, Imgproc.GC_INIT_WITH_RECT);
   Core.compare(result, source1, result, Core.CMP_EQ);// Get the pixels marked as likely foreground
   Mat foreground=new Mat(image4.size(),CvType.CV_8UC3,new Scalar(255,255,255));// Generate output image
   image4.copyTo(foreground,result); // bg pixels not copied
   Imgproc.rectangle(image4, new Point(rectangle.x,rectangle.y), new Point(rectangle.x + rectangle.width, rectangle.y + rectangle.height), new Scalar(255,255,255));// draw rectangle on original image
   Imgcodecs.imwrite("/home/nikita/Desktop/Integration/uid_extract.jpg",image4);
   Imgcodecs.imwrite("/home/nikita/Desktop/Integration/uid_final.jpg",foreground);

   Mat image5 =Imgcodecs.imread( "/home/nikita/Desktop/Integration/uid_final.jpg");
   Mat gray=new Mat();
   if (image5.channels() == 3)
   {
       Imgproc.cvtColor(image5, gray, Imgproc.COLOR_BGR2GRAY);// Transform source image to gray if it is not
   }
   else
   {
       gray = image5;
   }
   Mat bw=new Mat();// Transform it to binary and invert it. White on black is needed.
   Imgproc.threshold(gray, bw, 40, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
   MatOfPoint black_pixels=new MatOfPoint();   // output, locations of non-zero pixels
   Core.findNonZero(bw, black_pixels);
   MatOfInt4 hierarchy=new MatOfInt4();
   List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
   Imgproc.findContours(bw.clone(), contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));  // extract only the external blob
   Mat mask = Mat.zeros(bw.size(), CvType.CV_8UC1);
   for(int i = 0; i < contours.size(); i++)// draw the contours as a solid blob, and create a mask of the cloud
   Imgproc.drawContours(mask, contours, i, new Scalar(255, 255, 255), Core.FILLED, 8, hierarchy, 0, new Point());
   MatOfPoint all_pixels=new MatOfPoint();   // output, locations of non-zero pixels
   Core.findNonZero(mask, all_pixels);
      
   Mat image6 = Imgcodecs.imread("/home/nikita/Desktop/Integration/str.jpg",Imgcodecs.CV_LOAD_IMAGE_COLOR);
   Mat destination = new Mat(image6.rows(),image6.cols(),image6.type());
   Imgproc.GaussianBlur(image6, destination, new Size(0,0), 10);
   Core.addWeighted(image6, 2.5, destination, -0.5, 0, destination);
   Imgcodecs.imwrite("/home/nikita/Desktop/Integration/str_sharpen.jpg", destination);

   Mat image7= Imgcodecs.imread("/home/nikita/Desktop/Integration/str_sharpen.jpg");
   Mat thresh=new Mat();
   Mat eroded=new Mat();
   Imgproc.threshold(image7, thresh, 120, 255, Imgproc.THRESH_BINARY_INV);
   Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(2,2));
   Imgproc.erode(thresh,eroded,element);
   Imgcodecs.imwrite("/home/nikita/Desktop/Integration/str_final.jpg",eroded);
	
   Mat image8 =Imgcodecs.imread( "/home/nikita/Desktop/Integration/str_final.jpg");
   Mat gray1=new Mat();
   if (image8.channels() == 3)
   {
       Imgproc.cvtColor(image8, gray1, Imgproc.COLOR_BGR2GRAY);// Transform source image to gray if it is not
   }
   else
   {
       gray1 = image8;
   }
   Mat bw1=new Mat();// Transform it to binary and invert it. White on black is needed.
   Imgproc.threshold(gray1, bw1, 40, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
   MatOfPoint black_pixels1=new MatOfPoint();   // output, locations of non-zero pixels
   Core.findNonZero(bw1, black_pixels1);
   MatOfInt4 hierarchy1=new MatOfInt4();
   List<MatOfPoint> contours1 = new ArrayList<MatOfPoint>();
   Imgproc.findContours(bw1.clone(), contours1, hierarchy1, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));  // extract only the external blob
   Mat mask1 = Mat.zeros(bw1.size(), CvType.CV_8UC1);
   for(int i = 0; i < contours1.size(); i++)// draw the contours as a solid blob, and create a mask of the cloud
   Imgproc.drawContours(mask1, contours1, i, new Scalar(255, 255, 255), Core.FILLED, 8, hierarchy1, 0, new Point());
   MatOfPoint all_pixels1=new MatOfPoint();   // output, locations of non-zero pixels
   Core.findNonZero(mask1, all_pixels1);
      
   Mat image9= Imgcodecs.imread("/home/nikita/Desktop/Integration/latent.jpg");
   Mat src_gray1=new Mat();
   Mat thresh1=new Mat();
   Imgproc.cvtColor( image9, src_gray1, Imgproc.COLOR_BGR2GRAY );
   Imgproc.threshold(src_gray1,thresh1,70,255,Imgproc.THRESH_BINARY);
   Imgproc.medianBlur(thresh1,thresh1,3);
   Imgcodecs.imwrite("/home/nikita/Desktop/Integration/latent_final.jpg",thresh1);

   Mat image10 =Imgcodecs.imread( "/home/nikita/Desktop/Integration/latent_final.jpg");
   Mat m = new Mat();
   Core.extractChannel(image10, m, 0);
   int n = Core.countNonZero(m);
   
   try{
   File input=new File("/home/nikita/Desktop/Integration/ovi.jpg");
   BufferedImage image;
   int width,i,j;
   int height;
   image = ImageIO.read(input);
   width = image.getWidth();
   height = image.getHeight();
   int countr=0;
   int[][] g = new int[height][width];
   int[][] r = new int[height][width];
   int[][] b = new int[height][width];
   for( i=0; i<height; i++)
   	{
	   for( j=0; j<width; j++)
	   	{
		   Color c = new Color(image.getRGB(j, i));
		   r[i][j]=c.getRed();
		   b[i][j]=c.getBlue();
           g[i][j]=c.getGreen();
           countr+=g[i][j];
        }
   }
  
   File file = new File("/home/nikita/Desktop/countresult.txt"); // creates the file
   file.createNewFile(); // creates a FileWriter Object
   FileWriter writer = new FileWriter(file);// Writes the content to the file 
   writer.write("1,"+black_pixels.size()+","+all_pixels.size()+","+"0"+","+"\n"); 
   writer.write("2,"+black_pixels1.size()+","+all_pixels1.size()+","+"0"+","+"\n");
  
   writer.write("3,"+n+","+"0"+","+"0"+","+"\n"); 
   writer.write("4,"+countr+","+"0"+","+"0"+","); 
   writer.flush();
   writer.close();
   Path path = Paths.get("/home/nikita/Desktop/countresult.txt");
   Charset charset = StandardCharsets.UTF_8;

   String content = new String(Files.readAllBytes(path), charset);
   content = content.replaceAll("1x","");
   content = content.replaceAll("0x","");
   Files.write(path, content.getBytes(charset));
   }
   catch (Exception e) {}
   
 

  
  }
}