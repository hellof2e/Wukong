//
//  JYMCListView.m
//  JYMagicCube
//
//  Created by 姜灿明 on 2021/2/5.
//

#import <YogaKit/UIView+Yoga.h>

#import "JYMCListView.h"
#import "JYMCListElement.h"
#import "JYMCContainerView.h"
#import "JYMCElementView+Layout.h"
#import "JYMCDataParser.h"
#import "JYMCJSExpressionParser.h"
#import "JYMCStateInfo.h"
#import "JYMCActionContext.h"
#import "UIResponder+JYMagicCube.h"
#import "JYMCElementView+Action.h"

@interface JYMCListItem : NSObject
@property (nonatomic, strong) JYMCStateInfo * state;
@property (nonatomic, copy)   NSString* identifier;
@property (nonatomic, assign) CGSize  itemSize;
@property (nonatomic, assign) UIEdgeInsets edgeInsets;
@end

@implementation JYMCListItem
+ (instancetype)ItemWithState:(JYMCStateInfo *)state identifier:(NSString *)identifier {
    JYMCListItem* item = [[JYMCListItem alloc] init];
    item.state = state;
    item.identifier = identifier;
    
    return item;
}
@end


@interface JYMCListViewItem : UICollectionViewCell
@property (nonatomic, strong) JYMCContainerView* itemView;
@end

@implementation JYMCListViewItem
- (void)applyElement:(JYMCListItemElement *)element reuseIdentifier:(NSString *)reuseIdentifier info:(JYMCStateInfo *)info{
    JYMCListItemElement* itemElement = self.itemView.element;
    [_itemView removeFromSuperview];
    if (![itemElement.itemType isEqualToString:reuseIdentifier]) {
        _itemView = [[JYMCContainerView alloc] init];
        [_itemView applyElement:element];
    }
    [_itemView updateInfo:info.copy];
    _itemView.frame = self.bounds;
    [self.contentView addSubview:_itemView];
    [self refreshLayout];
}

- (void)refreshLayout{
    [_itemView layout_refrehLayout];
    CGRect frame = _itemView.frame;
    frame.origin = CGPointZero;
    _itemView.frame = frame;
}
 
- (void)mc_didActionTimerTick:(JYMCActionContext *)context {
    [[self nextResponder] mc_didActionTimerTick:context];
    [self refreshLayout];
}
@end

@interface JYMCListView ()<UICollectionViewDataSource, UICollectionViewDelegateFlowLayout>

@property (nonatomic, strong) UICollectionView *collectionView;
@property (nonatomic, strong) UICollectionViewFlowLayout* layout;
@property (nonatomic, strong) NSMutableArray<JYMCListItem *>* itemInfos;
@property (nonatomic, assign) UIEdgeInsets collectionViewPadding;
@end

@implementation JYMCListView

#pragma mark - Public

- (void)applyElement:(JYMCListElement *)element {
    [super applyElement:element];

    // registerClass
    [element.items enumerateKeysAndObjectsUsingBlock:^(NSString * _Nonnull key, JYMCListItemElement * _Nonnull obj, BOOL * _Nonnull stop) {
        [self.collectionView registerClass:JYMCListViewItem.class forCellWithReuseIdentifier:obj.itemType];
    }];
}

- (void)layout_applyLayout:(JYMCLayout *)config {
    [super layout_applyLayout:config];
    self.yoga.paddingLeft       = YGPointValue(0);
    self.yoga.paddingTop        = YGPointValue(0);
    self.yoga.paddingRight      = YGPointValue(0);
    self.yoga.paddingBottom     = YGPointValue(0);
    self.yoga.paddingVertical   = YGPointValue(0);
    self.yoga.paddingHorizontal = YGPointValue(0);

    self.collectionViewPadding = [self collectionPaddingInsets];
}

- (void)updateInfo:(JYMCStateInfo *)info {
    [super updateInfo:info];
    JYMCListElement* element = (JYMCListElement*)self.element;
    
    NSString* reuseIdentifierKey = element.itemKey;
    if (reuseIdentifierKey.length == 0) {
        reuseIdentifierKey = kJYMCListItemTypeDefaultKey;
    }
    
    _layout.minimumLineSpacing = 0;
    _layout.minimumInteritemSpacing = 0;
    
    NSString* scrollDirection = [JYMCDataParser getStringValueWithString:element.orientation info:info];
    self.layout.scrollDirection = [@"column" isEqualToString:scrollDirection] ? UICollectionViewScrollDirectionVertical : UICollectionViewScrollDirectionHorizontal;
    
    NSArray* listData = [JYMCDataParser getArrayValueWithString:element.listData info:info];
    self.itemInfos = [[NSMutableArray alloc] init];
    [listData enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        JYMCStateInfo* itemState = self.stateInfo.copy;
        [itemState updateScopeState:obj listItemIndex:idx clearExposes:YES];
        NSString* reuseIdentifier = [JYMCDataParser getStringValueWithString:reuseIdentifierKey info:itemState];
        [self.itemInfos addObject:[JYMCListItem ItemWithState:itemState identifier:reuseIdentifier]];
    }];

    // preload
    [self.collectionView reloadData];
}

#pragma mark - Private

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupView];
    }
    return self;
}

- (void)setupView {
    [self addSubview:self.collectionView];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.collectionView.frame = self.bounds;
}

- (JYMCListItemElement * _Nullable)elementforKey:(NSString *)itemKey {
    JYMCListElement* element = (JYMCListElement*)self.element;

    if ([itemKey isEqualToString:kJYMCListItemTypeDefaultKey]) {
        return (JYMCListItemElement *)element.children.firstObject;
    }
    
    return element.items[itemKey];
}

#pragma mark - delegate
- (nonnull __kindof UICollectionViewCell *)collectionView:(nonnull UICollectionView *)collectionView
                                   cellForItemAtIndexPath:(nonnull NSIndexPath *)indexPath {

    JYMCListItem* itemInfo = self.itemInfos[indexPath.section];
    NSString* reuseIdentifier = [JYMCDataParser getStringValueWithString:itemInfo.identifier info:itemInfo.state];

    JYMCListViewItem* item = [collectionView dequeueReusableCellWithReuseIdentifier:reuseIdentifier forIndexPath:indexPath];
    [item applyElement:[self elementforKey:reuseIdentifier] reuseIdentifier:reuseIdentifier info:itemInfo.state];

    return item;
}

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout insetForSectionAtIndex:(NSInteger)section {
    JYMCListItem* itemInfo = self.itemInfos[section];
    return itemInfo.edgeInsets;
}

- (CGSize)collectionView:(UICollectionView *)collectionView
                  layout:(UICollectionViewLayout *)collectionViewLayout
  sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    
    JYMCListItem* itemInfo = self.itemInfos[indexPath.section];
    JYMCListItemElement* itemElement = [self elementforKey:itemInfo.identifier];
    
    if (itemInfo.itemSize.width > 0 && itemInfo.itemSize.height > 0 ) {
        return itemInfo.itemSize;
    }
    
    itemInfo.edgeInsets = [self edgeInsetsForItemElement:itemElement];
    CGSize size = CGSizeZero;
    
    if (itemElement.layout.width.length > 0) {
        JYMCValue* widthValue = [JYMCDataParser getValueWithString:itemElement.layout.width];
        if (widthValue.unit == JYMCValueUnitPercent) {
            size.width = CGRectGetWidth(self.bounds) * widthValue.value / 100;
        } else {
            size.width = widthValue.value;
        }
    }
    
    if (itemElement.layout.height.length > 0) {
        JYMCValue* heightValue = [JYMCDataParser getValueWithString:itemElement.layout.height];
        if (heightValue.unit == JYMCValueUnitPercent) {
            size.height = CGRectGetHeight(self.bounds) * heightValue.value / 100;
        } else {
            size.height = heightValue.value;
        }
    }
    
    if (size.width == 0 && size.height > 0) {
        JYMCContainerView* itemView = [[JYMCContainerView alloc] init];
        [itemView applyElement:itemElement];
        [itemView updateInfo:itemInfo.state.copy];
        size.width = [itemView widthForHeight:size.height];
        size.width -= itemInfo.edgeInsets.left;
    } else if (size.height == 0 && size.width > 0) {
        JYMCContainerView* itemView = [[JYMCContainerView alloc] init];
        [itemView applyElement:itemElement];
        [itemView updateInfo:itemInfo.state.copy];
        size.height = [itemView heightForWidth:size.width];
        size.height -= itemInfo.edgeInsets.top;
    }
    
    itemInfo.itemSize = size;
    
    // 合并collection view padding
    UIEdgeInsets edgeInsets = itemInfo.edgeInsets;
    if (self.layout.scrollDirection == UICollectionViewScrollDirectionVertical) {
        if (indexPath.section == 0) {
            edgeInsets.top    += self.collectionViewPadding.top;
            edgeInsets.left   += self.collectionViewPadding.left;
            edgeInsets.right  += self.collectionViewPadding.right;
        } else if (indexPath.section < self.itemInfos.count - 1) {
            edgeInsets.left   += self.collectionViewPadding.left;
            edgeInsets.right  += self.collectionViewPadding.right;
        } else {
            edgeInsets.bottom += self.collectionViewPadding.bottom;
            edgeInsets.left   += self.collectionViewPadding.left;
            edgeInsets.right  += self.collectionViewPadding.right;
        }
    } else {
        if (indexPath.section == 0) {
            edgeInsets.top    += self.collectionViewPadding.top;
            edgeInsets.left   += self.collectionViewPadding.left;
            edgeInsets.bottom += self.collectionViewPadding.bottom;
        } else if (indexPath.section < self.itemInfos.count - 1) {
            edgeInsets.top    += self.collectionViewPadding.top;
            edgeInsets.bottom += self.collectionViewPadding.bottom;
        } else {
            edgeInsets.top    += self.collectionViewPadding.top;
            edgeInsets.right  += self.collectionViewPadding.right;
            edgeInsets.bottom += self.collectionViewPadding.bottom;
        }
    }
    itemInfo.edgeInsets = edgeInsets;

    return itemInfo.itemSize;
}

- (NSInteger)collectionView:(nonnull UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return 1;
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return self.itemInfos.count;
}

- (void)collectionView:(UICollectionView *)collectionView
       willDisplayCell:(UICollectionViewCell *)cell
    forItemAtIndexPath:(NSIndexPath *)indexPath {
    [((JYMCListViewItem *)cell).itemView action_applyexpose];
}

#pragma mark -
- (UIEdgeInsets)collectionPaddingInsets {
    
    UIEdgeInsets edgeInsets = UIEdgeInsetsZero;
    if (self.element.layout.paddingHorizontal.length > 0) {
        edgeInsets.left = [JYMCDataParser getFloatPixelWithString:self.element.layout.paddingHorizontal];
        edgeInsets.right = edgeInsets.left;
    }
    
    if (self.element.layout.paddingLeft.length > 0) {
        edgeInsets.left = [JYMCDataParser getFloatPixelWithString:self.element.layout.paddingLeft];
    }
    
    if (self.element.layout.paddingRight.length > 0) {
        edgeInsets.right = [JYMCDataParser getFloatPixelWithString:self.element.layout.paddingRight];
    }
    
    if (self.element.layout.paddingVertical.length > 0) {
        edgeInsets.top = [JYMCDataParser getFloatPixelWithString:self.element.layout.paddingVertical];
        edgeInsets.bottom = edgeInsets.top;
    }
    
    if (self.element.layout.top.length > 0) {
        edgeInsets.top = [JYMCDataParser getFloatPixelWithString:self.element.layout.top];
    }
    
    if (self.element.layout.bottom.length > 0) {
        edgeInsets.bottom = [JYMCDataParser getFloatPixelWithString:self.element.layout.bottom];
    }
    
    return edgeInsets;
}

- (UIEdgeInsets)edgeInsetsForItemElement:(JYMCListItemElement *)itemElement {
    UIEdgeInsets edgeInsets = UIEdgeInsetsZero;
    if (itemElement.layout.marginHorizontal.length > 0) {
        JYMCValue* marginHorizontal = [JYMCDataParser getValueWithString:itemElement.layout.marginHorizontal];
        if (marginHorizontal.unit == JYMCValueUnitPercent) {
            edgeInsets.left = CGRectGetWidth(self.bounds) * marginHorizontal.value / 100;
        } else {
            edgeInsets.left += marginHorizontal.value;
        }
        edgeInsets.right = edgeInsets.left;
    }
    
    if (itemElement.layout.marginLeft.length > 0) {
        JYMCValue* marginLeft = [JYMCDataParser getValueWithString:itemElement.layout.marginLeft];
        if (marginLeft.unit == JYMCValueUnitPercent) {
            edgeInsets.left = CGRectGetWidth(self.bounds) * marginLeft.value / 100;
        } else {
            edgeInsets.left = marginLeft.value;
        }
    }
    
    if (itemElement.layout.marginRight.length > 0) {
        JYMCValue* marginRight = [JYMCDataParser getValueWithString:itemElement.layout.marginRight];
        if (marginRight.unit == JYMCValueUnitPercent) {
            edgeInsets.right = CGRectGetWidth(self.bounds) * marginRight.value / 100;
        } else {
            edgeInsets.right = marginRight.value;
        }
    }
    
    if (itemElement.layout.marginVertical.length > 0) {
        JYMCValue* marginVertical = [JYMCDataParser getValueWithString:itemElement.layout.marginVertical];
        if (marginVertical.unit == JYMCValueUnitPercent) {
            edgeInsets.top = CGRectGetHeight(self.bounds) * marginVertical.value / 100;
        } else {
            edgeInsets.top = marginVertical.value;
        }
        edgeInsets.bottom = edgeInsets.top;
    }
    
    if (itemElement.layout.marginTop.length > 0) {
        JYMCValue* marginTop = [JYMCDataParser getValueWithString:itemElement.layout.marginTop];
        if (marginTop.unit == JYMCValueUnitPercent) {
            edgeInsets.top = CGRectGetHeight(self.bounds) * marginTop.value / 100;
        } else {
            edgeInsets.top = marginTop.value;
        }
    }
    
    if (itemElement.layout.marginBottom.length > 0) {
        JYMCValue* marginBottom = [JYMCDataParser getValueWithString:itemElement.layout.marginBottom];
        if (marginBottom.unit == JYMCValueUnitPercent) {
            edgeInsets.bottom = CGRectGetHeight(self.bounds) * marginBottom.value / 100;
        } else {
            edgeInsets.bottom = marginBottom.value;
        }
    }
    
    return edgeInsets;
}

#pragma mark - getters
- (UICollectionView *)collectionView {
    if (!_collectionView) {
        _layout = [[UICollectionViewFlowLayout alloc] init];
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:_layout];
        _collectionView.delegate = self;
        _collectionView.dataSource = self;
        _collectionView.backgroundView = nil;
        _collectionView.backgroundColor = [UIColor clearColor];
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.showsHorizontalScrollIndicator = NO;
        [_collectionView registerClass:[JYMCListViewItem class] forCellWithReuseIdentifier:kJYMCListItemTypeDefaultKey];
    }
    return _collectionView;
}

@end
