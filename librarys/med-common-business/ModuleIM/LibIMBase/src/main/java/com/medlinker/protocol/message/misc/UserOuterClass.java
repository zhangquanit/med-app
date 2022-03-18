// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: misc/user.proto

package com.medlinker.protocol.message.misc;

public final class UserOuterClass {
  private UserOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface UserOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.medlinker.protocol.message.misc.User)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * 为系统用户时: 0=系统消息 1=业务处理 2=新的朋友 3=医生助手
     * </pre>
     *
     * <code>int64 id = 1;</code>
     */
    long getId();

    /**
     * <pre>
     * 目前用户类型: 0=医联用户, 1=经纪人用户 9=系统用户
     * </pre>
     *
     * <code>int32 reference = 2;</code>
     */
    int getReference();

    /**
     * <code>string name = 3;</code>
     */
    String getName();
    /**
     * <code>string name = 3;</code>
     */
    com.google.protobuf.ByteString
        getNameBytes();

    /**
     * <code>string avatar = 4;</code>
     */
    String getAvatar();
    /**
     * <code>string avatar = 4;</code>
     */
    com.google.protobuf.ByteString
        getAvatarBytes();

    /**
     * <pre>
     * 医联用户角色(仅reference=0时有效): 1=医生用户 2=机构用户 3=手机注册用户 4=游客(仅redis) 5=微信 6=QQ 11=营销平台患者 33=第三方账号
     * </pre>
     *
     * <code>int32 type = 5;</code>
     */
    int getType();

    /**
     * <pre>
     *用户所在医院
     * </pre>
     *
     * <code>string hospital = 6;</code>
     */
    String getHospital();
    /**
     * <pre>
     *用户所在医院
     * </pre>
     *
     * <code>string hospital = 6;</code>
     */
    com.google.protobuf.ByteString
        getHospitalBytes();

    /**
     * <pre>
     * 职称
     * </pre>
     *
     * <code>string title = 7;</code>
     */
    String getTitle();
    /**
     * <pre>
     * 职称
     * </pre>
     *
     * <code>string title = 7;</code>
     */
    com.google.protobuf.ByteString
        getTitleBytes();

    /**
     * <pre>
     * 科室
     * </pre>
     *
     * <code>string section = 8;</code>
     */
    String getSection();
    /**
     * <pre>
     * 科室
     * </pre>
     *
     * <code>string section = 8;</code>
     */
    com.google.protobuf.ByteString
        getSectionBytes();
  }
  /**
   * Protobuf type {@code com.medlinker.protocol.message.misc.User}
   */
  public  static final class User extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:com.medlinker.protocol.message.misc.User)
      UserOrBuilder {
    // Use User.newBuilder() to construct.
    private User(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private User() {
      id_ = 0L;
      reference_ = 0;
      name_ = "";
      avatar_ = "";
      type_ = 0;
      hospital_ = "";
      title_ = "";
      section_ = "";
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private User(
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
            case 8: {

              id_ = input.readInt64();
              break;
            }
            case 16: {

              reference_ = input.readInt32();
              break;
            }
            case 26: {
              String s = input.readStringRequireUtf8();

              name_ = s;
              break;
            }
            case 34: {
              String s = input.readStringRequireUtf8();

              avatar_ = s;
              break;
            }
            case 40: {

              type_ = input.readInt32();
              break;
            }
            case 50: {
              String s = input.readStringRequireUtf8();

              hospital_ = s;
              break;
            }
            case 58: {
              String s = input.readStringRequireUtf8();

              title_ = s;
              break;
            }
            case 66: {
              String s = input.readStringRequireUtf8();

              section_ = s;
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
      return UserOuterClass.internal_static_protocol_message_misc_User_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return UserOuterClass.internal_static_protocol_message_misc_User_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              User.class, Builder.class);
    }

    public static final int ID_FIELD_NUMBER = 1;
    private long id_;
    /**
     * <pre>
     * 为系统用户时: 0=系统消息 1=业务处理 2=新的朋友 3=医生助手
     * </pre>
     *
     * <code>int64 id = 1;</code>
     */
    public long getId() {
      return id_;
    }

    public static final int REFERENCE_FIELD_NUMBER = 2;
    private int reference_;
    /**
     * <pre>
     * 目前用户类型: 0=医联用户, 1=经纪人用户 9=系统用户
     * </pre>
     *
     * <code>int32 reference = 2;</code>
     */
    public int getReference() {
      return reference_;
    }

    public static final int NAME_FIELD_NUMBER = 3;
    private volatile Object name_;
    /**
     * <code>string name = 3;</code>
     */
    public String getName() {
      Object ref = name_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        name_ = s;
        return s;
      }
    }
    /**
     * <code>string name = 3;</code>
     */
    public com.google.protobuf.ByteString
        getNameBytes() {
      Object ref = name_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        name_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int AVATAR_FIELD_NUMBER = 4;
    private volatile Object avatar_;
    /**
     * <code>string avatar = 4;</code>
     */
    public String getAvatar() {
      Object ref = avatar_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        avatar_ = s;
        return s;
      }
    }
    /**
     * <code>string avatar = 4;</code>
     */
    public com.google.protobuf.ByteString
        getAvatarBytes() {
      Object ref = avatar_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        avatar_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TYPE_FIELD_NUMBER = 5;
    private int type_;
    /**
     * <pre>
     * 医联用户角色(仅reference=0时有效): 1=医生用户 2=机构用户 3=手机注册用户 4=游客(仅redis) 5=微信 6=QQ 11=营销平台患者 33=第三方账号
     * </pre>
     *
     * <code>int32 type = 5;</code>
     */
    public int getType() {
      return type_;
    }

    public static final int HOSPITAL_FIELD_NUMBER = 6;
    private volatile Object hospital_;
    /**
     * <pre>
     *用户所在医院
     * </pre>
     *
     * <code>string hospital = 6;</code>
     */
    public String getHospital() {
      Object ref = hospital_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        hospital_ = s;
        return s;
      }
    }
    /**
     * <pre>
     *用户所在医院
     * </pre>
     *
     * <code>string hospital = 6;</code>
     */
    public com.google.protobuf.ByteString
        getHospitalBytes() {
      Object ref = hospital_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        hospital_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TITLE_FIELD_NUMBER = 7;
    private volatile Object title_;
    /**
     * <pre>
     * 职称
     * </pre>
     *
     * <code>string title = 7;</code>
     */
    public String getTitle() {
      Object ref = title_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        title_ = s;
        return s;
      }
    }
    /**
     * <pre>
     * 职称
     * </pre>
     *
     * <code>string title = 7;</code>
     */
    public com.google.protobuf.ByteString
        getTitleBytes() {
      Object ref = title_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        title_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int SECTION_FIELD_NUMBER = 8;
    private volatile Object section_;
    /**
     * <pre>
     * 科室
     * </pre>
     *
     * <code>string section = 8;</code>
     */
    public String getSection() {
      Object ref = section_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        section_ = s;
        return s;
      }
    }
    /**
     * <pre>
     * 科室
     * </pre>
     *
     * <code>string section = 8;</code>
     */
    public com.google.protobuf.ByteString
        getSectionBytes() {
      Object ref = section_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        section_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
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
      if (id_ != 0L) {
        output.writeInt64(1, id_);
      }
      if (reference_ != 0) {
        output.writeInt32(2, reference_);
      }
      if (!getNameBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, name_);
      }
      if (!getAvatarBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, avatar_);
      }
      if (type_ != 0) {
        output.writeInt32(5, type_);
      }
      if (!getHospitalBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 6, hospital_);
      }
      if (!getTitleBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 7, title_);
      }
      if (!getSectionBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 8, section_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (id_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(1, id_);
      }
      if (reference_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, reference_);
      }
      if (!getNameBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, name_);
      }
      if (!getAvatarBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, avatar_);
      }
      if (type_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, type_);
      }
      if (!getHospitalBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(6, hospital_);
      }
      if (!getTitleBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(7, title_);
      }
      if (!getSectionBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(8, section_);
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
      if (!(obj instanceof User)) {
        return super.equals(obj);
      }
      User other = (User) obj;

      boolean result = true;
      result = result && (getId()
          == other.getId());
      result = result && (getReference()
          == other.getReference());
      result = result && getName()
          .equals(other.getName());
      result = result && getAvatar()
          .equals(other.getAvatar());
      result = result && (getType()
          == other.getType());
      result = result && getHospital()
          .equals(other.getHospital());
      result = result && getTitle()
          .equals(other.getTitle());
      result = result && getSection()
          .equals(other.getSection());
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getId());
      hash = (37 * hash) + REFERENCE_FIELD_NUMBER;
      hash = (53 * hash) + getReference();
      hash = (37 * hash) + NAME_FIELD_NUMBER;
      hash = (53 * hash) + getName().hashCode();
      hash = (37 * hash) + AVATAR_FIELD_NUMBER;
      hash = (53 * hash) + getAvatar().hashCode();
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getType();
      hash = (37 * hash) + HOSPITAL_FIELD_NUMBER;
      hash = (53 * hash) + getHospital().hashCode();
      hash = (37 * hash) + TITLE_FIELD_NUMBER;
      hash = (53 * hash) + getTitle().hashCode();
      hash = (37 * hash) + SECTION_FIELD_NUMBER;
      hash = (53 * hash) + getSection().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static User parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static User parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static User parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static User parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static User parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static User parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static User parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static User parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static User parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static User parseFrom(
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
    public static Builder newBuilder(User prototype) {
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
     * Protobuf type {@code com.medlinker.protocol.message.misc.User}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.medlinker.protocol.message.misc.User)
        UserOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return UserOuterClass.internal_static_protocol_message_misc_User_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return UserOuterClass.internal_static_protocol_message_misc_User_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                User.class, Builder.class);
      }

      // Construct using com.medlinker.com.medlinker.protocol.message.misc.UserOuterClass.User.newBuilder()
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
        id_ = 0L;

        reference_ = 0;

        name_ = "";

        avatar_ = "";

        type_ = 0;

        hospital_ = "";

        title_ = "";

        section_ = "";

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return UserOuterClass.internal_static_protocol_message_misc_User_descriptor;
      }

      public User getDefaultInstanceForType() {
        return User.getDefaultInstance();
      }

      public User build() {
        User result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public User buildPartial() {
        User result = new User(this);
        result.id_ = id_;
        result.reference_ = reference_;
        result.name_ = name_;
        result.avatar_ = avatar_;
        result.type_ = type_;
        result.hospital_ = hospital_;
        result.title_ = title_;
        result.section_ = section_;
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
        if (other instanceof User) {
          return mergeFrom((User)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(User other) {
        if (other == User.getDefaultInstance()) return this;
        if (other.getId() != 0L) {
          setId(other.getId());
        }
        if (other.getReference() != 0) {
          setReference(other.getReference());
        }
        if (!other.getName().isEmpty()) {
          name_ = other.name_;
          onChanged();
        }
        if (!other.getAvatar().isEmpty()) {
          avatar_ = other.avatar_;
          onChanged();
        }
        if (other.getType() != 0) {
          setType(other.getType());
        }
        if (!other.getHospital().isEmpty()) {
          hospital_ = other.hospital_;
          onChanged();
        }
        if (!other.getTitle().isEmpty()) {
          title_ = other.title_;
          onChanged();
        }
        if (!other.getSection().isEmpty()) {
          section_ = other.section_;
          onChanged();
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
        User parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (User) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private long id_ ;
      /**
       * <pre>
       * 为系统用户时: 0=系统消息 1=业务处理 2=新的朋友 3=医生助手
       * </pre>
       *
       * <code>int64 id = 1;</code>
       */
      public long getId() {
        return id_;
      }
      /**
       * <pre>
       * 为系统用户时: 0=系统消息 1=业务处理 2=新的朋友 3=医生助手
       * </pre>
       *
       * <code>int64 id = 1;</code>
       */
      public Builder setId(long value) {
        
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 为系统用户时: 0=系统消息 1=业务处理 2=新的朋友 3=医生助手
       * </pre>
       *
       * <code>int64 id = 1;</code>
       */
      public Builder clearId() {
        
        id_ = 0L;
        onChanged();
        return this;
      }

      private int reference_ ;
      /**
       * <pre>
       * 目前用户类型: 0=医联用户, 1=经纪人用户 9=系统用户
       * </pre>
       *
       * <code>int32 reference = 2;</code>
       */
      public int getReference() {
        return reference_;
      }
      /**
       * <pre>
       * 目前用户类型: 0=医联用户, 1=经纪人用户 9=系统用户
       * </pre>
       *
       * <code>int32 reference = 2;</code>
       */
      public Builder setReference(int value) {
        
        reference_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 目前用户类型: 0=医联用户, 1=经纪人用户 9=系统用户
       * </pre>
       *
       * <code>int32 reference = 2;</code>
       */
      public Builder clearReference() {
        
        reference_ = 0;
        onChanged();
        return this;
      }

      private Object name_ = "";
      /**
       * <code>string name = 3;</code>
       */
      public String getName() {
        Object ref = name_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          name_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>string name = 3;</code>
       */
      public com.google.protobuf.ByteString
          getNameBytes() {
        Object ref = name_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          name_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string name = 3;</code>
       */
      public Builder setName(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        name_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string name = 3;</code>
       */
      public Builder clearName() {
        
        name_ = getDefaultInstance().getName();
        onChanged();
        return this;
      }
      /**
       * <code>string name = 3;</code>
       */
      public Builder setNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        name_ = value;
        onChanged();
        return this;
      }

      private Object avatar_ = "";
      /**
       * <code>string avatar = 4;</code>
       */
      public String getAvatar() {
        Object ref = avatar_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          avatar_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>string avatar = 4;</code>
       */
      public com.google.protobuf.ByteString
          getAvatarBytes() {
        Object ref = avatar_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          avatar_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string avatar = 4;</code>
       */
      public Builder setAvatar(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        avatar_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string avatar = 4;</code>
       */
      public Builder clearAvatar() {
        
        avatar_ = getDefaultInstance().getAvatar();
        onChanged();
        return this;
      }
      /**
       * <code>string avatar = 4;</code>
       */
      public Builder setAvatarBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        avatar_ = value;
        onChanged();
        return this;
      }

      private int type_ ;
      /**
       * <pre>
       * 医联用户角色(仅reference=0时有效): 1=医生用户 2=机构用户 3=手机注册用户 4=游客(仅redis) 5=微信 6=QQ 11=营销平台患者 33=第三方账号
       * </pre>
       *
       * <code>int32 type = 5;</code>
       */
      public int getType() {
        return type_;
      }
      /**
       * <pre>
       * 医联用户角色(仅reference=0时有效): 1=医生用户 2=机构用户 3=手机注册用户 4=游客(仅redis) 5=微信 6=QQ 11=营销平台患者 33=第三方账号
       * </pre>
       *
       * <code>int32 type = 5;</code>
       */
      public Builder setType(int value) {
        
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 医联用户角色(仅reference=0时有效): 1=医生用户 2=机构用户 3=手机注册用户 4=游客(仅redis) 5=微信 6=QQ 11=营销平台患者 33=第三方账号
       * </pre>
       *
       * <code>int32 type = 5;</code>
       */
      public Builder clearType() {
        
        type_ = 0;
        onChanged();
        return this;
      }

      private Object hospital_ = "";
      /**
       * <pre>
       *用户所在医院
       * </pre>
       *
       * <code>string hospital = 6;</code>
       */
      public String getHospital() {
        Object ref = hospital_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          hospital_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <pre>
       *用户所在医院
       * </pre>
       *
       * <code>string hospital = 6;</code>
       */
      public com.google.protobuf.ByteString
          getHospitalBytes() {
        Object ref = hospital_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          hospital_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       *用户所在医院
       * </pre>
       *
       * <code>string hospital = 6;</code>
       */
      public Builder setHospital(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        hospital_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *用户所在医院
       * </pre>
       *
       * <code>string hospital = 6;</code>
       */
      public Builder clearHospital() {
        
        hospital_ = getDefaultInstance().getHospital();
        onChanged();
        return this;
      }
      /**
       * <pre>
       *用户所在医院
       * </pre>
       *
       * <code>string hospital = 6;</code>
       */
      public Builder setHospitalBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        hospital_ = value;
        onChanged();
        return this;
      }

      private Object title_ = "";
      /**
       * <pre>
       * 职称
       * </pre>
       *
       * <code>string title = 7;</code>
       */
      public String getTitle() {
        Object ref = title_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          title_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <pre>
       * 职称
       * </pre>
       *
       * <code>string title = 7;</code>
       */
      public com.google.protobuf.ByteString
          getTitleBytes() {
        Object ref = title_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          title_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       * 职称
       * </pre>
       *
       * <code>string title = 7;</code>
       */
      public Builder setTitle(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        title_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 职称
       * </pre>
       *
       * <code>string title = 7;</code>
       */
      public Builder clearTitle() {
        
        title_ = getDefaultInstance().getTitle();
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 职称
       * </pre>
       *
       * <code>string title = 7;</code>
       */
      public Builder setTitleBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        title_ = value;
        onChanged();
        return this;
      }

      private Object section_ = "";
      /**
       * <pre>
       * 科室
       * </pre>
       *
       * <code>string section = 8;</code>
       */
      public String getSection() {
        Object ref = section_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          section_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <pre>
       * 科室
       * </pre>
       *
       * <code>string section = 8;</code>
       */
      public com.google.protobuf.ByteString
          getSectionBytes() {
        Object ref = section_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          section_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       * 科室
       * </pre>
       *
       * <code>string section = 8;</code>
       */
      public Builder setSection(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        section_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 科室
       * </pre>
       *
       * <code>string section = 8;</code>
       */
      public Builder clearSection() {
        
        section_ = getDefaultInstance().getSection();
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 科室
       * </pre>
       *
       * <code>string section = 8;</code>
       */
      public Builder setSectionBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        section_ = value;
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


      // @@protoc_insertion_point(builder_scope:com.medlinker.protocol.message.misc.User)
    }

    // @@protoc_insertion_point(class_scope:com.medlinker.protocol.message.misc.User)
    private static final User DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new User();
    }

    public static User getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<User>
        PARSER = new com.google.protobuf.AbstractParser<User>() {
      public User parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new User(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<User> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<User> getParserForType() {
      return PARSER;
    }

    public User getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_protocol_message_misc_User_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_protocol_message_misc_User_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\017misc/user.proto\022\025protocol.message.misc" +
      "\"\203\001\n\004User\022\n\n\002id\030\001 \001(\003\022\021\n\treference\030\002 \001(\005" +
      "\022\014\n\004name\030\003 \001(\t\022\016\n\006avatar\030\004 \001(\t\022\014\n\004type\030\005" +
      " \001(\005\022\020\n\010hospital\030\006 \001(\t\022\r\n\005title\030\007 \001(\t\022\017\n" +
      "\007section\030\010 \001(\tBB\n#com.medlinker.protocol" +
      ".message.miscZ\033grape/protocol/message/mi" +
      "scb\006proto3"
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
    internal_static_protocol_message_misc_User_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_protocol_message_misc_User_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_protocol_message_misc_User_descriptor,
        new String[] { "Id", "Reference", "Name", "Avatar", "Type", "Hospital", "Title", "Section", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
