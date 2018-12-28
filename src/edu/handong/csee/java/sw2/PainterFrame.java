package edu.handong.csee.java.sw2;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.swing.*;

public class PainterFrame {
	private int posX, posY, curX, curY;
	////////////////////////////////////////저장되는 선의 종류를 구분하기 위한 변수들.
	public int curMouseAction;
	//현재 저장되어야 하는 선의 종류를 구분하기 위한 상수값들 선언.
	private final int LINELOG = -1;
	private final int LINEARLINE = 0;
	private final int PENLINE = 1;
	private final int OVALLINE = 2;
	private final int RECTLINE = 3;
	//private final int ERASELINE = 4;
	private final int POLYLINE = 5;
	private boolean triggerPolyLine = false;
	private final int ERASEOBJECT = 6;
	private boolean triggerEraseObject = false;
	private Vector<Line> cachedLines = new Vector<Line>();//undo를 통해 지워진 선들을 저장하기 위한 벡터
	////////////////////////////////////////취해지는 메모리 액션들을 구분하고 저장하기 위한 변수들.
	public int curMnemonicAction;
	//현재 캐시와 캔버스의 저장공간에 왔다갔다하는 액션을 구분해주기 위한 상수값들 선언.
	private final int DRAW = 10;
	private final int CLEAR = 11;
	private final int REDO = 12;
	private final int UNDO = 13;
	private final int ERASE = 14;
	private Vector<Integer> cachedMnemonicAction = new Vector<Integer>();//이전에 취해졌던 메모리액션을 저장하는 벡터
	private Vector<Line> clearedLine = new Vector<Line>();//clear를 통해 지워진 선들을 저장하기 위한 벡터
	private int indexMnemonicAction;
	//저장되는 선의 색깔을 구분하기 위한 변수
	public Color curLineColor;
	public int curLineWeight;
	//초기화를 위한 생성자
	PainterFrame(){
		curMouseAction = LINEARLINE;
		curMnemonicAction = -1;
		posX = 0;
		posY = 0;
		curX = 0;
		curY = 0;
		curLineColor = Color.BLACK;
		curLineWeight = 5;
		indexMnemonicAction = 0;
	}
	
	public void Canvas() {
		JFrame mainWindow = new JFrame("Main Frame");
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setPreferredSize(new Dimension(1400,800));
		//디버깅 라벨
		JLabel buttonDebugger = new JLabel();
		//debugButton(buttonDebugger);
		buttonDebugger.setBackground(Color.WHITE);
		//메뉴 생성
		JMenuBar menuBarMain = new JMenuBar();
		//JMenu menuSave = new JMenu("File")
		JMenu menuColor = new JMenu("Color");
		JMenu menuWeight = new JMenu("Thickness");
		JMenuItem menuItemColorPicker = new JMenuItem("Pick");
		JSpinner lineWeight = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
		//파일 i/o를 위한 메뉴아이템들.
		//JMenuItem menuItemSave = new JMenuItem("Save");
		//JMenuItem menuItemSaveAs = new JMenuItem("Save As");
		//JMenuItem menuItemOpen = new JMenuItem("Open");
		//색깔을 선택하기 위한 메뉴 아이템
		menuItemColorPicker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curLineColor = JColorChooser.showDialog(null, "Pick", curLineColor);
				buttonDebugger.setForeground(curLineColor);
			}
		});
		//선의 굵기를 선택하기 위한 스피너
		
		//메뉴바를 프레임에 넣고 설정하기 위한 부분.
		menuColor.add(menuItemColorPicker);
		menuWeight.add(lineWeight);
		menuBarMain.add(menuColor);
		menuBarMain.add(menuWeight);
		mainWindow.setJMenuBar(menuBarMain);
		
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
		JButton undoDraw = new JButton("undo");
		JButton redoDraw = new JButton("redo");
		JButton drawLinearLine = new JButton("Line");//직선
		JButton drawOvalLine = new JButton("Circle");//원
		JButton drawRectLine = new JButton("Rect");//사각형
		JButton drawPenLine = new JButton("Pen");//펜
		JButton drawPolyLine = new JButton("Polyline");//폴리라인
		JButton eraseObject = new JButton("Erase by Object");//오브젝트 단위로 지우기
		//JButton drawEraseLine = new JButton("Erase");//지우개
		JButton clearLine = new JButton("Clear");//모든 선 제거
		JButton quitMainWindow = new JButton("Quit");
		
		//그리려는 선을 직선으로 설정.
		drawLinearLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				curMouseAction = LINEARLINE;
				//debugButton(buttonDebugger);
			}
		});
		//그리려는 선을 원으로 설정.
		drawOvalLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				curMouseAction = OVALLINE;
				//debugButton(buttonDebugger);
			}
			
		});
		//그리려는 선을 사각형으로 설정.
		drawRectLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				curMouseAction = RECTLINE;
				//debugButton(buttonDebugger);
			}
		});
		//그리려는 선을 펜선으로 설정.
		drawPenLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curMouseAction = PENLINE;
				//debugButton(buttonDebugger);
			}
		});
		//그리려는 선을 폴리라인으로 설정.
		drawPolyLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curMouseAction = POLYLINE;
				//debugButton(buttonDebugger);
			}
		});
		//오브젝트 단위로 지우는 것으로 설정.
		eraseObject.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curMouseAction = ERASEOBJECT;
				curMnemonicAction = ERASE;
				//debugButton(buttonDebugger);
			}
		});
		//일반적인 지우개
		/*drawEraseLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				curMouseAction = ERASELINE;
				debugButton(buttonDebugger);
			}
		});*/
		//캔버스의 라인로그를 캐시에 저장하고 캔버스를 클리어.
		clearLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curMnemonicAction = CLEAR;
				doMnemonicAction(canvasContainer);
				//debugButton(buttonDebugger);
				updateDoers(undoDraw, redoDraw);
			}
		});
		//quit 버튼 액션리스너
		quitMainWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow.dispose();
				System.exit(0);
			}
		});
		//undo 버튼 액션리스너
		undoDraw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curMnemonicAction = UNDO;
				doMnemonicAction(canvasContainer);
				updateDoers(undoDraw, redoDraw);
				//debugButton(buttonDebugger);
			}
		});
		//ctrl + z를 넣어보려 했으나
		/*
		undoDraw.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==KeyEvent.VK_Z && (arg0.getModifiers()&KeyEvent.CTRL_MASK)!=0) {
					curMnemonicAction = UNDO;
					doMnemonicAction(canvasContainer);
					updateDoers(undoDraw, redoDraw);
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});*/
		//redo 버튼 액션리스너
		redoDraw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				curMnemonicAction = REDO;
				doMnemonicAction(canvasContainer);
				updateDoers(undoDraw, redoDraw);
				//debugButton(buttonDebugger);
			}
		});//ctrl + y를 넣어보려 했으나
		/*
		redoDraw.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==KeyEvent.VK_Y && (arg0.getModifiers()&KeyEvent.CTRL_MASK)!=0) {
					curMnemonicAction = REDO;
					doMnemonicAction(canvasContainer);
					updateDoers(undoDraw, redoDraw);
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {
			}
			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});*/
		
		//캔버스에 직선을 그리기 위한 마우스 리스너를 추가.
		canvasContainer.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()==2 && arg0.getButton()==MouseEvent.BUTTON1) {
					if(curMouseAction == POLYLINE) {
						canvasContainer.LineLog.lastElement().multipleLine.removeElementAt(canvasContainer.LineLog.lastElement().multipleLine.size()-1);
						setEndPoint(arg0.getX(),arg0.getY());
						//System.out.println("Double clicked position"+curX+","+curY);
						triggerPolyLine = false;
						doMnemonicAction(canvasContainer);
						//debugButton(buttonDebugger);	
						updateDoers(undoDraw, redoDraw);
					}
				}
				else if(arg0.getClickCount()==1 && arg0.getButton()==MouseEvent.BUTTON1) {
					if(curMouseAction == POLYLINE) {
						curMnemonicAction = DRAW;
						//debugButton(buttonDebugger);
						curLineWeight = (int) lineWeight.getValue();
						setStartPoint(arg0.getX(), arg0.getY());
						setEndPoint(arg0.getX(),arg0.getY());
						//System.out.println("Single clicked position"+curX+","+curY);
						if(!triggerPolyLine) {
							addLine(canvasContainer);
							triggerPolyLine = true;
						}
						drawMultiLine(canvasContainer);
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				setStartPoint(arg0.getX(), arg0.getY());
				setEndPoint(arg0.getX(),arg0.getY());
				if(arg0.getButton() == MouseEvent.BUTTON1 && curMouseAction==ERASEOBJECT) {
					//System.out.println("Erase Object initiated");
					triggerEraseObject = true;
					curMnemonicAction = ERASE;
					//debugButton(buttonDebugger);
					eraseByObjects(canvasContainer);
				}
				else if(arg0.getButton()==MouseEvent.BUTTON1 && curMouseAction != POLYLINE) {
					//그리려는 선이 직선이라면
					//debugButton(buttonDebugger);
					curLineWeight = (int) lineWeight.getValue();
					//if(curMouseAction!=ERASELINE || !canvasContainer.LineLog.isEmpty()) {
						curMnemonicAction = DRAW;
						//debugButton(buttonDebugger);
						addLine(canvasContainer);
					//}
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(curMouseAction != POLYLINE && curMouseAction != ERASEOBJECT) {
					setEndPoint(arg0.getX(), arg0.getY());
					doMnemonicAction(canvasContainer);
					updateDoers(undoDraw, redoDraw);
					updateLine(canvasContainer);
				}
				if(curMouseAction==ERASEOBJECT) {
					//System.out.println("Erase Object exited");
					triggerEraseObject = false;
					updateDoers(undoDraw, redoDraw);
					//debugButton(buttonDebugger);
				}
			}
		});
		canvasContainer.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				if(curMouseAction == PENLINE ) {//|| (curMouseAction == ERASELINE && !canvasContainer.LineLog.isEmpty())) {
					setStartPoint(curX, curY);
					setEndPoint(arg0.getX(), arg0.getY());
					drawMultiLine(canvasContainer);
				}
				else if(curMouseAction==ERASEOBJECT) {
					if(triggerEraseObject) {
						//System.out.println("Erase Object ongoing");
						setStartPoint(curX, curY);
						setEndPoint(arg0.getX(), arg0.getY());
						if(eraseByObjects(canvasContainer)) doMnemonicAction(canvasContainer);
					}
				}
				//러버밴드 기능
				else if(curMouseAction != POLYLINE && curMouseAction != ERASEOBJECT) {
					//if(curMouseAction != ERASELINE || !canvasContainer.LineLog.isEmpty()) {
						setEndPoint(arg0.getX(),arg0.getY());
						updateLine(canvasContainer);
					//}
				}
				//debugButton(buttonDebugger);
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				if(!canvasContainer.LineLog.isEmpty() && triggerPolyLine) {
					if(canvasContainer.LineLog.elementAt(canvasContainer.LineLog.size()-1).LineType == POLYLINE && curMouseAction == POLYLINE) {
						setEndPoint(arg0.getX(),arg0.getY());
						//debugButton(buttonDebugger);
						updatePolyLine(canvasContainer);
					}
				}
			}
		});
		canvasContainer.setBackground(Color.WHITE);
		//undo와 redo버튼을 처음에는 disable해놓음
		undoDraw.setEnabled(false);
		redoDraw.setEnabled(false);
		
		//총 버튼 수.
		menuContainer.setLayout(new GridLayout(2,1));
		JPanel buttonContainer = new JPanel(new GridLayout(1, 10));
		buttonContainer.add(undoDraw);//1. undo 버튼
		buttonContainer.add(redoDraw);//2. redo 버튼
		buttonContainer.add(drawLinearLine);//3. 직선
		buttonContainer.add(drawOvalLine);//4. 원
		buttonContainer.add(drawRectLine);//5. 사각형
		buttonContainer.add(drawPenLine);//6. 펜
		buttonContainer.add(drawPolyLine);//7. 폴리라인
		buttonContainer.add(eraseObject);//8. 오브젝트 지우개
		//buttonContainer.add(drawEraseLine);//9. 지우개
		buttonContainer.add(clearLine);//9. 캔버스 클리어
		buttonContainer.add(quitMainWindow);//10. 창 닫기
		menuContainer.add(buttonContainer);
		menuContainer.add(buttonDebugger);
		
		//set visibility of main frame to true and scale it.
		mainWindow.pack();
		mainWindow.setVisible(true);
	}
	//새로운 선을 캔버스에 더하는 함수
	private void addLine(Canvas canvas) {
		Line newLine = new Line(posX, posY, curX, curY);
		newLine.lineColor = curLineColor;
		newLine.LineType = curMouseAction;
		newLine.LineWeight = curLineWeight;
		canvas.LineLog.add(newLine);
		canvas.repaint();
	}
	//러버밴드
	private void updateLine(Canvas canvas) {
		if(curMouseAction != PENLINE && !canvas.LineLog.isEmpty()) {
			canvas.LineLog.elementAt(canvas.LineLog.size()-1).curX =curX;
			canvas.LineLog.elementAt(canvas.LineLog.size()-1).curY =curY;
		}
		canvas.repaint();
	}
	//폴리라인 업데이트용 함수
	private void updatePolyLine(Canvas canvas) {
		Vector<Line> lastPolyline = canvas.LineLog.elementAt(canvas.LineLog.size()-1).multipleLine;
		if(!lastPolyline.isEmpty()) {
			lastPolyline.elementAt(lastPolyline.size()-1).curX = curX;
			lastPolyline.elementAt(lastPolyline.size()-1).curY = curY;
		}
		canvas.repaint();
	}
	//펜선을 그리기 위한 함수.
	private void drawMultiLine(Canvas canvas) {
		Line lineNode = new Line(posX, posY, curX, curY);
		lineNode.lineColor = curLineColor;
		lineNode.LineType = curMouseAction;
		lineNode.LineWeight = curLineWeight;
		canvas.LineLog.elementAt(canvas.LineLog.size()-1).multipleLine.add(lineNode);
		canvas.repaint();
	}
	//디버그용 라벨의 상태를 업데이트하는 함수
	/*private void debugButton(JLabel buttonDebugger) {
		String debugText = "";
		//그려지는 선의 종류를 표시
		if(curMouseAction == LINEARLINE) debugText = debugText+"[LINEARLINE]:";
		else if(curMouseAction == OVALLINE)	debugText +="[OVALLINE]:";
		else if(curMouseAction == RECTLINE) debugText +="[RECTLINE]:";
		else if(curMouseAction == PENLINE) debugText += "[PENLINE]:";
		else if(curMouseAction == POLYLINE) debugText += "[POLYLINE]";
		else if(curMouseAction == ERASEOBJECT) debugText += "[ERASEOBJECT]";
		//else if(curMouseAction == ERASELINE) debugText += "[ERASELINE]";
		//현재 취해지는 메모리 액션을 표시
		if(curMnemonicAction == DRAW) debugText+="DRAW";
		else if(curMnemonicAction == CLEAR) debugText+="CLEAR";
		else if(curMnemonicAction == UNDO) debugText+="UNDO";
		else if(curMnemonicAction == REDO) debugText+="REDO";
		else if(curMnemonicAction == -1) debugText+="NULL";
		//debugText+=", index of cached MA:"+indexMnemonicAction;
		//debugText+=", size of cachedMnemonicAction: "+cachedMnemonicAction.size();
		for(int cachedMA: cachedMnemonicAction) {
			debugText+=","+cachedMA;
		}
		buttonDebugger.setText(debugText);
	}*/
	
	//메모리 액션 함수
	@SuppressWarnings("unchecked")
	private void doMnemonicAction(Canvas canvas) {
		if(curMnemonicAction == DRAW) {
			if(indexMnemonicAction < cachedMnemonicAction.size()-1) {
				for(int indexToDelete = cachedMnemonicAction.size()-1; indexToDelete > indexMnemonicAction; indexToDelete--) {
					cachedMnemonicAction.removeElementAt(indexToDelete);
				}
			}
			cachedMnemonicAction.add(curMnemonicAction);
			cachedMnemonicAction.trimToSize();
			indexMnemonicAction = cachedMnemonicAction.size()-1;
		}
		else if(curMnemonicAction == CLEAR) {
			if((indexMnemonicAction >= 0 && indexMnemonicAction < cachedMnemonicAction.size()) && cachedMnemonicAction.elementAt(indexMnemonicAction)!=CLEAR) {
				//cachedLines.clear();
				Line clearLog = new Line();
				clearLog.LineType = LINELOG;
				clearLog.multipleLine = (Vector<Line>)canvas.LineLog.clone();
				clearedLine.add(clearLog);
				clearLines(canvas);
				if(indexMnemonicAction < cachedMnemonicAction.size()-1) {
					for(int indexToDelete = cachedMnemonicAction.size()-1; indexToDelete > indexMnemonicAction; indexToDelete--) {
						cachedMnemonicAction.removeElementAt(indexToDelete);
					}
				}
				cachedMnemonicAction.add(curMnemonicAction);
				cachedMnemonicAction.trimToSize();
				indexMnemonicAction = cachedMnemonicAction.size()-1;
			}
		}
		else if(curMnemonicAction == ERASE) {
			//System.out.println("erase detected");
			if(indexMnemonicAction>=0 && indexMnemonicAction < cachedMnemonicAction.size()) {
				if(indexMnemonicAction < cachedMnemonicAction.size()-1) {
					for(int indexToDelete = cachedMnemonicAction.size()-1; indexToDelete > indexMnemonicAction; indexToDelete--) {
						cachedMnemonicAction.removeElementAt(indexToDelete);
					}
				}
				cachedMnemonicAction.add(curMnemonicAction);
				cachedMnemonicAction.trimToSize();
				indexMnemonicAction = cachedMnemonicAction.size()-1;
			}
		}
		else if(curMnemonicAction == UNDO) {
			//System.out.println("undo detected");
			if(indexMnemonicAction >= 0) {
				indexMnemonicAction--;
				if(cachedMnemonicAction.elementAt(indexMnemonicAction+1)==DRAW) {
					//System.out.println("undoing draw");
					try {
						cachedLines.addElement(canvas.LineLog.lastElement());
						canvas.LineLog.removeElementAt(canvas.LineLog.size()-1);
					}
					catch(NoSuchElementException e) {
						//System.out.println("No such element exception"+e.getMessage());
						e.printStackTrace();
					}
					//cachedLines.addElement(canvas.LineLog.elementAt(canvas.LineLog.size()-1));
					//canvas.LineLog.removeElementAt(canvas.LineLog.size()-1);
				}
				else if(cachedMnemonicAction.elementAt(indexMnemonicAction+1)==CLEAR) {
					//System.out.println("undoing clear");
					canvas.LineLog.addAll((Vector<Line>)clearedLine.lastElement().multipleLine.clone());
					clearedLine.removeElementAt(clearedLine.size()-1);
					clearedLine.trimToSize();
				}
				else if(cachedMnemonicAction.elementAt(indexMnemonicAction+1)==ERASE) {
					if(!cachedLines.isEmpty()) {
						canvas.LineLog.add(cachedLines.elementAt(cachedLines.size()-1));
						cachedLines.removeElementAt(cachedLines.size()-1);
					}
				}
			}
		}
		else if(curMnemonicAction == REDO) {
			//System.out.println("redo detected");
			if(indexMnemonicAction < cachedMnemonicAction.size()) {
				indexMnemonicAction++;
				if(!cachedMnemonicAction.isEmpty()) {
					if(cachedMnemonicAction.elementAt(indexMnemonicAction)==DRAW) {
						canvas.LineLog.add(cachedLines.elementAt(cachedLines.size()-1));
						cachedLines.removeElementAt(cachedLines.size()-1);
					}
					else if(cachedMnemonicAction.elementAt(indexMnemonicAction)==CLEAR) {
						Line clearLog = new Line();
						clearLog.LineType = LINELOG;
						clearLog.multipleLine = (Vector<Line>)canvas.LineLog.clone();
						clearedLine.add(clearLog);
						clearLines(canvas);
					}
					else if(cachedMnemonicAction.elementAt(indexMnemonicAction)==ERASE) {
						if(!canvas.LineLog.isEmpty()) {
							cachedLines.addElement(canvas.LineLog.elementAt(canvas.LineLog.size()-1));
							canvas.LineLog.removeElementAt(canvas.LineLog.size()-1);
						}
					}
				}
			}
		}
		canvas.repaint();
	}
	//updateDoers(undoDraw, redoDraw);
	private void updateDoers(JButton undoDraw, JButton redoDraw) {
		if(indexMnemonicAction >= 0) {
			undoDraw.setEnabled(true);
		}
		else if(indexMnemonicAction < 0) {
			undoDraw.setEnabled(false);
		}
		if(indexMnemonicAction < cachedMnemonicAction.size()-1) {
			redoDraw.setEnabled(true);
		}
		else if(indexMnemonicAction >= cachedMnemonicAction.size()-1){
			redoDraw.setEnabled(false);
		}
	}
	//오브젝트 단위로 지우기 위한 함수.
	private boolean eraseByObjects(Canvas canvas) {
		//System.out.println("current posX:"+posX+", posY:"+posY+", curX:"+curX+", curY:"+curY);
		for(int i = 0; i < canvas.LineLog.size(); i++) {
			//System.out.println("checking is within");
			if(isWithinLine(canvas.LineLog.elementAt(i))) {
				cachedLines.add(canvas.LineLog.elementAt(i));
				//System.out.println("given index of line log to delete: "+i);
				canvas.LineLog.removeElementAt(i);
				canvas.repaint();
				return true;
			}
		}
		return false;
	}
	//마우스의 현재 좌표로 갱신되는 선에 걸치는지를 확인하기 위한 함수.
	private boolean isWithinLine(Line line) {
		Line2D eraser = new Line2D.Double(posX, posY, curX, curY);
		if(line.LineType==LINEARLINE) {
			Line2D linearLine = new Line2D.Double(line.posX, line.posY, line.curX, line.curY);
			if(linearLine.intersectsLine(eraser)) {
				return true;
			}
		}
		else if(line.LineType==RECTLINE) {
			Rectangle2D rectLine = new Rectangle2D.Double(Math.min(line.posX, line.curX), Math.min(line.posY, line.curY), Math.abs(line.posX - line.curX), Math.abs(line.posY - line.curY));
			if(rectLine.intersectsLine(eraser)) {
				return true;
			}
		}
		else if(line.LineType==OVALLINE) {
			Ellipse2D ovalLine = new Ellipse2D.Double(Math.min(line.posX, line.curX), Math.min(line.posY, line.curY), Math.abs(line.posX - line.curX), Math.abs(line.posY - line.curY));
			if(ovalLine.contains(eraser.getBounds())) {
				return true;
			}
		}
		else if(line.LineType==PENLINE) {
			for(Line lineNode: line.multipleLine) {
				Line2D nodedLine = new Line2D.Double(lineNode.posX, lineNode.posY, lineNode.curX, lineNode.curY);
				if(nodedLine.intersectsLine(eraser)) {
					//System.out.println("Penline detected");
					return true;
				}
			}
		}
		else if(line.LineType==POLYLINE) {
			for(Line lineNode: line.multipleLine) {
				Line2D nodedLine = new Line2D.Double(lineNode.posX, lineNode.posY, lineNode.curX, lineNode.curY);
				//System.out.println("nodedLine.pos:"+lineNode.posX+","+lineNode.posY);
				//System.out.println("nodedLine.cur:"+lineNode.curX+","+lineNode.curY);
				//System.out.println("Polyline detecting.....");
				if(eraser.intersectsLine(nodedLine)) {
					//System.out.println("\n\n\nPolyline detected\n\n\n");
					/*System.out.println("eraser.pos:"+posX+","+posY);
					System.out.println("eraser.cur:"+curX+","+curY);
					System.out.println("lineNode.pos:"+lineNode.posX+","+lineNode.posY);
					System.out.println("lineNode.cur:"+lineNode.curX+","+lineNode.curY);*/
					return true;
				}
			}
		}
		return false;
	}
	//캔버스에 선들을 저장한 벡터를 클리어
	private void clearLines(Canvas canvas) {
		canvas.LineLog.clear();
		canvas.repaint();
	}
	//시작점을 정하기 위한 함수
	private void setStartPoint(int x, int y) {
		posX = x;
		posY = y;
	}
	//끝점을 정하기 위한 함수
	private void setEndPoint(int x, int y) {
		curX = x;
		curY = y;
	}
}
