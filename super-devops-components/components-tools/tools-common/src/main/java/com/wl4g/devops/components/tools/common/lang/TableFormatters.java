/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.components.tools.common.lang;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Table grid formatter.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月31日
 * @since
 */
public class TableFormatters {
	public static final int ALIGN_LEFT = 1;// 左对齐
	public static final int ALIGN_RIGHT = 2;// 右对齐
	public static final int ALIGN_CENTER = 3;// 居中对齐

	private int align = ALIGN_CENTER;// 默认居中对齐
	private boolean equilong = false;// 默认不等宽
	private int padding = 1;// 左右边距默认为1
	private char h = '-';// 默认水平分隔符
	private char v = '|';// 默认竖直分隔符
	private char o = '+';// 默认交叉分隔符
	private char s = ' ';// 默认空白填充符
	private List<String[]> data;// 数据

	private TableFormatters() {
	}

	public static TableFormatters build(String[][] data) {
		TableFormatters self = new TableFormatters();
		self.data = new ArrayList<>(Arrays.asList(data));
		return self;
	}

	/**
	 * Chain call entry method, T can be String [], List < String >, any entity
	 * class can not be overloaded due to different Java generics, so here we
	 * write if instanceof for type determination.
	 * 
	 * @param data
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> TableFormatters build(List<T> data) {
		TableFormatters self = new TableFormatters();
		self.data = new ArrayList<>();
		if (data.size() <= 0)
			throw new RuntimeException("数据源至少得有一行吧");
		Object obj = data.get(0);

		if (obj instanceof String[]) {// 如果泛型为String数组，则直接设置
			self.data = (List<String[]>) data;
		} else if (obj instanceof List) {// 如果泛型为List，则把list中的item依次转为String[]，再设置
			int length = ((List) obj).size();
			for (Object item : data) {
				List<String> col = (List<String>) item;
				if (col.size() != length)
					throw new RuntimeException("数据源每列长度必须一致");
				self.data.add(col.toArray(new String[length]));
			}
		} else {// 如果泛型为实体类，则利用反射获取get方法列表，从而推算出属性列表。
				// 根据反射得来的属性列表设置表格第一行thead
			List<Col> colList = getColList(obj);
			String[] header = new String[colList.size()];
			for (int i = 0; i < colList.size(); i++) {
				header[i] = colList.get(i).colName;
			}
			self.data.add(header);
			// 利用反射调用相应get方法获取属性值来设置表格tbody
			for (int i = 0; i < data.size(); i++) {
				String[] item = new String[colList.size()];
				for (int j = 0; j < colList.size(); j++) {
					String value = null;
					try {
						Object invoke = obj.getClass().getMethod(colList.get(j).getMethodName).invoke(data.get(i));
						if (null == invoke) {
							continue;
						}
						value = null == invoke ? "" : invoke.toString();
						// value =
						// obj.getClass().getMethod(colList.get(j).getMethodName).invoke(data.get(i)).toString();
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
					item[j] = value == null ? "null" : value;
				}
				self.data.add(item);
			}
		}
		return self;
	}

	private static class Col {
		private String colName;// 列名
		private String getMethodName;// get方法名
	}

	/**
	 * Getting get method name and attribute name by reflection
	 *
	 * @return
	 */
	private static List<Col> getColList(Object obj) {
		List<Col> colList = new ArrayList<>();
		Method[] methods = obj.getClass().getMethods();
		for (Method m : methods) {
			StringBuilder getMethodName = new StringBuilder(m.getName());
			if (getMethodName.substring(0, 3).equals("get") && !m.getName().equals("getClass")) {
				Col col = new Col();
				col.getMethodName = getMethodName.toString();
				char first = Character.toLowerCase(getMethodName.delete(0, 3).charAt(0));
				getMethodName.delete(0, 1).insert(0, first);
				col.colName = getMethodName.toString();
				colList.add(col);
			}
		}
		return colList;
	}

	/**
	 * Gets the number of characters occupied by a string
	 *
	 * @param str
	 * @return
	 */
	private int getStringCharLength(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");// 利用正则找到中文
		if (StringUtils.isBlank(str)) {
			return 4;
		}
		Matcher m = p.matcher(str);
		int count = 0;
		while (m.find()) {
			count++;
		}
		return str.length() + count;
	}

	/**
	 * Longitudinal traversal to obtain the length of each column of data
	 * 
	 * @return
	 */
	private int[] getColLengths() {
		int[] result = new int[data.get(0).length];
		for (int x = 0; x < result.length; x++) {
			int max = 0;
			for (int y = 0; y < data.size(); y++) {
				int len = getStringCharLength(data.get(y)[x]);
				if (len > max) {
					max = len;
				}
			}
			result[x] = max;
		}
		if (equilong) {// 如果等宽表格
			int max = 0;
			for (int len : result) {
				if (len > max)
					max = len;
			}
			for (int i = 0; i < result.length; i++) {
				result[i] = max;
			}
		}
		return result;
	}

	/**
	 * Get the table string
	 * 
	 * @return
	 */
	public String getTableString() {
		StringBuilder sb = new StringBuilder();
		int[] colLengths = getColLengths();// 获取每列文字宽度
		StringBuilder line = new StringBuilder();// 表格横向分隔线
		line.append(o);
		for (int len : colLengths) {
			int allLen = len + padding * 2;// 还需要加上边距和分隔符的长度
			for (int i = 0; i < allLen; i++) {
				line.append(h);
			}
			line.append(o);
		}
		sb.append(line).append("\r\n");
		for (int y = 0; y < data.size(); y++) {
			sb.append(v);
			for (int x = 0; x < data.get(y).length; x++) {
				String cell = data.get(y)[x];
				switch (align) {
				case ALIGN_LEFT:
					for (int i = 0; i < padding; i++)
						sb.append(s);
					sb.append(cell);
					for (int i = 0; i < colLengths[x] - getStringCharLength(cell) + padding; i++)
						sb.append(s);
					break;
				case ALIGN_RIGHT:
					for (int i = 0; i < colLengths[x] - getStringCharLength(cell) + padding; i++)
						sb.append(s);
					sb.append(cell);
					for (int i = 0; i < padding; i++)
						sb.append(s);
					break;
				case ALIGN_CENTER:
					int space = colLengths[x] - getStringCharLength(cell);
					int left = space / 2;
					int right = space - left;
					for (int i = 0; i < left + padding; i++)
						sb.append(s);
					sb.append(cell);
					for (int i = 0; i < right + padding; i++)
						sb.append(s);
					break;
				}
				sb.append(v);
			}
			sb.append("\r\n");
			sb.append(line).append("\r\n");
		}
		return sb.toString();
	}

	/**
	 * Print tabular data.
	 */
	public void print() {
		System.out.println(getTableString());
	}

	public TableFormatters setAlign(int align) {
		this.align = align;
		return this;
	}

	public TableFormatters setEquilong(boolean equilong) {
		this.equilong = equilong;
		return this;
	}

	public TableFormatters setPadding(int padding) {
		this.padding = padding;
		return this;
	}

	public TableFormatters setH(char h) {
		this.h = h;
		return this;
	}

	public TableFormatters setV(char v) {
		this.v = v;
		return this;
	}

	public TableFormatters setO(char o) {
		this.o = o;
		return this;
	}

	public TableFormatters setS(char s) {
		this.s = s;
		return this;
	}

	static class User {
		String username;
		String password;
		String name;

		User(String username, String password, String name) {
			this.username = username;
			this.password = password;
			this.name = name;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static void main(String[] args) {
		List<String[]> data1 = new ArrayList<>();
		data1.add(new String[] { "用户名", "密码", "姓名" });
		data1.add(new String[] { "xiaoming", "xm123", "小明" });
		data1.add(new String[] { "xiaohong", "xh123", "小红" });
		TableFormatters.build(data1).print();

		List<List<String>> data2 = new ArrayList<>();
		data2.add(new ArrayList<>());
		data2.add(new ArrayList<>());
		data2.add(new ArrayList<>());
		data2.get(0).add("用户名");
		data2.get(0).add("密码");
		data2.get(0).add("姓名");
		data2.get(1).add("xiaoming");
		data2.get(1).add("xm123");
		data2.get(1).add("小明");
		data2.get(2).add("xiaohong");
		data2.get(2).add("xh123");
		data2.get(2).add("小红");
		TableFormatters.build(data2).setAlign(TableFormatters.ALIGN_LEFT).setPadding(5).setEquilong(true).print();

		List<User> data3 = new ArrayList<>();
		data3.add(new User("xiaoming", "xm123", "小明"));
		data3.add(new User("xiaohong", "xh123", "小红"));
		TableFormatters.build(data3).setH('=').setV('!').print();
	}

}