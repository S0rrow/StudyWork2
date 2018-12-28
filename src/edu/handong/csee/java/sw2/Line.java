package edu.handong.csee.java.sw2;

import java.awt.Color;
import java.util.Vector;

public class Line{
	public int posX, posY, curX, curY;
	public int LineType;
	public int LineWeight;
	public Color lineColor;
	public Vector<Line> multipleLine = new Vector<Line>();
	Line(){
		posX = 0;
		posY = 0;
		curX = 0;
		curY = 0;
		LineType = -1;
		LineWeight = 0;
		lineColor = Color.black;
	}
	Line(int x1, int y1, int x2, int y2){
		posX = x1;
		posY = y1;
		curX = x2;
		curY = y2;
		LineType = 0;
		lineColor = Color.BLACK;
	}
}
