package johnny.project.cluster.birch;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Test3 {
	
	public static void main(String[] args) throws Exception {
	
		int maxNodeEntries = 100;
		double distThreshold = Double.parseDouble(args[0]); // initial distance threshold (= sqrt(radius))
		int distFunction = CFTree.D0_DIST;
		boolean applyMergingRefinement = true;
		String datasetFile = args[1];
		
		// This initializes the tree
		CFTree birchTree = new CFTree(maxNodeEntries,distThreshold,distFunction,applyMergingRefinement);
			
		// Read one instace at a time from the dataset
		// Dataset format: each line contain a set of value  v1 v2 v3... separated by spaces
		BufferedReader in = new BufferedReader(new FileReader(datasetFile));
		String line = null;
		while((line=in.readLine())!=null) {
			String[] tmp = line.split("\\s");
			
			double[] x = new double[tmp.length];
			for(int i=0; i<x.length; i++) {
				x[i] = Double.parseDouble(tmp[i]);
			}
			
			// training birch, one instance at a time...
			boolean inserted = birchTree.insertEntry(x);
			if(!inserted) {
				System.err.println("ERROR: NOT INSERTED!");
				System.exit(1);
			}
		}
		in.close();
		birchTree.finishedInsertingData();
		
		// Maps each instance to its closest subcluster
		in = new BufferedReader(new FileReader(datasetFile));
		line = null;
		
		File f = new File("Ouput.txt");
		if (!f.exists()) {
			f.createNewFile();
		}

		FileWriter fw = new FileWriter(f.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		while((line=in.readLine())!=null) {
			String[] tmp = line.split("\\s");
			
			double[] x = new double[tmp.length];
			for(int i=0; i<x.length; i++) {
				x[i] = Double.parseDouble(tmp[i]);
			}
			
			// training birch, one instance at a time...
			int id = birchTree.mapToClosestSubcluster(x);
			// Write to file
			bw.write(id + " : " + line);
			// Write in next line
			bw.newLine();

			//System.out.println(id + " : " + line);
		}
		
		bw.close();
		
		System.out.println("Completed");
	}
}
