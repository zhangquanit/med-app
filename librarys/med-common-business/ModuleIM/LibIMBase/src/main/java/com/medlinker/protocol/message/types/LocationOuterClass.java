// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: types/location.proto

package com.medlinker.protocol.message.types;

public final class LocationOuterClass {
  private LocationOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface LocationOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.medlinker.protocol.message.types.Location)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * 经度
     * </pre>
     *
     * <code>double longitude = 1;</code>
     */
    double getLongitude();

    /**
     * <pre>
     * 纬度
     * </pre>
     *
     * <code>double latitude = 2;</code>
     */
    double getLatitude();
  }
  /**
   * <pre>
   * 位置消息
   * </pre>
   *
   * Protobuf type {@code com.medlinker.protocol.message.types.Location}
   */
  public  static final class Location extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:com.medlinker.protocol.message.types.Location)
      LocationOrBuilder {
    // Use Location.newBuilder() to construct.
    private Location(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Location() {
      longitude_ = 0D;
      latitude_ = 0D;
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private Location(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 9: {

              longitude_ = input.readDouble();
              break;
            }
            case 17: {

              latitude_ = input.readDouble();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return LocationOuterClass.internal_static_protocol_message_types_Location_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return LocationOuterClass.internal_static_protocol_message_types_Location_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              Location.class, Builder.class);
    }

    public static final int LONGITUDE_FIELD_NUMBER = 1;
    private double longitude_;
    /**
     * <pre>
     * 经度
     * </pre>
     *
     * <code>double longitude = 1;</code>
     */
    public double getLongitude() {
      return longitude_;
    }

    public static final int LATITUDE_FIELD_NUMBER = 2;
    private double latitude_;
    /**
     * <pre>
     * 纬度
     * </pre>
     *
     * <code>double latitude = 2;</code>
     */
    public double getLatitude() {
      return latitude_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (longitude_ != 0D) {
        output.writeDouble(1, longitude_);
      }
      if (latitude_ != 0D) {
        output.writeDouble(2, latitude_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (longitude_ != 0D) {
        size += com.google.protobuf.CodedOutputStream
          .computeDoubleSize(1, longitude_);
      }
      if (latitude_ != 0D) {
        size += com.google.protobuf.CodedOutputStream
          .computeDoubleSize(2, latitude_);
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof Location)) {
        return super.equals(obj);
      }
      Location other = (Location) obj;

      boolean result = true;
      result = result && (
          Double.doubleToLongBits(getLongitude())
          == Double.doubleToLongBits(
              other.getLongitude()));
      result = result && (
          Double.doubleToLongBits(getLatitude())
          == Double.doubleToLongBits(
              other.getLatitude()));
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + LONGITUDE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          Double.doubleToLongBits(getLongitude()));
      hash = (37 * hash) + LATITUDE_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          Double.doubleToLongBits(getLatitude()));
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static Location parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Location parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Location parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Location parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Location parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static Location parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static Location parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static Location parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static Location parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static Location parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(Location prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * <pre>
     * 位置消息
     * </pre>
     *
     * Protobuf type {@code com.medlinker.protocol.message.types.Location}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.medlinker.protocol.message.types.Location)
        LocationOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return LocationOuterClass.internal_static_protocol_message_types_Location_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return LocationOuterClass.internal_static_protocol_message_types_Location_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                Location.class, Builder.class);
      }

      // Construct using com.medlinker.com.medlinker.protocol.message.types.LocationOuterClass.Location.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        longitude_ = 0D;

        latitude_ = 0D;

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return LocationOuterClass.internal_static_protocol_message_types_Location_descriptor;
      }

      public Location getDefaultInstanceForType() {
        return Location.getDefaultInstance();
      }

      public Location build() {
        Location result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public Location buildPartial() {
        Location result = new Location(this);
        result.longitude_ = longitude_;
        result.latitude_ = latitude_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof Location) {
          return mergeFrom((Location)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(Location other) {
        if (other == Location.getDefaultInstance()) return this;
        if (other.getLongitude() != 0D) {
          setLongitude(other.getLongitude());
        }
        if (other.getLatitude() != 0D) {
          setLatitude(other.getLatitude());
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        Location parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (Location) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private double longitude_ ;
      /**
       * <pre>
       * 经度
       * </pre>
       *
       * <code>double longitude = 1;</code>
       */
      public double getLongitude() {
        return longitude_;
      }
      /**
       * <pre>
       * 经度
       * </pre>
       *
       * <code>double longitude = 1;</code>
       */
      public Builder setLongitude(double value) {
        
        longitude_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 经度
       * </pre>
       *
       * <code>double longitude = 1;</code>
       */
      public Builder clearLongitude() {
        
        longitude_ = 0D;
        onChanged();
        return this;
      }

      private double latitude_ ;
      /**
       * <pre>
       * 纬度
       * </pre>
       *
       * <code>double latitude = 2;</code>
       */
      public double getLatitude() {
        return latitude_;
      }
      /**
       * <pre>
       * 纬度
       * </pre>
       *
       * <code>double latitude = 2;</code>
       */
      public Builder setLatitude(double value) {
        
        latitude_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 纬度
       * </pre>
       *
       * <code>double latitude = 2;</code>
       */
      public Builder clearLatitude() {
        
        latitude_ = 0D;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:com.medlinker.protocol.message.types.Location)
    }

    // @@protoc_insertion_point(class_scope:com.medlinker.protocol.message.types.Location)
    private static final Location DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new Location();
    }

    public static Location getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Location>
        PARSER = new com.google.protobuf.AbstractParser<Location>() {
      public Location parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new Location(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Location> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<Location> getParserForType() {
      return PARSER;
    }

    public Location getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_protocol_message_types_Location_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_protocol_message_types_Location_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\024types/location.proto\022\026protocol.message" +
      ".types\"/\n\010Location\022\021\n\tlongitude\030\001 \001(\001\022\020\n" +
      "\010latitude\030\002 \001(\001BD\n$com.medlinker.protoco" +
      "l.message.typesZ\034grape/protocol/message/" +
      "typesb\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_protocol_message_types_Location_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_protocol_message_types_Location_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_protocol_message_types_Location_descriptor,
        new String[] { "Longitude", "Latitude", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
