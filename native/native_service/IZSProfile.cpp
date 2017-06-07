//
// Created by Sean on 2016/8/10.
//

#include "IZSProfile.h"

namespace android{
    class BpZSProfile : public BpInterface<IZSProfile>{
    public:
        BpZSProfile(const sp<IBinder>& binder)
                : BpInterface<IZSProfile>(binder)
        {
        }

        int getPid()
        {
            Parcel data, reply;
            data.writeInterfaceToken(BpZSProfile::getInterfaceDescriptor());
            data.writeInt32(1);
            remote()->transact(TASK_GET_PID, data, &reply);
            int32_t ret = reply.readExceptionCode();
            ret = reply.readInt32();
            return ret;
        }

    };

    IMPLEMENT_META_INTERFACE(ZSProfile, "com.demo.IZSProfile");

    status_t BnZSProfile::onTransact(uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags)
    {
        switch (code) {
            case TASK_GET_PID: {
                CHECK_INTERFACE(IZSProfile, data, reply);
                data.readInt32();
                int pid = getPid();
                reply->writeNoException();
                reply->writeInt32(pid);
                return NO_ERROR;

            } break;

            default:
                return BBinder::onTransact(code, data, reply, flags);
        }
    }
};

