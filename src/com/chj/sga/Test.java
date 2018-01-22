package com.chj.sga;

import java.io.IOException;

public class Test {
	public static void main(String[] args) {
		SGA sga = new SGA();
		try {
			sga.getInfo();
			sga.init();
			sga.RandomDNA();
			sga.Evolution();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}