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
package com.wl4g.devops.iam.sns.wechat.model;

//
// import java.util.ArrayList;
// import java.util.List;
//
// import com.fasterxml.jackson.annotation.JsonProperty;
// import com.wl4g.devops.common.utils.serialize.JacksonUtils;
// import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;
//
/// **
// * WeChat platform user information
// *
// * @author wangl.sir
// * @version v1.0 2019年2月19日
// * @since
// */
// public class WxMpUserInfo extends WxBasedUserInfo implements
// Oauth2UserProfile {
// private static final long serialVersionUID = 843944424065492261L;
//
// @JsonProperty("subscribe")
// private String subscribe;
//
// @JsonProperty("subscribe_time")
// private String subscribeTime;
//
// @JsonProperty("remark")
// private String remark;
//
// @JsonProperty("groupid")
// private String groupId;
//
// @JsonProperty("subscribe_scene")
// private String subscribeScene;
//
// @JsonProperty("qr_scene")
// private String qrScene;
//
// @JsonProperty("qr_scene_str")
// private String qrSceneStr;
//
// @JsonProperty("tagid_list")
// private List<String> tagidList = new ArrayList<>();
//
// public String getSubscribe() {
// return subscribe;
// }
//
// public void setSubscribe(String subscribe) {
// this.subscribe = subscribe;
// }
//
// public String getSubscribeTime() {
// return subscribeTime;
// }
//
// public void setSubscribeTime(String subscribeTime) {
// this.subscribeTime = subscribeTime;
// }
//
// public String getRemark() {
// return remark;
// }
//
// public void setRemark(String remark) {
// this.remark = remark;
// }
//
// public String getGroupId() {
// return groupId;
// }
//
// public void setGroupId(String groupId) {
// this.groupId = groupId;
// }
//
// public String getSubscribeScene() {
// return subscribeScene;
// }
//
// public void setSubscribeScene(String subscribeScene) {
// this.subscribeScene = subscribeScene;
// }
//
// public String getQrScene() {
// return qrScene;
// }
//
// public void setQrScene(String qrScene) {
// this.qrScene = qrScene;
// }
//
// public String getQrSceneStr() {
// return qrSceneStr;
// }
//
// public void setQrSceneStr(String qrSceneStr) {
// this.qrSceneStr = qrSceneStr;
// }
//
// public List<String> getTagidList() {
// return tagidList;
// }
//
// public void setTagidList(List<String> tagidList) {
// this.tagidList = tagidList;
// }
//
// @SuppressWarnings("unchecked")
// @Override
// public WxMpUserInfo build(String message) {
// return JacksonUtils.parseJSON(message, WxMpUserInfo.class);
// }
//
// }