import java.awt.Color;

public class GaussianNoise {
	private int sigma;
	private int[] pixel;
	private int[] noise;
	private final int MaxValue = 255;

	GaussianNoise(int sigma, int[][] colorBins) {
		this.sigma = sigma;
		this.pixel = new int[colorBins[0].length];
		noise = new int[pixel.length];

		RGBToGray(colorBins);
	}

	private void RGBToGray(int[][] colorBins) {
		for (int p = 0; p < colorBins[0].length; p++) {
			pixel[p] = (colorBins[0][p] + colorBins[1][p] + colorBins[2][p]) / 3;
		}
	}

	public int[] getNoise() {
		return noise;
	}

	public int[] noise_generation() {
		// generate noise
		for (int i = 0; i < noise.length; i++) {
			noise[i] = (int) Math.round(Box_Muller_transform(i));
		}

		// add noise
		for (int i = 0; i < pixel.length; i++) {
			pixel[i] = check(pixel[i], noise[i]);
		}

		return pixel;
	}

	private int check(int pixel, int noise) {
		int sum = pixel + noise;
		if (sum < 0) {
			return 0;
		} else if (sum > MaxValue) {
			return MaxValue;
		} else {
			return sum;
		}
	}

	private double Box_Muller_transform(int i) {
		double z, r, phi;
		r = random();
		phi = random();

		if (i % 2 == 0) {
			z = sigma * Math.cos(2 * Math.PI * phi) * Math.sqrt((-2) * Math.log(r));
		} else {
			z = sigma * Math.sin(2 * Math.PI * phi) * Math.sqrt((-2) * Math.log(r));
		}

		return z;
	}

	/*
	 * random r、phi range [0,1]
	 */
	private double random() {
		return Math.random();
	}
}
