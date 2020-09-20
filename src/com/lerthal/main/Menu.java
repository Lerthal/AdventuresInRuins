package com.lerthal.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.lerthal.world.World;

public class Menu {

	public String[] options = { "New Game", "Instructions", "Exit" };
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	public boolean up, down, enter;
	public static boolean pause = false;
	public static BufferedImage startPlay, startGuide, startExit, instrucoes;
	// public static boolean saveExists = false;
	// public static boolean saveGame = false;

	public static List<Option> mouseOptions = new ArrayList<>();
	public static int mouseX;
	public static int mouseY;
	public static boolean mousePressed = false;

	public static Option start = new Option(96*4, 100*4, 48*4, 15*4); // poe as coordenas e o tamanho
	public static Option guide = new Option(88*4, 119*4, 64*4, 15*4); // poe as coordenas e o tamanho
	public static Option exit = new Option(101*4, 137*4, 38*4, 15*4); // Poe as coordenadas e o tamanho



	public Menu(){
		mouseOptions.add(start);
		mouseOptions.add(guide);
		mouseOptions.add(exit);

		try {
			startPlay = ImageIO.read(World.class.getResource("/startPlay.png"));
			startGuide = ImageIO.read(World.class.getResource("/startCreditos.png"));
			startExit = ImageIO.read(World.class.getResource("/startExit.png"));
			instrucoes = ImageIO.read(getClass().getResource("/instrucoes.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void tick() {
		/*
		 * File file = new File("save.txt"); if (file.exists()) { saveExists = true; }
		 * else { saveExists = false; }
		 */

		if(mousePressed)
		{
			mousePressed = false;
			Option option;

			for (Option mouseOption : mouseOptions) {
				option = mouseOption;

				if (mouseX >= option.x && mouseX <= option.x + option.width && mouseY >= option.y && mouseY <= option.y + option.height) {
					if (option == exit) {
						System.exit(0);
					} else if (option == start) {
						// Lógica do start
						Game.gameState = "Normal";
						pause = false;
					}else if(option == guide){
						Game.gameState = "Guide";
					}
				}
			}
		}


		if (up) {
			up = false;
			currentOption--;
			if (currentOption < 0) {
				currentOption = maxOption;
			}
		}
		if (down) {
			down = false;
			currentOption++;
			if (currentOption > maxOption) {
				currentOption = 0;
			}
		}

		if (enter) {
			enter = false;
			if (options[currentOption] == "New Game") {
				Game.gameState = "Normal";
				pause = false;
				// file = new File("save.txt");
				// file.delete();
			} else if (options[currentOption] == "Exit") {
				System.exit(1);
			} else if (options[currentOption] == "Instructions") {
				Game.gameState = "Guide";
				// file = new File("save.txt");
				/*
				 * if (file.exists()) { String saver = loadGame(10); applySave(saver); }
				 */
			}
		}

		Option option;
		for(int i = 0; i < mouseOptions.size(); i++)
		{
			option = mouseOptions.get(i);
			if(mouseX >= option.x && mouseX <= option.x + option.width && mouseY >= option.y && mouseY <= option.y + option.height)
			{
				if(option == exit){
					currentOption = 2;
				} else if(option == start){
					currentOption = 0;
				}else if(option == guide){
					currentOption = 1;

			}
		}
	}
}

	/*
	 * public static void applySave(String str) { String[] spl = str.split("/"); for
	 * (int i = 0; i < spl.length; i++) { String[] spl2 = spl[i].split(":"); switch
	 * (spl2[0]) { case "level": World.restartGame("lvl" + spl2[1] + ".png");
	 * Game.gameState = "Normal"; pause = false; break; case "life":
	 * Game.player.life = Integer.parseInt(spl2[1]); break; } } }
	 * 
	 * public static String loadGame(int encode) { String line = ""; File file = new
	 * File("save.txt"); if (file.exists()) { try { String singleLine = null;
	 * BufferedReader reader = new BufferedReader(new FileReader("save.txt")); try {
	 * while ((singleLine = reader.readLine()) != null) { String[] trans =
	 * singleLine.split(":"); char[] val = trans[1].toCharArray(); trans[1] = "";
	 * for (int i = 0; i < val.length; i++) { val[i] -= encode; trans[1] += val[i];
	 * } line += trans[0]; line += ":"; line += trans[1]; line += "/"; } } catch
	 * (IOException e) { } } catch (FileNotFoundException e) { }
	 * 
	 * } return line;
	 * 
	 * }
	 * 
	 * public static void saveGame(String[] val1, int[] val2, int encode) {
	 * BufferedWriter write = null; try { write = new BufferedWriter(new
	 * FileWriter("save.txt")); } catch (IOException e) { }
	 * 
	 * for (int i = 0; i < val1.length; i++) { String current = val1[i]; current +=
	 * ":"; char[] value = Integer.toString(val2[i]).toCharArray(); for (int n = 0;
	 * n < value.length; n++) { value[n] += encode; current += value[n]; }
	 * 
	 * try { write.write(current); if (i < val1.length - 1) write.newLine(); } catch
	 * (IOException e) { } }
	 * 
	 * try { write.flush(); write.close(); } catch (IOException e) { }
	 * 
	 * }
	 */

	public void render(Graphics g) {

		if (options[currentOption] == "New Game") {
			g.drawImage(startPlay, 0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE, null);
		} else if (options[currentOption] == "Instructions") {
			g.drawImage(startGuide, 0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE, null);
		} else if (options[currentOption] == "Exit") {
			g.drawImage(startExit, 0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE, null);
		}








	}

}
