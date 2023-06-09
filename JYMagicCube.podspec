#
# Be sure to run `pod lib lint JYMagicCube.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see https://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'JYMagicCube'
  s.version          = '1.0.2'
  s.summary          = 'Mobile dynamic solution'
  s.description      = <<-DESC
  It is a complete cross-end native partial card dynamic display technology solution, 
  centered on business empowerment, dedicated to solving UI customization, 
  logic dynamicization, shortening the trial and error cycle, 
  improving human efficiency and reducing package volume and other related issues. 
  This enables businesses to achieve one-time development, online at any time, and multi-end reuse based on Wukong.
                       DESC
  s.license          = { :type => 'Apache License 2.0', :file => 'LICENSE' }
  s.homepage         = 'https://hellobike.yuque.com/org-wiki-nlsyth/hec0gc/mput57lpnzvhnh7k'
  s.author           = 'hellobike'
  s.source           = { :git => 'https://github.com/hellof2e/Wukong.git', :tag => s.version.to_s }
  s.ios.deployment_target = '10.0'
  s.requires_arc = true
  s.static_framework = true
  s.source_files = 'iOS/JYMagicCube/Classes/**/*'
  s.public_header_files = ['iOS/JYMagicCube/Classes/JYMagicCube.h',
                           'iOS/JYMagicCube/Classes/JYMCConfigure.h',
                           'iOS/JYMagicCube/Classes/JYMagicCubeView.h',
                           'iOS/JYMagicCube/Classes/JYMagicCubeView+Tool.h',
                           'iOS/JYMagicCube/Classes/Model/JYMCMetaData.h',
                           'iOS/JYMagicCube/Classes/JYMCActionContext.h',
                           'iOS/JYMagicCube/Classes/JYMagicCubeDefine.h',
                           'iOS/JYMagicCube/Classes/**/JYMCTrackAdapter.h',
                           'iOS/JYMagicCube/Classes/**/JYMCCustomerFactoryProtocol.h',
                           'iOS/JYMagicCube/Classes/**/JYMCStyleManager.h',
                           'iOS/JYMagicCube/Classes/**/JYMCStyleOperation.h',
                           'iOS/JYMagicCube/Classes/**/JYMCStylePrefetcher.h',
                           'iOS/JYMagicCube/Classes/**/Model/JYMCStyleMetaData.h',
                           'iOS/JYMagicCube/Classes/**/JYMCLocalParameters.h',
                           'iOS/JYMagicCube/Classes/**/JYMCTrackContext.h',
                           'iOS/JYMagicCube/Classes/**/JYMCPreload.h',
                           
                           # Alert
                           'iOS/JYMagicCube/Classes/**/JYMagicAlert.h',
                           'iOS/JYMagicCube/Classes/**/JYMCAlertConfig.h',
                           'iOS/JYMagicCube/Classes/**/JYMagicAlertLoader.h',
                           'iOS/JYMagicCube/Classes/**/JYMagicAlertLoaderDelegate.h',
                           'iOS/JYMagicCube/Classes/**/JYMagicAlertLoaderProtocol.h',
                           'iOS/JYMagicCube/Classes/**/JYMagicAlertProtocol.h',
                           'iOS/JYMagicCube/Classes/**/JYMagicAlertLifeCycleDelegate.h',
                          ]
  
  s.dependency 'YogaKit'
  s.dependency 'YYCache'
  s.dependency 'YYCategories'
  s.dependency 'MJExtension'
  s.dependency 'SDWebImage'
  s.dependency 'JYWKJSEngine'
  s.dependency 'lottie-ios', '~> 2.5.3'

end
