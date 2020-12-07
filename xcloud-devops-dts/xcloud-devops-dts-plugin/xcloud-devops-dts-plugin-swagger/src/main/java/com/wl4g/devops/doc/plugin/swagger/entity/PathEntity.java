package com.wl4g.devops.doc.plugin.swagger.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PathEntity {

	private String apiPackage;

	private String servicePackage;

	private String className;

	private String urlPath;

	private PathOperationEntity pathGetEntity;

	private PathOperationEntity pathDeleteEntity;

	private PathOperationEntity pathPostEntity;

	private PathOperationEntity pathPutEntity;

	private Set<ImportClassEntity> importPackageList;
}
