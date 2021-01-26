(defproject com.griffinscribe/clojure-aes "0.1.4"
  :description "Pure Clojure implementation of the Advanced Encryption Standard (AES). Supports encryption and decryption for 128, 192, and 256 bit keys."
  :url "https://github.com/griffinscribe-llc/clojure-aes"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"	
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :min-lein-version "2.0.0"
  :main clojure-aes.main
  :target-path "target/%s"
  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  :resource-paths ["resources" "target/cljsbuild"]
  :repositories [["releases" {:url "https://repo.clojars.org"
                              :creds :gpg}]]
  :deploy-repositories {"releases" {:url "https://repo.clojars.org" :creds :gpg}}
  :deploy-branches ["main"]
  :scm {:name "git"
        :url "https://github.com/griffinscribe-llc/clojure-aes"}
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
