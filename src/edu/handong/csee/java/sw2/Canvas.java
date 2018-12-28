package edu.handong.csee.java.sw2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Vector;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Canvas extends JPanel{
	private final int LINEARLINE = 0;
	private final int PENLINE = 1;
	private final int OVALLINE = 2;
	private final int RECTLINE = 3;
	//private final int ERASELINE = 4;
	private final int POLYLINE = 5;
	private final int ERASEOBJECT = 6;
	public Vector<Line> LineLog = new Vector<Line>();
	@Override
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		super.paint(g);
		setBackground(Color.WHITE);
		for(Line line: LineLog) {
			g.setColor(line.lineColor);
			g.setStroke(new BasicStroke(line.LineWeight));
			//직선
			if(line.LineType == LINEARLINE) g.drawLine(line.posX, line.posY, line.curX, line.curY);
			//원
			else if(line.LineType == OVALLINE) g.drawOval(Math.min(line.posX, line.curX), Math.min(line.posY, line.curY), Math.abs(line.posX-line.curX), Math.abs(line.posY-line.curY));
			//사각형
			else if(line.LineType == RECTLINE) g.drawRect(Math.min(line.posX, line.curX), Math.min(line.posY, line.curY), Math.abs(line.posX-line.curX), Math.abs(line.posY-line.curY));
			else if(line.LineType == PENLINE || line.LineType == POLYLINE) {
				for(Line nodeLine: line.multipleLine) {
					g.setColor(line.lineColor);
					g.setStroke(new BasicStroke(line.LineWeight));
					g.drawLine(nodeLine.posX, nodeLine.posY, nodeLine.curX, nodeLine.curY);
				}
			}
			else if(line.LineType == ERASEOBJECT) {
				//null
			}
			/*else if(line.LineType==ERASELINE) {
				//지우개
				for(Line penLine:line.multipleLine) {
					g.setColor(Color.WHITE);
					g.setStroke(new BasicStroke(line.LineWeight));
					g.drawLine(penLine.posX, penLine.posY, penLine.curX, penLine.curY);
				}
			}*/
		}
		
	}
}
