package johnny.project.cluster.birch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Test1 {

	public static void main(String[] args) throws Exception {
		int maxNodeEntries = Integer.parseInt(args[0]);
		double distThreshold = Double.parseDouble(args[1]);
		int distFunction = Integer.parseInt(args[2]);
		boolean applyMergingRefinement = Boolean.parseBoolean(args[3]);
		String datasetFile = args[4];

		CFTree birchTree = new CFTree(maxNodeEntries, distThreshold,
				CFTree.D0_DIST, applyMergingRefinement);
		birchTree.setMemoryLimit(100 * 1024 * 1024);

		BufferedReader in = new BufferedReader(new FileReader(datasetFile));

		String line = null;
		while ((line = in.readLine()) != null) {
			String[] tmp = line.split("\\s");

			double[] x = new double[tmp.length];
			for (int i = 0; i < x.length; i++) {
				x[i] = Double.parseDouble(tmp[i]);
			}

			boolean inserted = birchTree.insertEntry(x);
			if (!inserted) {
				System.err.println("NOT INSERTED!");
				System.exit(1);
			}
		}

		System.out.println("*************************************************");
		System.out.println("*************************************************");
		birchTree.printCFTree();
		System.out.println("*************************************************");
		System.out.println("*************************************************");

		System.out.println("****************** LEAVES *******************");
		birchTree.printLeafEntries();
		System.out.println("****************** END *******************");

		// System.out.println("****************** INDEXES *******************");
		// birchTree.printLeafIndexes();
		// System.out.println("****************** END *******************");

		File f = new File("Ouput.txt");
		if (!f.exists()) {
			f.createNewFile();
		}

		FileWriter fw = new FileWriter(f.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write("Total CF-Nodes = " + birchTree.countNodes());
		bw.newLine();
		bw.write("Total CF-Entries = " + birchTree.countEntries());
		bw.newLine();
		bw.write("Total CF-Leaf_Entries = " + birchTree.countLeafEntries());
		bw.newLine();

		// System.out.println("Total CF-Nodes = " + birchTree.countNodes());
		// System.out.println("Total CF-Entries = " + birchTree.countEntries());
		// System.out.println("Total CF-Leaf_Entries = " +
		// birchTree.countLeafEntries());

		CFTree oldTree = birchTree;
		CFTree newTree = null;
		double newThreshold = distThreshold;
		for (int i = 0; i < 10; i++) {
			newThreshold = oldTree.computeNewThreshold(
					oldTree.getLeafListStart(), distFunction, newThreshold);
			// System.out.println("new Threshold ["+i+"] = " + newThreshold);
			bw.write("New Threshold [" + i + "] = " + newThreshold);
			bw.newLine();
			newTree = oldTree.rebuildTree(maxNodeEntries, newThreshold,
					distFunction, true, false);
			bw.write("Total CF-Nodes in new Tree[" + i + "] = "
					+ newTree.countNodes());
			bw.newLine();
			bw.write("Total CF-Entries in new Tree[" + i + "] = "
					+ newTree.countEntries());
			bw.newLine();
			bw.write("Total CF-Leaf_Entries in new Tree[" + i + "] = "
					+ newTree.countLeafEntries());
			bw.newLine();
			bw.write("Total CF-Leaf_Entries lambdaSS in new Tree[" + i + "] = "
					+ newTree.computeSumLambdaSquared());
			bw.newLine();
			// System.out.println("Total CF-Nodes in new Tree["+i+"] = " +
			// newTree.countNodes());
			// System.out.println("Total CF-Entries in new Tree["+i+"] = " +
			// newTree.countEntries());
			// System.out.println("Total CF-Leaf_Entries in new Tree["+i+"] = "
			// + newTree.countLeafEntries());
			// System.out.println("Total CF-Leaf_Entries lambdaSS in new Tree["+i+"] = "
			// + newTree.computeSumLambdaSquared());

			oldTree = newTree;

		}

		bw.write("Sub Clusters");
		bw.newLine();

		ArrayList<ArrayList<Integer>> members = newTree.getSubclusterMembers();

		// print the index of instances in each subcluster
		for (ArrayList<Integer> subclust : members) {
			// System.out.println(Arrays.toString(subclust.toArray(new
			// Integer[0])));
			// Write to file
			bw.write(Arrays.toString(subclust.toArray(new Integer[0])));
			// Write in next line
			bw.newLine();
		}

		bw.close();

		System.out.println("Completed");
	}

}
