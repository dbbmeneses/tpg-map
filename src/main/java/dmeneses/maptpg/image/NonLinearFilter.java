package dmeneses.maptpg.image;

public class NonLinearFilter {
	public static double f(double v) {
		double ss = 1.0/8;
		
		if(v < ss) { //red
			v= (0.08)*(v/ss);
		}
		else if(v < 2*ss) { //orange
			v= 0.08 + (0.08)*((v-ss)/ss);
		}
		else if(v < 3*ss) { //yellow
			v= 0.16 + (0.07)*((v-2*ss)/ss);
		}
		else if(v < 4*ss) { //green
			v= 0.23 + (0.21)*((v-3*ss)/ss);
		}
		else if(v < 5*ss) { //light blue
			v= 0.44 + (0.1)*((v-4*ss)/ss);
		}
		else if(v < 6*ss) { //dark blue
			v= 0.54 + (0.19)*((v-5*ss)/ss);
		}
		else if(v < 7*ss) { //light pink
			v= 0.73 + (0.09)*((v-6*ss)/ss);
		}
		else { //dark pink
			v= 0.82 + (0.12)*((v-7*ss)/ss);
		}
		
		return v;
	}
}
