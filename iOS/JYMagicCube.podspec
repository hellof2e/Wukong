#
# Be sure to run `pod lib lint JYMagicCube.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see https://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'JYMagicCube'
  s.version          = '1.0.0'
  s.summary          = 'Mobile dynamic solution'
  s.description      = <<-DESC
  It is a complete cross-end native partial card dynamic display technology solution, 
  centered on business empowerment, dedicated to solving UI customization, 
  logic dynamicization, shortening the trial and error cycle, 
  improving human efficiency and reducing package volume and other related issues. 
  This enables businesses to achieve one-time development, online at any time, and multi-end reuse based on Wukong.
                       DESC
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'hellobike' => 'hellobike@hellobike.com' }
  s.ios.deployment_target = '9.0'
  s.static_framework = true
  s.source_files = 'JYMagicCube/Classes/**/*'
  s.resource_bundles = {
    'JYMagicCube' => ['JYMagicCube/Assets/**/*.{xib,gif,xcassets,png,json,mp3,plist}']
  }
  s.public_header_files = ['JYMagicCube/Classes/JYMagicCube.h',
                           'JYMagicCube/Classes/JYMCConfigure.h',
                           'JYMagicCube/Classes/JYMagicCubeView.h',
                           'JYMagicCube/Classes/JYMagicCubeView+Tool.h',
                           'JYMagicCube/Classes/Model/JYMCMetaData.h',
                           'JYMagicCube/Classes/JYMCActionContext.h',
                           'JYMagicCube/Classes/JYMagicCubeDefine.h',
                           'JYMagicCube/Classes/**/JYMCTrackAdapter.h',
                           'JYMagicCube/Classes/**/JYMCCustomerFactoryProtocol.h',
                           'JYMagicCube/Classes/**/JYMCStyleManager.h',
                           'JYMagicCube/Classes/**/JYMCStyleOperation.h',
                           'JYMagicCube/Classes/**/JYMCStylePrefetcher.h',
                           'JYMagicCube/Classes/**/Model/JYMCStyleMetaData.h',
                           'JYMagicCube/Classes/**/JYMCLocalParameters.h',
                           'JYMagicCube/Classes/**/JYMCTrackContext.h',
                           'JYMagicCube/Classes/**/JYMCPreload.h',
                           
                           # Alert
                           'JYMagicCube/Classes/**/JYMagicAlert.h',
                           'JYMagicCube/Classes/**/JYMCAlertConfig.h',
                           'JYMagicCube/Classes/**/JYMagicAlertLoader.h',
                           'JYMagicCube/Classes/**/JYMagicAlertLoaderDelegate.h',
                           'JYMagicCube/Classes/**/JYMagicAlertLoaderProtocol.h',
                           'JYMagicCube/Classes/**/JYMagicAlertProtocol.h',
                           'JYMagicCube/Classes/**/JYMagicAlertLifeCycleDelegate.h',
                          ]
  
  s.dependency 'YogaKit'
  s.dependency 'YYCache'
  s.dependency 'YYCategories'
  s.dependency 'MJExtension'
  s.dependency 'SDWebImage'
  s.dependency 'JYWKJSEngine'
  s.dependency 'lottie-ios', '~> 2.5.3'

end
