package com.wl4g.devops.tool.common.resource;

import com.wl4g.devops.tool.common.resource.resolver.PathPatternResourceMatchingResolver;

public class PathPatternResourceMatchingResolverTests {

	public static void main(String[] args) throws Exception {
		PathPatternResourceMatchingResolver resolver = new PathPatternResourceMatchingResolver();
		System.out.println("start scanning ...");
		for (Resource r : resolver.getResources("com/wl4g/devops/tool/common/resource/**/*.*")) {
			System.out.println(r);
		}

	}

}
