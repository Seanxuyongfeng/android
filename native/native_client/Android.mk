LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := main.cpp

LOCAL_C_INCLUDES := \
                $(LOCAL_PATH)/../native_service \
                system/core/include \
                frameworks/native/include

LOCAL_SHARED_LIBRARIES := \
    libzsprofile \
    libcutils \
    liblog \
    libbinder \
    libutils \
    libdl

LOCAL_MODULE := client

include $(BUILD_EXECUTABLE)