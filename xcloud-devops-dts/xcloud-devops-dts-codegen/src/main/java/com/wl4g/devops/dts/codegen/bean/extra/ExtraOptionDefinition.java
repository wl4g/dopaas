package com.wl4g.devops.dts.codegen.bean.extra;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notEmptyOf;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.common.bean.ConfigOption;
import com.wl4g.devops.dts.codegen.bean.GenProject;

import static com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenProviderAlias.IAM_SPINGCLOUD_MVN;
import static com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenProviderAlias.VUEJS;
import static com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenProviderAlias.NGJS;
import static com.wl4g.components.common.collection.CollectionUtils2.isEmpty;
import static com.wl4g.components.common.collection.Collections2.isEmptyArray;
import static com.wl4g.components.common.lang.Assert2.*;

/**
 * {@link GenProject} extensible configuration options definitions.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-16
 * @since
 */
public enum ExtraOptionDefinition {

	SpringCloudMvnBuildAssetsType(new GenExtraOption(IAM_SPINGCLOUD_MVN, "build.asset-type", "MvnAssTar", "SpringExecJar")),

	SpringCloudMvnIamSecurityMode(new GenExtraOption(IAM_SPINGCLOUD_MVN, "iam.mode", "local", "cluster", "gateway")),

	SpringCloudSwagger(new GenExtraOption(IAM_SPINGCLOUD_MVN, "swagger.ui", "none", "officialOas", "bootstrapSwagger2")),

	VueJSCompression(new GenExtraOption(VUEJS, "compression", "true", "false")),

	VueJSBasedOnAdminUi(new GenExtraOption(VUEJS, "basedon.adminui", "true", "false")),

	NgJSCompression(new GenExtraOption(NGJS, "compression", "true", "false"));

	/** Gen provider extra option of {@link GenExtraOption} . */
	@NotNull
	private final GenExtraOption option;

	private ExtraOptionDefinition(@NotNull GenExtraOption option) {
		notNullOf(option, "option");
		this.option = option.validate();
	}

	public final GenExtraOption getOption() {
		return option;
	}

	/**
	 * Gets {@link GenExtraOption} by providers.
	 * 
	 * @param provider
	 * @return
	 */
	public static List<GenExtraOption> getOptions(@Nullable String... providers) {
		final List<String> conditions = new ArrayList<>();
		if (!isEmptyArray(providers)) {
			conditions.addAll(asList(providers));
		}
		return asList(values()).stream().filter(o -> (isEmpty(conditions) || conditions.contains(o.getOption().getProvider())))
				.map(o -> o.getOption()).collect(toList());
	}

	/**
	 * Gen project extra options. see: {@link GenProject}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-09-16
	 * @since
	 */
	public static class GenExtraOption extends ConfigOption {

		/** {@link GeneratorProvider} alias. */
		@NotBlank
		private String provider;

		public GenExtraOption() {
			super();
		}

		public GenExtraOption(@NotBlank String provider, @NotBlank String name, @NotEmpty String... values) {
			this(provider, name, asList(notEmptyOf(values, "values")));
		}

		public GenExtraOption(@NotBlank String provider, @NotBlank String name, @NotEmpty List<String> values) {
			setProvider(provider);
			setName(name);
			setValues(values);
		}

		/**
		 * Gets extra option of gen provider.
		 * 
		 * @return
		 */
		@NotBlank
		public String getProvider() {
			return provider;
		}

		/**
		 * Sets extra option of gen provider.
		 * 
		 * @param provider
		 */
		public void setProvider(@NotBlank String provider) {
			this.provider = hasTextOf(provider, "provider");
		}

		/**
		 * Sets extra option of gen provider.
		 * 
		 * @param provider
		 */
		public GenExtraOption withProvider(@NotBlank String provider) {
			setProvider(provider);
			return this;
		}

		/**
		 * Validation for itself attributes.
		 * 
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public final GenExtraOption validate() {
			hasTextOf(getProvider(), "provider");
			super.validate();
			return this;
		}

	}

}