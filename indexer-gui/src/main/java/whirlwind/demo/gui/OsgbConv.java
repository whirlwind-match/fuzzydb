package whirlwind.demo.gui;



import org.fuzzydb.attrs.location.EcefVector;

import com.wwm.geo.Datum;
import com.wwm.geo.Ellipsoid;
import com.wwm.geo.OsgbGridCoord;




public class OsgbConv {

	  public static OsgbGridCoord convert(EcefVector vector) {

		  	Ellipsoid airy1830 = Datum.airy1830;
		    double OSGB_F0 = 0.9996012717;
		    double N0 = -100000.0;
		    double E0 = 400000.0;
		    double phi0 = Math.toRadians(49.0);
		    double lambda0 = Math.toRadians(-2.0);
		    double a = airy1830.a;
		    double b = airy1830.b;
		    double eSquared = airy1830.e2;
		    double phi = vector.getLatRads();
		    double lambda = vector.getLonRads();
		    double E = 0.0;
		    double N = 0.0;
		    double n = (a - b) / (a + b);
		    double v = a * OSGB_F0
		        * Math.pow(1.0 - eSquared * sinSquared(phi), -0.5);
		    double rho = a * OSGB_F0 * (1.0 - eSquared)
		        * Math.pow(1.0 - eSquared * sinSquared(phi), -1.5);
		    double etaSquared = (v / rho) - 1.0;
		    double M = (b * OSGB_F0)
		        * (((1 + n + ((5.0 / 4.0) * n * n) + ((5.0 / 4.0) * n * n * n)) * (phi - phi0))
		            - (((3 * n) + (3 * n * n) + ((21.0 / 8.0) * n * n * n))
		                * Math.sin(phi - phi0) * Math.cos(phi + phi0))
		            + ((((15.0 / 8.0) * n * n) + ((15.0 / 8.0) * n * n * n))
		                * Math.sin(2.0 * (phi - phi0)) * Math.cos(2.0 * (phi + phi0))) - (((35.0 / 24.0)
		            * n * n * n)
		            * Math.sin(3.0 * (phi - phi0)) * Math.cos(3.0 * (phi + phi0))));
		    double I = M + N0;
		    double II = (v / 2.0) * Math.sin(phi) * Math.cos(phi);
		    double III = (v / 24.0) * Math.sin(phi) * Math.pow(Math.cos(phi), 3.0)
		        * (5.0 - tanSquared(phi) + (9.0 * etaSquared));
		    double IIIA = (v / 720.0) * Math.sin(phi) * Math.pow(Math.cos(phi), 5.0)
		        * (61.0 - (58.0 * tanSquared(phi)) + Math.pow(Math.tan(phi), 4.0));
		    double IV = v * Math.cos(phi);
		    double V = (v / 6.0) * Math.pow(Math.cos(phi), 3.0)
		        * ((v / rho) - tanSquared(phi));
		    double VI = (v / 120.0)
		        * Math.pow(Math.cos(phi), 5.0)
		        * (5.0 - (18.0 * tanSquared(phi)) + (Math.pow(Math.tan(phi), 4.0))
		            + (14 * etaSquared) - (58 * tanSquared(phi) * etaSquared));

		    N = I + (II * Math.pow(lambda - lambda0, 2.0))
		        + (III * Math.pow(lambda - lambda0, 4.0))
		        + (IIIA * Math.pow(lambda - lambda0, 6.0));
		    E = E0 + (IV * (lambda - lambda0)) + (V * Math.pow(lambda - lambda0, 3.0))
		        + (VI * Math.pow(lambda - lambda0, 5.0));

		    return new OsgbGridCoord(E, N);

		  }
	  
	  private static double sinSquared(double x) {
		    return Math.sin(x) * Math.sin(x);
		  }
	  
	  protected static double tanSquared(double x) {
		    return Math.tan(x) * Math.tan(x);
		  }
	  
	
}
