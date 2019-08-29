package com.wl4g.devops.iam.handler.verification.cumulation;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Verification limiter accumulator
 * 
 * @author wangl.sir
 * @version v1.0 2019年4月19日
 * @since
 */
public interface Cumulator {

	/**
	 * Number of cumulative processing (e.g. to limit the number of login
	 * failures or the number of short messages sent by same source IP requests)
	 * 
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 * @param incrBy
	 *            Step increment value
	 * @return returns the maximum number of times under all constraint
	 *         factors.(e.g: max attempts failed count)
	 */
	default long accumulate(@NotNull List<String> factors, long incrBy) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the cumulative number of failures for the specified condition
	 * 
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 * @return returns the maximum number of times under all constraint
	 *         factors.(e.g: max attempts failed count)
	 */
	default long getCumulative(@NotBlank String factors) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the cumulative number of failures for the specified condition
	 * 
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 * @return returns the maximum number of times under all constraint
	 *         factors.(e.g: max attempts failed count)
	 */
	default long getCumulatives(@NotNull List<String> factors) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Cancel verification code
	 * 
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 */
	default void destroy(@NotNull List<String> factors) {
		throw new UnsupportedOperationException();
	}

}