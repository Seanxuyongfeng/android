#include <unistd.h>
#include <stdio.h>
#include <binder/ProcessState.h>
#include <binder/IPCThreadState.h>
#include "ZSProfileService.h"
#include <utils/RefBase.h>

using namespace android;

static sp<ZSProfileService> gZSProfileService;

int main() {

    gZSProfileService = new ZSProfileService();
    gZSProfileService->publish();
    sp<ProcessState> ps(ProcessState::self());
    ps->startThreadPool();
    ps->giveThreadPoolName();
    IPCThreadState::self()->joinThreadPool();

    return 0;
}