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
package com.wwm.geo;

/**
 * Abstract base for a Transverse Mercator based easting/northing grid reference
 * 
 * Conversion code to lat-long based on:
 * http://www.gps.gov.uk/guidec.pdf
 * http://www.gps.gov.uk/guidea.asp
 * 
 */
public abstract class GridCoord {
	public final double easting;
	public final double northing;

	public GridCoord(double easting, double northing) {
		this.easting = easting;
		this.northing = northing;
	}

	/**Converts this easting-nothing grid co-ord to lat-long, in degrees
	 * @return The co-ordinate in Degrees
	 */
	public final LatLongDegs toLatLongDegs() {
		return toLatLongRads().toDegrees();
	}

	/**Converts this easting-nothing grid co-ord to lat-long, in radians
	 * @return The co-ordinate in Radians
	 */
	public final LatLongRads toLatLongRads() {
		TransverseMercator tm = getProjection();

		// Scale factor
		final double f0 = tm.f0;

		// Ellipsoid
		final double a = tm.e.a;
		final double b = tm.e.b;
		final double e2 = tm.e.e2;

		// true origin
		final double lamda0 = tm.lamda0;
		final double phi0 = tm.phi0;

		// false origin
		final double e0 = tm.e0;
		final double n0 = tm.n0;

		double phiDash = calcPhiDash(northing, n0, a, f0, phi0);

		double m = calcM(b, f0, (a-b)/(a+b), phiDash, phi0);

		while (northing - n0 - m >= 0.00001) {
			phiDash = iteratePhiDash(northing, n0, m, a, f0, phiDash);
			m = calcM(b, f0, (a-b)/(a+b), phiDash, phi0);
		}

		final double rho = calcRho(a, f0, e2, phiDash);
		final double nu = calcNu(a, f0, e2, phiDash);
		final double eta2 = calcEta2(nu, rho);

		@SuppressWarnings("unused")
		final double rho2 = rho * rho;

		final double nu3 = nu * nu * nu;
		final double nu5 = nu3 * nu * nu;
		final double nu7 = nu5 * nu * nu;

		final double t = tan(phiDash);
		final double t2 = t * t;
		final double t4 = t2 * t2;
		final double t6 = t2 * t4;

		final double s = sec(phiDash);

		final double val_VII = t / (2 * rho * nu);
		final double val_VIII = (t / (24 * rho * nu3)) * (5 + 3 * t2 + eta2 - 9 * t2 * eta2);
		final double val_IX = (t / (720 * rho * nu5)) * (61 + 90 * t2 + 45 * t4);
		final double val_X = s / nu;
		final double val_XI = (s / (6 * nu3)) * (nu / rho + 2 * t2);
		final double val_XII = (s / (120 * nu5)) * (5 + 28 * t2 + 24 * t4);
		final double val_XIIA = (s / (5040 * nu7)) * (61 + 662 * t2 + 1320 * t4 + 720 * t6);

		final double de = easting - e0;

		final double phi = phiDash - val_VII * pow(de, 2) + val_VIII * pow(de, 4) - val_IX * pow(de, 6);
		final double lamda = lamda0 + val_X * de - val_XI * pow(de, 3) + val_XII * pow(de, 5) * val_XIIA * pow(de, 7);

		return new LatLongRads(phi, lamda);
	}

	/**Get the projection used by the grid co-ordinate system
	 * @return The projection
	 */
	protected abstract TransverseMercator getProjection();

	private final double sec(double value) {
		return 1.0/cos(value);
	}

	private final double sin(double value) {
		return Math.sin(value);
	}

	private final double cos(double value) {
		return Math.cos(value);
	}

	private final double tan(double value) {
		return Math.tan(value);
	}

	private final double pow(double x, double y) {
		return Math.pow(x,y);
	}

	private final double calcPhiDash(double n, double n0, double a, double f0, double phi0) {
		return ( (n - n0) / (a * f0) ) + phi0;	// C6
	}

	private final double iteratePhiDash(double n, double n0, double m, double a, double f0, double phiDash) {
		return ( (n - n0 - m) / (a * f0) ) + phiDash; // C7
	}

	@SuppressWarnings("unused")
	private final double calcN(double a, double b) {
		return (a-b) / (a+b);
	}

	private final double calcNu(double a, double f0, double e2, double phi) {
		final double s = sin(phi);
		final double s2 = s*s;

		return a * f0 * pow(1 - e2 * s2, -0.5);
	}

	private final double calcRho(double a, double f0, double e2, double phi) {
		final double s = sin(phi);
		final double s2 = s*s;
		return a * f0 * (1 - e2) * pow(1 - e2 * s2, -1.5);
	}

	private final double calcEta2(double nu, double rho) {
		return nu / rho - 1;
	}

	/** Eqn C3
	 * @return M
	 */
	private final double calcM(double b, double f0, double n, double phi, double phi0) {
		final double n2 = n * n;
		final double n3 = n2 * n;

		return b * f0 * (
				(1.0 + n + 5.0 / 4.0 * n2 + 5.0 / 4.0 * n3) * (phi - phi0) -
				(3.0 * n + 3.0 * n2 + 21.0 / 8.0 * n3) * sin(phi - phi0) * cos(phi + phi0) +
				(15.0 / 8.0 * n2 + 15.0 / 8.0 * n3) * sin(2.0 * (phi - phi0)) * cos(2.0 * (phi + phi0)) -
				35.0 / 24.0 * n3 * sin(3.0 * (phi - phi0)) * cos(3.0 * (phi + phi0))
		);
	}
}
