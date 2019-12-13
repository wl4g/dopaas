//package com.wl4g.devops.dao.iam;
//
//import com.wl4g.devops.common.bean.iam.DataPermission;
//import org.apache.ibatis.annotations.Param;
//
//import java.util.List;
//
//public interface DataPermissionDao {
//	int deleteByPrimaryKey(Integer id);
//
//	int deleteByObjectId(Integer id);
//
//	int insert(DataPermission record);
//
//	int insertSelective(DataPermission record);
//
//	DataPermission selectByPrimaryKey(Integer id);
//
//    List<Integer> selectOrgIdsByObjectId(Integer objectId);
//
//	int updateByPrimaryKeySelective(DataPermission record);
//
//	int updateByPrimaryKey(DataPermission record);
//
//	int insertBatch(@Param("dataPermissions") List<DataPermission> dataPermissions);
//
//	int insertBatchWithOrgIds(@Param("objectId") Integer objectId, @Param("orgIds") List<Integer> orgIds,
//			@Param("type") Integer type);
//}