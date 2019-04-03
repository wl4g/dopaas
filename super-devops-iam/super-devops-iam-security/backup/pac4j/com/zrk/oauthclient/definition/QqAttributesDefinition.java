package com.zrk.oauthclient.definition;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * 返回数据转化器
 * 
 * @author zrk
 * @date 2016年4月15日 下午5:43:17
 */
public class QqAttributesDefinition extends OAuthAttributesDefinition {

	public static final String ID = "id"; // id。数据库业务id
	public static final String OPEN_ID = "openid"; // openid。
	public static final String NICK_NAME = "nickname"; // 用户在昵称。
	public static final String GENDER = "gender"; // 性别。 如果获取不到则默认返回"男"
	public static final String PROVINCE = "province"; // 省
	public static final String CITY = "city"; // 市
	public static final String FIGUREURL_QQ_1 = "figureurl_qq_1"; // 大小为40×40像素的QQ头像URL。
	public static final String FIGUREURL_QQ_2 = "figureurl_qq_2"; // 大小为100×100像素的QQ头像URL
																	// 不是所有的用户都拥有QQ的100x100的头像，但40x40像素则是一定会有

	public QqAttributesDefinition() {
		addAttribute(ID, Converters.longConverter);
		addAttribute(OPEN_ID, Converters.stringConverter);
		addAttribute(NICK_NAME, Converters.stringConverter);
		addAttribute(GENDER, Converters.stringConverter);
		addAttribute(PROVINCE, Converters.stringConverter);
		addAttribute(CITY, Converters.stringConverter);
		addAttribute(FIGUREURL_QQ_1, Converters.stringConverter);
		addAttribute(FIGUREURL_QQ_2, Converters.stringConverter);
	}
	// {
	// "ret": 0,
	// "msg": "",
	// "is_lost": 0,
	// "nickname": "总有刁民想害朕",
	// "gender": "男",
	// "province": "上海",
	// "city": "杨浦",
	// "year": "1991",
	// "figureurl":
	// "http://qzapp.qlogo.cn/qzapp/101307876/AFF8E78BDA986866CA14ED03D77FF4C1/30",
	// "figureurl_1":
	// "http://qzapp.qlogo.cn/qzapp/101307876/AFF8E78BDA986866CA14ED03D77FF4C1/50",
	// "figureurl_2":
	// "http://qzapp.qlogo.cn/qzapp/101307876/AFF8E78BDA986866CA14ED03D77FF4C1/100",
	// "figureurl_qq_1":
	// "http://q.qlogo.cn/qqapp/101307876/AFF8E78BDA986866CA14ED03D77FF4C1/40",
	// "figureurl_qq_2":
	// "http://q.qlogo.cn/qqapp/101307876/AFF8E78BDA986866CA14ED03D77FF4C1/100",
	// "is_yellow_vip": "0",
	// "vip": "0",
	// "yellow_vip_level": "0",
	// "level": "0",
	// "is_yellow_year_vip": "0"
	// }
}
