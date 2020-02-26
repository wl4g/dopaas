package com.wl4g.devops.common.logging;

import org.slf4j.Logger;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static com.wl4g.devops.common.logging.AbstractTrackingMDCFilter.TraceMDCConstants.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 在logback日志输出中增加MDC参数选项 注意，此Filter尽可能的放在其他Filter之前</br>
 * 
 * 默认情况下，将会把“requestId”、“requestSeq”、“localIp”、“timestamp”、“uri”添加到MDC上下文中。
 * 1）其中requestId，requestSeq为调用链跟踪使用，开发者不需要手动修改他们。
 * 2）localIp为当前web容器的宿主机器的本地IP，为内网IP。
 * 3）timestamp为请求开始被servlet处理的时间戳，设计上为被此Filter执行的开始时间，可以使用此值来判断内部程序执行的效率。
 * 4）uri为当前request的uri参数值。
 * 
 * 我们可以在logback.xml文件的layout部分，通过%X{key}的方式使用MDC中的变量
 */
public abstract class AbstractTrackingMDCFilter implements Filter {

	public static final String KEY_HEADER_REQUEST_ID = "X-Request-ID";
	public static final String KEY_HEADER_REQUEST_SEQ = "X-Request-Seq";

	protected Logger log = getLogger(getClass());

	/**
	 * Whether to enable the headers mapping. for example:
	 * <b>%X{_C_:JSESSIONID}</b></br>
	 */
	private boolean mappedCookies;

	/**
	 * Whether to enable the headers mapping. for example:
	 * <b>%X{_H_:X-Forwarded-For}</b></br>
	 */
	private boolean mappedHeaders;

	/**
	 * Whether to enable the headers mapping. for example:
	 * <b>%X{_P_:userId}</b></br>
	 */
	private boolean mappedParameters;

	private String localIp;// 本机IP

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		mappedCookies = Boolean.valueOf(filterConfig.getInitParameter("mappedCookies"));
		mappedHeaders = Boolean.valueOf(filterConfig.getInitParameter("mappedHeaders"));
		mappedParameters = Boolean.valueOf(filterConfig.getInitParameter("mappedParameters"));
		// getLocalIp
		localIp = getLocalIp();
	}

	private String getLocalIp() {
		try {
			// 一个主机有多个网络接口
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = netInterfaces.nextElement();
				// 每个网络接口,都会有多个"网络地址",比如一定会有loopback地址,会有siteLocal地址等.以及IPV4或者IPV6
				// .
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();
					// get only :172.*,192.*,10.*
					if (address.isSiteLocalAddress() && !address.isLoopbackAddress()) {
						return address.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			//
		}
		return null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest hsr = (HttpServletRequest) request;
		try {
			setMDCLogDyeing(hsr);
		} catch (Exception e) {
		}

		try {
			chain.doFilter(request, response);
		} finally {
			MDC.clear(); // must
		}

	}

	protected void setMDCLogDyeing(HttpServletRequest hsr) {
		MDC.put(KEY_LOCAL_IP, localIp);
		MDC.put(KEY_REQUEST_ID, hsr.getHeader(KEY_HEADER_REQUEST_ID));
		String requestSeq = hsr.getHeader(KEY_HEADER_REQUEST_SEQ);
		if (requestSeq != null) {
			// seq will be like:000, real seq is the number of "0"
			String nextSeq = requestSeq + "0";
			MDC.put(KEY_NEXT_REQUEST_SEQ, nextSeq);
		} else {
			MDC.put(KEY_NEXT_REQUEST_SEQ, "0");
		}
		MDC.put(KEY_REQUEST_SEQ, requestSeq);
		MDC.put(KEY_TIMESTAMP, valueOf(currentTimeMillis()));
		MDC.put(KEY_URI, hsr.getRequestURI());

		if (mappedHeaders) {
			Enumeration<String> e = hsr.getHeaderNames();
			if (e != null) {
				while (e.hasMoreElements()) {
					String header = e.nextElement();
					String value = hsr.getHeader(header);
					MDC.put(KEY_PREFIX_HEADER + header, value);
				}
			}
		}

		if (mappedCookies) {
			Cookie[] cookies = hsr.getCookies();
			if (cookies != null && cookies.length > 0) {
				for (Cookie cookie : cookies) {
					String name = cookie.getName();
					String value = cookie.getValue();
					MDC.put(KEY_PREFIX_COOKIE + name, value);
				}
			}
		}

		if (mappedParameters) {
			Enumeration<String> e = hsr.getParameterNames();
			if (e != null) {
				while (e.hasMoreElements()) {
					String key = e.nextElement();
					String value = hsr.getParameter(key);
					MDC.put(KEY_PREFIX_PARAMETER + key, value);
				}
			}
		}
	}

	@Override
	public void destroy() {
	}

	/**
	 * Tracking log dyeing MDC constants.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年2月26日
	 * @since
	 */
	public static class TraceMDCConstants {

		public static final String KEY_REQUEST_ID = "requestId";

		public static final String KEY_REQUEST_SEQ = "requestSeq";

		// 追踪链下发时，使用的seq，由Filter生成，通常开发者不需要修改它。
		public static final String KEY_NEXT_REQUEST_SEQ = "nextRequestSeq";

		public static final String KEY_LOCAL_IP = "_localIp_";

		public static final String KEY_URI = "_uri_";

		public static final String KEY_TIMESTAMP = "_timestamp_"; // 进入filter的时间戳

		public static final String KEY_PREFIX_COOKIE = "_C_:";

		public static final String KEY_PREFIX_HEADER = "_H_:";

		public static final String KEY_PREFIX_PARAMETER = "_P_:";

	}

}