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
package com.wl4g.devops.common.utils.lang;

import static java.util.concurrent.ThreadLocalRandom.current;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

public class RandomDistributionTests {

	public static void main(String[] args) throws Exception {
		// gaussianRandomStreamTest1(args);
		simpleNormalDistributionTest2();
		multivariateNormalDistributionTest3();
	}

	public static void gaussianRandomStreamTest1(String[] args) throws Exception {
		System.out.println("=========gaussianRandomStreamTest1===========");
		DoubleStream gaussianStream = Stream.generate(current()::nextGaussian).mapToDouble(e -> e);
		LinkedHashMap<Range, Integer> gaussianRangeCountMap = gaussianStream.filter(e -> (e >= -1.0 && e < 1.0)).limit(1000000)
				.boxed().map(Ranges::of)
				.collect(Ranges::emptyRangeCountMap, (m, e) -> m.put(e, m.get(e) + 1), Ranges::mergeRangeCountMaps);

		gaussianRangeCountMap.forEach((k, v) -> System.out.println(k.from() + "\t" + v));
	}

	public static void simpleNormalDistributionTest2() {
		System.out.println("=========simpleNormalDistributionTest2===========");
		NormalDistribution nd = new NormalDistribution(0, 1.44);
		System.out.println(nd.probability(-3, 3));
		System.out.println(nd.probability(-1, 1));
		System.out.println(nd.probability(-0.1, 0.1));
		System.out.println(nd.probability(-0.1, 0));
		System.out.println(nd.probability(0, 0.1));
		System.out.println("-----------------------");
		System.out.println(nd.density(-0.1));
		System.out.println(nd.density(0));
		System.out.println(nd.density(0.1));
		System.out.println(nd.density(0.2));
		System.out.println(nd.density(0.3));
		System.out.println(nd.density(0.4));
		System.out.println(nd.density(0.5));
		System.out.println(nd.density(0.6));
		System.out.println(nd.density(0.7));
		System.out.println(nd.density(1));
		System.out.println(nd.density(2));
		System.out.println(nd.density(3));
	}

	public static void multivariateNormalDistributionTest3() {
		System.out.println("=========multivariateNormalDistributionTest3===========");
		final double[] mu = { 0, 0 };
		final double[][] sigma = { { 2, -1.1 }, { -1.1, 2 } };
		final MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(mu, sigma);
		System.out.println(mnd.getCovariances().getEntry(1, 0));
		System.out.println(mnd.density(new double[] { 1d, 1d }));
		System.out.println(mnd.density(new double[] { 1d, 2d }));
		System.out.println(mnd.density(new double[] { 1d, 3d }));
		System.out.println(mnd.density(new double[] { 2d, 2d }));
		System.out.println(mnd.density(new double[] { 2d, 3d }));
		System.out.println(mnd.density(new double[] { -2d, 3d }));
		System.out.println(mnd.density(new double[] { -2d, -1d }));
		System.out.println(mnd.density(new double[] { 1d, 2d }));
		System.out.println(mnd.density(new double[] { 250d, 150d }));
	}

	public static class Range {
		private final double from;
		private final double to;

		public Range(double from, double to) {
			this.from = from;
			this.to = to;
		}

		public double from() {
			return from;
		}

		public double to() {
			return to;
		}

		@Override
		public String toString() {
			return "From: " + from + " To: " + to;

		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Range range = (Range) o;

			if (Double.compare(range.from, from) != 0)
				return false;
			if (Double.compare(range.to, to) != 0)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result;
			long temp;
			temp = Double.doubleToLongBits(from);
			result = (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(to);
			result = 31 * result + (int) (temp ^ (temp >>> 32));
			return result;
		}
	}

	public static class Ranges {
		private static LinkedHashMap<Integer, Range> rangeMap = new LinkedHashMap<>();
		static {
			rangeMap.put(-10, new Range(-1.0, -0.9));
			rangeMap.put(-9, new Range(-0.9, -0.8));
			rangeMap.put(-8, new Range(-0.8, -0.7));
			rangeMap.put(-7, new Range(-0.7, -0.6));
			rangeMap.put(-6, new Range(-0.6, -0.5));
			rangeMap.put(-5, new Range(-0.5, -0.4));
			rangeMap.put(-4, new Range(-0.4, -0.3));
			rangeMap.put(-3, new Range(-0.3, -0.2));
			rangeMap.put(-2, new Range(-0.2, -0.1));
			rangeMap.put(-1, new Range(-0.1, 0.0));
			rangeMap.put(0, new Range(0.0, 0.1));
			rangeMap.put(1, new Range(0.1, 0.2));
			rangeMap.put(2, new Range(0.2, 0.3));
			rangeMap.put(3, new Range(0.3, 0.4));
			rangeMap.put(4, new Range(0.4, 0.5));
			rangeMap.put(5, new Range(0.5, 0.6));
			rangeMap.put(6, new Range(0.6, 0.7));
			rangeMap.put(7, new Range(0.7, 0.8));
			rangeMap.put(8, new Range(0.8, 0.9));
			rangeMap.put(9, new Range(0.9, 1.0));
		}

		public static Range of(double d) {
			int key = (int) Math.floor(d * 10);
			return rangeMap.get(key);
		}

		public static LinkedHashMap<Range, Integer> emptyRangeCountMap() {
			LinkedHashMap<Range, Integer> rangeCountMap = new LinkedHashMap<>();
			for (Range range : rangeMap.values()) {
				rangeCountMap.put(range, 0);
			}
			return rangeCountMap;
		}

		public static void mergeRangeCountMaps(Map<Range, Integer> map1, Map<Range, Integer> map2) {
			for (Range range : rangeMap.values()) {
				map1.put(range, map1.get(range) + map2.get(range));
			}
		}
	}

}