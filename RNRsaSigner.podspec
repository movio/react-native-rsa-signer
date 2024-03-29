Pod::Spec.new do |s|
  s.name         = "RNRsaSigner"
  s.version      = "1.3.2"
  s.summary      = "RNRsaSigner"
  s.description  = <<-DESC
                  RNRsaSigner
                  DESC
  s.homepage     = "https://numero.co"
  s.license      = "MIT"
  s.author       = { "author" => "matthias@movio.co" }
  s.platforms    = { :ios => "7.0" }
  s.source       = { :git => "https://github.com/movio/react-native-rsa-signer.git", :tag => "master" }
  s.source_files  = "ios/**/*.{h,m,swift}"
  s.requires_arc = true
  s.dependency "React-Core"

end
