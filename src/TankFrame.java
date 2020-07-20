import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;


public class TankFrame implements ActionListener,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final int X = 1200;		//坦克可活动的范围
	final int Y = 675;
	public enum Derect{left,right,up,down}
	enum Mode{single,two,server,client}
	Mode mode=Mode.two;
	public enum Owner{
		enemy,player, player2
	}
	StartPanel st;
	JFrame frame;
	static myTank mt=null;
	static myTank mt2=null;//玩家2
	static enemyTank et;
	static Vector<enemyTank> ets = new Vector<>();
	Vector<Tank> mts = new Vector<>();
	//Thread t;
	gamePanel gp = new gamePanel();
	boolean isOver = false;
	static Vector<Shot> etshots = new Vector<>();//所有敌人子弹集合
	int curY=457;
	
	void SerexchangObjs() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					NetBean.socket=NetBean.server.accept();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while(!isOver) {
					try {
						
						if(NetBean.socket != null) {
							NetBean.out=new ObjectOutputStream(NetBean.socket.getOutputStream());
							NetBean.out.writeObject(ets);
							
							
	//						for(int i=0;i<ets.size();i++) {
	//							out.writeObject(ets.get(i));
	//						}
							NetBean.out.writeObject(mt);
							NetBean.out.writeObject(etshots);
							NetBean.in=new ObjectInputStream(NetBean.socket.getInputStream());
							mt2=(myTank2)NetBean.in.readObject();
//							NetBean.in.close();
//							NetBean.out.close();
						}
						
						
//						for(int i=0;i<etshots.size();i++) {
//							out.writeObject(etshots.get(i));
//						}
				
					} catch (IOException | ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
				}
			}
		}).start();
		
	}
	
	void cliexchangObjs() {
		new Thread(new Runnable() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(!isOver) {
					try {
//						if(NetBean.server != null) {
						NetBean.in=new ObjectInputStream(NetBean.socket.getInputStream());
						ets=(Vector<TankFrame.enemyTank>)NetBean.in.readObject();
						mt=(myTank)NetBean.in.readObject();
						etshots=(Vector<TankFrame.Shot>)NetBean.in.readObject();
						NetBean.out=new ObjectOutputStream(NetBean.socket.getOutputStream());
						NetBean.out.writeObject(mt2);
//						NetBean.in.close();
//						NetBean.out.close();
//						}
						
					} catch (IOException | ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		}).start();
		
	}
	
	
	TankFrame(){
		st = new StartPanel();
		frame = new JFrame("经典坦克大战-杨群");
		frame.setBounds(0, 0, 1366, 768);
		Image img = Toolkit.getDefaultToolkit().getImage("image/tank.jpg");
		frame.setIconImage(img);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);;
		JMenuBar bar = new JMenuBar();
		JMenu menu2,menu1 = new JMenu("菜单"),modecho=new JMenu("模式");
		menu2=new JMenu("设置");
		bar.add(menu1);
		bar.add(modecho);
		bar.add(menu2);
		
		JMenuItem jmi = new JMenuItem("开始"),m1=new JMenuItem("单人游戏"),m2=new JMenuItem("双人游戏"),
				ser=new JMenuItem("server"),cli=new JMenuItem("client");
		menu1.add(jmi);
		frame.setJMenuBar(bar);
		frame.add(st);
		frame.setVisible(true);
		jmi.addActionListener(this);
		jmi.setActionCommand("start");
		m1.addActionListener(this);
		m1.setActionCommand("m1");
		m2.addActionListener(this);
		m2.setActionCommand("m2");
		ser.addActionListener(this);
		cli.addActionListener(this);
		ser.setActionCommand("ser");
		cli.setActionCommand("cli");
		
		modecho.add(m1);
		modecho.add(m2);
		modecho.add(ser);
		modecho.add(cli);
		
//		if(mode!=Mode.client) {
//			mt=new myTank();
//			frame.addKeyListener(mt);
//		}
//		else
//			mt2=new myTank2();
//		if(mode==Mode.server) {
//			try {
//				server=new ServerSocket(8888);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}else if(mode==Mode.client) {
//			try {
//				socket=new Socket("localhost", 8888);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("start")) {
			frame.remove(st);
			frame.getContentPane().add(gp);
			if(mode!=Mode.client) {
				mt=new myTank();
				frame.addKeyListener(mt);
				for(int i=0;i<7;i++) {
					et=new enemyTank();
					new Thread(et).start();	
					ets.add(et);
				}	
				mts.add(mt);
			}
			
			if(mode==Mode.two) {
				mt2=new myTank2();
				frame.addKeyListener(mt2);
				mts.add(mt2);
			}
			
			if(mode==Mode.server) {
				try {
					NetBean.server=new ServerSocket(8888);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SerexchangObjs();
			}else if(mode==Mode.client) {
				mt2=new myTank2();
				frame.addKeyListener(mt2);
				mts.add(mt2);
				try {
					NetBean.socket=new Socket("localhost", 8888);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				cliexchangObjs();
			}
		}
		
		if(e.getActionCommand().equals("m1")) {
			mode=Mode.single;
			curY=407;
		}
		if(e.getActionCommand().equals("m2")) {
			mode=Mode.two;
			curY=457;
		}
		if(e.getActionCommand().equals("ser")) {
			mode=Mode.server;
			curY=507;
		}
		if(e.getActionCommand().equals("cli")) {
			mode=Mode.client;
			curY=507;
		}
	}

	class StartPanel extends JPanel implements KeyListener{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, 1366, 768);
			g.setColor(Color.RED);
			Font f = new Font("隶书", Font.PLAIN, 30);
			g.setFont(f);
			g.drawString("Java 坦克大战", 500, 280);
			g.setColor(Color.GRAY);
			Font f2 = new Font("楷书", Font.PLAIN, 25);
			g.setFont(f2);
			g.drawString("作者:杨群", 500, 320);
			g.setColor(Color.YELLOW);
			g.fillRect(465, curY, 20, 10);
			g.setColor(Color.GRAY);
			g.drawString("单人游戏", 500, 420);
			g.drawString("双人游戏", 500, 470);
			g.drawString("网络对战", 500, 520);
			repaint();
		}
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
			
		}
		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	class gamePanel extends JPanel{
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public gamePanel() {
			// TODO Auto-generated constructor stub
	
		}
		@Override
		public void paint(Graphics g) {
			// TODO Auto-generated method stub
			super.paint(g);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, X, Y);
			if(mt!=null)
				mt.drawTank(g);
			Vector<Tank> ts=new Vector<>();
			if(mode!=Mode.single&&mt2!=null) {
				mt2.drawTank(g);
				for(int i=0;i<mt2.shots.size();i++) {//玩家2坦克子弹
					Shot s=mt2.shots.get(i);
					if(s.isAlive()==true) {
						s.drawShot(g);
						s.isShoot(ts, etshots);
					}
					else
						mt.shots.remove(s);
				}
			}
			
			for(enemyTank e:ets) {
				if(e.isLive) {
					e.drawTank(g);
					ts.add(e);
				}
				for(int i=0;i<e.shots.size();i++) {
					Shot s=e.shots.get(i);
					if(s.isLive)
						s.drawShot(g);
					else {
						e.shots.remove(s);
						etshots.remove(s);
					}
						
					s.isShoot(mts, mt.shots);
				}
					
				etshots.addAll(e.shots);
			}
			
			for(int i=0;i<mt.shots.size();i++) {//玩家1坦克子弹
				Shot s=mt.shots.get(i);
				if(s.isAlive()==true) {
					s.drawShot(g);
					s.isShoot(ts, etshots);
				}
				else
					mt.shots.remove(s);
			}
//			for()
			
			
//			System.out.println("y:"+et.position.y+"\t"+ "x:"+et.position.x + et.position.derct);
			if(isOver==false) {
//				if(mode==Mode.server) {
//					SerexchangObjs();
//				}else if(mode==Mode.client) {
//					cliexchangObjs();
//				}
				repaint();
			}	
			else
				return;
		}
	}
	
	abstract class Tank implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		boolean isLive = true;
		Vector<Shot> shots = new Vector<Shot>();
		protected Position position = new Position();
		protected int speed;
		protected short life = 1;
		short maxS = 5;//允许发射的最大子弹数
		public void drawTank(Graphics g) {
			switch (this.position.derct) {
			case down:
				g.fill3DRect(this.position.x-8, this.position.y-10, 7, 20, false);
				g.fill3DRect(this.position.x+4, this.position.y-10, 7, 20, false);
				g.fill3DRect(this.position.x-5, this.position.y-5, 12, 10, false);
				g.fill3DRect(this.position.x, this.position.y, 2, 12, false);
				break;
			case up:
				g.fill3DRect(this.position.x-8, this.position.y-10, 7, 20, false);
				g.fill3DRect(this.position.x+4, this.position.y-10, 7, 20, false);
				g.fill3DRect(this.position.x-5, this.position.y-5, 12, 10, false);
				g.fill3DRect(this.position.x, this.position.y-12, 2, 12, false);
				break;
			case left:
				g.fill3DRect(this.position.x-10, this.position.y-8,20,  7, false);
				g.fill3DRect(this.position.x-10, this.position.y+4,20,  7, false);
				g.fill3DRect(this.position.x-5, this.position.y-5,10,  12, false);
				g.fill3DRect(this.position.x-12, this.position.y, 12, 2, false);
				break;
			case right:
				g.fill3DRect(this.position.x-10, this.position.y-8,20,  7, false);
				g.fill3DRect(this.position.x-10, this.position.y+4,20,  7, false);
				g.fill3DRect(this.position.x-5, this.position.y-5,10,  12, false);
				g.fill3DRect(this.position.x, this.position.y, 12, 2, false);
				break;
			default:
				break;
			}
			
			
			
		}
		
		void fire(Owner o) {
			if(shots.size()<=maxS) {
				Shot s = new Shot(position);
				s.owner = o;
				shots.add(s);
			}
		}
		
	}

	@SuppressWarnings("hiding")
	//Tank为另一方坦克类
	class Shot extends Thread implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Position position = new Position();
		Boolean isLive=true;
		static final int speed = 3;		//每5ms走一个像素
		int st = 20;
		Owner owner;
		//Vector<Tank> tanks;
		
		public Shot(Position position){
			this.position.derct=position.derct;
			this.position.x=position.x;
			this.position.y=position.y;
			this.start();
			
		}
		
		void drawShot(Graphics g) {
			g.setColor(Color.white);
//			System.out.println(position.x+"  "+position.y);
			g.fillOval(position.x, position.y, 5, 5);
		}
		
		//判断是否击中
		void isShoot(Vector<Tank> tanks,Vector<Shot> sh) {
//			if(owner==Owner.player)//如果是玩家子弹
			for(int i=0;i<tanks.size();i++) {
				Tank t = tanks.get(i);
//				if(((position.derct==Derect.down||position.derct==Derect.up)&&(t.position.derct==
//						Derect.down||t.position.derct==Derect.up))||((position.derct==Derect.left||
//						position.derct==Derect.right)&&(t.position.derct==Derect.left||
//						t.position.derct==Derect.right)))//子弹方向与坦克方向相同的情况
					if(Math.abs(position.x-t.position.x)<10 && Math.abs(position.y-t.position.y)<10) {
						if(owner==Owner.player) {
//							ets.remove(t);
//							enemyTank e = new enemyTank();
//							ets.add(e);
//							new Thread(e).start();
							t.isLive=false;
							this.isLive=false;
							mt.shots.remove(this);
						}
						
					}
			}
			
			
		}
		
		 
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			for(;isLive;) {
				switch (position.derct) {
				case down:
					for(;isLive;) {
						position.y+=speed;
						if(position.y>=Y) {
							isLive=false;
						}
						try {
							Thread.sleep(st);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				case up:
					for(;isLive;) {	
						position.y-=speed;
//						System.out.println(position.y);
						if(position.y<=0) {
							isLive=false;
						}
						try {
							Thread.sleep(st);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				case left:
					for(;isLive;) {
						position.x-=speed;
						if(position.x<=0) {
							isLive=false;
						}
						try {
							Thread.sleep(st);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				case right:
					for(;isLive;) {
						position.x+=speed;
						if(position.x>=X) {
							isLive=false;
						}
						try {
							Thread.sleep(st);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;

				default:
					break;
				}
				
			}
//			this.destroy();
		}
	}
	
	class myTank extends Tank implements KeyListener,Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void drawTank(Graphics g) {
			if(g.getColor()==Color.BLUE) {
				g.setColor(Color.BLUE);
			}else {
				g.setColor(Color.YELLOW);
			}
			
			super.drawTank(g);
		}
		myTank(){		
			super();
			speed=4;
			life = 5;
			this.position.x=600;
			this.position.y=650;
			maxS = 10;
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			if(e.getKeyChar()=='f'||e.getKeyChar()=='F') {
				fire(Owner.player);
			}
				
		}



		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			switch (e.getKeyCode()) {
			case KeyEvent.VK_D:
				System.out.println("d");
				mt.position.derct = Derect.right;
				if(mt.position.x<X-15)
					mt.position.x+=speed;
				break;
			case KeyEvent.VK_A:
				mt.position.derct = Derect.left;
				if(mt.position.x>0)
					mt.position.x-=speed;
				break;
			case KeyEvent.VK_W:
				mt.position.derct = Derect.up;
				if(mt.position.y>0)
					mt.position.y-=speed;
				break;
			case KeyEvent.VK_S:
				mt.position.derct = Derect.down;
				if(mt.position.y<Y)
					mt.position.y+=speed;
				break;

			default:
				break;
			}
			
//			System.out.println(mt.position.y);
		}
		
	}
	
	class myTank2 extends myTank implements KeyListener,Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public myTank2() {
			// TODO Auto-generated constructor stub
			this.position.x=position.x+150;
		}
		public void drawTank(Graphics g) {
			// TODO Auto-generated method stub
			g.setColor(Color.BLUE);
			super.drawTank(g);
		}
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			if(e.getKeyChar()==';'||e.getKeyChar()==':') {
				fire(Owner.player2);
			}
				
		}
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			switch (e.getKeyCode()) {
			case KeyEvent.VK_L:
				mt2.position.derct = Derect.right;
				if(mt2.position.x<X-15)
					mt2.position.x+=speed;
				break;
			case KeyEvent.VK_J:
				mt2.position.derct = Derect.left;
				if(mt2.position.x>0)
					mt2.position.x-=speed;
				break;
			case KeyEvent.VK_I:
				mt2.position.derct = Derect.up;
				if(mt2.position.y>0)
					mt2.position.y-=speed;
				break;
			case KeyEvent.VK_K:
				mt2.position.derct = Derect.down;
				if(mt2.position.y<Y)
					mt2.position.y+=speed;
				break;

			default:
				break;
			}
			
//			System.out.println(mt.position.y);
			
		}
	
	};
	
	/**
	 * 一个敌方坦克
	 * @author yangqun
	 *
	 */
	class enemyTank extends Tank implements Runnable,Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public Color c = Color.RED;
		int sp = 10;		//每10ms走一个像素
		public enemyTank() {
			// TODO Auto-generated constructor stub
			super();
			
			speed = 1;
			maxS=4;
			this.position.derct=Derect.down;
			this.position.y=15;
			
			
			//产生出生位置
			Random r = new Random();
			switch (r.nextInt(3)) {
			case 0:
				this.position.x=300;
				break;
			case 1:
				this.position.x=600;
				break;
			case 2:
				this.position.x=900;
				break;

			default:
				break;
			}
			
//			t.start();
		}
		
		@Override
		public void drawTank(Graphics g) {
			// TODO Auto-generated method stub
			g.setColor(this.c);
			super.drawTank(g);
		}
		
		
		
		/*
		 * 每走一段随机距离或碰撞后改变方向
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int temp,tempd,d=100;
			Random isF=new Random();
			for(;;) {
				Random r = new Random();	//行走距离
				Random rd = new Random();	//方向
				
				switch (this.position.derct) {
				case up:
					//应达到的位置
					temp = r.nextInt(position.y);
					//未达到指定位置时
					for(;!(position.y>=temp  && position.y <= temp+speed);) {
						position.y-=speed;
						if(isLive&&isF.nextInt(d)==0) {
							fire(Owner.enemy);
						}
						try {
							Thread.sleep(sp);	//每0.1s刷新敌方坦克位置
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//改变方向
					if((tempd=rd.nextInt(3))==0) {
						position.derct=Derect.left;
					}else if(tempd==1) {
						position.derct=Derect.right;
					}else{
						position.derct=Derect.down;
					}
					
					break;
				case down:
					temp = r.nextInt(Y - this.position.y) + this.position.y;
//					System.out.println("temp:"+ temp);
					
					
					for(;!(position.y>=temp-speed  && position.y <= temp);) {
						this.position.y+=speed;
						if(isLive&&isF.nextInt(d)==0) {
							fire(Owner.enemy);
						}
//						System.out.println("y:" + this.position.y);
						try {
							Thread.sleep(sp);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					tempd=rd.nextInt(3);
					if(tempd==0) {
						position.derct=Derect.left;
					}else if(tempd==1) {
						position.derct=Derect.right;
					}else {
						position.derct=Derect.up;
					}
					
					break;
				case left:
					temp = r.nextInt(position.x);
					for(;!(position.x>=temp  && position.x <= temp+speed);) {
						position.x-=speed;
						if(isLive&&isF.nextInt(d)==0) {
							fire(Owner.enemy);
						}
						try {
							Thread.sleep(sp);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if((tempd=rd.nextInt(3))==0) {
						position.derct=Derect.down;
					}else if(tempd==1) {
						position.derct=Derect.right;
					}else {
						position.derct=Derect.up;
					}
					break;
				case right:
					temp = r.nextInt(X - position.x) + position.x;
					for(;!(position.x>=temp-speed  && position.x <= temp);) {
						position.x+=speed;
						if(isLive&&isF.nextInt(d)==0) {
							fire(Owner.enemy);
						}
						try {
							Thread.sleep(sp);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if((tempd=rd.nextInt(3))==0) {
						position.derct=Derect.left;
					}else if(tempd==1) {
						position.derct=Derect.down;
					}else {
						position.derct=Derect.up;
					}
					break;

				default:
					break;
				}
				if(!isLive) {
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					isLive=true;
				}
				
				
			}
		}
		
	}
	
	
	
	
	/*负责创建和管理敌方坦克
	 * 控制敌方坦克数量
	 * 如果敌方坦克中弹则删除该线程
	 */
	
	class enemyTankManager {
		int num;
		
	}
	
	class Position implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Derect derct = Derect.up;
		public int x,y;
	}
	

}