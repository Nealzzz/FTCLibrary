package com.lasarobotics.library.monkeyc;

import android.content.Context;

import com.lasarobotics.library.util.Timers;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * The MonkeyDo library handles executing commands generated by MonkeyC.
 */
public class MonkeyDo {

    private ArrayList<MonkeyData> commands;
    private Timers t;
    private String filename;
    private int commandsRead = 0;

    public MonkeyDo(String filename, Context context) {
        commands = MonkeyUtil.readFile(filename);
        t = new Timers();
        t.startClock("global");
        this.filename = filename;
        commandsRead = 0;
    }

    /**
     * Using an internal clock, returns the next logical patch from the loaded MonkeyC
     * program, playing back previous input
     *
     * @return MonkeyData object to patch current controller input
     */
    public MonkeyData getNextCommand() {
        long currentTime = t.getClockValue("global");

        //If before first patch, override the state of the MonkeyData.hasUpdate() method to return true
        if (currentTime < commands.get(0).getTime()) {
            return new MonkeyData(null, null, MonkeyUtil.MONKEYC_STARTING_CONSTANT);
        }
        //If there are still more commands to come
        else if (commandsRead + 1 < commands.size()) {
            //If next command's start time has past, increment patch.
            if (commands.get(commandsRead + 1).getTime() < currentTime) {
                commandsRead++;
            }
            return commands.get(commandsRead);
        }
        //Get the last one, without an OutOfRangeException!
        else if (commandsRead + 1 == commands.size()) {
            commandsRead++;
            return commands.get(commandsRead - 1);
        }
        /*Terminates the program, by feeding no values.
        * This causes MonkeyData.hasUpdate() to return false since both deltas are null
        */
        else {
            return new MonkeyData();
        }
    }

    /**
     * Gets the time of the MonkeyC clock
     *
     * @return The time of the clock, in seconds.
     */
    public float getTime() {
        return (float) t.getClockValue("global", TimeUnit.MILLISECONDS) / 1000.0f;
    }

    public int getCommandsRead() {
        return commandsRead;
    }

    public void pauseTime() {
        t.pauseClock("global");
    }

    public void resumeTime() {
        t.startClock("global");
    }

    public String getFilename() {
        return filename;
    }

    public void onStart() {
        t.resetClock("global");
    }
}