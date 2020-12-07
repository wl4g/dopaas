package com.wl4g.devops.doc.plugin.swagger.entity;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ImportClassEntity {

	private String className;
}
