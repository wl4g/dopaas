package com.wl4g.devops.doc.plugin.swagger.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PathDeleteOrGetEntity {

	private String className;

	private String methodName;

	private List<PathParamsEntity> params;

	private String responseType;
}
