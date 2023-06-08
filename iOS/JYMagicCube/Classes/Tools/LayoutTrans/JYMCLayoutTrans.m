//
//  JYMCLayoutTrans.m
//  JYMagicCube
//
//  Created by huangshengzhong118 on 2022/2/8.
//

#import "JYMCLayoutTrans.h"

YGFlexDirection YGFlexDirectionFromString(NSString *str) {
    if ([str isEqualToString:@"row"]) {
        return YGFlexDirectionRow;
    }else if ([str isEqualToString:@"column"]) {
        return YGFlexDirectionColumn;
    } else if ([str isEqualToString:@"row-reverse"]) {
        return YGFlexDirectionRowReverse;
    } else if ([str isEqualToString:@"column-reverse"]) {
        return YGFlexDirectionColumnReverse;
    }
    return YGFlexDirectionRow;
}

YGJustify YGJustifyFromString(NSString *str) {
    if ([str isEqualToString:@"flex-start"]) {
        return YGJustifyFlexStart;
    } else if ([str isEqualToString:@"flex-end"]) {
        return YGJustifyFlexEnd;
    } else if ([str isEqualToString:@"center"]) {
        return YGJustifyCenter;
    } else if ([str isEqualToString:@"space-between"]) {
        return YGJustifySpaceBetween;
    } else if ([str isEqualToString:@"space-around"]) {
        return YGJustifySpaceAround;
    }
    return YGJustifyFlexStart;
}

YGAlign YGAlignFromString(NSString *str) {
    if ([str isEqualToString:@"flex-start"]) {
        return YGAlignFlexStart;
    } else if ([str isEqualToString:@"flex-end"]) {
        return YGAlignFlexEnd;
    } else if ([str isEqualToString:@"center"]) {
        return YGAlignCenter;
    } else if ([str isEqualToString:@"baseline"]) {
        return YGAlignBaseline;
    } else if ([str isEqualToString:@"stretch"]) {
        return YGAlignStretch;
    } else if ([str isEqualToString:@"space-between"]) {
        return YGAlignSpaceBetween;
    } else if ([str isEqualToString:@"space-around"]) {
        return YGAlignSpaceAround;
    }
    return YGAlignFlexStart;
}

YGWrap YGWrapFromString(NSString *str) {
    if ([str isEqualToString:@"nowrap"]) {
        return YGWrapNoWrap;
    } else if ([str isEqualToString:@"wrap"]) {
        return YGWrapWrap;
    } else if ([str isEqualToString:@"wrap-reverse"]) {
        return YGWrapWrapReverse;
    }
    return YGWrapNoWrap;
}

YGPositionType YGPositionTypeFromString(NSString *str) {
    YGPositionType type = YGPositionTypeRelative;
    if ([str isEqualToString:@"absolute"] ) {
        type = YGPositionTypeAbsolute;
    }
    
    return type;
}

NSTextAlignment NSTextAlignmentFromString(NSString *str) {
    if (![str isKindOfClass:NSString.class]) {
        return NSTextAlignmentNatural;
    }
    
    if ([str isEqualToString:@"left"]) {
        return NSTextAlignmentLeft;
    } else if ([str isEqualToString:@"right"]) {
        return NSTextAlignmentRight;
    } else if ([str isEqualToString:@"center"]) {
        return NSTextAlignmentCenter;
    }
    return NSTextAlignmentNatural;
}


