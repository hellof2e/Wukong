//
//  JYMagicCubeMacro.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/2/23.
//

#import <Foundation/Foundation.h>
#import <os/lock.h>
#import <libkern/OSAtomic.h>

#ifndef JYMagicCubeMacro_h
#define JYMagicCubeMacro_h

#endif /* JYMagicCubeMacro_h */

#define MC_USE_OS_UNFAIR_LOCK TARGET_OS_MACCATALYST ||\
    (__IPHONE_OS_VERSION_MIN_REQUIRED >= __IPHONE_10_0) ||\
    (__MAC_OS_X_VERSION_MIN_REQUIRED >= __MAC_10_12) ||\
    (__TV_OS_VERSION_MIN_REQUIRED >= __TVOS_10_0) ||\
    (__WATCH_OS_VERSION_MIN_REQUIRED >= __WATCHOS_3_0)

#ifndef MC_LOCK_DECLARE
#if MC_USE_OS_UNFAIR_LOCK
#define MC_LOCK_DECLARE(lock) os_unfair_lock lock
#else
#define MC_LOCK_DECLARE(lock) os_unfair_lock lock API_AVAILABLE(ios(10.0), tvos(10), watchos(3), macos(10.12)); \
OSSpinLock lock##_deprecated;
#endif
#endif

#ifndef MC_LOCK_INIT
#if MC_USE_OS_UNFAIR_LOCK
#define MC_LOCK_INIT(lock) lock = OS_UNFAIR_LOCK_INIT
#else
#define MC_LOCK_INIT(lock) if (@available(iOS 10, tvOS 10, watchOS 3, macOS 10.12, *)) lock = OS_UNFAIR_LOCK_INIT; \
else lock##_deprecated = OS_SPINLOCK_INIT;
#endif
#endif

#ifndef MC_LOCK
#if MC_USE_OS_UNFAIR_LOCK
#define MC_LOCK(lock) os_unfair_lock_lock(&lock)
#else
#define MC_LOCK(lock) if (@available(iOS 10, tvOS 10, watchOS 3, macOS 10.12, *)) os_unfair_lock_lock(&lock); \
else OSSpinLockLock(&lock##_deprecated);
#endif
#endif

#ifndef MC_UNLOCK
#if MC_USE_OS_UNFAIR_LOCK
#define MC_UNLOCK(lock) os_unfair_lock_unlock(&lock)
#else
#define MC_UNLOCK(lock) if (@available(iOS 10, tvOS 10, watchOS 3, macOS 10.12, *)) os_unfair_lock_unlock(&lock); \
else OSSpinLockUnlock(&lock##_deprecated);
#endif
#endif

#ifndef dispatch_main_async_safe
#define dispatch_main_async_safe(block)\
    if (dispatch_queue_get_label(DISPATCH_CURRENT_QUEUE_LABEL) == dispatch_queue_get_label(dispatch_get_main_queue())) {\
        block();\
    } else {\
        dispatch_async(dispatch_get_main_queue(), block);\
    }
#endif
