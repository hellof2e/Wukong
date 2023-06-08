//
//  JYMCStyleParser.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import "JYMCStyleParser.h"
#import "JYMCError.h"
#import "JYMCElement.h"
#import "JYMCStyleMetaData.h"
#import "JYMCContainerElement.h"
#import "JYMCCustomerElement.h"
#import "JYMCCustomerView.h"
#import <MJExtension/MJExtension.h>

/// 自定义元素 type 前缀
static NSString * const MCCustomerTypePrefix = @"C-";
@implementation JYMCStyleParser

- (JYMCElement *)parseStyle:(JYMCStyleMetaData *)style error:(JYMCError * _Nullable __autoreleasing *)error {
    NSData *jsonData = [style.content dataUsingEncoding:NSUTF8StringEncoding];
    NSError *jsonError;
    NSDictionary *data = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:&jsonError];
    if (jsonError || ![data isKindOfClass:[NSDictionary class]] || data.count == 0) {
        if (error) {
            *error = [JYMCError errorWithCode:JYMCErrorInvalidStyle localizedDescription:@"style json 序列化异常"];
        }
        return nil;
    }

    JYMCElement *element;
    
    @try {
        element = [self elementWithData:data];
    } @catch (NSException *exception) {
        *error = [JYMCError errorWithCode:JYMCErrorInvalidStyle localizedDescription:exception.reason];
        return nil;
    }
    return element;
}

- (nullable JYMCElement *)elementWithData:(NSDictionary *)data {
    if (![data isKindOfClass:[NSDictionary class]] || data.count == 0) {
        return nil;
    }
    
    NSString *type = data[@"type"];
    if (![type isKindOfClass:[NSString class]] || type.length == 0) {
        @throw [self exceptionWithReason:@"type 为空"];
    }
    
    if ([type.capitalizedString hasPrefix:MCCustomerTypePrefix]) { // 自定义类型（兼容首字母大小写）
        type = @"customer";
    }
    
    Class aClass = [JYMCStyleParser2 MCGetElementClass:type];
    if (aClass == NULL) {
        NSString *reason = [NSString stringWithFormat:@"无法解析 type 为 '%@' 类型的元素", type];
        @throw [self exceptionWithReason:reason];
    }
    
    JYMCElement *element = [aClass mj_objectWithKeyValues:data];
    if (element == nil) {
        NSString *reason = [NSString stringWithFormat:@"type 为 '%@' 类型的元素模型化失败", type];
        @throw [self exceptionWithReason:reason];
    }
    
    element.viewClass = [self findViewClassWithElement:element];
    if ([element conformsToProtocol:@protocol(JYMCElementCoupleProtocol)]) {
        NSArray *children = data[@"children"];
        if ([children isKindOfClass:[NSArray class]]) {
            NSMutableArray *childElements = [[NSMutableArray alloc] init];
            for (NSDictionary *child in children) {
                JYMCElement *childElement = [self elementWithData:child];
                if (childElement) {
                    childElement.viewClass = [self findViewClassWithElement:childElement];
                    [childElements addObject:childElement];
                }
            }
            JYMCContainerElement *containerElement = (JYMCContainerElement *)element;
            containerElement.children = childElements;
        }
    }
    
    return element;
}

- (NSException *)exceptionWithReason:(NSString *)reason {
    if (reason.length == 0) {
        reason = @"";
    }
    NSException *exception = [NSException exceptionWithName:JYMagicCubeExceptionName reason:reason userInfo:@{}];
    return exception;
}

#pragma mark - get view Class

- (Class)findViewClassWithElement:(JYMCElement *)element {
    Class aClass = NULL;
    if ([element isKindOfClass:[JYMCCustomerElement class]]) {
        aClass = [JYMCCustomerView class];
    } else{
        aClass = [JYMCStyleParser2 MCGetElementViewClass:element.type];
    }
    return aClass;
}
@end

@implementation JYMCStyleParser2

+ (nullable JYMCElement *)parseStyle:(JYMCStyleMetaData *)style
                               error:(JYMCError * _Nullable * _Nullable)error {
    JYMCElement *element = nil;
    NSData *jsonData = [style.content dataUsingEncoding:NSUTF8StringEncoding];
    NSError *jsonError;
    NSDictionary *data = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:&jsonError];
    if (jsonError || ![data isKindOfClass:[NSDictionary class]] || data.count == 0) {
        if (error) {
            *error = [JYMCError errorWithCode:JYMCErrorInvalidStyle localizedDescription:@"style json 序列化异常"];
        }
        return nil;
    }
    
    @try {
        element = [self elementWithData:data];
    } @catch (NSException *exception) {
        *error = [JYMCError errorWithCode:JYMCErrorInvalidStyle localizedDescription:exception.reason];
        return nil;
    }
    return element;
}

+ (nullable JYMCElement *)elementWithData:(NSDictionary *)data {
    if (![data isKindOfClass:[NSDictionary class]] || data.count == 0) {
        return nil;
    }
    
    NSString *type = data[@"type"];
    if (![type isKindOfClass:[NSString class]] || type.length == 0) {
        @throw [self exceptionWithReason:@"type 为空"];
    }
    
    if ([type.capitalizedString hasPrefix:MCCustomerTypePrefix]) { // 自定义类型（兼容首字母大小写）
        type = @"customer";
    }
    
    Class aClass = [self MCGetElementClass:type];
    if (aClass == NULL) {
        NSString *reason = [NSString stringWithFormat:@"无法解析 type 为 '%@' 类型的元素", type];
        @throw [self exceptionWithReason:reason];
    }
    
    JYMCElement *element = [aClass mj_objectWithKeyValues:data];
    if (element == nil) {
        NSString *reason = [NSString stringWithFormat:@"type 为 '%@' 类型的元素模型化失败", type];
        @throw [self exceptionWithReason:reason];
    }
    
    element.viewClass = [self findViewClassWithElement:element];
    if ([element conformsToProtocol:@protocol(JYMCElementCoupleProtocol)]) {
        NSArray *children = data[@"children"];
        if ([children isKindOfClass:[NSArray class]]) {
            NSMutableArray *childElements = [[NSMutableArray alloc] init];
            for (NSDictionary *child in children) {
                JYMCElement *childElement = [self elementWithData:child];
                if (childElement) {
                    childElement.viewClass = [self findViewClassWithElement:childElement];
                    [childElements addObject:childElement];
                }
            }
            JYMCContainerElement *containerElement = (JYMCContainerElement *)element;
            containerElement.children = childElements;
        }
    }
    
    return element;
}

+ (NSException *)exceptionWithReason:(NSString *)reason {
    if (reason.length == 0) {
        reason = @"";
    }
    NSException *exception = [NSException exceptionWithName:JYMagicCubeExceptionName reason:reason userInfo:@{}];
    return exception;
}

#pragma mark - get view Class

+ (Class)findViewClassWithElement:(JYMCElement *)element {
    Class aClass = NULL;
    if ([element isKindOfClass:[JYMCCustomerElement class]]) {
        aClass = [JYMCCustomerView class];
    } else{
        aClass = [self MCGetElementViewClass:element.type];
    }
    return aClass;
}

+ (Class)MCGetElementViewClass:(NSString *)elementType {
    if (![elementType isKindOfClass:NSString.class] || elementType.length == 0) return nil;
    if ([elementType isEqualToString:@"container"]) {
        return NSClassFromString(@"JYMCContainerView");
    } else if ([elementType isEqualToString:@"counting"]) {
        return NSClassFromString(@"JYMCCountingView");
    } else if ([elementType isEqualToString:@"img"]) {
        return NSClassFromString(@"JYMCImageView");
    } else if ([elementType isEqualToString:@"list"]) {
        return NSClassFromString(@"JYMCListView");
    } else if ([elementType isEqualToString:@"lottie"]) {
        return NSClassFromString(@"JYMCLottieView");
    } else if ([elementType isEqualToString:@"progress"]) {
        return NSClassFromString(@"JYMCProgressView");
    } else if ([elementType isEqualToString:@"span"]) {
        return NSClassFromString(@"JYMCRichTextView");
    } else if ([elementType isEqualToString:@"text"]) {
        return NSClassFromString(@"JYMCTextView");
    }
    return nil;
}

+ (Class)MCGetElementClass:(NSString *)elementType {
    if (![elementType isKindOfClass:NSString.class] || elementType.length == 0) return nil;
    if ([elementType isEqualToString:@"container"]) {
        return NSClassFromString(@"JYMCContainerElement");
    } else if ([elementType isEqualToString:@"counting"]) {
        return NSClassFromString(@"JYMCCountingElement");
    } else if ([elementType isEqualToString:@"customer"]) {
        return NSClassFromString(@"JYMCCustomerElement");
    } else if ([elementType isEqualToString:@"img"]) {
        return NSClassFromString(@"JYMCImageElement");
    } else if ([elementType isEqualToString:@"list"]) {
        return NSClassFromString(@"JYMCListElement");
    } else if ([elementType isEqualToString:@"item"]) {
        return NSClassFromString(@"JYMCListItemElement");
    } else if ([elementType isEqualToString:@"lottie"]) {
        return NSClassFromString(@"JYMCLottieElement");
    } else if ([elementType isEqualToString:@"progress"]) {
        return NSClassFromString(@"JYMCProgressElement");
    } else if ([elementType isEqualToString:@"span"]) {
        return NSClassFromString(@"JYMCRichTextElement");
    } else if ([elementType isEqualToString:@"text"]) {
        return NSClassFromString(@"JYMCTextElement");
    }
    return nil;
}

@end
