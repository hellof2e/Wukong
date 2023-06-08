#
# Be sure to run `pod lib lint JYWKJSEngine.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see https://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'JYWKJSEngine'
  s.version          = '1.0.0'
  s.summary          = 'js runtime engine.'
  s.description      = <<-DESC
  js runtime engine of Wukong.
                       DESC
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'hellobike' => 'hellobike@hellobike.com' }
  s.swift_version = '5.0'
  s.ios.deployment_target = '9.0'
  s.static_framework = true
  s.pod_target_xcconfig = {
    'DEFINES_MODULE' => 'YES'
  }
  s.source_files = 'JYWKJSEngine/Classes/**/*'
  s.public_header_files = ['JYWKJSEngine/Classes/PublicHeaders/*.h']

end
