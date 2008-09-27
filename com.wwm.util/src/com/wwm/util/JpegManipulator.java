/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class JpegManipulator {
	public static class Jpeg {
		private byte[] data;
		private int width;
		private int height;
		
		public Jpeg(byte[] data) throws ImageFormatException {
			if (data != null) {
				int soi_index = 0;
				this.data = data;

				// First check it looks like a jpeg. First find start-of-image marker FFD8
				int jpeg = 0;
				boolean soi = false;
				byte hi_soi = (byte)0xff;
				byte lo_soi = (byte)0xd8;
				while (jpeg < data.length-2 && !soi) {
					if (data[jpeg] == hi_soi && data[jpeg+1] == lo_soi) {
						soi = true;
						jpeg += 2;
						soi_index = jpeg;
						break;	// break while
					}
					jpeg++;
				}
				
				if (!soi) throw new ImageFormatException("Missing JPEG Start of Image marker");
				
				// Must have APP0 or APP1 markers
				if (data[jpeg] != 0xff || (data[jpeg+1] != 0xe1 && data[jpeg+1] != 0xe0) ) if (!soi) throw new ImageFormatException("Missing APPx marker");
				
				/*
				// Now look for FFE0 jFIF marker
				boolean jfif = false;
				byte hi_jfif = (byte)0xff;
				byte lo_jfif = (byte)0xE0;
				while (jpeg < data.length-2 && !jfif) {
					if (data[jpeg] == hi_jfif && data[jpeg+1] == lo_jfif) {
						if (data[jpeg+4] == (byte)0x4A &&
								data[jpeg+5] == (byte)0x46 &&
								data[jpeg+6] == (byte)0x49 &&
								data[jpeg+7] == (byte)0x46 &&
								data[jpeg+8] == (byte)0x00) {
							jfif = true;
							break;	// break while
						}
					}
					jpeg++;
				}
				
				
				if (!jfif) throw new ImageFormatException("Missing JPEG JFIF marker");
				*/
				
				// Look for the SoF marker 0xFF{C0,C1,C2,C3 or CF} and pull out image width and height
				byte hi = (byte)0xff;
				byte lo_baseDCT = (byte)0xc0;	// Baseline DCT
				byte lo_extSeq = (byte)0xc1;	// Extended Sequential
				byte lo_progDCT = (byte)0xc2;	// Progressive DCT
				byte lo_lossSeq = (byte)0xc3;	// Lossless Sequential	(rare)
				byte lo_diffLoss = (byte)0xcf;	// Differential Lossless	(rare)
				int i = soi_index;
				while (i < data.length-8) {
					if (data[i] == hi && 
						((data[i+1] == lo_baseDCT) || (data[i+1] == lo_extSeq) || (data[i+1] == lo_progDCT) ||
						(data[i+1] == lo_lossSeq) || (data[i+1] == lo_diffLoss))) {
						height = 0xff & data[i+5];
						height <<= 8;
						height |= 0xff & data[i+6];
						width = 0xff & data[i+7];
						width <<= 8;
						width |= 0xff & data[i+8];
						return;
					} else if (data[i] == (byte)0xff) {
						int skip = 0xff & data[i+2];
						skip <<= 8;
						skip |= 0xff & data[i+3];
						i += skip + 2;
					} else {
						i++;
					}
					
				}
			}
			throw new ImageFormatException("Missing JPEG Start of Frame marker");
		}
		
		public byte[] getData() {
			return data;
		}

		public int getHeight() {
			return height;
		}

		public int getWidth() {
			return width;
		}
	}
	
	private JpegManipulator() {
		// static only
	}
	
	public static BufferedImage decodeImage(byte[] data) throws ImageFormatException, IOException {
	    // Create BufferedImage
	    BufferedImage bi = null;
    	// load file from disk using Sun's JPEGIMageDecoder
    	ByteArrayInputStream bis = new ByteArrayInputStream(data);
    	JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(bis);
    	bi = decoder.decodeAsBufferedImage();
	    return bi;
	}
	
	public static byte[] encodeImage(BufferedImage image, float quality) throws ImageFormatException, IOException {
		assert(quality>=0.0 && quality <= 1.0);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
		JPEGEncodeParam jep = encoder.getDefaultJPEGEncodeParam(image);
		jep.setQuality(quality, false);
		encoder.setJPEGEncodeParam(jep);
		encoder.encode(image);
		return bos.toByteArray();
	}
	
	public static BufferedImage limitSize(BufferedImage input, int maxWidth, int maxHeight) {
		
		float wScale = input.getWidth();
		wScale /= maxWidth;
		float hScale = input.getHeight();
		hScale /= maxHeight;
		
		double max = Math.max(wScale, hScale);
		if (max <= 1.0) return input;	// no rescale required

		int newWidth = input.getWidth();
		int newHeight = input.getHeight();
		
		if (wScale > hScale) {
			// width is controlling dimension
			newHeight /= wScale;
			newWidth = maxWidth;
		} else {
			// height is controlling dimension
			newHeight = maxHeight;
			newWidth /= hScale;
		}
		
		assert(newWidth <= maxWidth);
		assert(newHeight <= maxHeight);
		
		BufferedImage output=input;
		
		while (max > 2.0) {
			AffineTransform tx = AffineTransform.getScaleInstance(0.5, 0.5);
			AffineTransformOp scaleOp = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			output = new BufferedImage(input.getWidth()/2, input.getHeight()/2, input.getType());
			scaleOp.filter(input, output);
			input = output;
			max /= 2.0;
		}
		
		if (max > 1.0) {
			AffineTransform tx = AffineTransform.getScaleInstance(1.0/max, 1.0/max);
			AffineTransformOp scaleOp = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);
			output = new BufferedImage(newWidth, newHeight, input.getType());
			scaleOp.filter(input, output);
		}
		
		return output;
	}

}
