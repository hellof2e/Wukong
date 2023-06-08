//
//  JYMCActionInternalContext.m
//  JYMagicCube
//
//  Created by gaoshuaibin091 on 2022/7/5.
//

#import "JYMCActionInternalContext.h"

@implementation JYMCActionInternalContext

- (JYMCActionClickType)type
{
    if (self.url.length > 0) {
        return JYMCActionClickTypeJump;
    } else if (self.js) {
        return JYMCActionClickTypeJS;
    } else {
        return JYMCActionClickTypeNone;
    }
}

@end
