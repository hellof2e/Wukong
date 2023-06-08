//
//  JYMCError.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/3/20.
//

#import "JYMCError.h"

NSErrorDomain const JYMagicCubeErrorDomain = @"com.magiccube";
NSString * const JYMagicCubeExceptionName = @"com.magicCube.exception";

@implementation JYMCError

- (NSString *)errorCodeDesctription {
    switch (self.code) {
        case JYMCErrorInvalidURL:
            return @"invalid_url";
        case JYMCErrorVersionNotSupport:
            return @"version_not_support";
        case JYMCErrorDownloadFail:
            return @"download_fail";
        case JYMCErrorCancelled:
            return @"cancelled";
        case JYMCErrorInvalidStyle:
            return @"invalid_style";
        case JYMCErrorInvalidData:
            return @"invalid_data";
        default:
            return @"undefined";
    }
}

+ (instancetype)errorWithCode:(JYMCErrorCode)code {
    return [self errorWithCode:code localizedDescription:nil];
}

+ (instancetype)errorWithCode:(JYMCErrorCode)code localizedDescription:(NSString * _Nullable)localizedDescription {
    if (localizedDescription.length == 0) {
        switch (code) {
            case JYMCErrorInvalidURL:
                localizedDescription = @"style url is invalid";
                break;
            case JYMCErrorVersionNotSupport:
                localizedDescription = @"style version is not support";
                break;
            case JYMCErrorDownloadFail:
                localizedDescription = @"download style fail";
                break;
            case JYMCErrorCancelled:
                localizedDescription = @"operation is cancelled";
                break;
            case JYMCErrorInvalidStyle:
                localizedDescription = @"style is invalid";
                break;
            case JYMCErrorInvalidData:
                localizedDescription = @"data is invalid";
                break;
            default:
                localizedDescription = @"undefined error";
                break;
        }
    }
    NSMutableDictionary *userInfo = [[NSMutableDictionary alloc] init];
    userInfo[NSLocalizedDescriptionKey] = localizedDescription;
    JYMCError *error = [JYMCError errorWithDomain:JYMagicCubeErrorDomain code:code userInfo:userInfo];
    
    return error;
}

@end
