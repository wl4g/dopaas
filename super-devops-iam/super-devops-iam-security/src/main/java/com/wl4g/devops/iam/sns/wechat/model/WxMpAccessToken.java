// package com.wl4g.devops.iam.sns.wechat.model;
//
// import org.springframework.beans.BeanUtils;
//
// import com.wl4g.devops.common.utils.serialize.JacksonUtils;
//
/// **
// * WeChat public platform access token model
// *
// * @author wangl.sir
// * @version v1.0 2019年2月18日
// * @since
// */
// public class WxMpAccessToken extends WxBasedAccessToken {
// private static final long serialVersionUID = 6525294825751214763L;
//
// @SuppressWarnings("unchecked")
// @Override
// public WxMpAccessToken build(String message) {
// WxMpAccessToken at = JacksonUtils.parseJSON(message, this.getClass());
// BeanUtils.copyProperties(at, this);
// return this;
// }
//
// }
