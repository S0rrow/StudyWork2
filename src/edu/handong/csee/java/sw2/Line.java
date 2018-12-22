package edu.handong.csee.java.sw2;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Line extends JPanel{
	public int posX, posY, curX, curY;
	public int LineType;
	public Color lineColor;

	private final int LINEARLINE = 0;
	private final int PENLINE = 1;
	
	Line(int x1, int y1, int x2, int y2){
		posX = x1;
		posY = y1;
		curX = x2;
		curY = y2;
		LineType = LINEARLINE;
		lineColor = Color.BLACK;
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(lineColor);
		if(LineType == LINEARLINE) g.drawLine(posX, posY, curX, curY);
		else if(LineType == PENLINE) {
			//펜선 그리는 부분.
		}
	}
}
