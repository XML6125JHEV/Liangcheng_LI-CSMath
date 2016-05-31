package cn.edu.zju.zhouyu.mdl.cluster;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.edu.zju.zhouyu.mdl.util.FileUtil;

public class NMI {
	
	public static double cal(List<Integer> label,List<Integer> cluster){
		int N = label.size();
		double mi = 0.0;
		HashMap<Integer, Set<Integer>> gnd = new HashMap<Integer, Set<Integer>>();
		HashMap<Integer, Set<Integer>> fea = new HashMap<Integer, Set<Integer>>();
		for(int i = 0;i < N;i++){
			if(!gnd.containsKey(label.get(i)))
				gnd.put(label.get(i), new HashSet<Integer>());
			gnd.get(label.get(i)).add(i);
		}
		for(int i = 0;i < N;i++){
			if(!fea.containsKey(cluster.get(i)))
				fea.put(cluster.get(i), new HashSet<Integer>());
			fea.get(cluster.get(i)).add(i);
		}
		for(int i : fea.keySet())
			for(int j : gnd.keySet()){
				Set<Integer> wk = fea.get(i);
				Set<Integer> cj = gnd.get(j);
				Set<Integer> tmp = new HashSet<Integer>();
				tmp.addAll(wk);
				tmp.retainAll(cj);
				int n = tmp.size();
				if(n > 0)
					mi += n * Math.log(n * N * 1.0 / wk.size() / cj.size()) / N;
			}
		double hFea = 0.0,hGnd = 0.0;
		for(int i : fea.keySet())
			hFea += -(fea.get(i).size() * 1.0 / N) * Math.log(fea.get(i).size() * 1.0 / N);
		for(int i : gnd.keySet())
			hGnd += -(gnd.get(i).size() * 1.0 / N) * Math.log(gnd.get(i).size() * 1.0 / N);
		return mi * 2.0 / (hFea + hGnd);
	}
	public static void main(String[] args) {
		List<Integer> label = FileUtil.readLabel("./label.txt");
		List<Integer> cluster = FileUtil.readLabel("./result.txt");
		System.out.println(NMI.cal(label, cluster));
	}
}
