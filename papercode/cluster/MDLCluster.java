package cn.edu.zju.zhouyu.mdl.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.zju.zhouyu.mdl.util.FileUtil;
import cn.edu.zju.zhouyu.mdl.util.PageDao;
import cn.edu.zju.zhouyu.mdl.util.UrlSplit;
import cn.edu.zju.zhouyu.mdl.cluster.NMI;
import cn.edu.zju.zhouyu.mdl.cluster.Purity;

public class MDLCluster {

	public static final double C = 2.0;

	public static final double ALPHA = 1.7;

	public static final int K = 30;
	
	private int hostId;

	private List<Set<String>> cache = new ArrayList<Set<String>>();

	private Set<Integer> WHOLE = new HashSet<Integer>();
	
	public MDLCluster(int hostId) {
		this.hostId = hostId;
		List<String> urls = PageDao.getUrls(hostId);
		for (String url : urls)
			cache.add(UrlSplit.split(url));
		WHOLE = new HashSet<Integer>();
		for (int i = 0; i < cache.size(); i++)
			WHOLE.add(i);
	}

	private Set<String> getTerms(Set<Integer> W) {
		Set<String> terms = new HashSet<String>();
		for (int id : W)
			for (String t : cache.get(id))
				terms.add(t);
		return terms;
	}

	public double getDeltaMDL(Set<Integer> w, Set<Set<Integer>> partition) {
		double result = -C;
		int s = getScripts(w).size();
		for (Set<Integer> wi : partition)
			result += wi.size() * Math.log(wi.size()) / Math.log(2) + ALPHA
					* wi.size() * (getScripts(wi).size() - s);
		return result;
	}

	public double getMDL(Set<Integer> w, Set<Set<Integer>> partition) {
		/**
		 * heavily cost
		 */
		double result = C * partition.size();
		for (Set<Integer> wi : partition) {
			result = result - nLogN(wi.size()) - ALPHA * wi.size()
					* getScripts(wi).size();
		}
		return result;
	}

	private Set<Set<Integer>> findGreedyCandidateLinear(Set<Integer> W) {
		int N = W.size();
		Map<String, Set<String>> FDtwo = new HashMap<String, Set<String>>();
		Map<String, Set<String>> NFDtwo = new HashMap<String, Set<String>>();
		Map<Set<String>, Integer> cnt = new HashMap<Set<String>, Integer>();
		Set<Set<Integer>> result = new HashSet<Set<Integer>>();
		Set<String> legalTerms = getTerms(W);
		legalTerms.removeAll(getScripts(W));

		for (int id : W) {
			List<String> tmp = new ArrayList<String>(cache.get(id));
			for (int i = 0; i < tmp.size(); i++) {
				if (!legalTerms.contains(tmp.get(i)))
					continue;
				for (int j = i; j < tmp.size(); j++) {
					if (!legalTerms.contains(tmp.get(j)))
						continue;
					Set<String> pair = new HashSet<String>();
					pair.add(tmp.get(i));
					pair.add(tmp.get(j));
					if (cnt.containsKey(pair))
						cnt.put(pair, cnt.get(pair) + 1);
					else
						cnt.put(pair, 1);
				}
			}
		}

		for (Set<String> key : cnt.keySet()) {
			if (key.size() == 1) {
				String term = key.iterator().next();
				if (!FDtwo.containsKey(term))
					FDtwo.put(term, new HashSet<String>());
				FDtwo.get(term).add(term);
			} else {
				List<String> term = new ArrayList<String>();
				for (String t : key)
					term.add(t);
				Set<String> s0 = new HashSet<String>();
				Set<String> s1 = new HashSet<String>();
				s0.add(term.get(0));
				s1.add(term.get(1));
				// FD
				if (cnt.get(s0).equals(cnt.get(key))) {
					if (!FDtwo.containsKey(term.get(0)))
						FDtwo.put(term.get(0), new HashSet<String>());
					FDtwo.get(term.get(0)).add(term.get(1));
				}
				if (cnt.get(s1).equals(cnt.get(key))) {
					if (!FDtwo.containsKey(term.get(1)))
						FDtwo.put(term.get(1), new HashSet<String>());
					FDtwo.get(term.get(1)).add(term.get(0));
				}

				// NFD
				if (cnt.get(s0) + cnt.get(s1) - cnt.get(key) == N) {
					if (!NFDtwo.containsKey(term.get(0)))
						NFDtwo.put(term.get(0), new HashSet<String>());
					if (!NFDtwo.containsKey(term.get(1)))
						NFDtwo.put(term.get(1), new HashSet<String>());
					NFDtwo.get(term.get(0)).add(term.get(1));
					NFDtwo.get(term.get(1)).add(term.get(0));
				}
			}
		}
		double deltaMDL = Double.MAX_VALUE;
		int k = 0;
		String divide = null;

		// two-way
		for (String term : legalTerms) {
			Set<String> tmpSet = new HashSet<String>();
			tmpSet.add(term);
			int ni = cnt.get(tmpSet);

			int fd, nfd;
			if (FDtwo.get(term) == null)
				fd = 0;
			else
				fd = FDtwo.get(term).size();

			if (NFDtwo.get(term) == null)
				nfd = 0;
			else
				nfd = NFDtwo.get(term).size();

			double tmpDeltaMDL = C + nLogN(N) - nLogN(ni) - nLogN(N - ni)
					- ALPHA * (ni * fd + (N - ni) * nfd);
			if (deltaMDL > tmpDeltaMDL) {
				deltaMDL = tmpDeltaMDL;
				k = 1;
				divide = term;
			}
		}
		// k-way
		Set<Integer> rest = new HashSet<Integer>();
		rest.addAll(W);
		List<String> Ai = new ArrayList<String>();
		Map<String, Integer> CI = new HashMap<String, Integer>();
		List<Integer> FDM = new ArrayList<Integer>();
		List<Integer> NFDM = new ArrayList<Integer>();
		Map<String, Integer> allCnt = new HashMap<String, Integer>();

		for (int id : W)
			for (String term : cache.get(id)) {
				if (allCnt.get(term) == null)
					allCnt.put(term, 1);
				else
					allCnt.put(term, allCnt.get(term) + 1);
			}

		for (int i = 2; i < K; i++) {
			Map<String, Set<Integer>> tmpCnt = new HashMap<String, Set<Integer>>();
			for (int id : rest)
				for (String term : cache.get(id)) {
					if (tmpCnt.get(term) == null)
						tmpCnt.put(term, new HashSet<Integer>());
					else
						tmpCnt.get(term).add(id);
				}

			int maxVal = Integer.MIN_VALUE;
			String frequentTerm = null;
			for (String term : tmpCnt.keySet()) {
				int tmpSize = tmpCnt.get(term).size();
				if (maxVal < tmpSize) {
					maxVal = tmpSize;
					frequentTerm = term;
				}
				allCnt.put(term, allCnt.get(term) - tmpSize);
			}

			if (frequentTerm == null)
				break;
			Map<String, Integer> tmpFD = new HashMap<String, Integer>();
			for (int id : tmpCnt.get(frequentTerm))
				for (String term : cache.get(id)) {
					if (CI.containsKey(term))
						continue;
					if (tmpFD.get(term) == null)
						tmpFD.put(term, 1);
					else
						tmpFD.put(term, tmpFD.get(term) + 1);
				}
			int numFD = 0;
			for (String term : tmpFD.keySet())
				if (tmpFD.get(term).equals(maxVal))
					numFD++;
			CI.put(frequentTerm, maxVal);
			Ai.add(frequentTerm);
			FDM.add(numFD);
			rest.removeAll(tmpCnt.get(frequentTerm));
			int numNFD = 0;
			for (String term : allCnt.keySet())
				if (allCnt.get(term).equals(rest.size()))
					numNFD++;
			NFDM.add(numNFD);
		}

		for (int i = 2; i < K; i++) {
			if (FDM.size() < i)
				break;
			double tmpDeltaMDL = C + nLogN(N);
			int nRest = W.size();
			for (int j = 0; j < i; j++) {
				int ni = CI.get(Ai.get(j));
				nRest -= ni;
				tmpDeltaMDL += (-nLogN(ni) - ALPHA * ni * FDM.get(j));
			}
			tmpDeltaMDL += (-nLogN(nRest) - ALPHA * nRest * NFDM.get(i - 1));
			if (tmpDeltaMDL < deltaMDL) {
				deltaMDL = tmpDeltaMDL;
				k = i;
			}
		}

		if (k == 0 || deltaMDL >= 0.0)
			return result;

		if (k == 1) {
			Set<Integer> include = new HashSet<Integer>();
			Set<Integer> exclude = new HashSet<Integer>();
			for (int i : W)
				if (cache.get(i).contains(divide))
					include.add(i);
				else
					exclude.add(i);
			result.add(include);
			result.add(exclude);
		} else {
			Set<Integer> ei = new HashSet<Integer>();
			for (int i = 0; i < k; i++) {
				Set<Integer> partition = new HashSet<Integer>();
				String scriptTerm = Ai.get(i);
				for (int id : W)
					if (!ei.contains(id) && cache.get(id).contains(scriptTerm))
						partition.add(id);
				result.add(partition);
			}
			Set<Integer> rW = new HashSet<Integer>();
			rW.addAll(W);
			rW.removeAll(ei);
			result.add(rW);
		}

		return result;
	}

	private static double nLogN(int n) {
		return n * Math.log(n) / Math.log(2);
	}

	private Set<Set<Integer>> findGreedyCandidate(Set<Integer> W) {
		Set<String> legalTerms = getTerms(W);
		legalTerms.removeAll(getScripts(W));
		Set<Set<Integer>> base = new HashSet<Set<Integer>>();
		base.add(W);
		double mdl = getMDL(W, base);
		Set<Set<Integer>> best = new HashSet<Set<Integer>>();
		// two-way greedy partitions
		for (String t : legalTerms) {
			Set<Integer> include = new HashSet<Integer>();
			Set<Integer> exclude = new HashSet<Integer>();
			for (int i : W)
				if (cache.get(i).contains(t))
					include.add(i);
				else
					exclude.add(i);
			Set<Set<Integer>> tmp = new HashSet<Set<Integer>>();
			tmp.add(include);
			tmp.add(exclude);

			if (mdl > getMDL(W, tmp)) {
				mdl = getMDL(W, tmp);
				best = tmp;
			}
		}

		// k-way greedy partitions(k >= 2)
		for (int k = 2; k < K; k++) {
			Set<Set<Integer>> tmp = new HashSet<Set<Integer>>();
			Set<Integer> ei = new HashSet<Integer>();
			Set<String> es = new HashSet<String>();
			for (int i = 1; i <= k; i++) {
				Set<Integer> wi = new HashSet<Integer>();
				Map<String, Integer> cnt = new HashMap<String, Integer>();
				for (int id : W) {
					if (ei.contains(id))
						continue;
					for (String t : cache.get(id)) {
						if (!legalTerms.contains(t) || es.contains(t))
							continue;
						if (cnt.get(t) == null)
							cnt.put(t, 1);
						else
							cnt.put(t, cnt.get(t) + 1);
					}
				}
				int maxVal = Integer.MIN_VALUE;
				String term = null;
				for (String key : cnt.keySet())
					if (maxVal < cnt.get(key)) {
						maxVal = cnt.get(key);
						term = key;
					}
				if (term == null)
					return best;
				for (int id : W) {
					if (ei.contains(id))
						continue;
					for (String t : cache.get(id))
						if (t.equals(term))
							wi.add(id);
				}
				tmp.add(wi);
				ei.addAll(wi);
				es.add(term);
			}
			Set<Integer> rest = new HashSet<Integer>();
			for (int id : W)
				if (!ei.contains(id))
					rest.add(id);
			tmp.add(rest);
			if (mdl > getMDL(W, tmp)) {
				mdl = getMDL(W, tmp);
				best = tmp;
			}
		}
		return best;
	}

	private Set<String> getScripts(Set<Integer> W) {
		Map<String, Integer> scripts = new HashMap<String, Integer>();
		for (int id : W)
			for (String term : cache.get(id)) {
				if (scripts.get(term) == null)
					scripts.put(term, 1);
				else
					scripts.put(term, scripts.get(term) + 1);
			}
		Set<String> result = new HashSet<String>();
		for (String term : scripts.keySet()) {
			if (scripts.get(term).equals(W.size()))
				result.add(term);
		}
		return result;
	}

	public Set<Set<Integer>> getCluster() {
		return recursiveMDL(WHOLE);
	}

	private Set<Set<Integer>> recursiveMDL(Set<Integer> W) {
		Set<Set<Integer>> result = new HashSet<Set<Integer>>();
		Set<Set<Integer>> cGreedy = findGreedyCandidateLinear(W);
		if (cGreedy.isEmpty()) {
			result.add(W);
			return result;
		} else {
			for (Set<Integer> w : cGreedy)
				result.addAll(recursiveMDL(w));
			return result;
		}
	}
	
	public Set<Set<Integer>> getSample(){
		List<Integer> pageId = PageDao.getIds(hostId);
		long t1, t2;
		t1 = System.currentTimeMillis();
		Set<Set<Integer>> cluster = getCluster();
		t2 = System.currentTimeMillis();
		System.out.println(t2 - t1);
		Map<Integer,Integer> query = new HashMap<Integer, Integer>();
		for(int i = 0;i < pageId.size();i++)
			query.put(i, pageId.get(i));
		Set<Set<Integer>> result = new HashSet<Set<Integer>>();
		for(Set<Integer> tmpS : cluster){
			Set<Integer> newS = new HashSet<Integer>();
			for(int j : tmpS)
				newS.add(query.get(j));
			//System.out.println(newS);
			result.add(newS);
		}
		return result;
	}
	
	public static void test(){
		int hostId = 3;
		MDLCluster c = new MDLCluster(hostId);
		long t1, t2;
		t1 = System.currentTimeMillis();
		Set<Set<Integer>> cluster = c.getCluster();
		t2 = System.currentTimeMillis();
		System.out.println(t2 - t1);

		List<Integer> clusterOrderId = PageDao.getClusterOrderId(hostId);
		List<Integer> pageId = PageDao.getIds(hostId);
		List<Integer> label = PageDao.getLabels(hostId);
		List<Integer> tmp = new ArrayList<Integer>();
		List<Integer> result = new ArrayList<Integer>();
		Map<Integer,Integer> query = new HashMap<Integer, Integer>();
		tmp.addAll(label);
		
		for(int i = 0;i < pageId.size();i++)
			query.put(pageId.get(i), i);
		
		int cluster_id = 1;
		for (Set<Integer> s : cluster) {
			//System.out.println(s);
			for (int id : s)
				tmp.set(id, cluster_id);
			cluster_id++;
		}
		
		for(int i = 0;i < clusterOrderId.size();i++)
			result.add(tmp.get(query.get(clusterOrderId.get(i))));
		
		FileUtil.saveCluster(label,"./label.txt");
		FileUtil.saveCluster(result, "./result.txt");
		
		List<Double> r = RandIndex.cal(label, result);
		System.out.println("Purity = "
				+ Purity.cal(label, result) + ", RandIndex = " + r.get(0)
				+ " ,P = " + r.get(1) + ", R = " + r.get(2) + ", F = "
				+ r.get(3) + ", NMI = " + NMI.cal(label, result));
	}

	public static void main(String[] args) {
		test();
	}
}
