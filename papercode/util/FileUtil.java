package cn.edu.zju.zhouyu.mdl.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @ClassName: FileUtil
 * @Description: File util class
 * @author zhouyu
 * @date 2013-11-27 下午1:29:44
 * @version V0.1 
 *
 */
public class FileUtil {
	public static String readFile(String path) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)), "UTF-8"));
			String line = "";
			while ((line = br.readLine()) != null)
				sb.append(line + "\n");
			br.close();
		} catch (Exception e) {
			System.err.println("Read file: " + path + " fails!\n");
		}
		return sb.toString();
	}
	
	public static List<Integer> readLabel(String name){
		List<Integer> label = new ArrayList<Integer>();
		try {
			Scanner scanner = new Scanner(new File(name));
			int i = 0, j = 0;
			while (scanner.hasNextInt()) {
				label.add(scanner.nextInt());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return label;
	}

	public static void writeFile(String path, String content) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)));
			out.println(content);
			out.close();
		} catch (Exception e) {
			System.err.println("Write file: " + path + " falis\n");
		}
	}
	
	public static double[][] loadMatrix(String name, int N) {
		double[][] matrix = new double[N][N];
		try {
			Scanner scanner = new Scanner(new File(name));
			int i = 0, j = 0;
			while (scanner.hasNextDouble()) {
				if (j < N) {
					matrix[i][j++] = scanner.nextDouble();
				} else {
					j = 0;
					matrix[++i][j++] = scanner.nextDouble();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matrix;
	}

	public static void saveCluster(List<Integer> cluster, String name) {
		try {
			File f = new File("./" + name);
			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			for (int i = 0; i < cluster.size(); i++)
				output.write(cluster.get(i) + "\n");
			output.flush();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveMatrix(double[][] matrix, String name) {
		try {
			File f = new File("./" + name);
			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix.length; j++) {
					output.write(matrix[i][j] + "\t");
				}
				output.write("\n");
				output.flush();
			}
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		FileUtil.writeFile("D:\\1.txt",
				FileUtil.readFile("D:\\Dropbox\\ѧϰ\\code\\README.md"));
	}
}
