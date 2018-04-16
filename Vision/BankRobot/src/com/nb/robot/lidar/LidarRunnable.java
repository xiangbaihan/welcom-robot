package com.nb.robot.lidar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.nb.robot.common.Constants;
import com.nb.robot.common.HardwareModule;

public class LidarRunnable implements Runnable, HardwareModule {
	private static Logger logger = Logger.getLogger(LidarRunnable.class);
	private static int MAX_DEGREE = 720;

	private RplidarNative rplidar = null;
	private long driver = 0;
	volatile boolean isRunning = true;

	// Current cached LIDAR scan result. It may combine several recent scans.
	CachedMeasurementNode[] cachedMeasurementNodes = new CachedMeasurementNode[MAX_DEGREE];
	// Mutex for cachedMeasurementNodes.
	private static Semaphore semaphore = new Semaphore(1);
	// Mutex for rplidar and driber.
	private static Semaphore semaphoreDriver = new Semaphore(1);
	// This is empty if OK.
	String errorMessage = "";
	
	private int degree ;

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted() && isRunning) {
			try {
				Thread.sleep(Constants.LIDAR_SLEEP_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}

			// Restart driver if necessary.
//			if (!isHealthy() && !init()) {
//				logger.debug("LidarRunnable is not healthy: " + errorMessage());
//				continue;
//			}

			MeasurementNode[] oneScan = null;
			try {
				semaphoreDriver.acquire();
				if (rplidar != null && driver > 0) {
					oneScan = rplidar.getScanData(driver);
				}
				// oneScan = getScanDataForTesting();
			    semaphoreDriver.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (oneScan == null) {
				errorMessage = "RpLidar reports no scan result.";
				continue;
			}
			addOneScanToCache(oneScan);
            reportSuccess();
			
		}
	}

	@Override
	public boolean init() {
		close();
		isRunning = true;
		boolean is_connected = false;
		try {
			semaphoreDriver.acquire();
			rplidar = new RplidarNative();
			driver = rplidar.createDriver();
			if (driver > 0) {
				is_connected = rplidar.connect(driver, Constants.LIDAR_DEFAULT_PORT);				
				if (is_connected) {
					rplidar.startScan(driver);
				} else {
					errorMessage = "Failed to bind serial port " + Constants.LIDAR_DEFAULT_PORT + " for RpLidar driver.";
				}				
			} else {				
				errorMessage = "Failed to create RpLidar driver.";
			}
		    semaphoreDriver.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return is_connected;
	}

	@Override
	public void close() {
		try {
			semaphoreDriver.acquire();
		    if (rplidar != null && driver > 0) {
				rplidar.stopScan(driver);
				rplidar.disposeDriver(driver);
			}
		    rplidar = null;
		    driver = 0;
		    semaphoreDriver.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		isRunning = false;
	}

	@Override
	public boolean isHealthy() {
		boolean isLidarHealthy = false;
		try {
			semaphoreDriver.acquire();
			if (driver > 0) {
				isLidarHealthy = rplidar.checkHealth(driver);				
				if (!isLidarHealthy) {
					errorMessage = "RpLidar is unhealthy.";
				}
			} else {
				errorMessage = "Failed to create RpLidar driver.";				
			}
		    semaphoreDriver.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return isLidarHealthy;
	}

	@Override
	public String errorMessage() {
		return errorMessage;
	}

	// Returns current LIDAR scan result.
	public List<MeasurementNode> getLidarInfos() {
		List<MeasurementNode> measurementNodes = new ArrayList<MeasurementNode>();
		try {
		    semaphore.acquire();
			long now = System.currentTimeMillis();
			for (int degree = 0; degree < MAX_DEGREE; ++degree) {
				if (cachedMeasurementNodes[degree] == null) {
					continue;
				}
				// Remove obsolete MeasurementNode.
				if (now - cachedMeasurementNodes[degree].getTimestamp() > Constants.LIDAR_CACHE_MAX_AGE) {
					cachedMeasurementNodes[degree] = null;
					continue;
				}
				measurementNodes.add(cachedMeasurementNodes[degree].getNode());
			}
		    semaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return measurementNodes;
	}

	// Adds one scan result to cache. The cache is useful for merging several
	// scans to avoid missing nodes at certain degrees.
	private void addOneScanToCache(MeasurementNode[] oneScan) {
		try {
		    semaphore.acquire();
			long now = System.currentTimeMillis();
			for (MeasurementNode node : oneScan) {
				if (node.getSyncQuality() <= 0) {
					continue;
				}
				if(node.getAngle()<30 || (node.getAngle()<360 && node.getAngle()>330)) {
					degree = Math.round(node.getAngle());
					cachedMeasurementNodes[degree] = new CachedMeasurementNode(node, now);
				}
			}
		   semaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	Random rand = new Random();

	// For testing.
	private MeasurementNode[] getScanDataForTesting() {
		List<MeasurementNode> nodes = new ArrayList<MeasurementNode>();
		for (int i = 0; i < 360; i++) {
			if (rand.nextBoolean()) {
				continue;
			}
			nodes.add(new MeasurementNode(1, i, rand.nextFloat() * 4000.0f));
		}
		MeasurementNode[] nodesArray = new MeasurementNode[nodes.size()];
		nodesArray = nodes.toArray(nodesArray);
		return nodesArray;
	}

	// For testing.
	public List<MeasurementNode> getLidarInfosForTesting() {
		List<MeasurementNode> nodes = new ArrayList<MeasurementNode>();
		for (int i = 0; i < 360; i++) {
			if (rand.nextBoolean()) {
				continue;
			}
			nodes.add(new MeasurementNode(1, i, rand.nextFloat() * 4000.0f));
		}
		return nodes;
	}

	// Reports success.
	private void reportSuccess() {
		errorMessage = "";
	}
}
