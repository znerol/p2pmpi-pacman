package pacifism;

import java.util.Random;
import java.util.Scanner;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import deism.core.Event;
import deism.core.EventCondition;
import deism.process.DefaultProcessBuilder;
import deism.run.ExecutionGovernor;
import deism.run.MessageCenter;
import deism.run.NoStateController;
import deism.run.RealtimeExecutionGovernor;
import deism.run.Runloop;
import deism.run.Service;
import deism.run.StateController;

import model.Model;
import model.ReproducibleRandom;
import model.sprites.Sprite;

public class PacmanSingleUser {
    private static double BASE_TIME_SCALE = 60. / 1000.;

    public static void main(String args[]) {
        // log4j
        BasicConfigurator.configure();

        /* Game speed parameter */
        double speed = Double.valueOf(System.getProperty("speed", "1.0"));

        /* Random seed parameter */
        long seed = Long.valueOf(System.getProperty("seed", "0"));

        /* Log level parameter */
        Level loglevel = Level.toLevel(System.getProperty("loglevel", "WARN"));
        Logger.getRootLogger().setLevel(loglevel);

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
                        + "x.......xxxxx.......x\n"
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
        
        EventCondition terminationCondition = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return false;
            }
        };
        EventCondition snapshotCondition = new EventCondition() {
            @Override
            public boolean match(Event e) {
                return false;
            }
        };

        Service service = new Service();
        ExecutionGovernor governor = new RealtimeExecutionGovernor(BASE_TIME_SCALE * speed);
        service.register(governor);
        MessageCenter messageCenter = new MessageCenter(governor);
        StateController stateController = new NoStateController();

        DefaultProcessBuilder builder = new DefaultProcessBuilder(service);

        Model model = new Model(strArr, 1);
        int pacId = 0;
        for (Sprite sprite : model.getSprites()) {
            builder.add(sprite);
            if (sprite.isPacman())
                pacId = sprite.getSpriteId();
        }

        // Add random number generator with timewarp support
        Random origRng = new Random(seed);
        ReproducibleRandom<Long> rng = new ReproducibleRandom<Long>(origRng);
        model.setRandomGenerator(rng);
        service.register(rng);

        KeyboardController keyboardController = new KeyboardController(governor, pacId);
        builder.add(keyboardController);

        GameGui gui = new GameGui(governor, keyboardController, model, "No Stats available");
        gui.setVisible(true);

        Runloop runloop = new Runloop(governor, terminationCondition, stateController, snapshotCondition, messageCenter, service);
        runloop.run(builder.getProcess());
    }
}
