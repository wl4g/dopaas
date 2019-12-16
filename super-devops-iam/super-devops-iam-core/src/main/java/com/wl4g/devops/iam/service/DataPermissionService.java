//package com.wl4g.devops.iam.service;
//
//import com.wl4g.devops.common.bean.iam.DataPermission;
//
//import java.util.List;
//
///**
// * @author vjay
// * @date 2019-12-13 16:06:00
// */
//public interface DataPermissionService {
//
//	void save(Integer objectId, Integer orgId, Integer type);
//
//	void save(Integer objectId, Integer[] orgIds, Integer type);
//
//	void save(Integer objectId, List<Integer> orgIds, Integer type);
//
//	void save(List<DataPermission> dataPermissions);
//
//	void update(Integer objectId, Integer[] orgIds, Integer type);
//
//	void update(Integer objectId, List<Integer> orgIds, Integer type);
//
//    List<Integer> selectOrgIdsByObjectId(Integer objectId);
//
//    //get login user groupIds
//    List<Integer> getCurrentUserGroupIds();
//
//
//}
