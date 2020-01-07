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
package com.wl4g.devops.iam.common.authz.permission;

public class GenericWildcardPermissionTests {

	public static void main(String[] args) {
		test1();
		test2();
		test3();
		test4();
		test5();
		test6();
		test7();
		test8();
		test9();
		test10();
	}

	public static void test1() {
		EnhancedWildcardPermission define1 = new EnhancedWildcardPermission("ci,ci:list");
		EnhancedWildcardPermission own1 = new EnhancedWildcardPermission("ci");
		System.out.println("test1=" + own1.implies(define1));// false
	}

	public static void test2() {
		EnhancedWildcardPermission define1 = new EnhancedWildcardPermission("ci,ci:task");
		EnhancedWildcardPermission own1 = new EnhancedWildcardPermission("ci:task");
		System.out.println("test2=" + own1.implies(define1));// false
	}

	public static void test3() {
		EnhancedWildcardPermission define1 = new EnhancedWildcardPermission("ci");
		EnhancedWildcardPermission own1 = new EnhancedWildcardPermission("ci,ci:task");
		System.out.println("test3=" + own1.implies(define1));// true
	}

	public static void test4() {
		EnhancedWildcardPermission define1 = new EnhancedWildcardPermission("ci:task");
		EnhancedWildcardPermission own1 = new EnhancedWildcardPermission("ci");
		System.out.println("test4=" + own1.implies(define1));// false
	}

	public static void test5() {
		EnhancedWildcardPermission define1 = new EnhancedWildcardPermission("ci,ci:*");
		EnhancedWildcardPermission own1 = new EnhancedWildcardPermission("ci,ci:task");
		System.out.println("test5=" + own1.implies(define1));// true
	}

	public static void test6() {
		try {
			// TODO unsuppost can not contain *
			EnhancedWildcardPermission define1 = new EnhancedWildcardPermission("ci,ci*");
			EnhancedWildcardPermission own1 = new EnhancedWildcardPermission("ci");
			System.out.println("test6=" + own1.implies(define1));// false
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test7() {
		try {
			EnhancedWildcardPermission define1 = new EnhancedWildcardPermission("ci,ci:**");
			EnhancedWildcardPermission own1 = new EnhancedWildcardPermission("ci,ci:task:list");
			System.out.println("test7=" + own1.implies(define1));// true
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test8() {
		try {
			// TODO unsuppost , just one *
			EnhancedWildcardPermission define1 = new EnhancedWildcardPermission("ci,ci:*:*");
			EnhancedWildcardPermission own1 = new EnhancedWildcardPermission("ci,ci:task:list");
			System.out.println("test8=" + own1.implies(define1));// false
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void test9() {
		EnhancedWildcardPermission define1 = new EnhancedWildcardPermission("ci,ci:*");
		EnhancedWildcardPermission own1 = new EnhancedWildcardPermission("ci");
		System.out.println("test9=" + own1.implies(define1));// false
	}

	public static void test10() {
		try {
			// TODO unsuppost , just suppost at last
			EnhancedWildcardPermission define1 = new EnhancedWildcardPermission("ci,**:ci");
			EnhancedWildcardPermission own1 = new EnhancedWildcardPermission("ci,ci:task:list");
			System.out.println("test10=" + own1.implies(define1));// false
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}