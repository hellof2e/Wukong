//
//  JYTestShareExportHandler.h
//  JYMagicCube_Example
//
//  Created by gaoshuaibin091 on 2023/5/23.
//  Copyright © 2023 姜灿明. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <JavaScriptCore/JavaScriptCore.h>

@protocol JYShareExport <JSExport>

JSExportAs(goShare, - (void)goShare:(NSDictionary *)params);

@end

NS_ASSUME_NONNULL_BEGIN

@interface JYTestShareExportHandler : NSObject<JYShareExport>

@end

NS_ASSUME_NONNULL_END
