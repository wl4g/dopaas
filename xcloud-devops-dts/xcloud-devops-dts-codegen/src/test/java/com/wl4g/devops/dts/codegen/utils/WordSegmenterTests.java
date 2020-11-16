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
package com.wl4g.devops.dts.codegen.utils;

import java.util.List;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;

public class WordSegmenterTests {

	public static void main(String[] args) {
		// 移除停用词
		// List<Word> words1 = WordSegmenter.seg("你说什么?敢辱骂主人?主人要好好惩罚你!裤子脱");
		List<Word> words1 = WordSegmenter.seg("统计类型(1.计划完成 2.实际完成) ");
		System.out.println("words1=" + words1);

		// 保留停用词
		List<Word> words2 = WordSegmenter.segWithStopWords("XCloud DevOps这个一站式开发运维平台的作者是Wanglsir");
		System.out.println("words2=" + words2);

		// 对文本进行分词时，可显式指定特定的分词算法，如：
		/*
		 * SegmentationAlgorithm的可选类型为： 正向最大匹配算法：MaximumMatching
		 * 逆向最大匹配算法：ReverseMaximumMatching 正向最小匹配算法：MinimumMatching
		 * 逆向最小匹配算法：ReverseMinimumMatching 双向最大匹配算法：BidirectionalMaximumMatching
		 * 双向最小匹配算法：BidirectionalMinimumMatching
		 * 双向最大最小匹配算法：BidirectionalMaximumMinimumMatching 全切分算法：FullSegmentation
		 * 最少分词算法：MinimalWordCount 最大Ngram分值算法：MaxNgramScore
		 */
		List<Word> words3 = WordSegmenter.seg("XCloud DevOps是一个SaaS级的一站式开发运维平台",
				SegmentationAlgorithm.BidirectionalMaximumMatching);
		System.out.println("words3=" + words3);

		// WordSegmenter.processCommand("d");
	}

}