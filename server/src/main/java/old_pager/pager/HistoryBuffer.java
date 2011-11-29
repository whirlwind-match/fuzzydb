/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package old_pager.pager;

public class HistoryBuffer {

	private final int length;
	private final float[] values;
	private final int[] weights;
	
	private long lastUpdateTime;
	private final long startTime;
	private int position;
	private int weight;
	private float total;
	private int resolutionMillis;
	private boolean bufferFull = false;
	
	public HistoryBuffer(int length, int resolutionMillis) {
		this.length = length;
		this.resolutionMillis = resolutionMillis;
		this.values = new float[length];
		this.weights = new int[length];
		this.position = 0;
		this.weight = 0;
		this.total = 0;
		this.startTime = this.lastUpdateTime = System.currentTimeMillis();
	}
	
	public void insert(float value) {
		update();
		values[position] += value;
		weights[position]++;
		weight++;
		total += value;
	}
	
	public float getMean() {
		if (weight==0) return 0;
		update();
		float rval;
		rval = total;
		rval /= weight;
		return rval;
	}
	
	
	public float getRollingTotal() {
		update();
		if (bufferFull) {
			return total;
		}
		long life = System.currentTimeMillis()-startTime;
		if (life > (long)length * (long)resolutionMillis) {
			bufferFull = true;
			return total;
		}
		if (life == 0) {
			return Float.MAX_VALUE;
		}
		
		return (total*length*resolutionMillis)/life;
		
	}
	private void update() {
		int spincount = 0;
		long newTime = System.currentTimeMillis();
		long delta = newTime - lastUpdateTime;
		while (delta > resolutionMillis) {
			position++;
			if (position >= length) {
				position = 0;
			}
			spincount++;
			if (spincount > length) {
				delta /= resolutionMillis;
				delta *= resolutionMillis; // round off
				lastUpdateTime += delta;
				assert(total==0);
				assert(weight==0);
				return;
			}
			total -= values[position];
			weight -= weights[position];
			values[position] = 0;
			weights[position] = 0;
			delta -= resolutionMillis;
			lastUpdateTime += resolutionMillis;
		}
	}
	
	
	
	
}
