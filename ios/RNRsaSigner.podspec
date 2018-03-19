Pod::Spec.new do |s|
  s.name         = "RNRsaSigner"
  s.version      = "1.0.0"
  s.summary      = "RNRsaSigner"
  s.description  = <<-DESC
                  RNRsaSigner
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  s.author       = { "author" => "matthias@movio.co" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/movio/react-native-rsa-signer.git", :tag => "master" }
  s.source_files  = "RNRsaSigner/**/*.{h,m}"
  s.requires_arc = true
  s.dependency "React"

end
