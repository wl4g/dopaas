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
public class ModelClassEntity {

	private String modelPackage;

	private String className;

	private List<ModelPropEntity> modelProps;
}