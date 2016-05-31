package cn.edu.zju.zhouyu.mdl.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cn.edu.zju.zhouyu.mdl.util.FileUtil;

public class Purity {
	
	public static double cal(List<Integer> label,List<Integer> cluster){
		HashMap<Integer, HashSet<Integer> > cnt = new HashMap<Integer, HashSet<Integer>>();
		for(int i = 0;i < cluster.size();i++){
			if(null == cnt.get(cluster.get(i)))
				cnt.put(cluster.get(i), new HashSet<Integer>());
			cnt.get(cluster.get(i)).add(i);
		}
		int sum = 0;
		for(int key : cnt.keySet()){
			HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
			for(int idx : cnt.get(key)){
				if(null == tmp.get(label.get(idx)))
					tmp.put(label.get(idx), 1);
				else
					tmp.put(label.get(idx), tmp.get(label.get(idx)) + 1);
			}
			int max = 0;
			for(int i : tmp.keySet())
				if(max < tmp.get(i))
					max = tmp.get(i);
			sum += max;
		}
		return sum * 1.0 / cluster.size();
	}

	public static void main(String[] args){
		List<Integer> label = FileUtil.readLabel("./label.txt");
		List<Integer> cluster = FileUtil.readLabel("./result.txt");
		System.out.println(Purity.cal(label, cluster));
	}
}
