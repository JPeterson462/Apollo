package net.digiturtle.apollo;

public class SharedUtils {
	
	public static float[] generateBins(int n) {
		float[] bins = new float[n];
		for (int i = 0; i < n; i++) {
			bins[i] = (float)i/n;
		}
		return bins;
	}

	public static int assignBin(float[] cutoffs, float value) {
		for (int i = cutoffs.length - 1; i >= 0; i--) {
			if (value > cutoffs[i]) {
				return i;
			}
		}
		return 0;
	}
	
}
