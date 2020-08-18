package com.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DealWithCsv {
	public static void main(String[] args) throws IOException {
		List<String> fileList = getAllCsv("csv\\input\\");
		List<AmmeterInfo> dayEle = new ArrayList<>();
		for (String filePath : fileList) {
			// 一天24小时所有水表电力读数
			List<AmmeterInfo> dayDataList = getCsvData(filePath);
			// 日期
			String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.lastIndexOf("."));
			System.out.println(fileName);
			// 按每戶分組
			Map<String, List<AmmeterInfo>> collect = dayDataList.stream()
					.collect(Collectors.groupingBy(AmmeterInfo::getAmmeterID));
			// 每户电力使用量算出
			List<AmmeterInfo> out30MinEle = new ArrayList<>();
			collect.forEach((ammeterID, list) -> {
				// 每30分钟
				Stream.iterate(0, i -> i + 1).limit(list.size() - 1).forEach(i -> {
					AmmeterInfo ammeterInfo = new AmmeterInfo();
					ammeterInfo.setAmmeterID(ammeterID);
					ammeterInfo.setReadTime(list.get(i + 1).getReadTime());
					Long ele = Long.valueOf(list.get(i + 1).getAmmeterReading())
							- Long.valueOf(list.get(i).getAmmeterReading());
					ammeterInfo.setAmmeterReading(ele);
					out30MinEle.add(ammeterInfo);
				});

				// 1天使用量
				AmmeterInfo ammeterInfo2 = new AmmeterInfo();
				ammeterInfo2.setAmmeterID(ammeterID);
				ammeterInfo2.setReadTime(fileName);
				Long max = list.stream().mapToLong(AmmeterInfo -> AmmeterInfo.getAmmeterReading()).max().getAsLong();
				Long min = list.stream().mapToLong(AmmeterInfo -> AmmeterInfo.getAmmeterReading()).min().getAsLong();
				ammeterInfo2.setAmmeterReading(max - min);
				dayEle.add(ammeterInfo2);

			});
			// 30分钟点力量出力
			writeCsv(out30MinEle, fileName + "_" + "30min");
		}
		// 每天电力使用量排序
		List<AmmeterInfo> outDayEle = dayEle.stream().sorted(Comparator.comparing(AmmeterInfo::getAmmeterID, Comparator.reverseOrder())
				.thenComparing(AmmeterInfo::getReadTime, Comparator.reverseOrder()))
		.collect(Collectors.toList());
		// 按天电力量出力
		writeCsv(outDayEle, "DayEle");
	}

	private static void writeCsv(List<AmmeterInfo> out30MinEle, String fileName) {
		File outFile = new File("csv\\output\\" + fileName + ".csv");
		String lineOut = "";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
			for (AmmeterInfo ammeterInfo : out30MinEle) {
				lineOut = ammeterInfo.getAmmeterID() + "," + ammeterInfo.getReadTime() + ","
						+ ammeterInfo.getAmmeterReading().toString();
				writer.write(lineOut);
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static List<AmmeterInfo> getCsvData(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
		String line = "";
		List<AmmeterInfo> lineList = new ArrayList<>();
		AmmeterInfo ammeterInfo = null;
		while ((line = br.readLine()) != null) {
			String[] rowData = line.split(",");
			if ("电表ID".equals(rowData[0])) {
				continue;
			}
			ammeterInfo = new AmmeterInfo();
			ammeterInfo.setAmmeterID(rowData[0]);
			ammeterInfo.setReadTime(rowData[1]);
			ammeterInfo.setAmmeterReading(Long.valueOf(rowData[2]));
			lineList.add(ammeterInfo);
		}
		br.close();
		return lineList;
	}

	/**
	 * 获取目录当前路径下所有csv文件的绝对路径
	 * 
	 * @param path 入力csv保存的路径
	 * @return 所有csv文件的绝对路径
	 */
	private static List<String> getAllCsv(String path) {
		File root = new File(path);
		File[] listFile = root.listFiles();
		List<String> strList = new ArrayList<String>();
		if (listFile == null) {
			return strList;
		}
		for (File file : listFile) {
			if ((file.isFile()) && (".csv"
					.equals(file.getName().substring(file.getName().lastIndexOf("."), file.getName().length())))) {
				strList.add(file.getAbsolutePath());
			}
		}
		return strList;
	}

}
