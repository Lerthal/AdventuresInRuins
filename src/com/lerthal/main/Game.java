package com.lerthal.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import javax.swing.JFrame;

import com.lerthal.entities.Enemy;
import com.lerthal.entities.Entity;
import com.lerthal.entities.Player;
import com.lerthal.entities.Shoot;
import com.lerthal.entities.Sign;
import com.lerthal.graficos.Spritesheet;
import com.lerthal.graficos.UI;
import com.lerthal.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener , MouseMotionListener {

	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 4;
	private int CUR_LEVEL = 1, MAX_LEVEL = 3;
	public static String gameState = "Menu";
	private boolean showMessageGameOver = true;
	private boolean showMessageVictory = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	// public static boolean saveGame = false;
	// public static boolean showSaveIcon = true;

	private BufferedImage image;

	public void initFrame() {
		frame = new JFrame("Adventures in Ruins");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<Shoot> bullets;
	public static Player player;
	public static Enemy enemy;
	public static Spritesheet spritesheet;
	public static World world;
	public static Random rand;
	public UI ui;
	public Sign sign;

	public Menu menu;
	public MenuPause menuPause;

	public Game() {
		Sound.musicBackground.loop();
		Sound.musicBackground.setVolume(-39);
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		/*
		 * TimerUtils.runTaskTimer(20, new Runnable() {
		 * 
		 * @Override public void run() { saveGame = true; showSaveIcon = true;
		 * System.out.println("Jogo Salvo"); } });
		 */
		// Inicializando Objetos :)
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<Shoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(16, 16, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/lvl1.png");
		menu = new Menu();
		menuPause = new MenuPause();
		// sign = new Sign(120, 470, 16, 16, spritesheet.getSprite(4, 144, 16, 16));
		// entities.add(sign);

	}

	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}

	public void tick() {
		if (gameState == "Normal") {
			/*
			 * if (saveGame) { //saveGame = false; String[] opt1 = { "level", "life" };
			 * int[] opt2 = { this.CUR_LEVEL, (int) player.life }; Menu.saveGame(opt1, opt2,
			 * 10); }
			 */
			restartGame = false;
			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}

			for (int i = 0; i < bullets.size(); i++) {
				bullets.get(i).tick();
			}

			if (enemies.size() == 0) {
				// Avançar de Lvl
				CUR_LEVEL++;
				if (CUR_LEVEL > MAX_LEVEL) {
					gameState = "Victory";
					return;
				}
				String newWorld = "lvl" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}

		} else if (gameState == "Game_Over") {
			this.framesGameOver++;
			if (this.framesGameOver == 37) {
				this.framesGameOver = 0;
				if (showMessageGameOver) {
					showMessageGameOver = false;
				} else {
					showMessageGameOver = true;
				}
			}

		} else if (gameState == "Menu") {
			menu.tick();
		} else if(gameState == "Pause"){
			menuPause.tick();
		}

		if (restartGame) {
			restartGame = false;
			this.gameState = "Normal";
			CUR_LEVEL = 1;
			String newWorld = "lvl" + CUR_LEVEL + ".png";
			World.restartGame(newWorld);
		}

		if (gameState == "Menu" || gameState == "Guide") {
			Sound.musicGame.loop();
			Sound.musicGame.setVolume(-25);
		}

	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = image.getGraphics();
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		// g.drawImage(player[curAnimation] , 20 , 20 , null);

		/* Renderização do jogo */
		world.render(g);
		Collections.sort(entities, Entity.nodeSorter);
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		ui.render(g);

		/**/

		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		if (gameState == "Game_Over") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0, 0, 0, 50));
			g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			g2.setFont(new Font("arial", Font.BOLD, 40));
			g2.setColor(Color.white);
			g2.drawString("Você morreu!", 380, 300);
			g2.setFont(new Font("arial", Font.BOLD, 40));
			if (showMessageGameOver) {
				g2.drawString(">Aperte o botão X para reiniciar<", 200, 345);
			}
		} else if (gameState == "Menu" && gameState == "Pause") {
			menu.render(g);
		}

		if (gameState == "Victory") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0, 0, 0, 50));
			g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			g2.setFont(new Font("arial", Font.BOLD, 30));
			g2.setColor(Color.white);
			g2.drawString("Parabéns, você venceu! :) ", 300, 300);
			g2.setFont(new Font("arial", Font.BOLD, 25));
			if (showMessageVictory) {
				g2.drawString("> Aperte M para sair do jogo! :) <", 240, 345);
			}
		} else if (gameState == "Menu") {
			menu.render(g);
		}else if(gameState == "Pause"){
			menuPause.render(g);
		}
		if (gameState == "Guide") {
			g.drawImage(Menu.instrucoes , 0 , 0 , WIDTH*SCALE , HEIGHT*SCALE ,null);
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial" , Font.BOLD , 13));
			g.drawString("Movimentação" , 50 , 150);
			g.drawString("Tiro", 205,151 );
			g.drawString("W" , 90 , 100);
			g.drawString("S" , 92 , 120);
			g.drawString("A" , 70 , 120);
			g.drawString("D" , 112 , 120);
			g.setFont(new Font("arial" , Font.ITALIC , 18));
			g.drawString("Pegue o cajado e derrote todos os inimigos para avançar de nível;" , 38 , 276);
			g.setFont(new Font("arial" , Font.ITALIC , 16));
			g.drawString("Cajado lvl1 (arma principal)" , 65 , 440 );
			g.drawString("Cajado lvl2 (causa mais dano)" , 65 , 520 );
			g.drawString("Cajado lvl3 (tiro duplo)" , 65 , 600 );
			g.drawString("Poção de Energia" , 330 , 430);
			g.drawString("(+Speed)" , 360 , 450);
			g.drawString("Poção de Vida" , 335 , 585);
			g.drawString("Icone de Speed" , 565 , 435);
			g.drawString("Icone de evolução" , 565 , 580);
			g.drawString("do cajado" , 580 , 600);
			g.drawString("Essência Azul" , 800 , 425);
			g.drawString("(Munição)" , 810 , 450);
			g.drawString("Icone de Essência" , 810 , 590);
			g.drawString("Aperte I para iniciar" , 800 , 15);

		}
		bs.show();

	}

	public synchronized void start() {
		Thread thread = new Thread(this);
		thread.start();
		isRunning = true;
	}

	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			if (System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}

		}

		stop();

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_M && enemies.size() == 0) {
			System.exit(0);
		}

		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;

			if (gameState == "Menu") {
				menu.up = true;
			}else if(gameState == "Pause"){
				menuPause.up = true;
			}

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;

			if (gameState == "Menu") {
				menu.down = true;
			}else if(gameState == "Pause"){
				menuPause.down = true;
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (gameState == "Menu") {
				menu.enter = true;
			}else if(gameState == "Pause"){
				menuPause.enter = true;
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_X) {
			restartGame = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			gameState = "Pause";
		}

		if (gameState == "Guide" && e.getKeyCode() == KeyEvent.VK_I ){
			gameState = "Normal";
		}

		// Atirar com teclado
		/*
		 * if (e.getKeyCode() == KeyEvent.VK_F) { player.isShooting = true; }
		 */

	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX() / 4);
		player.my = (e.getY() / 4);

		Menu.mousePressed = true;
		MenuPause.mousePressed = true;

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Menu.mouseX = e.getX();
		Menu.mouseY = e.getY();

		MenuPause.mouseX = e.getX();
		MenuPause.mouseY = e.getY();
	}
}
