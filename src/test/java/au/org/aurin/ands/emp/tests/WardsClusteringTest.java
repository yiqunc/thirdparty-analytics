package au.org.aurin.ands.emp.tests;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.util.JSONUtils;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import au.edu.uq.preload.Rserve;
import au.org.aurin.ands.emp.WardsClustering;
import au.org.aurin.ands.emp.SpatialData2RConnection;
import au.org.aurin.ands.emp.DataFrame2JSON;


public class WardsClusteringTest {

	@BeforeClass
	public static void initRserve() {
		boolean rRunning = false;
		// 0. Start Rserve - This should already be running, if not we start it
		rRunning = Rserve.checkLocalRserve();
		System.out.println("Rserve running? " + rRunning);
		if (!rRunning) {
			Assert.fail("Without Rserve running we cannot proceed");
		}
	}
	@AfterClass
	public static void terminateRserve() {
		boolean rRunning = true;
		// Stop Rserve if we started it
		rRunning = Rserve.shutdownRserve();
		System.out.println("Rserve shutdown? " + rRunning);
		if (!rRunning) {
			Assert.fail("Cannot Shutdown Rserve, Check if there are permissions "
					+ "to shut it down if the process is owned by a different user");
		}
	}
	
	@Test
	public void test() throws RserveException {
		
		System.out.println("========= Test case NewWards");
		SpatialData2RConnection sd2R = new SpatialData2RConnection();
		String path  = this.getClass().getClassLoader().getResource("data/ABS_data_by_DZN/DZN").getPath();
		path += "/" + "smalldata";
		
		sd2R.shpUrl = path;	
		sd2R.geoJSONFilePath = path + ".geojson";
		sd2R.spatialDataFormatMode = 1;
		
		System.out.println(path);
		System.out.println(sd2R.geoJSONFilePath);
		
		sd2R.exec();
		
		WardsClustering wc = new WardsClustering();

		wc.c = sd2R.c;
		
		wc.geodisthreshold = 10;
		wc.targetclusternum = 1;
		wc.interestedColNamesString = "X2310,X2412,X8500";
		wc.displayColNamesString = "LGA_CODE,LGA,ZONE_CODE";
		wc.interestedColWeightsString = "0.333,0.333,0.333";
		wc.spatialNonSpatialDistWeightsString = "0.9,0.1";
		wc.ignoreEmptyRowJobNum = 20;
		wc.vcmode = true;
		wc.compute();
		
		DataFrame2JSON op= new DataFrame2JSON();
		op.c = wc.cOut;
		op.exec();

	}

}