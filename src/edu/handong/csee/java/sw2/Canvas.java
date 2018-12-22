package edu.handong.csee.java.sw2;

import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class Canvas extends JComponent{
	Vector<Line> LineLog = new Vector<Line>();
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(Line line: LineLog) {
			g.setColor(line.lineColor);
			g.drawLine(line.posX, line.posY, line.curX, line.curY);
		}
	}
}
