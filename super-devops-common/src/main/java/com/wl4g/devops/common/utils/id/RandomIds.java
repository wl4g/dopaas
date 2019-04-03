package com.wl4g.devops.common.utils.id;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Random IDS
 * 
 * @author wangl.sir
 * @version v1.0 2019年3月1日
 * @since
 */
public class RandomIds {

	/**
	 * Secure generation of specified minimum length UUID random strings
	 * 
	 * @param minLen
	 * @return
	 */
	public static String genVariableMeaningUUID(int minLen) {
		return genVariableMeaningUUID("g_", minLen);
	}

	/**
	 * Secure generation of specified minimum length UUID random strings
	 * 
	 * @param prefix
	 * @param minLen
	 * @return
	 */
	public static String genVariableMeaningUUID(String prefix, int minLen) {
		// UUID origin
		StringBuffer uuids = new StringBuffer();
		int len = uuids.length();
		while ((len = uuids.length()) <= minLen) {
			// Generate random UUID
			uuids.append(UUID.randomUUID().toString().replaceAll("-", ""));
		}
		Assert.isTrue((minLen < (len - 1)), String.format("Minimum length (%s) greater than UUID length (%s)", minLen, len));

		// Random
		ThreadLocalRandom current = ThreadLocalRandom.current();
		int start = current.nextInt(0, len - minLen);
		int end = current.nextInt(start + minLen, len);
		// Sub random UUID
		String res = uuids.substring(Math.min(start, end), Math.max(start, end));
		// Append prefix
		if (!StringUtils.isEmpty(prefix)) {
			res = prefix + res.substring(2);
		}
		return res;
	}

}
