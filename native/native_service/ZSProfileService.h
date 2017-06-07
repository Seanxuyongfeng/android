//
// Created by Sean on 2016/8/10.
//

#ifndef ANDROID_ZSPROFILESERVICE_H
#define ANDROID_ZSPROFILESERVICE_H

#include "IZSProfile.h"

namespace android{

    class ZSProfileService : public BnZSProfile {
    public:
        void publish();
        virtual int getPid();
    };

};
#endif //ANDROID_ZSPROFILESERVICE_H
