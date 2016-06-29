package com.whck.msf.excel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.whck.msf.panel.Constants;

public class CVS {

	public static <T> void saveFile(List<T> list, String outFile) throws IOException {

		outFile+=".xls";
		if (list == null || list.isEmpty()) {
			return;
		}

		if (StringUtils.isEmpty(outFile)) {
			throw new IllegalArgumentException("outfile is null");
		}

		boolean isFirst = true;
		BufferedWriter out = null;

		try {
			out = new BufferedWriter(new FileWriter(outFile));
			for (T t : list) {
				StringBuilder sb1 = new StringBuilder();
				StringBuilder sb2 = new StringBuilder();
				Class clazz = (Class) t.getClass();
				Field[] fs = clazz.getDeclaredFields();

				for (int i = 0; i < fs.length; i++) {
					Field f = fs[i];
					f.setAccessible(true);
					try {
						if (isFirst) {
							sb1.append(Constants.en_cn_map.get(f.getName()));
							sb1.append(",");
						}

						Object val = f.get(t);
						if (val == null) {
							sb2.append("");
						} else {
							sb2.append(val.toString());
						}
						sb2.append(",");
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}

				if (isFirst) {
					out.write(sb1.toString());
					isFirst = false;
					out.newLine();
				}
				out.write(sb2.toString());
				out.newLine();
			}

		} catch (IOException e1) {
			throw e1;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e2) {
				throw e2;
			}
		}
	}
}
