package algorithms.realtime.weighted_extensions;

public enum Normalization {
	
	NONE ("No") {
		@Override
		public double normalize(double v, double maxV, double minV) {
			return v;
		}
	},
	
	PROPORTIONAL_0_1 ("Unit") {
		@Override
		public double normalize(double v, double maxV, double minV) {
			return (maxV == minV)? 1.0d : (v - minV) / (maxV - minV); //if INFINITE was returned, the value would loose information
		}
	},

	PROPORTIONAL_1_2 ("Unit_1_2") {
		@Override
		public double normalize(double v, double maxV, double minV) {
			double unit01 = (maxV == minV)? 1.0d : (v - minV) / (maxV - minV);
			return unit01 + 1.0d;
		}
	},
	
	PROPORTIONAL_TO_MIN_X ("Pmin+") {
		@Override
		public double normalize(double v, double maxV, double minV) {
			return v / (minV + 1.0d);
		}
	},
	
	PROPORTIONAL_TO_MIN ("Pmin") {
		@Override
		public double normalize(double v, double maxV, double minV) {
			if (minV == 0) return v; //if INFINITE was returned, the value would loose information
			else return v / minV;
		}
	},

	DIFFERENCE_TO_MIN ("Dmin") {
		@Override
		public double normalize(double v, double maxV, double minV) {
			return v - minV + 1;
		}
	},
	
	DIFFERENCE_TO_MIN_0 ("Dmin0") {
		@Override
		public double normalize(double v, double maxV, double minV) {
			return v - minV; // pode dar problemas quando usa multiplicacao
		}
	}

	;

	private String label;
	
	private Normalization(String label) {
		this.label = label;
	}
	
	public abstract double normalize(double v, double maxV, double minV);
	
	public String toString() {
		return label;
	}

}
