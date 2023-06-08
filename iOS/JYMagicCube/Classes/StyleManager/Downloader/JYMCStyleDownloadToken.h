//
//  JYMCStyleDownloadToken.h
//  JYMagicCube
//
//  Created by wuyunhai on 2021/4/27.
//

#import <Foundation/Foundation.h>
#import <JYMagicCube/JYMCStyleOperation.h>

@protocol JYMCStyleDownloadToken <JYMCStyleOperation>

- (NSURL*)url;

- (void)cancel;

@end
