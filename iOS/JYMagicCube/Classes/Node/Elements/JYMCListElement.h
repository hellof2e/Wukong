//
//  JYMCListElement.h
//  JYMagicCube
//
//  Created by 姜灿明 on 2020/10/10.
//

#import "JYMCContainerElement.h"

NS_ASSUME_NONNULL_BEGIN

FOUNDATION_EXTERN NSString* const kJYMCListItemTypeDefaultKey;

@interface JYMCListItemElement : JYMCContainerElement
@property (nonatomic, copy) NSString* itemType;
@end

@interface JYMCListElement : JYMCElement<JYMCElementCoupleProtocol>
@property (nonatomic, copy)    NSString* listData;     
@property (nonatomic, copy)    NSString* orientation;
@property (nonatomic, copy)    NSString* itemKey;
@property (nonatomic, strong)  NSDictionary<NSString*, JYMCListItemElement*>* items;
@end


NS_ASSUME_NONNULL_END
