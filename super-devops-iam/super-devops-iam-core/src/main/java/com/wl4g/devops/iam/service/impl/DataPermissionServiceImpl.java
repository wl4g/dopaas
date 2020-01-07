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
package com.wl4g.devops.iam.service.impl;
//
//import com.wl4g.devops.common.bean.iam.DataPermission;
//import com.wl4g.devops.dao.iam.DataPermissionDao;
//import com.wl4g.devops.dao.iam.GroupUserDao;
//import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
//import com.wl4g.devops.iam.service.DataPermissionService;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//
//import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getPrincipalInfo;
//
///**
// * @author vjay
// * @date 2019-12-13 16:06:00
// */
//@Service
//public class DataPermissionServiceImpl implements DataPermissionService {
//
//	@Autowired
//	private DataPermissionDao dataPermissionDao;
//
//	@Autowired
//    private GroupUserDao groupUserDao;
//
//	@Override
//	public void save(Integer objectId, Integer orgId, Integer type) {
//		DataPermission dataPermission = new DataPermission();
//		dataPermission.setObjectId(objectId);
//		dataPermission.setOrgId(orgId);
//		dataPermission.setType(type);
//		dataPermissionDao.insertSelective(dataPermission);
//	}
//
//    @Override
//    public void save(Integer objectId, Integer[] orgIds, Integer type) {
//        save(objectId,Arrays.asList(orgIds),type);
//    }
//
//    @Override
//    public void save(Integer objectId, List<Integer> orgIds, Integer type) {
//        dataPermissionDao.insertBatchWithOrgIds(objectId,orgIds,type);
//    }
//
//    @Override
//    public void save(List<DataPermission> dataPermissions){
//        dataPermissionDao.insertBatch(dataPermissions);
//    }
//
//    @Override
//    public void update(Integer objectId, Integer[] orgIds, Integer type) {
//        dataPermissionDao.deleteByObjectId(objectId);
//        update(objectId,Arrays.asList(orgIds),type);
//    }
//
//    @Override
//	public void update(Integer objectId, List<Integer> orgIds, Integer type) {
//		dataPermissionDao.deleteByObjectId(objectId);
//        dataPermissionDao.insertBatchWithOrgIds(objectId,orgIds,type);
//	}
//
//	public List<Integer> selectOrgIdsByObjectId(Integer objectId){
//	    return dataPermissionDao.selectOrgIdsByObjectId(objectId);
//    }
//
//    @Override
//    public List<Integer> getCurrentUserGroupIds() {
//        IamPrincipalInfo info = getPrincipalInfo();
//        if(Objects.nonNull(info) && StringUtils.isNotBlank(info.getPrincipalId())){
//            return groupUserDao.selectGroupIdByUserId(Integer.valueOf(info.getPrincipalId()));
//        }
//        return null;
//    }
//
//
//}