(defproject clojure-aes "0.1.1"
  :description "Pure Clojure implementation of the Advanced Encryption Standard (AES). Supports encryption and decryption for 128, 192, and 256 bit keys."
  :url "https://github.com/GriffinScribe-LLC/clojure-aes"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"	
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :main ^:skip-aot clojure-aes.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
