package com.wl4g.devops.iam.common.web.model;

import javax.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.annotations.Beta;

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Sessions destroy model.
 *
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-31
 * @since
 */
@Beta
public class SessionDestroyModel implements Serializable {
	private static final long serialVersionUID = 2579844578836104918L;

	@NotEmpty
	private List<Serializable> sessionIds = new ArrayList<>(4);

	public List<Serializable> getSessionIds() {
		return sessionIds;
	}

	public void setSessionIds(List<Serializable> sessionIds) {
		if (!isEmpty(sessionIds)) {
			this.sessionIds.addAll(sessionIds);
		}
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}
