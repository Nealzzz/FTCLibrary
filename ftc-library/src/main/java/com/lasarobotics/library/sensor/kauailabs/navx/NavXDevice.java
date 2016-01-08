package com.lasarobotics.library.sensor.kauailabs.navx;

import com.kauailabs.navx.ftc.AHRS;
import com.lasarobotics.library.util.Vector3;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * NavX MXP controller
 */
public class NavXDevice {

    AHRS ahrs;
    DataType dataType;

    /**
     * Initialize a NavX MXP or NavX micro device
     *
     * @param map                       HardwareMap instance
     * @param deviceInterfaceModuleName String name of the device interface module the sensor is on
     * @param i2cPort                   The i2C port the sensor is currently on
     */
    NavXDevice(HardwareMap map, String deviceInterfaceModuleName, int i2cPort) {
        initialize(map, deviceInterfaceModuleName, i2cPort, SensorSpeed.NORMAL_FAST, DataType.PROCESSED_DATA);
    }

    /**
     * Initialize a NavX MXP or NavX micro device, and also set the sensor's speed
     *
     * @param map                       HardwareMap instance
     * @param deviceInterfaceModuleName String name of the device interface module the sensor is on
     * @param i2cPort                   The i2C port the sensor is currently on
     * @param speed                     Sensor read speed (recommended anything from VERY_SLOW to VERY_FAST - other values are experimental)
     */
    NavXDevice(HardwareMap map, String deviceInterfaceModuleName, int i2cPort, SensorSpeed speed) {
        initialize(map, deviceInterfaceModuleName, i2cPort, speed, DataType.PROCESSED_DATA);
    }

    private void initialize(HardwareMap map, String deviceInterfaceModuleName, int i2cPort, SensorSpeed speed, DataType type) {
        ahrs = AHRS.getInstance(map.deviceInterfaceModule.get(deviceInterfaceModuleName),
                i2cPort, DataType.PROCESSED_DATA.getValue(), speed.getSpeedHertzByte());
        this.dataType = type;
    }

    public boolean isCalibrating() {
        return ahrs.isCalibrating();
    }

    public boolean isCalibrated() {
        return !ahrs.isCalibrating() && ahrs.isMagnetometerCalibrated();
    }

    public boolean isConnected() {
        return ahrs.isConnected();
    }

    public boolean isMoving() {
        return ahrs.isMoving();
    }

    public boolean isRotating() {
        return ahrs.isRotating() && !ahrs.isMagneticDisturbance();
    }

    public void resetYaw() {
        ahrs.zeroYaw();
    }

    /**
     * Get update frequency in Hertz
     *
     * @return Update rate in Hertzs
     */
    public float getUpdateRate() {
        return (float) ahrs.getActualUpdateRate();
    }

    /**
     * Get the ambient temperature, in degrees Celsius
     *
     * @return Ambient temperature, in degrees Celsius
     */
    public float getTemperature() {
        return ahrs.getTempC();
    }

    public Vector3<Float> getLinearAcceleration() {
        return new Vector3<>(ahrs.getWorldLinearAccelX(), ahrs.getWorldLinearAccelY(), ahrs.getWorldLinearAccelZ());
    }

    /**
     * Gets the rotation vector from the processed gyroscope measurements
     *
     * @return YAW (x-axis), PITCH (y-axis), ROLL (z-axis) rotation in meters/second
     */
    public Vector3<Float> getRotation() {
        return new Vector3<>(ahrs.getYaw(), ahrs.getPitch(), ahrs.getRoll());
    }

    /**
     * Get the heading in degrees, from 0 to 360.
     *
     * @return The heading in degrees from 0 to 360.
     */
    public float getHeading() {
        return ahrs.getFusedHeading();
    }

    public void destroy() {
        ahrs.close();
    }


    public enum DataType {
        PROCESSED_DATA(AHRS.DeviceDataType.kProcessedData),
        RAW_DATA(AHRS.DeviceDataType.kQuatAndRawData),
        RAW_AND_PROCESSED(AHRS.DeviceDataType.kAll);

        private AHRS.DeviceDataType value;

        DataType(AHRS.DeviceDataType value) {
            this.value = value;
        }

        AHRS.DeviceDataType getValue() {
            return value;
        }
    }

    /**
     * NavX response speed
     * <p/>
     * Anything with "EXTREMELY" or "SLOWEST"/"FASTEST" is experimental.
     */
    public enum SensorSpeed {

        SLOWEST(1.0),
        EXTREMELY_SLOW(5.0),
        VERY_SLOW(15.38),
        SLOW(25.0),
        NORMAL_SLOW(33.3),
        NORMAL(40.0),
        NORMAL_FAST(50.0),
        VERY_FAST(66.6),
        EXTREMELY_FAST(100.0),
        FASTEST(200.0);

        double hertz;

        SensorSpeed(double hz) {
            this.hertz = hz;
        }

        public double getSpeedHertz() {
            return hertz;
        }

        public byte getSpeedHertzByte() {
            return (byte) ((int) hertz);
        }
    }
}