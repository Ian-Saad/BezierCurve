import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.*;
import java.util.ArrayList;


public class CurveDrawer {
	private BufferedImage frameBuffer;
	private int width;
	private int height;

	private int white;

	private Graphics2D g2;

	public boolean canAddPoints;

	private static final int RADIUS = 7;
	private static final int HALFRADIUS = 3;


	private Color currentColor;

	ArrayList<Point> splinePoints;

	ArrayList<Point> controlPoints; //controlpoints
	//ArrayList<Point> thePoints;		//line segment points
	int[] binomial;

	/**
	 * draws Bezier curves
	 * @param width the width
	 * @param height the 
	 */
	public CurveDrawer(int width, int height){
		this.width = width;
		this.height = height;
		frameBuffer = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		white = Color.WHITE.getRGB();

		g2 = frameBuffer.createGraphics();

		canAddPoints = true;

		currentColor = Color.Blue; 

		controlPoints = new ArrayList<Point>();
	}


	public void drawBackground(){
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				frameBuffer.setRGB(i, j, white);
			}
		}
	}
	//adds a point to controlPoints and redraws
	public void drawPoint(int x, int y){
		if(canAddPoints){
			clear();
			controlPoints.add(new Point(x, y));
			//draw the curve affected by control points
			if(controlPoints.size()>=2){
				drawCurve(controlPoints);
			}
			//cap
			if(controlPoints.size() == 13){
				canAddPoints = false;
			}
			drawControlPoints();
		}
	}
	//draws a point on canvas
	public void drawPoint(Point p){
		g2.setColor(Color.black);
		g2.fillOval(p.x,p.y,RADIUS,RADIUS);
	}
	//draws controlPoints on canvas
	public void drawControlPoints(){
		for(Point P: controlPoints){
			g2.setColor(Color.black);
			g2.fillOval(P.x,P.y,RADIUS,RADIUS);
		}

	}
	//draws a line connecting each controlPoint
	public void drawPolygon(){
		g2.setColor(Color.BLACK);
		
		for(int i = 0; i<controlPoints.size()-1; i++){
			g2.drawLine(controlPoints.get(i).x+HALFRADIUS, 
					controlPoints.get(i).y+HALFRADIUS, 
					controlPoints.get(i+1).x+HALFRADIUS,
					controlPoints.get(i+1).y+HALFRADIUS);
		}	
		}

	public void erasePolygon(){
		g2.setColor(Color.white);
		for(int i = 0; i<controlPoints.size()-1; i++){
			g2.drawLine(controlPoints.get(i).x+HALFRADIUS, 
					controlPoints.get(i).y+HALFRADIUS, 
					controlPoints.get(i+1).x+HALFRADIUS,
					controlPoints.get(i+1).y+HALFRADIUS);
		}
		drawCurve(controlPoints);
		drawControlPoints();
	}

	/*
	 * draw curve based on given array
	 */
	public void drawCurve(ArrayList<Point> theList){
		//generate formula based on theList.size()
		double x1, y1, x2 = 0, y2 = 0;
		x1 = theList.get(0).x;
		y1 = theList.get(0).y;
		for(double k=.025;k<=1.025;k+=.025){
			//reset x2,y2
			x2 = 0;
			y2 = 0;
			for(int i = 0; i <= theList.size()-1; i++){
				x2 += theList.get(i).x * bernstein(k, theList.size()-1, i);
				y2 += theList.get(i).y * bernstein(k, theList.size()-1, i);
			}
			g2.setColor(currentColor);
			g2.drawLine((int)x1+HALFRADIUS,(int)y1+HALFRADIUS, (int)x2+HALFRADIUS, (int)y2+HALFRADIUS);
			x1 = x2;
			y1 = y2;
			
		}
	}

	/** Math Things!
	 */

	// calculate bernstein polynomial at given position

	public double bernstein(double n, int exp, int i){
		return getBinomial(exp, i) * Math.pow(1-n, exp-i) * Math.pow(n,i) ;
	}

	/*
	 * n choose k
	 */
	private int getBinomial(int n, int k){
		if (k < 0)  return 0;
		else if (k > n)  return 0;
		else return (factorial(n) / (factorial(k) * factorial(n-k)));
	}

	/*
	 * simple factorial function
	 */
	private int factorial(int n){
		if(n == 0){
			return 1;
		}
		if(n == 1){
			return 1;
		}
		else return n * factorial(n-1);
	}

	/*
	 * clears the screen and removes all control points
	 */
	public void reset(){
		controlPoints.clear();
		canAddPoints = true;
		clear();
	}

	/*
	 * clears the screen
	 */
	public void clear(){
		g2.setColor(Color.white);
		g2.fillRect(0, 0, width, height);
	}

	/*
	 * checks to see if a given mouse click was on a point or not
	 */
	public Point checkPoint(int x, int y, ArrayList<Point> theList){
		for(Point p : theList){
			if(distance(x,p.x,y,p.y) <= RADIUS){
				return p;
			}
		}
		return null;
	}
	/*
	 * distance formula
	 */
	private double distance(int x1, int x2, int y1, int y2){
		double result = Math.sqrt((Math.pow((x2-x1), 2)) + (Math.pow(y1-y2, 2)));
		return result;
	}
	/*
	 * removes a control point and redraws the curve
	 */
	public void removePoint(Point p){
		clear();
		controlPoints.remove(p);
		controlPoints.trimToSize();
		canAddPoints = true;
		drawControlPoints();
		drawCurve(controlPoints);
	}
	/*
	 * erases the physical dot on the screen
	 */
	public void erasePoint(Point p){
		g2.setColor(Color.white);
		g2.fillOval(p.x,p.y,RADIUS,RADIUS);
	}
	//color options
	public void setColor(String s){
		if(s.equals("Red")){
			currentColor = Color.RED;
		}
		else if(s.equals("Blue")){
			currentColor = Color.BLUE;
		}
		else if(s.equals("Green")){
			currentColor = Color.GREEN;
		}
		else if(s.equals("Magenta")){
			currentColor = Color.MAGENTA;
		}
		else if(s.equals("Cyan")){
			currentColor = Color.CYAN;
		}
		else if(s.equals("Black")){
			currentColor = Color.BLACK;
		}

		if(controlPoints.size()>0){
			if(controlPoints.size()>1){
				drawCurve(controlPoints);
			}
			drawControlPoints();
		}
	}

	/**
	 * Returns the rendered image
	 * @return A Image representation of the currently rendered image
	 */
	public Image getImage()
	{
		return frameBuffer;
	}

	/**
	 * takes a float array color and converts it to an int
	 * @param rgba a color represented by a float array
	 * @return the integer representation of the color
	 */
	public int colorInt(float[] rgba){

		int r = (int)((rgba[0])*(255));
		int g = (int)((rgba[1])*(255));
		int b = (int)((rgba[2])*(255));
		int a = (int)((rgba[3])*(255));
		int color = (a<<24)|(r<<16)|(g<<8)|(b);
		return color;
	}
}
