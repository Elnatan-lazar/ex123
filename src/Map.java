import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * This class represents a 2D map as a "screen" or a raster matrix or maze over integers.
 * @author boaz.benmoshe
 *
 */
public class Map implements Map2D, Serializable{
	private int[][] _map;
	private boolean _cyclicFlag = true;
	
	/**
	 * Constructs a w*h 2D raster map with an init value v.
	 * @param w
	 * @param h
	 * @param v
	 */
	public Map(int w, int h, int v) {
		init(w,h, v);
	}
	/**
	 * Constructs a square map (size*size).
	 * @param size
	 */
	public Map(int size) {this(size,size, 0);}
	
	/**
	 * Constructs a map from a given 2D array.
	 * @param data
	 */
	public Map(int[][] data) {
		init(data);
	}
	@Override
	public void init(int w, int h, int v) {

		_map=new int[w][h];
		for (int i = 0; i <w ; i++) {
			for (int j = 0; j <h ; j++) {
				_map[i][j]=v;
			}
		}
	}
	@Override
	public void init(int[][] arr) {
		
		if(arr == null){
			System.out.println(" board is null");
		}
		if(arr.length==0||arr[0].length==0){
			System.out.println("borad is 0");
		}
		int colomLength = arr[0].length;
		
		for (int i = 0; i < arr.length ; i++) {
			if(arr[i].length!=colomLength){
				System.out.println("board is raged ");
			}
		}
		_map=new int[arr.length][colomLength];
		for (int i = 0; i <_map.length ; i++) {
			for (int j = 0; j <_map[0].length ; j++) {
				_map[i][j]=arr[i][j];
			}
		}
		
		
		
		
		
		
	}
	@Override
	public int[][] getMap() {
		int colom=getHeight();
		int row=getWidth();
		int[][] copymap = new int[row][colom];
		for (int i = 0; i <this.getWidth() ; i++) {
			for (int j = 0; j <this.getHeight() ; j++) {
				copymap[i][j]=_map[i][j];
			}
		}
		return copymap;
	}
	@Override
	public int getWidth() {return _map.length;}

	@Override
	public int getHeight() {return _map[0].length;}
	@Override
	public int getPixel(int x, int y) {
		return _map[x][y];
	}
	@Override
	public int getPixel(Pixel2D p) {
		return this.getPixel(p.getX(),p.getY());
	}
	@Override
	public void setPixel(int x, int y, int v) {_map[x][y] = v;}
	@Override
	public void setPixel(Pixel2D p, int v) {

		setPixel(p.getX(), p.getY(), v);
	}


	@Override
	/** 
	 * Fills this map with the new color (new_v) starting from p.
	 * https://en.wikipedia.org/wiki/Flood_fill
	 */
	public int fill(Pixel2D xy, int new_v) {
		int ans2 = 0;
		Pixel2D start=new Index2D(xy);
		Map2D ans = null;
		if(this.isInside(start)==false ) {              // if the starting point is outside
			return  0;
		}
		if(this.getPixel(start) == new_v ){               // if the starting point is an obsColor
			return  0;
		}
		Queue<Pixel2D> queue=new LinkedList<>();
		queue.add(start);
		int color= this.getPixel(start);//init ans
		                     // update the map to -1 for obs and -2 for optional pass
		this.setPixel(start,new_v);
		ans2++;
		while(!queue.isEmpty()){
			start = queue.poll();

			Pixel2D Left_Pixel_neighbor  = new Index2D(start.getX()-1,start.getY());
			Pixel2D Right_Pixel_neighbor = new Index2D(start.getX()+1,start.getY());
			Pixel2D Down_Pixel_neighbor = new Index2D(start.getX(),start.getY()-1);
			Pixel2D Up_Pixel_neighbor = new Index2D(start.getX(),start.getY()+1);

			if(isCyclic()) {


				if (start.getX() == 0) {
					Left_Pixel_neighbor = new Index2D(getWidth() - 1, start.getY());
				}
				if (start.getX() == this.getWidth() - 1) {
					Right_Pixel_neighbor = new Index2D(0, start.getY());
				}
				if (start.getY() == 0) {
					Down_Pixel_neighbor = new Index2D(start.getX(), getHeight() - 1);
				}
				if (start.getY() == getHeight() - 1) {
					Up_Pixel_neighbor = new Index2D(start.getX(), 0);
				}
			}

				if(isInside(Left_Pixel_neighbor )) {
					if (this.getPixel(Left_Pixel_neighbor ) == color) {
						this.setPixel(Left_Pixel_neighbor , new_v);
						queue.add(Left_Pixel_neighbor );
						ans2++;

					}
				}

				if(isInside(Right_Pixel_neighbor)) {
					if (this.getPixel(Right_Pixel_neighbor) == color) {
						this.setPixel(Right_Pixel_neighbor, new_v);
						queue.add(Right_Pixel_neighbor);
						ans2++;

					}
				}

				if(isInside(Up_Pixel_neighbor)) {
					if (this.getPixel(Up_Pixel_neighbor) == color) {
						this.setPixel(Up_Pixel_neighbor, new_v);
						queue.add(Up_Pixel_neighbor);
						ans2++;

					}
				}
				if(isInside(Down_Pixel_neighbor)) {
					if (this.getPixel(Down_Pixel_neighbor) == color) {
						this.setPixel(Down_Pixel_neighbor, new_v);
						queue.add(Down_Pixel_neighbor);
						ans2++;

					}
				}


			}
		return ans2;
	}




	/**
	 * Computes the distance of the shortest path (minimal number of consecutive neighbors) from p1 to p2.
	 * Notes: the distance is using computing the shortest path and returns its length-1, as the distance fro  a point
	 * to itself is 0, while the path contains a single point.
	 
	 */
	public int shortestPathDist(Pixel2D p1, Pixel2D p2, int obsColor) {

		Pixel2D [] shortestPath=shortestPath(p1,p2,obsColor);
		if(shortestPath == null){
			return  -1;
		}

		return shortestPath.length-1;
	}
	@Override
	/**
	 * BFS like shortest the computation based on iterative raster implementation of BFS, see:
	 * https://en.wikipedia.org/wiki/Breadth-first_search
	 */
	public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor) {
		Pixel2D[] ans = null;  // the result.
		if(!isInside(p1) || !isInside(p2)){ // if one of the points outside the map
			return null;
		}
		if(this.getPixel(p1) == obsColor|| this.getPixel(p2)== obsColor ){ // if one of the points are an obs
			return null;
		}
		if(p1.equals(p2)){
			return new Pixel2D[]{p1};
		}

		Map2D copyMapDist = allDistance(p1,obsColor);
		if(copyMapDist.getPixel(p2)==-1){          // we couldn't reach p2
			return null;
		}
		ans =new Pixel2D[copyMapDist.getPixel(p2)+1];
		ans[0]= new Index2D(p2);
		int index=1;

		Pixel2D start=new Index2D(p2);

		while (copyMapDist.getPixel(start)!=0) {
			int lastNumberIndex = copyMapDist.getPixel(start);

			Pixel2D Left_Pixel_neighbor  = new Index2D(start.getX()-1,start.getY());
			Pixel2D Right_Pixel_neighbor = new Index2D(start.getX()+1,start.getY());
			Pixel2D Down_Pixel_neighbor = new Index2D(start.getX(),start.getY()-1);
			Pixel2D Up_Pixel_neighbor = new Index2D(start.getX(),start.getY()+1);
			if (isCyclic()) {


				if (start.getX() == 0) {
					Left_Pixel_neighbor = new Index2D(getWidth() - 1, start.getY());
				}
				if (start.getX() == this.getWidth() - 1) {
					Right_Pixel_neighbor = new Index2D(0, start.getY());
				}
				if (start.getY() == 0) {
					Down_Pixel_neighbor = new Index2D(start.getX(), getHeight() - 1);
				}
				if (start.getY() == getHeight() - 1) {
					Up_Pixel_neighbor = new Index2D(start.getX(), 0);
				}

			}
				if (isInside(Left_Pixel_neighbor)) {
					if (copyMapDist.getPixel(Left_Pixel_neighbor) == lastNumberIndex - 1) {
						ans[index] = new Index2D(Left_Pixel_neighbor);
						index++;
						start = Left_Pixel_neighbor;
						continue;
					}
				}

				if (isInside(Right_Pixel_neighbor)) {
					if (copyMapDist.getPixel(Right_Pixel_neighbor) == lastNumberIndex - 1) {
						ans[index] = new Index2D(Right_Pixel_neighbor);
						index++;
						start = Right_Pixel_neighbor;
						continue;
					}
				}

				if (isInside(Up_Pixel_neighbor)) {
					if (copyMapDist.getPixel(Up_Pixel_neighbor) == lastNumberIndex - 1) {
						ans[index] = new Index2D(Up_Pixel_neighbor);
						index++;
						start = Up_Pixel_neighbor;
						continue;
					}
				}
				if (isInside(Down_Pixel_neighbor)) {
					if (copyMapDist.getPixel(Down_Pixel_neighbor) == lastNumberIndex - 1) {
						ans[index] = new Index2D(Down_Pixel_neighbor);
						index++;
						start = Down_Pixel_neighbor;

					}
				}


			}

		return ans;
	}

	@Override
	public Pixel2D[] shortestPath(Pixel2D[] points, int obsColor) {
		Pixel2D[] ans = null;
		// add you code here

		////////////////////
		return ans;
	}

	@Override
	public boolean isInside(Pixel2D p) {
		return isInside(p.getX(),p.getY());
	}

	@Override
	public boolean isCyclic() {
		return _cyclicFlag;
	}

	@Override
	public void setCyclic(boolean cy) {
		_cyclicFlag = cy;
	}

	private boolean isInside(int x, int y) { ///  3
		return x>=0 && y>=0 && x<this.getWidth() && y<this.getHeight();
	}
	@Override
	public Map2D allDistance(Pixel2D start, int obsColor) {
		Map2D ans = null;
		if(!this.isInside(start)) {
			ans = new Map(getWidth(), getHeight(), -1);
			return  ans;
		}
		if(this.getPixel(start) == obsColor ){
			ans = new Map(getWidth(), getHeight(), -1);
			return  ans;
		}

		Queue<Pixel2D> queue=new LinkedList<>();
		queue.add(start);
		ans=new Map(getWidth(),getHeight(),0);
		updateMap(ans,obsColor);
		ans.setPixel(start,0);
		while(!queue.isEmpty()){
			start = queue.poll();
			int num= ans.getPixel(start);
			Pixel2D Left_Pixel_neighbor  = new Index2D(start.getX()-1,start.getY());
			Pixel2D Right_Pixel_neighbor = new Index2D(start.getX()+1,start.getY());
			Pixel2D Down_Pixel_neighbor = new Index2D(start.getX(),start.getY()-1);
			Pixel2D Up_Pixel_neighbor = new Index2D(start.getX(),start.getY()+1);
			if(isCyclic()) {


				if (start.getX() == 0) {
					Left_Pixel_neighbor = new Index2D(getWidth() - 1, start.getY());
				}
				if (start.getX() == this.getWidth() - 1) {
					Right_Pixel_neighbor = new Index2D(0, start.getY());
				}
				if (start.getY() == 0) {
					Down_Pixel_neighbor = new Index2D(start.getX(), getHeight() - 1);
				}
				if (start.getY() == getHeight() - 1) {
					Up_Pixel_neighbor = new Index2D(start.getX(), 0);
				}
			}

				if(isInside(Left_Pixel_neighbor)) {
					if (ans.getPixel(Left_Pixel_neighbor) == -2) {
						ans.setPixel(Left_Pixel_neighbor, num + 1);
						queue.add(Left_Pixel_neighbor);

					}
				}

				if(isInside(Right_Pixel_neighbor)) {
					if (ans.getPixel(Right_Pixel_neighbor) == -2) {
						ans.setPixel(Right_Pixel_neighbor, num + 1);
						queue.add(Right_Pixel_neighbor);

					}
				}

				if(isInside(Up_Pixel_neighbor)) {
					if (ans.getPixel(Up_Pixel_neighbor) == -2) {
						ans.setPixel(Up_Pixel_neighbor, num + 1);
						queue.add(Up_Pixel_neighbor);

					}
				}
				if(isInside(Down_Pixel_neighbor)) {
					if (ans.getPixel(Down_Pixel_neighbor) == -2) {
						ans.setPixel(Down_Pixel_neighbor, num + 1);
						queue.add(Down_Pixel_neighbor);

					}
				}



			}


		for (int i = 0; i < this.getWidth(); i++) {
			for (int j = 0; j <getHeight() ; j++) {
				if(ans.getPixel(i,j)==-2){
					ans.setPixel(i,j,-1);
				}
			}
		}





		return ans;
	}

	private void updateMap(Map2D map,int obsColor){
		for (int i = 0; i <this.getWidth() ; i++) {
			for (int j = 0; j <getHeight() ; j++) {
				if(this.getPixel(i,j)!=obsColor){
					map.setPixel(i,j,-2);
				}
				else {
					map.setPixel(i,j,-1);
				}
			}
		}
	}

	@Override
	public int numberOfConnectedComponents(int obsColor) {
		int ans = -1;
		// add you code here

		////////////////////
		return ans;
	}

	@Override
	public boolean equals(Object ob) {
		boolean ans = false;
		if(ob==null){return  false;}
		if(!(ob instanceof Map)){return  false;}

		Map2D temp =(Map) ob;
		if(this.isCyclic()!=temp.isCyclic()){
			return  false;
		}
		if(temp.getHeight()!=this.getHeight()||temp.getWidth()!=this.getWidth()){
			return  false;
		}

		for (int i = 0; i <this.getWidth() ; i++) {
			for (int j = 0; j <this.getHeight() ; j++) {
				if (this.getPixel(i,j)!=temp.getPixel(i,j)){
					return false;
				}
			}
		}

		return true;
	}
	////////////////////// Private Methods ///////////////////////
	// add you code here

	////////////////////

}
