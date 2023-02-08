(ns clojure-aes.main
  (:gen-class)
  (:require [clojure-aes.core :as core]
            [clojure-aes.utils :as u]))

(defn -main
  "Initial entry point for command line usage"
  [& [secret-key message key-length scheme debug-print]]

  (when (= "-v" debug-print)
    (println "\nActual Input:")
    (println "message: "  message )
    (println "key-length:" key-length)
    (println "scheme:" scheme))
  
  (if-not (and secret-key message key-length scheme)
    
    (do
      (println "\nRequired input: secret-key, message, key-length (in bits), scheme ")
      (println "Secret-key (hex-formatted). Example: 526b00c38662e0c58a49ce6ccc83fe9a")
      (println "Message (hex-formatted, 16-bytes). Example: 629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80 ")
      (println "Key-length: 192")
      (println "Scheme: pass the `-d` flag for decryption; `-e` for encryption")
      (println "verbose: pass the `-v` flag for verbose logging of internal state representation throughout encryption and decryption.")
      (println "\n Example usage: java -jar target/uberjar/clojure-aes-0.1.0-SNAPSHOT-standalone.jar 629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80 526b00c38662e0c58a49ce6ccc83fe9a 192 -e "))
    
    (let [length (Integer/valueOf key-length)
          verbose? (= "-v" debug-print)]
      (when verbose?
        (reset! u/debug-print true ))
      (when (= scheme "-d")
        (let [decrypted-result (core/decrypt
                                secret-key message length verbose?)]
          (println decrypted-result)
          decrypted-result))
      (when (= scheme "-e")
        (let [encrypted-result (core/encrypt
                                secret-key message length verbose?)]
          (println encrypted-result)
          encrypted-result)))))
