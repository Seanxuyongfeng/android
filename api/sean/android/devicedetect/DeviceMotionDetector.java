package sean.android.devicedetect;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;

import com.zeusis.smartpolicy.DebugUtils;

public class DeviceMotionDetector {
    public interface DeviceIdleCallback {
        public void onAnyMotionResult(int result);
    }

    private static final String TAG = "DeviceMotionDetector";

    private static final boolean DEBUG = false;

    /** Stationary status is unknown due to insufficient orientation measurements. */
    public static final int RESULT_UNKNOWN = -1;

    /** Device is stationary, e.g. still on a table. */
    public static final int RESULT_STATIONARY = 0;

    /** Device has been moved. */
    public static final int RESULT_MOVED = 1;

    /** Orientation measurements are being performed or are planned. */
    private static final int STATE_INACTIVE = 0;

    /** No orientation measurements are being performed or are planned. */
    private static final int STATE_ACTIVE = 1;

    /** Current measurement state. */
    private int mState;

    /** Threshold angle in degrees beyond which the device is considered moving. */
    private final float THRESHOLD_ANGLE = 2f;

    /** Threshold energy above which the device is considered moving. */
    private final float THRESHOLD_ENERGY = 5f;

    /** The duration of the accelerometer orientation measurement. */
    private static final long ORIENTATION_MEASUREMENT_DURATION_MILLIS = 2500;

    /** The maximum duration we will collect accelerometer data. */
    private static final long ACCELEROMETER_DATA_TIMEOUT_MILLIS = 3000;

    /** The interval between accelerometer orientation measurements. */
    private static final long ORIENTATION_MEASUREMENT_INTERVAL_MILLIS = 5000;

    /**
     * The duration in milliseconds after which an orientation measurement is considered
     * too stale to be used.
     */
    private static final int STALE_MEASUREMENT_TIMEOUT_MILLIS = 2 * 60 * 1000;

    /** The accelerometer sampling interval. */
    private static final int SAMPLING_INTERVAL_MILLIS = 40;

    private final Handler mHandler;
    private final Object mLock = new Object();
    private Sensor mAccelSensor;
    private SensorManager mSensorManager;
    private PowerManager.WakeLock mWakeLock;

    /** The minimum number of samples required to detect AnyMotion. */
    private int mNumSufficientSamples;

    /** True if an orientation measurement is in progress. */
    private boolean mMeasurementInProgress;

    /** The most recent gravity vector. */
    private Vector3 mCurrentGravityVector = null;

    /** The second most recent gravity vector. */
    private Vector3 mPreviousGravityVector = null;

    /** Running sum of squared errors. */
    private RunningSignalStats mRunningStats;

    private DeviceIdleCallback mCallback = null;

    public DeviceMotionDetector(PowerManager pm, Handler handler, SensorManager sm,
                                DeviceIdleCallback callback) {
        if (DEBUG) debugMessage(TAG, "DeviceMotionDetector instantiated.");
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.setReferenceCounted(false);
        mHandler = handler;
        mSensorManager = sm;
        mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMeasurementInProgress = false;
        mState = STATE_INACTIVE;
        mCallback = callback;
        mRunningStats = new RunningSignalStats();
        mNumSufficientSamples = (int) Math.ceil(
                ((double)ORIENTATION_MEASUREMENT_DURATION_MILLIS / SAMPLING_INTERVAL_MILLIS));
        if (DEBUG) debugMessage(TAG, "mNumSufficientSamples = " + mNumSufficientSamples);
    }

    /*
     * Acquire accel data until we determine AnyMotion status.
     */
    public void checkForAnyMotion() {
        if (DEBUG) debugMessage(TAG, "checkForAnyMotion(). mState = " + mState);
        if (mState != STATE_ACTIVE) {
            mState = STATE_ACTIVE;
            if (DEBUG) debugMessage(TAG, "Moved from STATE_INACTIVE to STATE_ACTIVE.");
            mCurrentGravityVector = null;
            mPreviousGravityVector = null;
            startOrientationMeasurement();
        }
    }

    public void stop() {
        if (mState == STATE_ACTIVE) {
            mState = STATE_INACTIVE;
            if (DEBUG) debugMessage(TAG, "Moved from STATE_ACTIVE to STATE_INACTIVE.");
            if (mMeasurementInProgress) {
                mMeasurementInProgress = false;
                mSensorManager.unregisterListener(mListener);
            }
            mHandler.removeCallbacks(mMeasurementTimeout);
            mHandler.removeCallbacks(mSensorRestart);
            mWakeLock.release();
            mCurrentGravityVector = null;
            mPreviousGravityVector = null;
        }
    }

    private void startOrientationMeasurement() {
        if (DEBUG) debugMessage(TAG, "startOrientationMeasurement: mMeasurementInProgress=" +
                mMeasurementInProgress + ", (mAccelSensor != null)=" + (mAccelSensor != null));

        if (!mMeasurementInProgress && mAccelSensor != null) {
            if (mSensorManager.registerListener(mListener, mAccelSensor,
                    SAMPLING_INTERVAL_MILLIS * 1000)) {
                mWakeLock.acquire();
                mMeasurementInProgress = true;
                mRunningStats.reset();
            }

            Message msg = Message.obtain(mHandler, mMeasurementTimeout);
            msg.setAsynchronous(true);
            mHandler.sendMessageDelayed(msg, ACCELEROMETER_DATA_TIMEOUT_MILLIS);
        }
    }

    private int stopOrientationMeasurementLocked() {
        if (DEBUG) debugMessage(TAG, "stopOrientationMeasurement. mMeasurementInProgress=" +
                mMeasurementInProgress);
        int status = RESULT_UNKNOWN;
        if (mMeasurementInProgress) {
            mSensorManager.unregisterListener(mListener);
            mHandler.removeCallbacks(mMeasurementTimeout);
            mWakeLock.release();
            long detectionEndTime = SystemClock.elapsedRealtime();
            mMeasurementInProgress = false;
            mPreviousGravityVector = mCurrentGravityVector;
            mCurrentGravityVector = mRunningStats.getRunningAverage();
            if (DEBUG) {
                debugMessage(TAG, "mRunningStats = " + mRunningStats.toString());
                String currentGravityVectorString = (mCurrentGravityVector == null) ?
                        "null" : mCurrentGravityVector.toString();
                String previousGravityVectorString = (mPreviousGravityVector == null) ?
                        "null" : mPreviousGravityVector.toString();
                debugMessage(TAG, "mCurrentGravityVector = " + currentGravityVectorString);
                debugMessage(TAG, "mPreviousGravityVector = " + previousGravityVectorString);
            }
            mRunningStats.reset();
            status = getStationaryStatus();
            if (DEBUG) debugMessage(TAG, "getStationaryStatus() returned " + status);
            if (status != RESULT_UNKNOWN) {
                if (DEBUG) debugMessage(TAG, "Moved from STATE_ACTIVE to STATE_INACTIVE. status = " +
                        status);
                mState = STATE_INACTIVE;
            } else {
                /*
                 * Unknown due to insufficient measurements. Schedule another orientation
                 * measurement.
                 */
                if (DEBUG) debugMessage(TAG, "stopOrientationMeasurementLocked(): another measurement" +
                        " scheduled in " + ORIENTATION_MEASUREMENT_INTERVAL_MILLIS +
                        " milliseconds.");
                Message msg = Message.obtain(mHandler, mSensorRestart);
                msg.setAsynchronous(true);
                mHandler.sendMessageDelayed(msg, ORIENTATION_MEASUREMENT_INTERVAL_MILLIS);
            }
        }
        return status;
    }

    /*
     * Updates mStatus to the current AnyMotion status.
     */
    public int getStationaryStatus() {
        if ((mPreviousGravityVector == null) || (mCurrentGravityVector == null)) {
            return RESULT_UNKNOWN;
        }
        Vector3 previousGravityVectorNormalized = mPreviousGravityVector.normalized();
        Vector3 currentGravityVectorNormalized = mCurrentGravityVector.normalized();
        float angle = previousGravityVectorNormalized.angleBetween(currentGravityVectorNormalized);
        if (DEBUG) debugMessage(TAG, "getStationaryStatus: angle = " + angle);
        if ((angle < THRESHOLD_ANGLE) && (mRunningStats.getEnergy() < THRESHOLD_ENERGY)) {
            return RESULT_STATIONARY;
        } else if (Float.isNaN(angle)) {
            /**
             * Floating point rounding errors have caused the angle calcuation's dot product to
             * exceed 1.0. In such case, we report RESULT_MOVED to prevent devices from rapidly
             * retrying this measurement.
             */
            return RESULT_MOVED;
        }
        long diffTime = mCurrentGravityVector.timeMillisSinceBoot -
                mPreviousGravityVector.timeMillisSinceBoot;
        if (diffTime > STALE_MEASUREMENT_TIMEOUT_MILLIS) {
            if (DEBUG) debugMessage(TAG, "getStationaryStatus: mPreviousGravityVector is too stale at " +
                    diffTime + " ms ago. Returning RESULT_UNKNOWN.");
            return RESULT_UNKNOWN;
        }
        return RESULT_MOVED;
    }

    private final SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int status = RESULT_UNKNOWN;
            synchronized (mLock) {
                Vector3 accelDatum = new Vector3(SystemClock.elapsedRealtime(), event.values[0],
                        event.values[1], event.values[2]);
                mRunningStats.accumulate(accelDatum);

                // If we have enough samples, stop accelerometer data acquisition.
                if (mRunningStats.getSampleCount() >= mNumSufficientSamples) {
                    status = stopOrientationMeasurementLocked();
                }
            }
            if (status != RESULT_UNKNOWN) {
                mCallback.onAnyMotionResult(status);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private final Runnable mSensorRestart = new Runnable() {
        @Override
        public void run() {
            synchronized (mLock) {
                startOrientationMeasurement();
            }
        }
    };

    private final Runnable mMeasurementTimeout = new Runnable() {
        @Override
        public void run() {
            int status = RESULT_UNKNOWN;
            synchronized (mLock) {
                if (DEBUG) DebugUtils.i(TAG, "mMeasurementTimeout. Failed to collect sufficient accel " +
                        "data within " + ACCELEROMETER_DATA_TIMEOUT_MILLIS + " ms. Stopping " +
                        "orientation measurement.");
                status = stopOrientationMeasurementLocked();
            }
            if (status != RESULT_UNKNOWN) {
                mCallback.onAnyMotionResult(status);
            }
        }
    };

    /**
     * A timestamped three dimensional vector and some vector operations.
     */
    private static class Vector3 {
        public long timeMillisSinceBoot;
        public float x;
        public float y;
        public float z;

        public Vector3(long timeMillisSinceBoot, float x, float y, float z) {
            this.timeMillisSinceBoot = timeMillisSinceBoot;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private float norm() {
            return (float) Math.sqrt(dotProduct(this));
        }

        private Vector3 normalized() {
            float mag = norm();
            return new Vector3(timeMillisSinceBoot, x / mag, y / mag, z / mag);
        }

        /**
         * Returns the angle between this 3D vector and another given 3D vector.
         * Assumes both have already been normalized.
         *
         * @param other The other Vector3 vector.
         * @return angle between this vector and the other given one.
         */
        public float angleBetween(Vector3 other) {
            double degrees = Math.toDegrees(Math.acos(this.dotProduct(other)));
            float returnValue = (float) degrees;
            debugMessage(TAG, "angleBetween: this = " + this.toString() +
                    ", other = " + other.toString());
            debugMessage(TAG, "    degrees = " + degrees + ", returnValue = " + returnValue);
            return returnValue;
        }

        @Override
        public String toString() {
            String msg = "";
            msg += "timeMillisSinceBoot=" + timeMillisSinceBoot;
            msg += " | x=" + x;
            msg += ", y=" + y;
            msg += ", z=" + z;
            return msg;
        }

        public float dotProduct(Vector3 v) {
            return x * v.x + y * v.y + z * v.z;
        }

        public Vector3 times(float val) {
            return new Vector3(timeMillisSinceBoot, x * val, y * val, z * val);
        }

        public Vector3 plus(Vector3 v) {
            return new Vector3(v.timeMillisSinceBoot, x + v.x, y + v.y, z + v.z);
        }

        public Vector3 minus(Vector3 v) {
            return new Vector3(v.timeMillisSinceBoot, x - v.x, y - v.y, z - v.z);
        }
    }

    /**
     * Maintains running statistics on the signal revelant to AnyMotion detection, including:
     * <ul>
     *   <li>running average.
     *   <li>running sum-of-squared-errors as the energy of the signal derivative.
     * <ul>
     */
    private static class RunningSignalStats {
        Vector3 previousVector;
        Vector3 currentVector;
        Vector3 runningSum;
        float energy;
        int sampleCount;

        public RunningSignalStats() {
            reset();
        }

        public void reset() {
            previousVector = null;
            currentVector = null;
            runningSum = new Vector3(0, 0, 0, 0);
            energy = 0;
            sampleCount = 0;
        }

        /**
         * Apply a 3D vector v as the next element in the running SSE.
         */
        public void accumulate(Vector3 v) {
            if (v == null) {
                if (DEBUG) debugMessage(TAG, "Cannot accumulate a null vector.");
                return;
            }
            sampleCount++;
            runningSum = runningSum.plus(v);
            previousVector = currentVector;
            currentVector = v;
            if (previousVector != null) {
                Vector3 dv = currentVector.minus(previousVector);
                float incrementalEnergy = dv.x * dv.x + dv.y * dv.y + dv.z * dv.z;
                energy += incrementalEnergy;
                if (DEBUG) debugMessage(TAG, "Accumulated vector " + currentVector.toString() +
                        ", runningSum = " + runningSum.toString() +
                        ", incrementalEnergy = " + incrementalEnergy +
                        ", energy = " + energy);
            }
        }

        public Vector3 getRunningAverage() {
            if (sampleCount > 0) {
                return runningSum.times((float)(1.0f / sampleCount));
            }
            return null;
        }

        public float getEnergy() {
            return energy;
        }

        public int getSampleCount() {
            return sampleCount;
        }

        @Override
        public String toString() {
            String msg = "";
            String currentVectorString = (currentVector == null) ?
                    "null" : currentVector.toString();
            String previousVectorString = (previousVector == null) ?
                    "null" : previousVector.toString();
            msg += "previousVector = " + previousVectorString;
            msg += ", currentVector = " + currentVectorString;
            msg += ", sampleCount = " + sampleCount;
            msg += ", energy = " + energy;
            return msg;
        }
    }

    private static void debugMessage(String tag, String msg){
        DebugUtils.d(tag, msg);
    }
}
