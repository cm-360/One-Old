package com.github.cm360.onegame.utils;

public class PseudoRandom {

	// Simplified version of java.util.Random 
	// https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
	
	private long seed;
	
	public PseudoRandom(long seed) {
		setSeed(seed);
	}
	
	public void setSeed(long seed) {
		seed = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
	}
	
	public int next(int bits) {
		seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
		return (int) (seed >>> (48 - bits));
	}
	
	public void nextBytes(byte[] bytes) {
		for (int i = 0; i < bytes.length;)
			for (int rnd = next(32), n = Math.min(bytes.length - i, 4); n-- > 0; rnd >>= 8)
				bytes[i++] = (byte) rnd;
	}

}
