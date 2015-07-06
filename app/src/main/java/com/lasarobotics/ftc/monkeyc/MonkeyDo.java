package com.lasarobotics.ftc.monkeyc;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * The MonkeyDo library handles executing instructions generated by MonkeyDo.
 */
public class MonkeyDo {

    HardwareMap map;
    OpMode mode;
    ByteBuffer instruction;

    public MonkeyDo(HardwareMap map, OpMode mode)
    {
        this.map = map;
        this.mode = mode;
    }

    void run(ArrayList<byte[]> instructions)
    {
        for(byte[] instruction : instructions)
            run(instruction);
    }

    public void run(byte[] instruction) throws InvalidParameterException
    {
        ByteBuffer buffer = ByteBuffer.wrap(instruction);

        //find the action to correspond to the ID
        byte id = buffer.get();
        Command.Actions action = null;
        for (Command.Actions a : Command.Actions.values()) {
            if (a.getByte() == id) {
                action = a;
                break;
            }
        }
        if (action == null)
            throw new InvalidParameterException("Could not find the proper Action for the instruction id.");

        //get the details for the action


        //store temporary instruction buffer
        this.instruction = buffer.asReadOnlyBuffer();

        //run instruction

    }
}
