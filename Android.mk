# Copyright 2007-2008 The Android Open Source Project

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under)
LOCAL_STATIC_JAVA_LIBRARIES :=poi android transforms achart
LOCAL_PACKAGE_NAME := InfoMonitor
LOCAL_CERTIFICATE := platform
WITH_DEXPREOPT = false; 
LOCAL_PROGUARD_ENABLED := disabled


include $(BUILD_PACKAGE)
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES :=poi:libs/poi-3.11-beta2-20140822.jar \
android:libs/android-support-v4.jar \
transforms:libs/transforms.jar \
achart:libs/achartengine-1.0.0.jar
include $(BUILD_MULTI_PREBUILT)