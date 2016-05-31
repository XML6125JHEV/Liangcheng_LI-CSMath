package cn.edu.zju.zhouyu.mdl.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PageDao {
	
	public static List<String> getUrls(int hostId) {
		String sql = "select url from page where host_id = ?";
		List<String> htmls = new ArrayList<String>();
		try {
			Connection conn = DBUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, hostId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) 
				htmls.add(rs.getString(1));
			DBUtils.free(rs, ps, conn);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return htmls;
	}
	
	public static List<Integer> getClusterOrderId(int hostId){
		String sql = "select id from page where host_id = ? order by cluster_id";
		List<Integer> ids = new ArrayList<Integer>();
		try {
			Connection conn = DBUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, hostId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) 
				ids.add(rs.getInt(1));
			DBUtils.free(rs, ps, conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ids;
	}
	
	public static List<Integer> getLabels(int hostId){
		String sql = "select cluster_id from page where host_id = ? order by cluster_id";
		List<Integer> labels = new ArrayList<Integer>();
		try {
			Connection conn = DBUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, hostId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) 
				labels.add(rs.getInt(1));
			DBUtils.free(rs, ps, conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return labels;
	}
	
	public static List<Integer> getIds(int hostId){
		String sql = "select id from page where host_id = ?";
		List<Integer> ids = new ArrayList<Integer>();
		try {
			Connection conn = DBUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, hostId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) 
				ids.add(rs.getInt(1));
			DBUtils.free(rs, ps, conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ids;
	}
	
	public static int cntFail(int hostId,int ruleId){
		String sql = "select count(*) from checkitem where host_id = ? and rule_id = ? and result = 'FAIL'";
		int result = 0;
		try {
			Connection conn = DBUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, hostId);
			ps.setInt(2, ruleId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) 
				result = rs.getInt(1);
			DBUtils.free(rs, ps, conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<Integer> getPageId(int hostId){
		String sql = "select id from page where host_id = ?";
		List<Integer> ids = new ArrayList<Integer>();
		try {
			Connection conn = DBUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, hostId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) 
				ids.add(rs.getInt(1));
			DBUtils.free(rs, ps, conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ids;
	}
	
	public static int cnt(int hostId,int ruleId){
		String sql = "select count(*) from checkitem where host_id = ? and rule_id = ?";
		int result = 0;
		try {
			Connection conn = DBUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, hostId);
			ps.setInt(2, ruleId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) 
				result = rs.getInt(1);
			DBUtils.free(rs, ps, conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Set<Integer> cntFailPageId(int hostId,int ruleId){
		String sql = "select page_id from checkitem where host_id = ? and rule_id = ? and result = 'FAIL'";
		Set<Integer> result = new HashSet<Integer>();
		try {
			Connection conn = DBUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, hostId);
			ps.setInt(2, ruleId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) 
				result.add(rs.getInt(1));
			DBUtils.free(rs, ps, conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Set<Integer> cntAllPageId(int hostId,int ruleId){
		String sql = "select page_id from checkitem where host_id = ? and rule_id = ?";
		Set<Integer> result = new HashSet<Integer>();
		try {
			Connection conn = DBUtils.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, hostId);
			ps.setInt(2, ruleId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) 
				result.add(rs.getInt(1));
			DBUtils.free(rs, ps, conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void insertUrl(String idxFile){
		List<String> urls = new ArrayList<String>();
		String content = FileUtil.readFile(idxFile);
		for(String url : content.split("\n"))
			urls.add(url);
		String sql = "insert into page (url,host_id) values (?,?)";
		try {
			Connection con = DBUtils.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			for(int i = 0;i < urls.size();i++){
				ps.setString(1, urls.get(i));
				ps.setInt(2, 9);
				ps.executeUpdate();
			}
			// log.info(insertHTML);
			DBUtils.free(ps, con);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.toString());
		} finally {
			
		}
	}
	
	public static void main(String[] args){
		PageDao.insertUrl("./www.sinopec.com.txt");
	}
}
