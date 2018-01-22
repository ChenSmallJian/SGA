package com.chj.sga;

import javax.swing.JPanel;
import org.jfree.ui.ApplicationFrame;

public class LineChart extends ApplicationFrame {
	private static final long serialVersionUID = 1L;

	public LineChart(String s,JPanel jpanel) {
		super(s);
		setContentPane(jpanel);
	}
}