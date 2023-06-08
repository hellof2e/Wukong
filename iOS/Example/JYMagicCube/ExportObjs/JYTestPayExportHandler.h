//
//  JYTestPayExportHandler.h
//  JYMagicCube_Example
//
//  Created by gaoshuaibin091 on 2023/5/17.
//  Copyright © 2023 姜灿明. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <JavaScriptCore/JavaScriptCore.h>

@protocol JYPayExport <JSExport>

JSExportAs(goPay, - (void)goPay:(NSDictionary *)params);

@end

NS_ASSUME_NONNULL_BEGIN

@interface JYTestPayExportHandler : NSObject<JYPayExport>

@end

NS_ASSUME_NONNULL_END
