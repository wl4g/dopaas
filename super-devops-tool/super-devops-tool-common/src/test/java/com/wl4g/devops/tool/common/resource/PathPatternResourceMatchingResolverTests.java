package com.wl4g.devops.tool.common.resource;

import com.wl4g.devops.tool.common.resource.resolver.GenericPathPatternResourceMatchingResolver;

public class PathPatternResourceMatchingResolverTests {

	public static void main(String[] args) throws Exception {
		GenericPathPatternResourceMatchingResolver resolver = new GenericPathPatternResourceMatchingResolver();
		System.out.println("start scanning ...");
		for (StreamResource r : resolver.getResources("com/wl4g/devops/tool/common/resource/**/*.*")) {
			System.out.println(r);
		}

	}

}
