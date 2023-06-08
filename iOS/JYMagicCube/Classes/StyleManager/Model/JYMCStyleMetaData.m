//
//  JYMCStyleMetaData.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/4/25.
//

#import "JYMCStyleMetaData.h"
#import <YYCategories/YYCategories.h>
#import "JYMCElement.h"

@interface JYMCStyleMetaData ()
@property (nonatomic, copy) NSString* contentMD5String;
@end

@implementation JYMCStyleMetaData

- (NSString *)contentMD5String {
    if (!_contentMD5String) {
        _contentMD5String = _content.md5String;
    }
    return _contentMD5String;
}

+ (BOOL)supportsSecureCoding {
    return YES;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    if (self = [super init]) {
        if (!aDecoder) {
            return self;
        }
        _guid = [aDecoder decodeObjectOfClass:[NSString class] forKey:@"guid"];
        _url = [aDecoder decodeObjectOfClass:[NSString class] forKey:@"url"];
        _content = [aDecoder decodeObjectOfClass:[NSString class] forKey:@"content"];
        _supportedVersion = [aDecoder decodeObjectOfClass:[NSString class] forKey:@"supportedVersion"];
    }
    return self;
}

- (void)encodeWithCoder:(NSCoder *)aCoder {
    [aCoder encodeObject:_guid forKey:@"guid"];
    [aCoder encodeObject:_url forKey:@"url"];
    [aCoder encodeObject:_content forKey:@"content"];
    [aCoder encodeObject:_supportedVersion forKey:@"supportedVersion"];
}

- (BOOL)isEqual:(JYMCStyleMetaData *)object {
    return [object.contentMD5String isEqualToString:self.contentMD5String];
}

- (BOOL)needExecuteScript {
    return (self.rootElement.jsLifecycle && self.rootElement.logic.length > 0);
}

@end
