package com.wl4g.devops.iam.common.web.model;

import static java.util.Objects.nonNull;

import java.io.Serializable;

import static com.wl4g.devops.support.cache.ScanCursor.CursorWrapper.parse;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Session attributes cursor index information.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-06
 * @since
 */
public class CursorIndexModel implements Serializable {
	private static final long serialVersionUID = 8491557330003820999L;

	final public static String KEY_SESSION_INDEX = "index";

	/** Cursor string. */
	private String cursorString = EMPTY;

	/** Cursor has next records. */
	private Boolean hasNext = false;

	public CursorIndexModel() {
		super();
	}

	public CursorIndexModel(String cursorString, Boolean hasNext) {
		setCursorString(cursorString);
		setHasNext(hasNext);
	}

	public String getCursorString() {
		return cursorString;
	}

	public void setCursorString(String cursorString) {
		if (isNotBlank(cursorString)) {
			parse(cursorString); // Check.
			this.cursorString = cursorString;
		}
	}

	public Boolean getHasNext() {
		return hasNext;
	}

	public void setHasNext(Boolean hasNext) {
		if (nonNull(hasNext)) {
			this.hasNext = hasNext;
		}
	}
}
