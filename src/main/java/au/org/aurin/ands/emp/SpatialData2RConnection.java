package au.org.aurin.ands.emp;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;


import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Out;
import oms3.annotations.Status;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPVector;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REXPDouble;


import au.edu.uq.interfaces.Statistics;
import au.edu.uq.preload.LoadRScript;
import au.edu.uq.preload.Rserve;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.jts.GeometryBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;

public class SpatialData2RConnection {

	@In
	public String shpUrl = "";
	@In
	public String geoJSONFilePath = "";
	@In
	public String geojSONString = "";
	
	@Description("different ways to pass spatial data into data frame: 0: use shapefile, 1: use geojson file, 2: use geojson string")
	@In
	public int spatialDataFormatMode = 0;
	
	@In
	public String rWorkingDir;
	
	@Out
	public RConnection c;
	
	@Execute
	public void exec() throws RserveException{
		try {
			this.rWorkingDir = this.getClass().getClassLoader().getResource("outputs").getPath();
			System.out.println(this.rWorkingDir);
			
			this.c = new RConnection();
			this.c.assign("script", LoadRScript.getGeoJSON2DataFrameScript());
			this.c.assign("shpUrl", new REXPString(this.shpUrl));
			this.c.assign("geoJSONFilePath", new REXPString(this.geoJSONFilePath));
			this.c.assign("geojSONString", new REXPString(this.geojSONString));
			this.c.assign("spatialDataFormatMode", new REXPInteger(this.spatialDataFormatMode));

			this.c.assign("rWorkingDir", new REXPString(this.rWorkingDir));
			
			this.c.eval("try(eval(parse(text=script)),silent=FALSE)");
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
