package cn.edu.zju.zhouyu.mdl.util;

import java.util.HashSet;
import java.util.Set;

public class UrlSplit {
	
	public static Set<String> split(String url){
		Set<String> result = new HashSet<String>();
		String[] terms = url.split("//")[1].split("/");
		for(int i = 0;i < terms.length;i++){
			if(i == terms.length - 1 && terms[i].contains("?")){
				String newTerm = terms[i].split("\\?")[0];
				String args = terms[i].split("\\?")[1];
				result.add("POS" + (i + 1) + "=" + newTerm);
				for(String arg : args.split("&")){
					result.add(arg);
					result.add(arg.split("=")[0]);
				}
			}
			else
				result.add("POS" + (i + 1) + "=" + terms[i]);
		}
		return result;
	}
	
	public static void main(String[] args){
		System.out.println(split("http://site.com/z.shtml?1"));
	}
}
