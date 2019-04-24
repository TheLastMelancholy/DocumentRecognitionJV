import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.Point;
import java.awt.image.*;
import java.util.ArrayList;

import java.io.IOException;
import javax.imageio.ImageIO;

import java.io.File;

class dot{
	int x;
	int y;
	dot(int _x, int _y){
		x=_x; y=_y;
	}
}

class square{
	dot[] d;
	int area;
	square(dot[] _dots){
		d = _dots;
	}
	square(dot d1, dot d2, dot d3, dot d4){
		d = new dot[4];
		d[0]=d1;
		d[1]=d2;
		d[2]=d3;
		d[3]=d4;
		area=(d[1].x-d[0].x)*(d[1].y-d[2].y);
		//System.out.println(area);
	}

	float index(){
		return ((float)((float)d[1].x-(float)d[0].x))/(float)(((float)d[1].y-(float)d[2].y));
	}

	point[][] fill(point[][] image){
		//System.out.println(d[2].y +" " + d[0].y + " " + d[1].x + " " + d[0].x);
for(int i = d[2].y+3; i< d[0].y-3; i++){
	for(int j=d[0].x+3; j< d[1].x-3; j++){
		//System.out.println("i " + i + " j " + j);
		image[i][j] = new point(0,0,255,255);
	}

}
return image;

	}

 	float occupied(point[][] image){
 		int counter=0;
 		for(int i = d[2].y+3; i< d[0].y-3; i++){
		for(int j=d[0].x+3; j< d[1].x-3; j++){
		//System.out.println("i " + i + " j " + j);
		if(image[i][j].isBlack()) counter++;
	}
 	}
 	return (float)counter/(float)area;
}
}


class point{
int R, G, B, A;	
	
point(int _r, int _g, int _b, int _a){
	R=_r; G=_g; B=_b; A=_a;
}

point(int val){
	R=val; G=val; B=val; A=255;
	//R=val; G=0; B=0; A=255;
}

boolean isGray() {
	return R==G && B==R && G==B;
}

int avColor() {
	return (R+G+B/3);
}

boolean isBlack(){
if(R==0 && B==0 && G==0)
return true;
return false;
}

boolean isWhite(){
if(R==255 && B==255 && G==255)
return true;
return false;
}

boolean isRed(){
	if(R==255 && B==0 && G==0)
return true;
return false;
}

boolean isBlue(){
		if(R==0 && B==255 && G==0)
return true;
return false;
}
}








public class jv_try_10 {

	static point[][] image;
	static int w;
	static int h;
	static int cols = 20;
	static int rows = 23;

	static int boxTop, boxBottom, boxLeft, boxRight;
	
   public static void main(String[] args) throws IOException {

	   
	  File img = new File("Test" + args[0] + ".jpg");
   	
      BufferedImage hugeImage = ImageIO.read(img);
      image = convertTo2DWithoutUsingGetRGB(hugeImage);
      
      if(image==null) System.out.println("Can't read");

      w=image[0].length;
      h=image.length;

      System.out.println("w = " + w + " h = " + h);

      
      //turnItRed();
      makeItGrey(190, 190, 0); //???????????
      //writeItDown("result1.png", image);
      fillHoles();
      //writeItDown("result2.png", image);
      //applyNet(5);
      //fillSquares();

      boxTop=0; boxBottom=0; boxLeft=0; boxRight=0;
      ArrayList<square> squares = borderApproach();
      System.out.println("Threre are " + squares.size() + " blue dots");
      //paintSqBlue(squares);
      //squares = sortByArea(squares);

      squares = trim(squares, 25, (int)w*h/2);

      // for(square s: squares){
      // 	System.out.print(s.area + " ");
      // }

      //paintSqBlue(squares);
      System.out.println("\nMedian is " + squares.get(squares.size()/2).area);

      int avInd = mostCommon(squares, 5);
      System.out.println("\nMost common is " + squares.get(avInd).area);

      ArrayList<square> commons = selectCommons(squares, avInd, 15);

		for(square c: commons){
       	System.out.print(c.area + " ");
       }


       //!!!
       commons = excludeLong(commons, 0.5f, 1.5f);




       //paintSqBlue(commons);
       //commons = returnOrder(commons);

       //!!!
       //ArrayList<square> group = groupOnly(commons, 20*23);
	   ArrayList<square> group = groupOnly(commons, cols*rows);
	   //ArrayList<square> group=commons;

      //     for(int i=0; i<group.size();i+=1) image = group.get(i).fill(image);
      //     writeItDown("result8.png", image);

       //paintSqBlue(group);
       //writeItDown("result.png", image);

       //for(square s: group) image = s.fill(image); 

       System.out.println("\nselected " + group.size());

       //!!!
       //float[] cellData = new float[20*23];
       float[] cellData = new float[cols*rows];

	   for(int i=0; i<cols*rows; i++)cellData[i]=group.get(i).occupied(image);


	   	boolean[][] result = new boolean[rows][cols];

	   	int counter=0;
	   	//Cols ~ j
	   	//Rows ~ i
	   for(int i =0; i<cols*rows; i++){
	   	if(cellData[i]>0.03f) {counter++;
	   		result[(i-i%cols)/cols][i%cols] = false;
	   		image = group.get(i).fill(image);
	   	}
	   	else result[(i-i%cols)/cols][i%cols] = true;
	   }

	   System.out.println("There are " + counter + " crimes");


	   for(int i=0; i<rows; i++){
	   	for(int j=0; j<cols; j++){
	   		if(result[i][j])
	   		System.out.print(" ");
	   		else System.out.print("Н");
	   	}
	   	System.out.println();
	   }


      writeItDown("result.png", image);
                   
      
   }


   static void fillHoles(){
   	for(int i=1; i<image.length-1; i++){
   		for(int j=1;j<image[0].length-1; j++){
   			if(image[i][j].isWhite() && image[i][j-1].isBlack() && image[i][j+1].isBlack()) image[i][j]=new point(0,0,0,255);
   			if(image[i][j].isWhite() && image[i-1][j].isBlack() && image[i+1][j].isBlack()) image[i][j]=new point(0,0,0,255);
   		}
   	}
   }


   	static ArrayList<square> returnOrder(ArrayList<square> sq){
   		for(int i=0; i<sq.size(); i++){
   			for(int j=0; j<sq.size()-1; j++){
   				if(sq.get(j).d[0].x>sq.get(j).d[0].x || sq.get(j+1).d[0].y>sq.get(j+1).d[0].y){
   					square tmp = sq.get(j);
   					sq.set(j, sq.get(j+1));

   					//sq[j]=sq[j+1];
   					sq.set(j+1, tmp);
    				}
   			}
   		}
   		return sq;
   	}

   	static ArrayList<square> groupOnly(ArrayList<square> sq, int groupSize){
   		ArrayList<square> group = new ArrayList<square>();
   		for(int i=0; i<groupSize; i++){
   			group.add(sq.get(i));
   		}
   		return group;
   	}



   	static ArrayList<square> excludeLong(ArrayList<square> sq, float left, float right){
   		for(int i=0; i<sq.size(); i++){
   			if(sq.get(i).index() < left || sq.get(i).index() > right) {sq.remove(i); i--; continue;}
   		}
   		return sq;
   	}

static ArrayList<square> selectCommons(ArrayList<square> sq, int comm, int deviation){
ArrayList<square> res = new ArrayList<square>();
int absDev=(int)(((float)sq.get(comm).area/100.0f)*(float)deviation);
int exp = sq.get(comm).area;
for(int i=0; i<sq.size(); i++){
	if(Math.abs(sq.get(i).area-exp)<=absDev) res.add(sq.get(i));
}
return res;
}

   static int mostCommon(ArrayList<square> sq, int deviation){
int absDev = 0;
int counter=0;
int maxCounter=0;
int maxInd=0;
for(int i=0; i<sq.size(); i++){
		absDev=(int)(((float)sq.get(i).area/100.0f)*(float)deviation);
		counter=0;
		for(int j=0; j<sq.size(); j++){
			if(i==j) continue;
			if(Math.abs(sq.get(i).area-sq.get(j).area)<absDev) counter++;
		}
		if(counter>maxCounter){
			maxInd=i;
			maxCounter=counter;
		}
}
return maxInd;
   }



   static void paintSqBlue(ArrayList<square> sq){
	for(int i=0; i<sq.size(); i++){
	for(int j=0; j<4; j++)
		image[sq.get(i).d[j].y][sq.get(i).d[j].x]=new point(0,0,255,255);
}

   }

   	static ArrayList<square> sortByArea(ArrayList<square> sq){
   		for(int i=0; i<sq.size(); i++){
   			for(int j=0; j<sq.size()-1; j++){
   				if(sq.get(j).area>sq.get(j+1).area){
   					square tmp = sq.get(j);
   					sq.set(j, sq.get(j+1));

   					//sq[j]=sq[j+1];
   					sq.set(j+1, tmp);
    				}
   			}
   		}
   		return sq;
   	}

   	static ArrayList<square> trim(ArrayList<square> sq, int minPos, int maxPos){
   		for(int i=0; i<sq.size(); i++){
   			if(sq.get(i).area<=minPos) {sq.remove(i); i--; continue;}
   			if(sq.get(i).area>=maxPos) {sq.remove(i); i--; continue;}
   		}
   		return sq;
   	}

  //  	static void applyNet(int netStep){
  //  		for(int i=0; i<image.length; i+=netStep){
  //  			for(int j=1; j<image[0].length-1; j+=1){
  //  				if(image[i][j].isBlack()){
  //  					int counter=1;
  //  					while(j+counter<image[0].length-1 && image[i][j+counter].isBlack()){
  //  						counter++;
  //  					}
  //  					//System.out.println(counter);
  //  					if(counter<5 && counter>=1)
  //  						for(int k = 0; k<counter; k++)
  //  							image[i][j+k] = new point(255, 0 , 0, 255);

  //  				} //&& image[i][j-1].isBlack() && image[i][j+1].isBlack())
  //  					//image[i][j] = new point(255, 0, 0, 255);
  //  			}
  //  		}
  //  		for(int i=0; i<image.length; i++){
  //  			for(int j=1; j<image[0].length-1; j+=netStep){
  //  				if(image[i][j].isBlack()){
  //  					int counter=1;
  //  					while(i+counter<image.length-1 && image[i+counter][j].isBlack()){
  //  						counter++;
  //  					}
  //  					//System.out.println(counter);
  //  					if(counter<5 && counter>=1)
  //  						for(int k = 0; k<counter; k++){

  //  							if(!image[i+k][j].isRed())
  //  							image[i+k][j] = new point(0, 0 , 255, 255);
  //  							else image[i+k][j] = new point(0, 255 , 0, 255);

  //  					}
  //  						}

  //  				} //&& image[i][j-1].isBlack() && image[i][j+1].isBlack())
  //  					//image[i][j] = new point(255, 0, 0, 255);
  //  			}
  //  		}

  //  	static void fillSquares(){
  //  		for(int i=0; i<image.length; i++){
  //  			for(int j=0; j<image[0].length; j++)
  //  				if(image[i][j].isWhite()){ //{fillArea(i, j);}
  //  				int t = top(i, j);
  //  				int d = down(i, j);
  //  				int l = left(i, j);
  //  				int r = right(i, j);

  //  				int height = t-d;
  //  				int width  = r-l;

  //  				System.out.println("Width is " + width + " height is " + height);

  //  				float sqCf = (float)height/(float)width;

  //  				if(sqCf>=1 && sqCf<=2){
  //  					if(i-width/2 >=0 && j-height/2 >=0)
  //  					image[i-width/2][j-height/2]=new point(255,0,0,255);
  //  					if(i-width/2 >=0 && j+height/2 <image[0].length)
  //  					image[i-width/2][j+height/2]=new point(255,0,0,255);
  //  					if(i+width/2 <image.length && j-height/2 >=0)
  //  					image[i+width/2][j-height/2]=new point(255,0,0,255);
		// 			if(i+width/2 <image.length && j+height/2 <image[0].length)
  //  					image[i+width/2][j+height/2]=new point(255,0,0,255);
  //  				}

  //  				} 
  //  		}
  //  	}

  //  	static int down(int I, int J){
  //  		while(I>=0 && image[I][J].isWhite())
  //  			I--;
  //  		return I;
  //  	}

  //  	static int top(int I, int J){
		// while(I<image.length && image[I][J].isWhite())
  //  			I++;
  //  		return I;

  //  	}

  //  	static int left(int I, int J){
  //  		while(J>=0 && image[I][J].isWhite())
  //  			J--;
  //  		return J;

  //  	}

  //  	static int right(int I, int J){
  //  		while(J>=image.length && image[I][J].isWhite())
  //  			J++;
  //  		return J;
  //  	}

   	// static int[] fillArea(int I, int J){
   	// 	if(image[I][J].isWhite()){
   	// 		image[I][J] = new point(255,0,0,255);
   	// 		if(I-1>=0){fillArea(I-1, J);}
   	// 	    if(I+1<image.length){fillArea(I+1, J);}
   	// 	    if(J+1<image[0].length){fillArea(I, J+1);}
   	// 	    if(J-1>=0){fillArea(I, J-1);}
   	// 		 	}
   	// 	else {
   	// 		int[] cords = new int[2];
   	// 		cords[0] = I;
   	// 		cords[1] = J;
   	// 	}
   	// }

   static int blueCounter(){
   	int counter=0;
   	for(int i=0; i<image.length; i++)
   			for(int j=0; j<image[0].length; j++)
   				if(image[i][j].isBlue()) counter++;
   				
   				return counter;
   }

   	static ArrayList<square> borderApproach(){
   		ArrayList<square> dots = new ArrayList<square>();
   		for(int i=0; i<image.length; i++)
   			for(int j=0; j<image[0].length; j++)
   				if(image[i][j].isWhite()) {
   					boxTop=i; boxBottom=i; boxLeft=j; boxRight=j;
   					fillBorder(i, j);

   					if(boxTop!=i || boxBottom!=i || boxLeft!=j || boxRight!=j){
   					dots.add(new square(new dot(boxLeft,  boxTop),
   						  				new dot(boxRight, boxTop),
   						 				new dot(boxRight, boxBottom),
   						  				new dot(boxLeft,  boxBottom)));

   					//image[boxTop][boxRight]= new point(0,0,255,255);
   					//image[boxTop][boxLeft]= new point(0,0,255,255);
   					//image[boxBottom][boxRight]= new point(0,0,255,255);
   					//image[boxBottom][boxLeft]= new point(0,0,255,255);
   				}
   				}
   				return dots;
   	}


   	static int blackNeghbours(int i, int j){
   		int counter=0;
   		if(i-1>=0) 									if(image[i-1][j].isBlack()) counter++;
   		if(j-1>=0) 									if(image[i][j-1].isBlack()) counter++;
   		if(i+1<image.length) 						if(image[i+1][j].isBlack()) counter++;
   		if(j+1<image[0].length) 					if(image[i][j+1].isBlack()) counter++;
   		if(i-1>=0 && j-1>=0) 						if(image[i-1][j-1].isBlack()) counter++;
   		if(i-1>=0 && j+1<image[0].length) 			if(image[i-1][j+1].isBlack()) counter++;
   		if(j-1>=0 && i+1<image.length) 				if(image[i+1][j-1].isBlack()) counter++;
   		if(j+1<image[0].length && i+1<image.length) if(image[i+1][j+1].isBlack()) counter++;

   		return counter;
   	}

   	static int whiteNeighbours(int i, int j){
   		int counter=0;
   		if(i-1>=0) 									if(image[i-1][j].isWhite()) counter++;
   		if(j-1>=0) 									if(image[i][j-1].isWhite()) counter++;
   		if(i+1<image.length) 						if(image[i+1][j].isWhite()) counter++;
   		if(j+1<image[0].length) 					if(image[i][j+1].isWhite()) counter++;
   		if(i-1>=0 && j-1>=0) 						if(image[i-1][j-1].isWhite()) counter++;
   		if(i-1>=0 && j+1<image[0].length) 			if(image[i-1][j+1].isWhite()) counter++;
   		if(j-1>=0 && i+1<image.length) 				if(image[i+1][j-1].isWhite()) counter++;
   		if(j+1<image[0].length && i+1<image.length) if(image[i+1][j+1].isWhite()) counter++;

   		return counter;
   	}


   	static void fillBorder(int i, int j){
   		if(blackNeghbours(i,j)>0){
   			image[i][j]=new point(255,0,0,255);
   			if(i>boxTop) boxTop=i;
   			if(i<boxBottom) boxBottom=i;
   			if(j>boxRight) boxRight=j;
   			if(j<boxLeft) boxLeft=j;


   		if(i-1>=0) 										if(image[i-1][j].isWhite())   fillBorder(i-1, j);
   		if(j-1>=0) 										if(image[i][j-1].isWhite())   fillBorder(i, j-1);
   		if(i+1<image.length-145) 						if(image[i+1][j].isWhite())   fillBorder(i+1, j); //TO DEBUG //!!!
   		if(j+1<image[0].length)  						if(image[i][j+1].isWhite())   fillBorder(i, j+1);
   		if(i-1>=0              && j-1>=0) 				if(image[i-1][j-1].isWhite()) fillBorder(i-1, j-1);
   		if(i-1>=0              && j+1<image[0].length) 	if(image[i-1][j+1].isWhite()) fillBorder(i-1, j+1);
   		if(j-1>=0              && i+1<image.length) 	if(image[i+1][j-1].isWhite()) fillBorder(i+1, j-1);
   		if(j+1<image[0].length && i+1<image.length) 	if(image[i+1][j+1].isWhite()) fillBorder(i+1, j+1);
   	}

   	}



	  static void makeItGrey(int blackWave, int whiteWave, int del){
	  	for(int i=0;i<image.length; i++) {
			   for(int j=0; j<image[0].length; j++) {
				   int r = image[i][j].R;
				   int g = image[i][j].G;
				   int b = image[i][j].B;
				   
				   if(r-del<=blackWave && g-del<=blackWave && b-del<=blackWave){
				   	//System.out.println("Black gate activated");
				   	image[i][j] = new point(0,0,0,255);
				   }

				   if(r+del>=whiteWave && g+del>=whiteWave && b+del>=whiteWave){
				   	image[i][j]= new point(255,255,255,255);
				   	//System.out.println("white gate activated");
				   }

				   }
		   }
	  }
	   
	   
	   static void clearTransperency() {
		   for(int i=0;i<image.length; i++) {
			   for(int j=0; j<image[0].length; j++) {
				   
				   if(image[i][j].A!=255) image[i][j].A=0;}
		   }
		   
	   }
	   
	   
	   
	   
	   
	   
	   static point[][] createCopy(){
		   point[][] imageCleared = new point[image.length][image[0].length];
		   for(int i=0; i<image.length; i++) {
			   for(int j=0; j<image[0].length; j++) {
				imageCleared[i][j]=image[i][j];
			   }
		   }
		   return imageCleared;
	   }
	   
	   
   
   
   static void cutPart(int I, int J, int lenght, int counter) throws IOException {
	   
	   point[][] temp = new point[lenght][lenght];
	   for(int i=0; i<lenght; i++) {
		   for(int j=0; j<lenght; j++) {
			   temp[i][j]=image[I+i][J+j];
		   }
	   }
	   writeItDown(new String("G:\\Arch\\img") +Integer.toString(counter) + ".png", temp);
   }
   
   
   static void imageCutter(int pieces) throws IOException {
	   int counter=0;
	   
	       for(int i=0; i<=image.length    - image.length/pieces;    i+=image.length/pieces) {
	    	   
		   for(int j=0; j<=image[0].length - image[0].length/pieces; j+=image[0].length/pieces) {
			   counter++;
			   //System.out.println(counter);
			   cutPart(i,j, image.length/pieces, counter);
			   
			   
		   }
		   
	   }
	   
	   
	   
   }
   
   
   
   
   
   
   static void writeItDown(String name, point[][] array) throws IOException {
	   
	   System.out.println(array.length);
	   
	   final byte[] pixelsToWrite = new byte[array.length*array[0].length*4];
	   //System.out.println(pixelsToWrite.length);

	   
	   final int pixelLength = 4;
	   for (int pixel = 0, row = 0, col = 0; pixel < pixelsToWrite.length; pixel += pixelLength) {
           //System.out.println(row + " " + col);
		   //System.out.println(array[col][row].A);

		   pixelsToWrite[pixel] =  (byte)  (array[col][row].A);
		   pixelsToWrite[pixel+1] =  (byte) (array[col][row].B);
		   pixelsToWrite[pixel+2] = (byte)  (array[col][row].G);
		   pixelsToWrite[pixel+3] = (byte)  (array[col][row].R);
          
           row++;
           if (row == array[0].length) {
              row = 0;
              col++;
           }
        }
	   
	   BufferedImage BufImg = new BufferedImage(array[0].length, array.length, BufferedImage.TYPE_4BYTE_ABGR);
	   BufImg.setData(Raster.createRaster(BufImg.getSampleModel(), new DataBufferByte(pixelsToWrite, pixelsToWrite.length), new Point() ) );
	   
	   
	   
	   //"G:\\Arch\\result.png"
	   
	   File img = new File(name);
	   
	   ImageIO.write(BufImg, "png", img);
	   
	   
   }


   private static point[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

      final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
      
      
      final int width = image.getWidth();
      final int height = image.getHeight();
      final boolean hasAlphaChannel = image.getAlphaRaster() != null;

      point[][] result = new point[height][width];
      if (!hasAlphaChannel) {
      //int maxRow=0, maxCol=0;
         final int pixelLength = 3;
         int pixel, row, col;
         System.out.println(pixels.length);
         for (pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
            point p = new point(((int) pixels[pixel + 2] & 0xff),  // Red
            		((int) pixels[pixel + 1] & 0xff),              // Green
            		(int) pixels[pixel] & 0xff,                // Blue
            		255);         // Alpha
                   
            result[col][row] = p;
            //System.out.println(p.A);
            row++;
            if (row == width) {
               row = 0;
               col++;
            }
         }
       
         System.out.println(row + " " + col);}
      return result;
   }

   
}