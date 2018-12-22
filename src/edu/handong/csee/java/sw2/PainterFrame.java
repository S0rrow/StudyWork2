package edu.handong.csee.java.sw2;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class PainterFrame {
	private int posX, posY, curX, curY;
	public int curMouseAction;
	private final int LINEARLINE = 0;
	private final int PENLINE = 1;
	private Color curLineColor;
	
	PainterFrame(){
		curMouseAction = LINEARLINE;
		posX = 0;
		posY = 0;
		curX = 0;
		curY = 0;
		curLineColor = Color.BLACK;
	}
	
	public void Canvas() {
		JFrame mainWindow = new JFrame();
		mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainWindow.setPreferredSize(new Dimension(1400,800));
		//그림을 그리기위한 캔버스 패널
		Canvas canvasContainer = new Canvas();
		canvasContainer.setBackground(Color.WHITE);
		//각종 메뉴 버튼들을 저장하기 위한 패널
		JPanel menuContainer = new JPanel();
		//캔버스 패널을 프레임의 중앙에 배치.
		mainWindow.add(canvasContainer, BorderLayout.CENTER);
		//메뉴 패널을 프레임의 상단에 배치.
		mainWindow.add(menuContainer, BorderLayout.NORTH);
		
		//관련 메뉴 버튼들의 선언 부분.
		JButton drawLinearLine = new JButton("Line");//직선
		JButton drawPenLine = new JButton("Pen");//펜
		JButton clearLine = new JButton("Clear");
		JButton quitMainWindow = new JButton("Quit");
		//디버깅용 라벨
		JLabel buttonDebugger = new JLabel("[LINEARLINE]:"+"pos:("+posX+","+posY+")"+", cur:("+curX+","+curY+")");
		//선의 색상을 빨간색으로 변경하기 위한 버튼
		JButton setLineColortoRed = new JButton("Red");
		setLineColortoRed.setForeground(Color.RED);
		setLineColortoRed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				curLineColor = Color.RED;
				buttonDebugger.setForeground(curLineColor);
			}
		});
		//선의 색상을 검은색으로 변셩하기 위한 버튼
		JButton setLineColortoBlack = new JButton("Black");
		setLineColortoBlack.setForeground(Color.BLACK);
		setLineColortoBlack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				curLineColor = Color.BLACK;
				buttonDebugger.setForeground(curLineColor);
			}
			
		});
		//그리려는 선을 직선으로 설정.
		drawLinearLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				curMouseAction = LINEARLINE;
				buttonDebugger.setText("[LINEARLINE]:"+"pos:("+posX+","+posY+")"+", cur:("+curX+","+curY+")");
				buttonDebugger.setForeground(curLineColor);
			}
		});
		//그리려는 선을 펜선으로 설정.
		drawPenLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				curMouseAction = PENLINE;
				buttonDebugger.setText("[PENLINE]:"+"pos:("+posX+","+posY+")"+", cur:("+curX+","+curY+")");
				buttonDebugger.setForeground(curLineColor);
			}
		});
		clearLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearLines(canvasContainer);
			}
		});
		quitMainWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow.dispose();
				System.exit(0);
			}
		});
		
		//캔버스에 직선을 그리기 위한 마우스 리스너를 추가.
		canvasContainer.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.getButton()==MouseEvent.BUTTON1) {
					//그리려는 선이 직선이라면
					setStartPoint(arg0.getX(), arg0.getY());
					if(curMouseAction == LINEARLINE) buttonDebugger.setText("[LINEARLINE]:"+"pos:("+posX+","+posY+")"+", cur:("+curX+","+curY+")");
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				setEndPoint(arg0.getX(), arg0.getY());
				if(curMouseAction == LINEARLINE) {
					buttonDebugger.setText("[LINEARLINE]:"+"pos:("+posX+","+posY+")"+", cur:("+curX+","+curY+")");
					//직선을 그리는 부분.
					addLine(canvasContainer);
				}
			}
		});
		menuContainer.setLayout(new GridLayout(1,7));
		menuContainer.add(clearLine);
		menuContainer.add(drawLinearLine);
		menuContainer.add(drawPenLine);
		menuContainer.add(setLineColortoBlack);
		menuContainer.add(setLineColortoRed);
		menuContainer.add(buttonDebugger);
		menuContainer.add(quitMainWindow);
		
		//set visibility of main frame to true and scale it.
		
		mainWindow.pack();
		mainWindow.setVisible(true);
	}

	private void addLine(Canvas canvas) {
		Line newLinearLine = new Line(posX, posY, curX, curY);
		newLinearLine.lineColor = curLineColor;
		newLinearLine.LineType = curMouseAction;
		canvas.LineLog.add(newLinearLine);
		canvas.repaint();
	}
	private void clearLines(Canvas canvas) {
		canvas.LineLog.clear();
		canvas.repaint();
	}
	private void setStartPoint(int x, int y) {
		posX = x;
		posY = y;
	}
	
	private void setEndPoint(int x, int y) {
		curX = x;
		curY = y;
	}
}
