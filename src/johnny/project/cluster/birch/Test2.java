package johnny.project.cluster.birch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;


public class Test2 {
	
	public static void main(String[] args) throws Exception {
	
		int maxNodeEntries = 100;
		double distThreshold = Double.parseDouble(args[0]); // initial distance threshold (= sqrt(radius))
		int distFunction = CFTree.D0_DIST;
		boolean applyMergingRefinement = true;
		int memoryLimit = Integer.parseInt(args[1]); // in MB
		int memoryLimitPeriodicCheck = 10000; // verify memory usage after every 10000 inserted instances 
		String datasetFile = args[2];
		
		// This initializes the tree
		CFTree birchTree = new CFTree(maxNodeEntries,distThreshold,distFunction,applyMergingRefinement);
		
		// comment the following three lines, if you do not want auto rebuild based on memory usage constraints
		// if auto-rebuild is not active, you need to set distThreshold by hand
		birchTree.setAutomaticRebuild(true); 
		birchTree.setMemoryLimitMB(memoryLimit);
		birchTree.setPeriodicMemLimitCheck(memoryLimitPeriodicCheck); // verify memory usage after every memoryLimitPeriodicCheck
		
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

		// get the results
		ArrayList<ArrayList<Integer>> subclusters = birchTree.getSubclusterMembers();
		
		File f = new File("Ouput.txt");
		if (!f.exists()) {
			f.createNewFile();
		}

		FileWriter fw = new FileWriter(f.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		// print the index of instances in each subcluster
		for(ArrayList<Integer> subclust : subclusters) {
			//System.out.println(Arrays.toString(subclust.toArray(new Integer[0])));
			// Write to file
						bw.write(Arrays.toString(subclust.toArray(new Integer[0])));
						// Write in next line
						bw.newLine();
		}
		
		bw.close();
		
		System.out.println("Completed");
	}
}
