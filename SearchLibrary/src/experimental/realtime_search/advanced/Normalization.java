package experimental.realtime_search.advanced;

public enum Normalization {
	NONE {
		@Override
		public double normalize(double v, double maxV, double minV) {
			return v;
		}
	},
	
	PROPORTIONAL_0_1 {
		@Override
		public double normalize(double v, double maxV, double minV) {
			return (maxV == minV)? INFINITE : (v - minV) / (maxV - minV);
		}
	},
	
	PROPORTIONAL_TO_MIN {
		@Override
		public double normalize(double v, double maxV, double minV) {
			return v / minV;
		}
	},
	
	DIFFERENCE_TO_MIN {
		@Override
		public double normalize(double v, double maxV, double minV) {
			return v - minV;
		}
	},
	
	
//	RELATIVE_TO_MAX {
//		@Override
//		public double normalize(double v, double maxV, double minV) {
//			return v / maxV;
//		}
//	}
	;


	private static double INFINITE = Math.sqrt(Double.MAX_VALUE);
	
	public abstract double normalize(double v, double maxV, double minV); 
}
