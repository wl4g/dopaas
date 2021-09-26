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
package com.wl4g.dopaas.lcdp.tools.devel.translate;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.wl4g.dopaas.lcdp.tools.devel.translate.translator.AliyunTranslator;
import com.wl4g.dopaas.lcdp.tools.devel.translate.translator.GoogleTranslator;

/**
 * {@link CommentTranslationTool}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-09-26 v1.0.0
 * @since v1.0.0
 */
public class CommentTranslationTool {

    private static final long DEPLAY_MS = Long.parseLong(System.getenv().getOrDefault("DEPLAY_MS", "500"));
    private static final ThreadLocal<Pattern> isChinesePattern = new ThreadLocal<>();
    private static final ThreadLocal<Pattern> isAlphanumericPattern = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            System.err.println(String.format("Usage: <fromLang> <toLang> <translator> <fileExtSuffix> <baseDir>\n"
                    + "\n    fromLang, toLang:  Options are e.g: ali(en|zh), google(en|zh-CN)"
                    + "\n    translator:  Options are: ali|google" + "\n    fileExtSuffix:  e.g: java,scala,go"
                    + "\n    baseDir:  e.g: /tmp/example-project", ""));
            System.exit(1);
        }
        String fromLang = args[0];
        String toLang = args[1];
        String translator = args[2];
        String fileExtSuffix = args[3];
        String baseDir = args[4];
        print(fromLang, toLang, translator, fileExtSuffix, baseDir);

        AtomicInteger counter = new AtomicInteger(0);
        process(counter, fromLang, toLang, translator, fileExtSuffix, new File(baseDir));
        System.out.println(String.format("Successful translated files: %s", counter));
    }

    private static void process(AtomicInteger counter, String fromLang, String toLang, String translator, String fileExtSuffix,
            File file) throws Exception {
        List<String> suffixs = Arrays.asList(fileExtSuffix.split(","));
        for (File f : file.listFiles()) {
            String ext = Files.getFileExtension(f.getAbsolutePath());
            if (f.isDirectory()) {
                process(counter, fromLang, toLang, translator, fileExtSuffix, f);
            } else if (f.isFile() && suffixs.contains(ext)) {
                counter.incrementAndGet();
                doProcess(fromLang, toLang, translator, f);
            }
        }
    }

    private static void doProcess(String fromLang, String toLang, String translator, File file) throws Exception {
        System.out.println("Processing: " + file);
        Pattern pattern = getPattern(fromLang, toLang);
        List<String> list = new ArrayList<>();
        for (String line : Files.readLines(file, Charsets.UTF_8)) {
            int start = line.indexOf("//");
            if (start > 0 && pattern.matcher(line).find()) { // Must contain
                                                             // source
                                                             // characters.
                String translated = translate(fromLang, toLang, translator, line.substring(start + 2));
                line = line.substring(0, start).concat("//").concat(translated); // re-Join
                                                                                 // to
                                                                                 // new
                                                                                 // characters
            }
            list.add(line);
            // System.out.println(line);
        }
        try (BufferedWriter writer = Files.newWriter(file, Charsets.UTF_8);) {
            for (String line : list) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private static String translate(String fromLang, String toLang, String translator, String text) throws Exception {
        Thread.sleep(DEPLAY_MS); // 防止翻译服务限流
        if (translator.equalsIgnoreCase("ALI") || translator.equalsIgnoreCase("ALIYUN")) {
            return AliyunTranslator.doTranslate(fromLang, toLang, text);
        } else if (translator.equalsIgnoreCase("GOOGLE")) {
            return GoogleTranslator.doTranslate(fromLang, toLang, text);
        }
        throw new UnsupportedOperationException("Unknown translator of " + translator);
    }

    private static Pattern getPattern(String fromLang, String toLang) {
        if (("zh-CN".equalsIgnoreCase(fromLang) || "zh".equalsIgnoreCase(fromLang)) && "en".equalsIgnoreCase(toLang)) {
            return getChinesePattern();
        } else if (("zh-CN".equalsIgnoreCase(toLang) || "zh".equalsIgnoreCase(toLang)) && "en".equalsIgnoreCase(fromLang)) {
            return getAlphanumericPattern();
        }
        throw new UnsupportedOperationException(String.format(
                "Unknown language from: % to: %s, At present, only [zh-CN|zh] <-> [en] mutual translation is supported", fromLang,
                toLang));
    }

    private static Pattern getChinesePattern() {
        if (isChinesePattern.get() == null) {
            isChinesePattern.set(Pattern.compile("[\u4e00-\u9fcc]+"));
        }
        return isChinesePattern.get();
    }

    private static Pattern getAlphanumericPattern() {
        if (isAlphanumericPattern.get() == null) {
            isAlphanumericPattern.set(Pattern.compile("[a-zA-Z0-9]+"));
        }
        return isAlphanumericPattern.get();
    }

    private static void print(String fromLang, String toLang, String translator, String fileExtSuffix, String baseDir) {
        System.out.println("args:fromLang: " + fromLang);
        System.out.println("args:toLang: " + toLang);
        System.out.println("args:translator: " + translator);
        System.out.println("args:fileExtSuffix: " + fileExtSuffix);
        System.out.println("args:baseDir: " + baseDir);
        System.out.println("env:DEPLAY_MS: " + DEPLAY_MS);
        System.out.println("env:ALIYUN_APP_ID: " + AliyunTranslator.ALIYUN_APP_ID);
        System.out.println("env:ALIYUN_APP_SECRET: " + AliyunTranslator.ALIYUN_APP_SECRET);
    }

}
