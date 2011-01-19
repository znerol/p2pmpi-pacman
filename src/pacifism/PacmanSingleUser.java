package pacifism;

import java.util.Scanner;

import org.apache.log4j.BasicConfigurator;

import deism.run.ExecutionGovernor;
import deism.run.RealtimeExecutionGovernor;

import model.Model;

public class PacmanSingleUser {
    public static void main(String args[]) {
        // log4j
        BasicConfigurator.configure();

        char[][] strArr = new char[22][21];
        Scanner in =
                new Scanner("xxxxxxxxxxxxxxxxxxxxx\n"
                        + "x.........x.........x\n"
                        + "xsxxx.xxx.x.xxx.xxxsx\n"
                        + "x.xxx.xxx.x.xxx.xxx.x\n"
                        + "x.........a.........x\n"
                        + "x.xxx.x.xxxxx.x.xxx.x\n"
                        + "x.....x...x...x.....x\n"
                        + "xxxxx.xxx.x.xxx.xxxxx\n"
                        + "xxxxx.x.b.c.d.x.xxxxx\n"
                        + "xxxxx.x.xxxxx.x.xxxxx\n"
                        + "........xxxxx........\n"
                        + "xxxxx.x.xxxxx.x.xxxxx\n"
                        + "xxxxx.x.......x.xxxxx\n"
                        + "xxxxx.x.xxxxx.x.xxxxx\n"
                        + "x.........x.........x\n"
                        + "x.xxx.xxx.x.xxx.xxx.x\n"
                        + "xs..x.....4.....x..sx\n"
                        + "xxx.x.x.xxxxx.x.x.xxx\n"
                        + "x..2..x...x...x..3..x\n"
                        + "x.xxxxxxx.x.xxxxxxx.x\n"
                        + "x.........1.........x\n"
                        + "xxxxxxxxxxxxxxxxxxxxx\n");
        int i = 0;
        while (in.hasNextLine()) {
            strArr[i] = in.nextLine().trim().toCharArray();
            i++;
        }
        Model model = new Model(strArr, 1);
        
        ExecutionGovernor governor = new RealtimeExecutionGovernor(1000./60.);
        KeyboardController keyboardController = new KeyboardController(governor, 0);
        GameGui gui = new GameGui(governor, keyboardController, model);
        gui.setVisible(true);
        System.out.println("bye");
    }
}
