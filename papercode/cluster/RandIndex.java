package cn.edu.zju.zhouyu.mdl.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.edu.zju.zhouyu.mdl.util.FileUtil;


public class RandIndex {

	public static List<Double> cal(List<Integer> label, List<Integer> cluster) {
		HashMap<Integer, Integer> cnt = new HashMap<Integer, Integer>();
		for (int i = 0; i < label.size(); i++)
			if (cnt.get(label.get(i)) == null)
				cnt.put(label.get(i), 1);
			else
				cnt.put(label.get(i), cnt.get(label.get(i)) + 1);
		int N = cnt.keySet().size(), TP = 0, TN = 0, FP = 0, FN = 0, base = 0;
		for (int i = 1; i <= N; i++) {

			for (int j = base; j < base + cnt.get(i); j++)
				for (int k = j + 1; k < base + cnt.get(i); k++)
					if (cluster.get(j).equals(cluster.get(k)))
						TP++;
					else
						FN++;

			for (int j = base; j < base + cnt.get(i); j++)
				for (int k = base + cnt.get(i); k < label.size(); k++)
					if (!cluster.get(j).equals(cluster.get(k)))
						TN++;
					else
						FP++;
			base += cnt.get(i);
		}
		System.out.println(TP + "," + TN + "," + FP + "," + FN);
		double RI = (TP + TN) * 1.0 / label.size() / (label.size() - 1) * 2, P = TP
				* 1.0 / (TP + FP), R = TP * 1.0 / (TP + FN), F = 2 * P * R
				/ (P + R);
		return Arrays.asList(RI, P, R, F);
	}

	public static void main(String[] args) {
		List<Integer> label = FileUtil.readLabel("./label.txt");
		List<Integer> cluster = FileUtil.readLabel("./result.txt");
		System.out.println(RandIndex.cal(label, cluster));
	}
}
