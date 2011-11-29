package whirlwind.demo.gui;

import java.util.ArrayList;

public class CoordRemap {

	private class Point implements Comparable<Point> {
		public final float fromx;
		public final float fromy;
		public final float tox;
		public final float toy;
		public float xscale;
		public float yscale;
		public Point(float fromx, float fromy, float tox, float toy) {
			this.fromx = fromx;
			this.fromy = fromy;
			this.tox = tox;
			this.toy = toy;
		}
		public float convertx(float in) {
//			 to = (in + offset) / scale
			return (in + xoffset) / xscale;
		}
		public float converty(float in) {
//			 to = (in + offset) / scale
			return (in + yoffset) / yscale;
		}
		public int compareTo(Point o) {
			if (fromx < o.fromx) return -1;
			if (fromx > o.fromx) return 1;
			if (tox < o.tox) return -1;
			if (tox > o.tox) return 1;
			if (xscale < o.xscale) return -1;
			if (xscale > o.xscale) return 1;
			if (fromy < o.fromy) return -1;
			if (fromy > o.fromy) return 1;
			if (toy < o.toy) return -1;
			if (toy > o.toy) return 1;
			if (yscale < o.yscale) return -1;
			if (yscale > o.yscale) return 1;
			return 0;
		}
	}
	
	public static class Result {
		public final float x;
		public final float y;
		public Result(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
	
//	private static class DistanceResult implements Comparable<DistanceResult> {
//		float d;
//		Point p;
//		DistanceResult(Point p, float d) {
//			this.p = p;
//			this.d = d;
//		}
//
//		public int compareTo(DistanceResult o) {
//			if (d < o.d) return -1;
//			if (d > o.d) return 1;
//			return p.compareTo(o.p);
//		}
//		
//		
//	}
	
	ArrayList<Point> points = new ArrayList<Point>();
	
	float minx = Float.MAX_VALUE;
	float maxx = Float.MIN_VALUE;
	float miny = Float.MAX_VALUE;
	float maxy = Float.MIN_VALUE;
	float range;
	float xoffset;
	float yoffset;
	boolean invalid = true;
	
	public void addPoint(float fromx, float fromy, float tox, float toy) {
		points.add(new Point(fromx, fromy, tox, toy));
		invalidate();
	}

	private void invalidate() {
		invalid = true;
	}

	public void validate() {
		if (!invalid) return;
		invalid = false;

		
		
		
		float xscale = 0;
		float yscale = 0;
		int count = 0;
		for (Point p1 : points) {
			
			minx = Math.min(minx, p1.fromx);
			miny = Math.min(miny, p1.fromy);
			maxx = Math.max(maxx, p1.fromx);
			maxy = Math.max(maxy, p1.fromy);
			
			for (Point p2 : points) {
				if (p2.equals(p1)) continue;
				xscale += (p1.fromx-p2.fromx) / (p1.tox-p2.tox);
				yscale += (p1.fromy-p2.fromy) / (p1.toy-p2.toy);
				count++;
			}
		}
		
		xscale /= count;
		yscale /= count;

		xoffset = 0;
		yoffset = 0;
		
		for (Point p1 : points) {
			
			for (Point p2 : points) {
				if (p2.equals(p1)) continue;
				xoffset += (p1.tox * xscale - p1.fromx);
				yoffset += (p1.toy * yscale - p1.fromy);
			}
		}
		
		xoffset /= count;
		yoffset /= count;
		
		
		for (Point p1 : points) {
			p1.xscale = (p1.fromx + xoffset) / p1.tox;
			p1.yscale = (p1.fromy + yoffset) / p1.toy;
		}
		

		// to = (in + offset) / scale
		// (in + offset) / to = scale
			
		
		
		float a = maxx - minx;
		float b = maxy - miny;
		range = (float)Math.sqrt(a*a+b*b);
		

	}
	
//	private float distPointToVec(float px, float py, float p1x, float p1y, float p2x, float p2y)
//	{
//		float v1x = 
//		float dp = 
//	}
	
	public Result convert(float x, float y) {
		validate();

//		// Find nearest 3 points
//		TreeSet<DistanceResult> nearest = new TreeSet<DistanceResult>();
//		TreeSet<DistanceResult> triangle = new TreeSet<DistanceResult>();
//		
//		for (Point p : points) {
//			float dx = x - p.fromx;
//			float dy = y - p.fromy;
//			float dist = (float) Math.sqrt(dx*dx+dy*dy) / range;
//			
//			nearest.add(new DistanceResult(p, dist));
//		}
//		
//		// find closest east
//		for (DistanceResult d : nearest) {
//			if (d.p.fromx <= x && !triangle.contains(d)) {
//				triangle.add(d);
//				break;
//			}
//		}
//
//		// find closest west
//		for (DistanceResult d : nearest) {
//			if (d.p.fromx >= x && !triangle.contains(d)) {
//				triangle.add(d);
//				break;
//			}
//		}
//
//		// find closest north
//		for (DistanceResult d : nearest) {
//			if (d.p.fromy >= y && !triangle.contains(d)) {
//				triangle.add(d);
//				break;
//			}
//		}
//
//		// find closest south
//		for (DistanceResult d : nearest) {
//			if (d.p.fromy <= y && !triangle.contains(d)) {
//				triangle.add(d);
//				break;
//			}
//		}
//		
//		if (triangle.size()==4) {
//			triangle.remove(triangle.last());
//		}
//
//		
//		float weights[] = new float[3];
//		float sum = 0;
//		int i = 0;
//		for (DistanceResult p : triangle) {
//			weights[i] = 1/(p.d);
//			if (weights[i] == Float.POSITIVE_INFINITY) weights[i] = Float.MAX_VALUE;
//			sum += weights[i];
//			i++;
//		}
//		
//		// calc distance from ab line
////		{
////			Iterator<DistanceResult> it = triangle.iterator();
////			DistanceResult a = it.next();
////			DistanceResult b = it.next();
////			DistanceResult c = it.next();
////			float abmag = sqrt()
////			float dp = a.p.fromx * b.p.fromx + a.p.fromy * b.p.fromy;
////		}
//		
//		
//		if (sum == Float.POSITIVE_INFINITY) sum = Float.MAX_VALUE;
//		
//		for (i = 0; i < weights.length; i++) {
//			weights[i] /= sum;
//		}
//		
//		float xout = 0;
//		float yout = 0;
//		i = 0;
//		for (DistanceResult d : triangle) {
//			xout += d.p.convertx(x) * weights[i];
//			yout += d.p.converty(y) * weights[i];
//			i++;
//		}
		
		
		float weights[] = new float[points.size()];
		float sum = 0;
		int i = 0;
		for (Point p : points) {
			float dx = x - p.fromx;
			float dy = y - p.fromy;
			float dist = (float) Math.sqrt(dx*dx+dy*dy) / range;
			
			dist *= 1;	//0-4
			//if (dist>1) dist=100000;
			
			weights[i] = (float)(1/Math.pow(dist, 3));
			//weights[i] = (1/(dist));
			if (weights[i] == Float.POSITIVE_INFINITY) weights[i] = Float.MAX_VALUE;
			sum += weights[i];
			i++;
		}
		if (sum == Float.POSITIVE_INFINITY) sum = Float.MAX_VALUE;
		
		for (i = 0; i < weights.length; i++) {
			weights[i] /= sum;
		}
		
		float xout = 0;
		float yout = 0;
		
		for (i = 0; i < weights.length; i++) {
			Point p = points.get(i);
			xout += p.convertx(x) * weights[i];
			yout += p.converty(y) * weights[i];
		}
		
		return new Result(xout,yout);
	}
	
	
	
}
