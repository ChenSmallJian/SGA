package com.chj.sga;

public class DNA {
	private int index;
	private String DNA;
	private double adaption;
	private double probability;

	public DNA(int index, String dNA, double adaption, double probability) {
		this.index = index;
		this.DNA = dNA;
		this.adaption = adaption;
		this.probability = probability;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getDNA() {
		return DNA;
	}

	public void setDNA(String dNA) {
		DNA = dNA;
	}

	public double getAdaption() {
		return adaption;
	}

	public void setAdaption(double adaption) {
		this.adaption = adaption;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public String toString() {
		return "DNA [index=" + index + ", DNA=" + DNA + ", adaption=" + adaption + ", probability=" + probability + "]";
	}

}