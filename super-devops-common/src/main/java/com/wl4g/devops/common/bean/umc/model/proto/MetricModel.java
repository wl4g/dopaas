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
package com.wl4g.devops.common.bean.umc.model.proto;

@SuppressWarnings({ "unused", "unchecked" })
public final class MetricModel {
	private MetricModel() {
	}

	public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {
	}

	public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
		registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
	}

	public interface MetricAggregateOrBuilder extends
			// @@protoc_insertion_point(interface_extends:com.wl4g.devops.common.bean.umc.model.proto.MetricAggregate)
			com.google.protobuf.MessageOrBuilder {

		/**
		 * <code>string classify = 1;</code>
		 */
		java.lang.String getClassify();

		/**
		 * <code>string classify = 1;</code>
		 */
		com.google.protobuf.ByteString getClassifyBytes();

		/**
		 * <code>string host = 2;</code>
		 */
		java.lang.String getHost();

		/**
		 * <code>string host = 2;</code>
		 */
		com.google.protobuf.ByteString getHostBytes();

		/**
		 * <code>string endpoint = 3;</code>
		 */
		java.lang.String getEndpoint();

		/**
		 * <code>string endpoint = 3;</code>
		 */
		com.google.protobuf.ByteString getEndpointBytes();

		/**
		 * <code>string namespace = 4;</code>
		 */
		java.lang.String getNamespace();

		/**
		 * <code>string namespace = 4;</code>
		 */
		com.google.protobuf.ByteString getNamespaceBytes();

		/**
		 * <code>int64 timestamp = 5;</code>
		 */
		long getTimestamp();

		/**
		 * <pre>
		 * Array&lt;Metric&gt;
		 * </pre>
		 *
		 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
		 */
		java.util.List<com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric> getMetricsList();

		/**
		 * <pre>
		 * Array&lt;Metric&gt;
		 * </pre>
		 *
		 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
		 */
		com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric getMetrics(int index);

		/**
		 * <pre>
		 * Array&lt;Metric&gt;
		 * </pre>
		 *
		 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
		 */
		int getMetricsCount();

		/**
		 * <pre>
		 * Array&lt;Metric&gt;
		 * </pre>
		 *
		 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
		 */
		java.util.List<? extends com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricOrBuilder> getMetricsOrBuilderList();

		/**
		 * <pre>
		 * Array&lt;Metric&gt;
		 * </pre>
		 *
		 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
		 */
		com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricOrBuilder getMetricsOrBuilder(int index);
	}

	/**
	 * Protobuf type
	 * {@code com.wl4g.devops.common.bean.umc.model.proto.MetricAggregate}
	 */
	public static final class MetricAggregate extends com.google.protobuf.GeneratedMessageV3 implements
			// @@protoc_insertion_point(message_implements:com.wl4g.devops.common.bean.umc.model.proto.MetricAggregate)
			MetricAggregateOrBuilder {
		private static final long serialVersionUID = 0L;

		// Use MetricAggregate.newBuilder() to construct.
		private MetricAggregate(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
			super(builder);
		}

		private MetricAggregate() {
			classify_ = "";
			host_ = "";
			endpoint_ = "";
			namespace_ = "";
			metrics_ = java.util.Collections.emptyList();
		}

		@java.lang.Override
		protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
			return new MetricAggregate();
		}

		@java.lang.Override
		public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
			return this.unknownFields;
		}

		private MetricAggregate(com.google.protobuf.CodedInputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			this();
			if (extensionRegistry == null) {
				throw new java.lang.NullPointerException();
			}
			int mutable_bitField0_ = 0;
			com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google.protobuf.UnknownFieldSet.newBuilder();
			try {
				boolean done = false;
				while (!done) {
					int tag = input.readTag();
					switch (tag) {
					case 0:
						done = true;
						break;
					case 10: {
						java.lang.String s = input.readStringRequireUtf8();

						classify_ = s;
						break;
					}
					case 18: {
						java.lang.String s = input.readStringRequireUtf8();

						host_ = s;
						break;
					}
					case 26: {
						java.lang.String s = input.readStringRequireUtf8();

						endpoint_ = s;
						break;
					}
					case 34: {
						java.lang.String s = input.readStringRequireUtf8();

						namespace_ = s;
						break;
					}
					case 40: {

						timestamp_ = input.readInt64();
						break;
					}
					case 50: {
						if (!((mutable_bitField0_ & 0x00000001) != 0)) {
							metrics_ = new java.util.ArrayList<com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric>();
							mutable_bitField0_ |= 0x00000001;
						}
						metrics_.add(input.readMessage(com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.parser(),
								extensionRegistry));
						break;
					}
					default: {
						if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
							done = true;
						}
						break;
					}
					}
				}
			} catch (com.google.protobuf.InvalidProtocolBufferException e) {
				throw e.setUnfinishedMessage(this);
			} catch (java.io.IOException e) {
				throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
			} finally {
				if (((mutable_bitField0_ & 0x00000001) != 0)) {
					metrics_ = java.util.Collections.unmodifiableList(metrics_);
				}
				this.unknownFields = unknownFields.build();
				makeExtensionsImmutable();
			}
		}

		public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
			return com.wl4g.devops.common.bean.umc.model.proto.MetricModel.internal_static_com_wl4g_devops_common_bean_umc_model_proto_MetricAggregate_descriptor;
		}

		@java.lang.Override
		protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
			return com.wl4g.devops.common.bean.umc.model.proto.MetricModel.internal_static_com_wl4g_devops_common_bean_umc_model_proto_MetricAggregate_fieldAccessorTable
					.ensureFieldAccessorsInitialized(
							com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate.class,
							com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate.Builder.class);
		}

		public static final int CLASSIFY_FIELD_NUMBER = 1;
		private volatile java.lang.Object classify_;

		/**
		 * <code>string classify = 1;</code>
		 */
		public java.lang.String getClassify() {
			java.lang.Object ref = classify_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				classify_ = s;
				return s;
			}
		}

		/**
		 * <code>string classify = 1;</code>
		 */
		public com.google.protobuf.ByteString getClassifyBytes() {
			java.lang.Object ref = classify_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				classify_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int HOST_FIELD_NUMBER = 2;
		private volatile java.lang.Object host_;

		/**
		 * <code>string host = 2;</code>
		 */
		public java.lang.String getHost() {
			java.lang.Object ref = host_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				host_ = s;
				return s;
			}
		}

		/**
		 * <code>string host = 2;</code>
		 */
		public com.google.protobuf.ByteString getHostBytes() {
			java.lang.Object ref = host_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				host_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int ENDPOINT_FIELD_NUMBER = 3;
		private volatile java.lang.Object endpoint_;

		/**
		 * <code>string endpoint = 3;</code>
		 */
		public java.lang.String getEndpoint() {
			java.lang.Object ref = endpoint_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				endpoint_ = s;
				return s;
			}
		}

		/**
		 * <code>string endpoint = 3;</code>
		 */
		public com.google.protobuf.ByteString getEndpointBytes() {
			java.lang.Object ref = endpoint_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				endpoint_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int NAMESPACE_FIELD_NUMBER = 4;
		private volatile java.lang.Object namespace_;

		/**
		 * <code>string namespace = 4;</code>
		 */
		public java.lang.String getNamespace() {
			java.lang.Object ref = namespace_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				namespace_ = s;
				return s;
			}
		}

		/**
		 * <code>string namespace = 4;</code>
		 */
		public com.google.protobuf.ByteString getNamespaceBytes() {
			java.lang.Object ref = namespace_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				namespace_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int TIMESTAMP_FIELD_NUMBER = 5;
		private long timestamp_;

		/**
		 * <code>int64 timestamp = 5;</code>
		 */
		public long getTimestamp() {
			return timestamp_;
		}

		public static final int METRICS_FIELD_NUMBER = 6;
		private java.util.List<com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric> metrics_;

		/**
		 * <pre>
		 * Array&lt;Metric&gt;
		 * </pre>
		 *
		 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
		 */
		public java.util.List<com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric> getMetricsList() {
			return metrics_;
		}

		/**
		 * <pre>
		 * Array&lt;Metric&gt;
		 * </pre>
		 *
		 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
		 */
		public java.util.List<? extends com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricOrBuilder> getMetricsOrBuilderList() {
			return metrics_;
		}

		/**
		 * <pre>
		 * Array&lt;Metric&gt;
		 * </pre>
		 *
		 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
		 */
		public int getMetricsCount() {
			return metrics_.size();
		}

		/**
		 * <pre>
		 * Array&lt;Metric&gt;
		 * </pre>
		 *
		 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
		 */
		public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric getMetrics(int index) {
			return metrics_.get(index);
		}

		/**
		 * <pre>
		 * Array&lt;Metric&gt;
		 * </pre>
		 *
		 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
		 */
		public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricOrBuilder getMetricsOrBuilder(int index) {
			return metrics_.get(index);
		}

		private byte memoizedIsInitialized = -1;

		@java.lang.Override
		public final boolean isInitialized() {
			byte isInitialized = memoizedIsInitialized;
			if (isInitialized == 1)
				return true;
			if (isInitialized == 0)
				return false;

			memoizedIsInitialized = 1;
			return true;
		}

		@java.lang.Override
		public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
			if (!getClassifyBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 1, classify_);
			}
			if (!getHostBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 2, host_);
			}
			if (!getEndpointBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 3, endpoint_);
			}
			if (!getNamespaceBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 4, namespace_);
			}
			if (timestamp_ != 0L) {
				output.writeInt64(5, timestamp_);
			}
			for (int i = 0; i < metrics_.size(); i++) {
				output.writeMessage(6, metrics_.get(i));
			}
			unknownFields.writeTo(output);
		}

		@java.lang.Override
		public int getSerializedSize() {
			int size = memoizedSize;
			if (size != -1)
				return size;

			size = 0;
			if (!getClassifyBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, classify_);
			}
			if (!getHostBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, host_);
			}
			if (!getEndpointBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, endpoint_);
			}
			if (!getNamespaceBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, namespace_);
			}
			if (timestamp_ != 0L) {
				size += com.google.protobuf.CodedOutputStream.computeInt64Size(5, timestamp_);
			}
			for (int i = 0; i < metrics_.size(); i++) {
				size += com.google.protobuf.CodedOutputStream.computeMessageSize(6, metrics_.get(i));
			}
			size += unknownFields.getSerializedSize();
			memoizedSize = size;
			return size;
		}

		@java.lang.Override
		public boolean equals(final java.lang.Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate)) {
				return super.equals(obj);
			}
			com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate other = (com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate) obj;

			if (!getClassify().equals(other.getClassify()))
				return false;
			if (!getHost().equals(other.getHost()))
				return false;
			if (!getEndpoint().equals(other.getEndpoint()))
				return false;
			if (!getNamespace().equals(other.getNamespace()))
				return false;
			if (getTimestamp() != other.getTimestamp())
				return false;
			if (!getMetricsList().equals(other.getMetricsList()))
				return false;
			if (!unknownFields.equals(other.unknownFields))
				return false;
			return true;
		}

		@java.lang.Override
		public int hashCode() {
			if (memoizedHashCode != 0) {
				return memoizedHashCode;
			}
			int hash = 41;
			hash = (19 * hash) + getDescriptor().hashCode();
			hash = (37 * hash) + CLASSIFY_FIELD_NUMBER;
			hash = (53 * hash) + getClassify().hashCode();
			hash = (37 * hash) + HOST_FIELD_NUMBER;
			hash = (53 * hash) + getHost().hashCode();
			hash = (37 * hash) + ENDPOINT_FIELD_NUMBER;
			hash = (53 * hash) + getEndpoint().hashCode();
			hash = (37 * hash) + NAMESPACE_FIELD_NUMBER;
			hash = (53 * hash) + getNamespace().hashCode();
			hash = (37 * hash) + TIMESTAMP_FIELD_NUMBER;
			hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getTimestamp());
			if (getMetricsCount() > 0) {
				hash = (37 * hash) + METRICS_FIELD_NUMBER;
				hash = (53 * hash) + getMetricsList().hashCode();
			}
			hash = (29 * hash) + unknownFields.hashCode();
			memoizedHashCode = hash;
			return hash;
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parseFrom(java.nio.ByteBuffer data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parseFrom(java.nio.ByteBuffer data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parseFrom(
				com.google.protobuf.ByteString data) throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parseFrom(
				com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parseFrom(byte[] data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parseFrom(byte[] data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parseFrom(java.io.InputStream input)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parseFrom(java.io.InputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parseDelimitedFrom(
				java.io.InputStream input) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parseDelimitedFrom(
				java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parseFrom(
				com.google.protobuf.CodedInputStream input) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parseFrom(
				com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
		}

		@java.lang.Override
		public Builder newBuilderForType() {
			return newBuilder();
		}

		public static Builder newBuilder() {
			return DEFAULT_INSTANCE.toBuilder();
		}

		public static Builder newBuilder(com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate prototype) {
			return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
		}

		@java.lang.Override
		public Builder toBuilder() {
			return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
		}

		@java.lang.Override
		protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
			Builder builder = new Builder(parent);
			return builder;
		}

		/**
		 * Protobuf type
		 * {@code com.wl4g.devops.common.bean.umc.model.proto.MetricAggregate}
		 */
		public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
				// @@protoc_insertion_point(builder_implements:com.wl4g.devops.common.bean.umc.model.proto.MetricAggregate)
				com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregateOrBuilder {
			public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
				return com.wl4g.devops.common.bean.umc.model.proto.MetricModel.internal_static_com_wl4g_devops_common_bean_umc_model_proto_MetricAggregate_descriptor;
			}

			@java.lang.Override
			protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
				return com.wl4g.devops.common.bean.umc.model.proto.MetricModel.internal_static_com_wl4g_devops_common_bean_umc_model_proto_MetricAggregate_fieldAccessorTable
						.ensureFieldAccessorsInitialized(
								com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate.class,
								com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate.Builder.class);
			}

			// Construct using
			// com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate.newBuilder()
			private Builder() {
				maybeForceBuilderInitialization();
			}

			private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
				super(parent);
				maybeForceBuilderInitialization();
			}

			private void maybeForceBuilderInitialization() {
				if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
					getMetricsFieldBuilder();
				}
			}

			@java.lang.Override
			public Builder clear() {
				super.clear();
				classify_ = "";

				host_ = "";

				endpoint_ = "";

				namespace_ = "";

				timestamp_ = 0L;

				if (metricsBuilder_ == null) {
					metrics_ = java.util.Collections.emptyList();
					bitField0_ = (bitField0_ & ~0x00000001);
				} else {
					metricsBuilder_.clear();
				}
				return this;
			}

			@java.lang.Override
			public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
				return com.wl4g.devops.common.bean.umc.model.proto.MetricModel.internal_static_com_wl4g_devops_common_bean_umc_model_proto_MetricAggregate_descriptor;
			}

			@java.lang.Override
			public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate getDefaultInstanceForType() {
				return com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate.getDefaultInstance();
			}

			@java.lang.Override
			public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate build() {
				com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate result = buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(result);
				}
				return result;
			}

			@java.lang.Override
			public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate buildPartial() {
				com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate result = new com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate(
						this);
				int from_bitField0_ = bitField0_;
				result.classify_ = classify_;
				result.host_ = host_;
				result.endpoint_ = endpoint_;
				result.namespace_ = namespace_;
				result.timestamp_ = timestamp_;
				if (metricsBuilder_ == null) {
					if (((bitField0_ & 0x00000001) != 0)) {
						metrics_ = java.util.Collections.unmodifiableList(metrics_);
						bitField0_ = (bitField0_ & ~0x00000001);
					}
					result.metrics_ = metrics_;
				} else {
					result.metrics_ = metricsBuilder_.build();
				}
				onBuilt();
				return result;
			}

			@java.lang.Override
			public Builder clone() {
				return super.clone();
			}

			@java.lang.Override
			public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
				return super.setField(field, value);
			}

			@java.lang.Override
			public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
				return super.clearField(field);
			}

			@java.lang.Override
			public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
				return super.clearOneof(oneof);
			}

			@java.lang.Override
			public Builder setRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, int index,
					java.lang.Object value) {
				return super.setRepeatedField(field, index, value);
			}

			@java.lang.Override
			public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
				return super.addRepeatedField(field, value);
			}

			@java.lang.Override
			public Builder mergeFrom(com.google.protobuf.Message other) {
				if (other instanceof com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate) {
					return mergeFrom((com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate) other);
				} else {
					super.mergeFrom(other);
					return this;
				}
			}

			public Builder mergeFrom(com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate other) {
				if (other == com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate.getDefaultInstance())
					return this;
				if (!other.getClassify().isEmpty()) {
					classify_ = other.classify_;
					onChanged();
				}
				if (!other.getHost().isEmpty()) {
					host_ = other.host_;
					onChanged();
				}
				if (!other.getEndpoint().isEmpty()) {
					endpoint_ = other.endpoint_;
					onChanged();
				}
				if (!other.getNamespace().isEmpty()) {
					namespace_ = other.namespace_;
					onChanged();
				}
				if (other.getTimestamp() != 0L) {
					setTimestamp(other.getTimestamp());
				}
				if (metricsBuilder_ == null) {
					if (!other.metrics_.isEmpty()) {
						if (metrics_.isEmpty()) {
							metrics_ = other.metrics_;
							bitField0_ = (bitField0_ & ~0x00000001);
						} else {
							ensureMetricsIsMutable();
							metrics_.addAll(other.metrics_);
						}
						onChanged();
					}
				} else {
					if (!other.metrics_.isEmpty()) {
						if (metricsBuilder_.isEmpty()) {
							metricsBuilder_.dispose();
							metricsBuilder_ = null;
							metrics_ = other.metrics_;
							bitField0_ = (bitField0_ & ~0x00000001);
							metricsBuilder_ = com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders
									? getMetricsFieldBuilder() : null;
						} else {
							metricsBuilder_.addAllMessages(other.metrics_);
						}
					}
				}
				this.mergeUnknownFields(other.unknownFields);
				onChanged();
				return this;
			}

			@java.lang.Override
			public final boolean isInitialized() {
				return true;
			}

			@java.lang.Override
			public Builder mergeFrom(com.google.protobuf.CodedInputStream input,
					com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
				com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate parsedMessage = null;
				try {
					parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
				} catch (com.google.protobuf.InvalidProtocolBufferException e) {
					parsedMessage = (com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate) e
							.getUnfinishedMessage();
					throw e.unwrapIOException();
				} finally {
					if (parsedMessage != null) {
						mergeFrom(parsedMessage);
					}
				}
				return this;
			}

			private int bitField0_;

			private java.lang.Object classify_ = "";

			/**
			 * <code>string classify = 1;</code>
			 */
			public java.lang.String getClassify() {
				java.lang.Object ref = classify_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					classify_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string classify = 1;</code>
			 */
			public com.google.protobuf.ByteString getClassifyBytes() {
				java.lang.Object ref = classify_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
					classify_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string classify = 1;</code>
			 */
			public Builder setClassify(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				classify_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string classify = 1;</code>
			 */
			public Builder clearClassify() {

				classify_ = getDefaultInstance().getClassify();
				onChanged();
				return this;
			}

			/**
			 * <code>string classify = 1;</code>
			 */
			public Builder setClassifyBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				classify_ = value;
				onChanged();
				return this;
			}

			private java.lang.Object host_ = "";

			/**
			 * <code>string host = 2;</code>
			 */
			public java.lang.String getHost() {
				java.lang.Object ref = host_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					host_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string host = 2;</code>
			 */
			public com.google.protobuf.ByteString getHostBytes() {
				java.lang.Object ref = host_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
					host_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string host = 2;</code>
			 */
			public Builder setHost(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				host_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string host = 2;</code>
			 */
			public Builder clearHost() {

				host_ = getDefaultInstance().getHost();
				onChanged();
				return this;
			}

			/**
			 * <code>string host = 2;</code>
			 */
			public Builder setHostBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				host_ = value;
				onChanged();
				return this;
			}

			private java.lang.Object endpoint_ = "";

			/**
			 * <code>string endpoint = 3;</code>
			 */
			public java.lang.String getEndpoint() {
				java.lang.Object ref = endpoint_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					endpoint_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string endpoint = 3;</code>
			 */
			public com.google.protobuf.ByteString getEndpointBytes() {
				java.lang.Object ref = endpoint_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
					endpoint_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string endpoint = 3;</code>
			 */
			public Builder setEndpoint(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				endpoint_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string endpoint = 3;</code>
			 */
			public Builder clearEndpoint() {

				endpoint_ = getDefaultInstance().getEndpoint();
				onChanged();
				return this;
			}

			/**
			 * <code>string endpoint = 3;</code>
			 */
			public Builder setEndpointBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				endpoint_ = value;
				onChanged();
				return this;
			}

			private java.lang.Object namespace_ = "";

			/**
			 * <code>string namespace = 4;</code>
			 */
			public java.lang.String getNamespace() {
				java.lang.Object ref = namespace_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					namespace_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string namespace = 4;</code>
			 */
			public com.google.protobuf.ByteString getNamespaceBytes() {
				java.lang.Object ref = namespace_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
					namespace_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string namespace = 4;</code>
			 */
			public Builder setNamespace(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				namespace_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string namespace = 4;</code>
			 */
			public Builder clearNamespace() {

				namespace_ = getDefaultInstance().getNamespace();
				onChanged();
				return this;
			}

			/**
			 * <code>string namespace = 4;</code>
			 */
			public Builder setNamespaceBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				namespace_ = value;
				onChanged();
				return this;
			}

			private long timestamp_;

			/**
			 * <code>int64 timestamp = 5;</code>
			 */
			public long getTimestamp() {
				return timestamp_;
			}

			/**
			 * <code>int64 timestamp = 5;</code>
			 */
			public Builder setTimestamp(long value) {

				timestamp_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>int64 timestamp = 5;</code>
			 */
			public Builder clearTimestamp() {

				timestamp_ = 0L;
				onChanged();
				return this;
			}

			private java.util.List<com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric> metrics_ = java.util.Collections
					.emptyList();

			private void ensureMetricsIsMutable() {
				if (!((bitField0_ & 0x00000001) != 0)) {
					metrics_ = new java.util.ArrayList<com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric>(metrics_);
					bitField0_ |= 0x00000001;
				}
			}

			private com.google.protobuf.RepeatedFieldBuilderV3<com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric, com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.Builder, com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricOrBuilder> metricsBuilder_;

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public java.util.List<com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric> getMetricsList() {
				if (metricsBuilder_ == null) {
					return java.util.Collections.unmodifiableList(metrics_);
				} else {
					return metricsBuilder_.getMessageList();
				}
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public int getMetricsCount() {
				if (metricsBuilder_ == null) {
					return metrics_.size();
				} else {
					return metricsBuilder_.getCount();
				}
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric getMetrics(int index) {
				if (metricsBuilder_ == null) {
					return metrics_.get(index);
				} else {
					return metricsBuilder_.getMessage(index);
				}
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public Builder setMetrics(int index, com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric value) {
				if (metricsBuilder_ == null) {
					if (value == null) {
						throw new NullPointerException();
					}
					ensureMetricsIsMutable();
					metrics_.set(index, value);
					onChanged();
				} else {
					metricsBuilder_.setMessage(index, value);
				}
				return this;
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public Builder setMetrics(int index,
					com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.Builder builderForValue) {
				if (metricsBuilder_ == null) {
					ensureMetricsIsMutable();
					metrics_.set(index, builderForValue.build());
					onChanged();
				} else {
					metricsBuilder_.setMessage(index, builderForValue.build());
				}
				return this;
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public Builder addMetrics(com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric value) {
				if (metricsBuilder_ == null) {
					if (value == null) {
						throw new NullPointerException();
					}
					ensureMetricsIsMutable();
					metrics_.add(value);
					onChanged();
				} else {
					metricsBuilder_.addMessage(value);
				}
				return this;
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public Builder addMetrics(int index, com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric value) {
				if (metricsBuilder_ == null) {
					if (value == null) {
						throw new NullPointerException();
					}
					ensureMetricsIsMutable();
					metrics_.add(index, value);
					onChanged();
				} else {
					metricsBuilder_.addMessage(index, value);
				}
				return this;
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public Builder addMetrics(com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.Builder builderForValue) {
				if (metricsBuilder_ == null) {
					ensureMetricsIsMutable();
					metrics_.add(builderForValue.build());
					onChanged();
				} else {
					metricsBuilder_.addMessage(builderForValue.build());
				}
				return this;
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public Builder addMetrics(int index,
					com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.Builder builderForValue) {
				if (metricsBuilder_ == null) {
					ensureMetricsIsMutable();
					metrics_.add(index, builderForValue.build());
					onChanged();
				} else {
					metricsBuilder_.addMessage(index, builderForValue.build());
				}
				return this;
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public Builder addAllMetrics(
					java.lang.Iterable<? extends com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric> values) {
				if (metricsBuilder_ == null) {
					ensureMetricsIsMutable();
					com.google.protobuf.AbstractMessageLite.Builder.addAll(values, metrics_);
					onChanged();
				} else {
					metricsBuilder_.addAllMessages(values);
				}
				return this;
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public Builder clearMetrics() {
				if (metricsBuilder_ == null) {
					metrics_ = java.util.Collections.emptyList();
					bitField0_ = (bitField0_ & ~0x00000001);
					onChanged();
				} else {
					metricsBuilder_.clear();
				}
				return this;
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public Builder removeMetrics(int index) {
				if (metricsBuilder_ == null) {
					ensureMetricsIsMutable();
					metrics_.remove(index);
					onChanged();
				} else {
					metricsBuilder_.remove(index);
				}
				return this;
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.Builder getMetricsBuilder(int index) {
				return getMetricsFieldBuilder().getBuilder(index);
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricOrBuilder getMetricsOrBuilder(int index) {
				if (metricsBuilder_ == null) {
					return metrics_.get(index);
				} else {
					return metricsBuilder_.getMessageOrBuilder(index);
				}
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public java.util.List<? extends com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricOrBuilder> getMetricsOrBuilderList() {
				if (metricsBuilder_ != null) {
					return metricsBuilder_.getMessageOrBuilderList();
				} else {
					return java.util.Collections.unmodifiableList(metrics_);
				}
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.Builder addMetricsBuilder() {
				return getMetricsFieldBuilder()
						.addBuilder(com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.getDefaultInstance());
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.Builder addMetricsBuilder(int index) {
				return getMetricsFieldBuilder().addBuilder(index,
						com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.getDefaultInstance());
			}

			/**
			 * <pre>
			 * Array&lt;Metric&gt;
			 * </pre>
			 *
			 * <code>repeated .com.wl4g.devops.common.bean.umc.model.proto.Metric metrics = 6;</code>
			 */
			public java.util.List<com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.Builder> getMetricsBuilderList() {
				return getMetricsFieldBuilder().getBuilderList();
			}

			private com.google.protobuf.RepeatedFieldBuilderV3<com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric, com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.Builder, com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricOrBuilder> getMetricsFieldBuilder() {
				if (metricsBuilder_ == null) {
					metricsBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric, com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.Builder, com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricOrBuilder>(
							metrics_, ((bitField0_ & 0x00000001) != 0), getParentForChildren(), isClean());
					metrics_ = null;
				}
				return metricsBuilder_;
			}

			@java.lang.Override
			public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
				return super.setUnknownFields(unknownFields);
			}

			@java.lang.Override
			public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
				return super.mergeUnknownFields(unknownFields);
			}

			// @@protoc_insertion_point(builder_scope:com.wl4g.devops.common.bean.umc.model.proto.MetricAggregate)
		}

		// @@protoc_insertion_point(class_scope:com.wl4g.devops.common.bean.umc.model.proto.MetricAggregate)
		private static final com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate DEFAULT_INSTANCE;
		static {
			DEFAULT_INSTANCE = new com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate();
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate getDefaultInstance() {
			return DEFAULT_INSTANCE;
		}

		private static final com.google.protobuf.Parser<MetricAggregate> PARSER = new com.google.protobuf.AbstractParser<MetricAggregate>() {
			@java.lang.Override
			public MetricAggregate parsePartialFrom(com.google.protobuf.CodedInputStream input,
					com.google.protobuf.ExtensionRegistryLite extensionRegistry)
					throws com.google.protobuf.InvalidProtocolBufferException {
				return new MetricAggregate(input, extensionRegistry);
			}
		};

		public static com.google.protobuf.Parser<MetricAggregate> parser() {
			return PARSER;
		}

		@java.lang.Override
		public com.google.protobuf.Parser<MetricAggregate> getParserForType() {
			return PARSER;
		}

		@java.lang.Override
		public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate getDefaultInstanceForType() {
			return DEFAULT_INSTANCE;
		}

	}

	public interface MetricOrBuilder extends
			// @@protoc_insertion_point(interface_extends:com.wl4g.devops.common.bean.umc.model.proto.Metric)
			com.google.protobuf.MessageOrBuilder {

		/**
		 * <code>string Metric = 1;</code>
		 */
		java.lang.String getMetric();

		/**
		 * <code>string Metric = 1;</code>
		 */
		com.google.protobuf.ByteString getMetricBytes();

		/**
		 * <code>map&lt;string, string&gt; tags = 2;</code>
		 */
		int getTagsCount();

		/**
		 * <code>map&lt;string, string&gt; tags = 2;</code>
		 */
		boolean containsTags(java.lang.String key);

		/**
		 * Use {@link #getTagsMap()} instead.
		 */
		@java.lang.Deprecated
		java.util.Map<java.lang.String, java.lang.String> getTags();

		/**
		 * <code>map&lt;string, string&gt; tags = 2;</code>
		 */
		java.util.Map<java.lang.String, java.lang.String> getTagsMap();

		/**
		 * <code>map&lt;string, string&gt; tags = 2;</code>
		 */

		java.lang.String getTagsOrDefault(java.lang.String key, java.lang.String defaultValue);

		/**
		 * <code>map&lt;string, string&gt; tags = 2;</code>
		 */

		java.lang.String getTagsOrThrow(java.lang.String key);

		/**
		 * <code>double Value = 3;</code>
		 */
		double getValue();
	}

	/**
	 * Protobuf type {@code com.wl4g.devops.common.bean.umc.model.proto.Metric}
	 */
	public static final class Metric extends com.google.protobuf.GeneratedMessageV3 implements
			// @@protoc_insertion_point(message_implements:com.wl4g.devops.common.bean.umc.model.proto.Metric)
			MetricOrBuilder {
		private static final long serialVersionUID = 0L;

		// Use Metric.newBuilder() to construct.
		private Metric(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
			super(builder);
		}

		private Metric() {
			metric_ = "";
		}

		@java.lang.Override
		protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
			return new Metric();
		}

		@java.lang.Override
		public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
			return this.unknownFields;
		}

		private Metric(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			this();
			if (extensionRegistry == null) {
				throw new java.lang.NullPointerException();
			}
			int mutable_bitField0_ = 0;
			com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google.protobuf.UnknownFieldSet.newBuilder();
			try {
				boolean done = false;
				while (!done) {
					int tag = input.readTag();
					switch (tag) {
					case 0:
						done = true;
						break;
					case 10: {
						java.lang.String s = input.readStringRequireUtf8();

						metric_ = s;
						break;
					}
					case 18: {
						if (!((mutable_bitField0_ & 0x00000001) != 0)) {
							tags_ = com.google.protobuf.MapField.newMapField(TagsDefaultEntryHolder.defaultEntry);
							mutable_bitField0_ |= 0x00000001;
						}
						com.google.protobuf.MapEntry<java.lang.String, java.lang.String> tags__ = input
								.readMessage(TagsDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
						tags_.getMutableMap().put(tags__.getKey(), tags__.getValue());
						break;
					}
					case 25: {

						value_ = input.readDouble();
						break;
					}
					default: {
						if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
							done = true;
						}
						break;
					}
					}
				}
			} catch (com.google.protobuf.InvalidProtocolBufferException e) {
				throw e.setUnfinishedMessage(this);
			} catch (java.io.IOException e) {
				throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
			} finally {
				this.unknownFields = unknownFields.build();
				makeExtensionsImmutable();
			}
		}

		public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
			return com.wl4g.devops.common.bean.umc.model.proto.MetricModel.internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_descriptor;
		}

		@SuppressWarnings({ "rawtypes" })
		@java.lang.Override
		protected com.google.protobuf.MapField internalGetMapField(int number) {
			switch (number) {
			case 2:
				return internalGetTags();
			default:
				throw new RuntimeException("Invalid map field number: " + number);
			}
		}

		@java.lang.Override
		protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
			return com.wl4g.devops.common.bean.umc.model.proto.MetricModel.internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_fieldAccessorTable
					.ensureFieldAccessorsInitialized(com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.class,
							com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.Builder.class);
		}

		public static final int METRIC_FIELD_NUMBER = 1;
		private volatile java.lang.Object metric_;

		/**
		 * <code>string Metric = 1;</code>
		 */
		public java.lang.String getMetric() {
			java.lang.Object ref = metric_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				metric_ = s;
				return s;
			}
		}

		/**
		 * <code>string Metric = 1;</code>
		 */
		public com.google.protobuf.ByteString getMetricBytes() {
			java.lang.Object ref = metric_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
				metric_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		public static final int TAGS_FIELD_NUMBER = 2;

		private static final class TagsDefaultEntryHolder {
			static final com.google.protobuf.MapEntry<java.lang.String, java.lang.String> defaultEntry = com.google.protobuf.MapEntry.<java.lang.String, java.lang.String> newDefaultInstance(
					com.wl4g.devops.common.bean.umc.model.proto.MetricModel.internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_TagsEntry_descriptor,
					com.google.protobuf.WireFormat.FieldType.STRING, "", com.google.protobuf.WireFormat.FieldType.STRING, "");
		}

		private com.google.protobuf.MapField<java.lang.String, java.lang.String> tags_;

		private com.google.protobuf.MapField<java.lang.String, java.lang.String> internalGetTags() {
			if (tags_ == null) {
				return com.google.protobuf.MapField.emptyMapField(TagsDefaultEntryHolder.defaultEntry);
			}
			return tags_;
		}

		public int getTagsCount() {
			return internalGetTags().getMap().size();
		}

		/**
		 * <code>map&lt;string, string&gt; tags = 2;</code>
		 */

		public boolean containsTags(java.lang.String key) {
			if (key == null) {
				throw new java.lang.NullPointerException();
			}
			return internalGetTags().getMap().containsKey(key);
		}

		/**
		 * Use {@link #getTagsMap()} instead.
		 */
		@java.lang.Deprecated
		public java.util.Map<java.lang.String, java.lang.String> getTags() {
			return getTagsMap();
		}

		/**
		 * <code>map&lt;string, string&gt; tags = 2;</code>
		 */

		public java.util.Map<java.lang.String, java.lang.String> getTagsMap() {
			return internalGetTags().getMap();
		}

		/**
		 * <code>map&lt;string, string&gt; tags = 2;</code>
		 */

		public java.lang.String getTagsOrDefault(java.lang.String key, java.lang.String defaultValue) {
			if (key == null) {
				throw new java.lang.NullPointerException();
			}
			java.util.Map<java.lang.String, java.lang.String> map = internalGetTags().getMap();
			return map.containsKey(key) ? map.get(key) : defaultValue;
		}

		/**
		 * <code>map&lt;string, string&gt; tags = 2;</code>
		 */

		public java.lang.String getTagsOrThrow(java.lang.String key) {
			if (key == null) {
				throw new java.lang.NullPointerException();
			}
			java.util.Map<java.lang.String, java.lang.String> map = internalGetTags().getMap();
			if (!map.containsKey(key)) {
				throw new java.lang.IllegalArgumentException();
			}
			return map.get(key);
		}

		public static final int VALUE_FIELD_NUMBER = 3;
		private double value_;

		/**
		 * <code>double Value = 3;</code>
		 */
		public double getValue() {
			return value_;
		}

		private byte memoizedIsInitialized = -1;

		@java.lang.Override
		public final boolean isInitialized() {
			byte isInitialized = memoizedIsInitialized;
			if (isInitialized == 1)
				return true;
			if (isInitialized == 0)
				return false;

			memoizedIsInitialized = 1;
			return true;
		}

		@java.lang.Override
		public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
			if (!getMetricBytes().isEmpty()) {
				com.google.protobuf.GeneratedMessageV3.writeString(output, 1, metric_);
			}
			com.google.protobuf.GeneratedMessageV3.serializeStringMapTo(output, internalGetTags(),
					TagsDefaultEntryHolder.defaultEntry, 2);
			if (value_ != 0D) {
				output.writeDouble(3, value_);
			}
			unknownFields.writeTo(output);
		}

		@java.lang.Override
		public int getSerializedSize() {
			int size = memoizedSize;
			if (size != -1)
				return size;

			size = 0;
			if (!getMetricBytes().isEmpty()) {
				size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, metric_);
			}
			for (java.util.Map.Entry<java.lang.String, java.lang.String> entry : internalGetTags().getMap().entrySet()) {
				com.google.protobuf.MapEntry<java.lang.String, java.lang.String> tags__ = TagsDefaultEntryHolder.defaultEntry
						.newBuilderForType().setKey(entry.getKey()).setValue(entry.getValue()).build();
				size += com.google.protobuf.CodedOutputStream.computeMessageSize(2, tags__);
			}
			if (value_ != 0D) {
				size += com.google.protobuf.CodedOutputStream.computeDoubleSize(3, value_);
			}
			size += unknownFields.getSerializedSize();
			memoizedSize = size;
			return size;
		}

		@java.lang.Override
		public boolean equals(final java.lang.Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric)) {
				return super.equals(obj);
			}
			com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric other = (com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric) obj;

			if (!getMetric().equals(other.getMetric()))
				return false;
			if (!internalGetTags().equals(other.internalGetTags()))
				return false;
			if (java.lang.Double.doubleToLongBits(getValue()) != java.lang.Double.doubleToLongBits(other.getValue()))
				return false;
			if (!unknownFields.equals(other.unknownFields))
				return false;
			return true;
		}

		@java.lang.Override
		public int hashCode() {
			if (memoizedHashCode != 0) {
				return memoizedHashCode;
			}
			int hash = 41;
			hash = (19 * hash) + getDescriptor().hashCode();
			hash = (37 * hash) + METRIC_FIELD_NUMBER;
			hash = (53 * hash) + getMetric().hashCode();
			if (!internalGetTags().getMap().isEmpty()) {
				hash = (37 * hash) + TAGS_FIELD_NUMBER;
				hash = (53 * hash) + internalGetTags().hashCode();
			}
			hash = (37 * hash) + VALUE_FIELD_NUMBER;
			hash = (53 * hash) + com.google.protobuf.Internal.hashLong(java.lang.Double.doubleToLongBits(getValue()));
			hash = (29 * hash) + unknownFields.hashCode();
			memoizedHashCode = hash;
			return hash;
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parseFrom(java.nio.ByteBuffer data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parseFrom(java.nio.ByteBuffer data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parseFrom(
				com.google.protobuf.ByteString data) throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parseFrom(
				com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parseFrom(byte[] data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parseFrom(byte[] data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parseFrom(java.io.InputStream input)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parseFrom(java.io.InputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parseDelimitedFrom(java.io.InputStream input)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parseDelimitedFrom(java.io.InputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parseFrom(
				com.google.protobuf.CodedInputStream input) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parseFrom(
				com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
		}

		@java.lang.Override
		public Builder newBuilderForType() {
			return newBuilder();
		}

		public static Builder newBuilder() {
			return DEFAULT_INSTANCE.toBuilder();
		}

		public static Builder newBuilder(com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric prototype) {
			return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
		}

		@java.lang.Override
		public Builder toBuilder() {
			return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
		}

		@java.lang.Override
		protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
			Builder builder = new Builder(parent);
			return builder;
		}

		/**
		 * Protobuf type
		 * {@code com.wl4g.devops.common.bean.umc.model.proto.Metric}
		 */
		public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
				// @@protoc_insertion_point(builder_implements:com.wl4g.devops.common.bean.umc.model.proto.Metric)
				com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricOrBuilder {
			public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
				return com.wl4g.devops.common.bean.umc.model.proto.MetricModel.internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_descriptor;
			}

			@SuppressWarnings({ "rawtypes" })
			protected com.google.protobuf.MapField internalGetMapField(int number) {
				switch (number) {
				case 2:
					return internalGetTags();
				default:
					throw new RuntimeException("Invalid map field number: " + number);
				}
			}

			@SuppressWarnings({ "rawtypes" })
			protected com.google.protobuf.MapField internalGetMutableMapField(int number) {
				switch (number) {
				case 2:
					return internalGetMutableTags();
				default:
					throw new RuntimeException("Invalid map field number: " + number);
				}
			}

			@java.lang.Override
			protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
				return com.wl4g.devops.common.bean.umc.model.proto.MetricModel.internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_fieldAccessorTable
						.ensureFieldAccessorsInitialized(com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.class,
								com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.Builder.class);
			}

			// Construct using
			// com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.newBuilder()
			private Builder() {
				maybeForceBuilderInitialization();
			}

			private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
				super(parent);
				maybeForceBuilderInitialization();
			}

			private void maybeForceBuilderInitialization() {
				if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
				}
			}

			@java.lang.Override
			public Builder clear() {
				super.clear();
				metric_ = "";

				internalGetMutableTags().clear();
				value_ = 0D;

				return this;
			}

			@java.lang.Override
			public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
				return com.wl4g.devops.common.bean.umc.model.proto.MetricModel.internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_descriptor;
			}

			@java.lang.Override
			public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric getDefaultInstanceForType() {
				return com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.getDefaultInstance();
			}

			@java.lang.Override
			public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric build() {
				com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric result = buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(result);
				}
				return result;
			}

			@java.lang.Override
			public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric buildPartial() {
				com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric result = new com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric(
						this);
				int from_bitField0_ = bitField0_;
				result.metric_ = metric_;
				result.tags_ = internalGetTags();
				result.tags_.makeImmutable();
				result.value_ = value_;
				onBuilt();
				return result;
			}

			@java.lang.Override
			public Builder clone() {
				return super.clone();
			}

			@java.lang.Override
			public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
				return super.setField(field, value);
			}

			@java.lang.Override
			public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
				return super.clearField(field);
			}

			@java.lang.Override
			public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
				return super.clearOneof(oneof);
			}

			@java.lang.Override
			public Builder setRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, int index,
					java.lang.Object value) {
				return super.setRepeatedField(field, index, value);
			}

			@java.lang.Override
			public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
				return super.addRepeatedField(field, value);
			}

			@java.lang.Override
			public Builder mergeFrom(com.google.protobuf.Message other) {
				if (other instanceof com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric) {
					return mergeFrom((com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric) other);
				} else {
					super.mergeFrom(other);
					return this;
				}
			}

			public Builder mergeFrom(com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric other) {
				if (other == com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric.getDefaultInstance())
					return this;
				if (!other.getMetric().isEmpty()) {
					metric_ = other.metric_;
					onChanged();
				}
				internalGetMutableTags().mergeFrom(other.internalGetTags());
				if (other.getValue() != 0D) {
					setValue(other.getValue());
				}
				this.mergeUnknownFields(other.unknownFields);
				onChanged();
				return this;
			}

			@java.lang.Override
			public final boolean isInitialized() {
				return true;
			}

			@java.lang.Override
			public Builder mergeFrom(com.google.protobuf.CodedInputStream input,
					com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
				com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric parsedMessage = null;
				try {
					parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
				} catch (com.google.protobuf.InvalidProtocolBufferException e) {
					parsedMessage = (com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric) e.getUnfinishedMessage();
					throw e.unwrapIOException();
				} finally {
					if (parsedMessage != null) {
						mergeFrom(parsedMessage);
					}
				}
				return this;
			}

			private int bitField0_;

			private java.lang.Object metric_ = "";

			/**
			 * <code>string Metric = 1;</code>
			 */
			public java.lang.String getMetric() {
				java.lang.Object ref = metric_;
				if (!(ref instanceof java.lang.String)) {
					com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
					java.lang.String s = bs.toStringUtf8();
					metric_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>string Metric = 1;</code>
			 */
			public com.google.protobuf.ByteString getMetricBytes() {
				java.lang.Object ref = metric_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
					metric_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>string Metric = 1;</code>
			 */
			public Builder setMetric(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}

				metric_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>string Metric = 1;</code>
			 */
			public Builder clearMetric() {

				metric_ = getDefaultInstance().getMetric();
				onChanged();
				return this;
			}

			/**
			 * <code>string Metric = 1;</code>
			 */
			public Builder setMetricBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				checkByteStringIsUtf8(value);

				metric_ = value;
				onChanged();
				return this;
			}

			private com.google.protobuf.MapField<java.lang.String, java.lang.String> tags_;

			private com.google.protobuf.MapField<java.lang.String, java.lang.String> internalGetTags() {
				if (tags_ == null) {
					return com.google.protobuf.MapField.emptyMapField(TagsDefaultEntryHolder.defaultEntry);
				}
				return tags_;
			}

			private com.google.protobuf.MapField<java.lang.String, java.lang.String> internalGetMutableTags() {
				onChanged();
				;
				if (tags_ == null) {
					tags_ = com.google.protobuf.MapField.newMapField(TagsDefaultEntryHolder.defaultEntry);
				}
				if (!tags_.isMutable()) {
					tags_ = tags_.copy();
				}
				return tags_;
			}

			public int getTagsCount() {
				return internalGetTags().getMap().size();
			}

			/**
			 * <code>map&lt;string, string&gt; tags = 2;</code>
			 */

			public boolean containsTags(java.lang.String key) {
				if (key == null) {
					throw new java.lang.NullPointerException();
				}
				return internalGetTags().getMap().containsKey(key);
			}

			/**
			 * Use {@link #getTagsMap()} instead.
			 */
			@java.lang.Deprecated
			public java.util.Map<java.lang.String, java.lang.String> getTags() {
				return getTagsMap();
			}

			/**
			 * <code>map&lt;string, string&gt; tags = 2;</code>
			 */

			public java.util.Map<java.lang.String, java.lang.String> getTagsMap() {
				return internalGetTags().getMap();
			}

			/**
			 * <code>map&lt;string, string&gt; tags = 2;</code>
			 */

			public java.lang.String getTagsOrDefault(java.lang.String key, java.lang.String defaultValue) {
				if (key == null) {
					throw new java.lang.NullPointerException();
				}
				java.util.Map<java.lang.String, java.lang.String> map = internalGetTags().getMap();
				return map.containsKey(key) ? map.get(key) : defaultValue;
			}

			/**
			 * <code>map&lt;string, string&gt; tags = 2;</code>
			 */

			public java.lang.String getTagsOrThrow(java.lang.String key) {
				if (key == null) {
					throw new java.lang.NullPointerException();
				}
				java.util.Map<java.lang.String, java.lang.String> map = internalGetTags().getMap();
				if (!map.containsKey(key)) {
					throw new java.lang.IllegalArgumentException();
				}
				return map.get(key);
			}

			public Builder clearTags() {
				internalGetMutableTags().getMutableMap().clear();
				return this;
			}

			/**
			 * <code>map&lt;string, string&gt; tags = 2;</code>
			 */

			public Builder removeTags(java.lang.String key) {
				if (key == null) {
					throw new java.lang.NullPointerException();
				}
				internalGetMutableTags().getMutableMap().remove(key);
				return this;
			}

			/**
			 * Use alternate mutation accessors instead.
			 */
			@java.lang.Deprecated
			public java.util.Map<java.lang.String, java.lang.String> getMutableTags() {
				return internalGetMutableTags().getMutableMap();
			}

			/**
			 * <code>map&lt;string, string&gt; tags = 2;</code>
			 */
			public Builder putTags(java.lang.String key, java.lang.String value) {
				if (key == null) {
					throw new java.lang.NullPointerException();
				}
				if (value == null) {
					throw new java.lang.NullPointerException();
				}
				internalGetMutableTags().getMutableMap().put(key, value);
				return this;
			}

			/**
			 * <code>map&lt;string, string&gt; tags = 2;</code>
			 */

			public Builder putAllTags(java.util.Map<java.lang.String, java.lang.String> values) {
				internalGetMutableTags().getMutableMap().putAll(values);
				return this;
			}

			private double value_;

			/**
			 * <code>double Value = 3;</code>
			 */
			public double getValue() {
				return value_;
			}

			/**
			 * <code>double Value = 3;</code>
			 */
			public Builder setValue(double value) {

				value_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>double Value = 3;</code>
			 */
			public Builder clearValue() {

				value_ = 0D;
				onChanged();
				return this;
			}

			@java.lang.Override
			public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
				return super.setUnknownFields(unknownFields);
			}

			@java.lang.Override
			public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
				return super.mergeUnknownFields(unknownFields);
			}

			// @@protoc_insertion_point(builder_scope:com.wl4g.devops.common.bean.umc.model.proto.Metric)
		}

		// @@protoc_insertion_point(class_scope:com.wl4g.devops.common.bean.umc.model.proto.Metric)
		private static final com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric DEFAULT_INSTANCE;
		static {
			DEFAULT_INSTANCE = new com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric();
		}

		public static com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric getDefaultInstance() {
			return DEFAULT_INSTANCE;
		}

		private static final com.google.protobuf.Parser<Metric> PARSER = new com.google.protobuf.AbstractParser<Metric>() {
			@java.lang.Override
			public Metric parsePartialFrom(com.google.protobuf.CodedInputStream input,
					com.google.protobuf.ExtensionRegistryLite extensionRegistry)
					throws com.google.protobuf.InvalidProtocolBufferException {
				return new Metric(input, extensionRegistry);
			}
		};

		public static com.google.protobuf.Parser<Metric> parser() {
			return PARSER;
		}

		@java.lang.Override
		public com.google.protobuf.Parser<Metric> getParserForType() {
			return PARSER;
		}

		@java.lang.Override
		public com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric getDefaultInstanceForType() {
			return DEFAULT_INSTANCE;
		}

	}

	private static final com.google.protobuf.Descriptors.Descriptor internal_static_com_wl4g_devops_common_bean_umc_model_proto_MetricAggregate_descriptor;
	private static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internal_static_com_wl4g_devops_common_bean_umc_model_proto_MetricAggregate_fieldAccessorTable;
	private static final com.google.protobuf.Descriptors.Descriptor internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_descriptor;
	private static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_fieldAccessorTable;
	private static final com.google.protobuf.Descriptors.Descriptor internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_TagsEntry_descriptor;
	private static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_TagsEntry_fieldAccessorTable;

	public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
		return descriptor;
	}

	private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
	static {
		java.lang.String[] descriptorData = { "\n>com/wl4g/devops/common/bean/umc/model/"
				+ "proto/metric_model.proto\022+com.wl4g.devop" + "s.common.bean.umc.model.proto\"\257\001\n\017Metric"
				+ "Aggregate\022\020\n\010classify\030\001 \001(\t\022\014\n\004host\030\002 \001("
				+ "\t\022\020\n\010endpoint\030\003 \001(\t\022\021\n\tnamespace\030\004 \001(\t\022\021"
				+ "\n\ttimestamp\030\005 \001(\003\022D\n\007metrics\030\006 \003(\01323.com"
				+ ".wl4g.devops.common.bean.umc.model.proto"
				+ ".Metric\"\241\001\n\006Metric\022\016\n\006Metric\030\001 \001(\t\022K\n\004ta"
				+ "gs\030\002 \003(\0132=.com.wl4g.devops.common.bean.u" + "mc.model.proto.Metric.TagsEntry\022\r\n\005Value"
				+ "\030\003 \001(\001\032+\n\tTagsEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005valu"
				+ "e\030\002 \001(\t:\0028\001b\006proto3" };
		descriptor = com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData,
				new com.google.protobuf.Descriptors.FileDescriptor[] {});
		internal_static_com_wl4g_devops_common_bean_umc_model_proto_MetricAggregate_descriptor = getDescriptor().getMessageTypes()
				.get(0);
		internal_static_com_wl4g_devops_common_bean_umc_model_proto_MetricAggregate_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
				internal_static_com_wl4g_devops_common_bean_umc_model_proto_MetricAggregate_descriptor,
				new java.lang.String[] { "Classify", "Host", "Endpoint", "Namespace", "Timestamp", "Metrics", });
		internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_descriptor = getDescriptor().getMessageTypes().get(1);
		internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
				internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_descriptor,
				new java.lang.String[] { "Metric", "Tags", "Value", });
		internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_TagsEntry_descriptor = internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_descriptor
				.getNestedTypes().get(0);
		internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_TagsEntry_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
				internal_static_com_wl4g_devops_common_bean_umc_model_proto_Metric_TagsEntry_descriptor,
				new java.lang.String[] { "Key", "Value", });
	}

	// @@protoc_insertion_point(outer_class_scope)
}