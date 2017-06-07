//
// Created by Sean on 2016/8/10.
//

#ifndef ANDROID_IZSPROFILE_H
#define ANDROID_IZSPROFILE_H

#include <binder/IInterface.h>
#include <utils/RefBase.h>
#include <binder/Parcel.h>

namespace android{

class IZSProfile : public IInterface {
    public:
        enum {
            TASK_GET_PID = IBinder::FIRST_CALL_TRANSACTION,
        };

        virtual int getPid() = 0;

        DECLARE_META_INTERFACE(ZSProfile);
};

class BnZSProfile : public BnInterface<IZSProfile>{
    public:
        virtual status_t onTransact(uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags = 0);
};

};

#endif //ANDROID_IZSPROFILE_H
