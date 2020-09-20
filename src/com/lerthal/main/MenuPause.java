package com.lerthal.main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuPause {

    public String[] options = { "Resume", "Guide", "Exit" };
    public int currentOption = 0;
    public int maxOption = options.length - 1;
    public boolean up, down, enter;
    public static boolean pause = false;

    public static List<Option> mouseOptions = new ArrayList<>();
    public static int mouseX;
    public static int mouseY;
    public static boolean mousePressed = false;

    public static BufferedImage pauseResume , pauseGuide , pauseExit;
    public static Option resumePause = new Option(92*4, 51*4, 58*4, 17*4); // poe as coordenas e o tamanho
    public static Option guidePause = new Option(99*4, 71*4, 46*4, 15*4); // poe as coordenas e o tamanho
    public static Option exitPause = new Option(102*4, 89*4, 38*4, 15*4); // Poe as coordenadas e o tamanho


    public MenuPause(){

        mouseOptions.add(resumePause);
        mouseOptions.add(guidePause);
        mouseOptions.add(exitPause);

        try {
            pauseResume = ImageIO.read(getClass().getResource("/pauseResume.png"));
            pauseGuide = ImageIO.read(getClass().getResource("/pauseGuide.png"));
            pauseExit = ImageIO.read(getClass().getResource("/pauseExit.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void tick(){

        if(mousePressed)
        {
            mousePressed = false;
            Option option;

            for (Option mouseOption : mouseOptions) {
                option = mouseOption;

                if (mouseX >= option.x && mouseX <= option.x + option.width && mouseY >= option.y && mouseY <= option.y + option.height) {
                    if (option == exitPause) {
                        System.exit(0);
                    } else if (option == resumePause) {
                        // Lógica do start
                        Game.gameState = "Normal";
                        pause = false;
                    }else if(option == guidePause){
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
            if (options[currentOption] == "Resume") {
                Game.gameState = "Normal";
                pause = false;
                // file = new File("save.txt");
                // file.delete();
            } else if (options[currentOption] == "Exit") {
                System.exit(1);
            } else if (options[currentOption] == "Guide") {
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
                if(option == exitPause){
                    currentOption = 2;
                } else if(option == resumePause){
                    currentOption = 0;
                }else if(option == guidePause){
                    currentOption = 1;

                }
            }
        }
    }

    public void render(Graphics g){
        if (options[currentOption] == "Resume") {
            g.drawImage(pauseResume, 0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE, null);
        } else if (options[currentOption] == "Guide") {
            g.drawImage(pauseGuide, 0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE, null);
        } else if (options[currentOption] == "Exit") {
            g.drawImage(pauseExit, 0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE, null);
        }


    }


}
