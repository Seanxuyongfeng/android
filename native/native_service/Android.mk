LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := IZSProfile.cpp

LOCAL_SHARED_LIBRARIES := \
                libbinder \
                libcutils \
                libutils

LOCAL_MODULE:= libzsprofile

include $(BUILD_SHARED_LIBRARY)
##############################################
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
    main.cpp \
    ZSProfileService.cpp \
    IZSProfile.cpp

LOCAL_C_INCLUDES := \
                system/core/include \
                frameworks/native/include

LOCAL_SHARED_LIBRARIES := \
    libzsprofile \
    libcutils \
    liblog \
    libbinder \
    libutils \
    libdl

LOCAL_MODULE := zsprofile

include $(BUILD_EXECUTABLE)