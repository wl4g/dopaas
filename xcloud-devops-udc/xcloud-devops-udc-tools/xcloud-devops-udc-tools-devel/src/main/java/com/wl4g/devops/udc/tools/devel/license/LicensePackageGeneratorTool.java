/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.udc.tools.devel.license;

import java.io.*;

import static org.apache.commons.lang3.StringUtils.endsWithAny;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.apache.commons.lang3.SystemUtils;

/**
 * Development coding license comments generator tool.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-19
 * @since
 */
public final class LicensePackageGeneratorTool {

	static String DEFAULT_COPYRIGHT = "/*\n * Copyright (C) 2017 ~ 2025 the original author or authors.\n * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.\n * All rights reserved.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *      http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n * \n * Reference to website: http://wl4g.com\n */";
	static String DEFUALT_SELECT_PATH_TIP = "Please select your need comment file or directory";
	static int count = 0;

	static void commentFile(File file, String comments) {
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				String[] children = file.list();
				for (int i = 0; i < children.length; i++) {
					File child = new File(file.getPath() + System.getProperty("file.separator") + children[i]);
					commentFile(child, comments);
				}
			} else {
				/**
				 * <pre>
				 *  ".clj"  Clojure Source Code
				 *  ".cljs" ClojureScript Source Code
				 *  ".cljc" Clojure Macros Code
				 *  ".cljx" Clojure Portable Code
				 *  ".edn"  Clojure Extensible Data Notation File
				 * </pre>
				 */
				if (endsWithAny(file.getName().toLowerCase(), ".java", ".scala", ".groovy", ".kt", ".clj", ".cljs", ".cljc",
						".cljx", ".edn")) {
					System.out.println(file.getName());
					count++;
					try {
						RandomAccessFile raFile = new RandomAccessFile(file, "rw");
						byte[] content = new byte[(int) raFile.length()];
						raFile.readFully(content);
						String all = new String(content);
						all = all.trim();
						while (all.startsWith("\n")) {
							all = all.substring(1);
						}
						if (all.indexOf("package") != -1) {
							all = all.substring(all.indexOf("package"));
						}
						if (all.indexOf("import") != -1) {
							all = all.substring(all.indexOf("package"));
						}
						all = comments + "\n" + all;
						raFile.close();
						FileWriter writer = new FileWriter(file);
						writer.write(all);
						writer.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	public static void main(String[] args) {
		JFrame frame = new JFrame("Simple Lisences Generator v0.1.1 <Wanglsir@gmail.com>");
		frame.setLocation(480, 250);
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});

		// Center panel
		JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		JPanel pathPane = new JPanel(new BorderLayout());
		JTextField txtPath = new JTextField();
		txtPath.setText(SystemUtils.USER_DIR);
		pathPane.add(txtPath, BorderLayout.CENTER);
		JButton btnSelectPath = new JButton("Browser...");
		btnSelectPath.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser(SystemUtils.USER_DIR);
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int returnVal = chooser.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				txtPath.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});
		btnSelectPath.setMnemonic('B');
		pathPane.add(btnSelectPath, BorderLayout.EAST);
		centerPanel.add(pathPane, BorderLayout.NORTH);
		JTextArea txtComments = new JTextArea();
		txtComments.setText(DEFAULT_COPYRIGHT);
		centerPanel.add(new JScrollPane(txtComments), BorderLayout.CENTER);

		// Content panel
		JPanel contentPanel = (JPanel) frame.getContentPane();
		contentPanel.add(centerPanel, BorderLayout.CENTER);

		// Lisence selection.
		JComboBox selectLisence = new JComboBox();
		selectLisence.setToolTipText("使用内置Lisence");
		LisenceRegistryFactory.getCopyrights().forEach((name, content) -> selectLisence.addItem(name));
		contentPanel.add(selectLisence, BorderLayout.PAGE_START);
		selectLisence.addActionListener(e -> {
			String name = selectLisence.getSelectedItem().toString();
			txtComments.setText(LisenceRegistryFactory.getCopyrights().get(name));
		});

		// Lisence file type.
		// JComboBox selectFileType = new JComboBox();
		// selectFileType.setToolTipText("文件类型");
		// contentPanel.add(selectFileType, BorderLayout.NORTH);

		// Buttom panel
		JPanel buttomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		JButton btnOK = new JButton("Generate Go");
		btnOK.addActionListener(e -> {
			String path = txtPath.getText();
			File file = new File(path);
			if (!file.exists()) {
				JOptionPane.showMessageDialog(frame, "Path '" + path + "' not exist.", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				commentFile(file, txtComments.getText());
				JOptionPane.showMessageDialog(frame, "Finish, total " + count + " files are processed.", "Information",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnOK.setMnemonic('G');
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(e -> {
			System.exit(0);
		});
		btnClose.setMnemonic('C');
		buttomPanel.add(btnOK);
		buttomPanel.add(btnClose);
		contentPanel.add(buttomPanel, BorderLayout.SOUTH);

		frame.setSize(800, 650);
		frame.show(true);
	}

}