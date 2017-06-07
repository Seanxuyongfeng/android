#include <unistd.h>
#include <stdio.h>
#include <binder/ProcessState.h>
#include <binder/IPCThreadState.h>
#include "ZSProfileService.h"
#include <utils/RefBase.h>
#include <binder/IServiceManager.h>

int main() {
    using namespace android;
    sp<IServiceManager> sm = defaultServiceManager();  
    sp<IBinder> binder =sm->getService(String16("ZSProfileService"));  
    sp<IZSProfile> zsprofile =interface_cast<IZSProfile>(binder);  
    printf("zsprofile Service pid %d, client pid:%d \n",zsprofile->getPid(), getpid());  
    return 0;
}