#include <binder/IServiceManager.h>
#include "ZSProfileService.h"

namespace android{

    void ZSProfileService::publish(){
        defaultServiceManager()->addService(String16("ZSProfileService"), this);
    }

    int ZSProfileService::getPid(){
        return 34;
    }

};

