(ns clojure-aes.decrypt.inverse-cipher-core
  (:require [clojure-aes.shared :as shared]
            [clojure-aes.decrypt.inverse-shift-rows :as isr]
            [clojure-aes.utils :as utils]
            [clojure-aes.decrypt.inverse-sub-bytes :as isb]
            [clojure-aes.decrypt.inverse-mix-columns :as imc]
            [clojure-aes.add-round-key :as ark]))

(defn inv-cipher
  "Decrypts the passed in `cipher-text` a 4x4 byte array, using the designated secret-key, a byte array."
  [cipher-text expanded-key num-bits secret-key]
  (let [merged-cipher-text  (apply str
                                   (flatten (utils/byte2-to-hex-string
                                             cipher-text)))
        merged-secret-key (apply str (flatten
                                      (utils/byte-to-hex-string secret-key)))
        state (atom cipher-text)
        num-rounds (shared/number-of-rounds num-bits)
        loop-transform #(doseq [n (reverse (range 1 num-rounds))]
                          (utils/print-array @state "round start" n)
                          (reset! state
                                  (-> @state
                                      (isr/inv-shift-rows n)
                                      (isb/inv-sub-bytes n)
                                      (ark/addRoundKey expanded-key n)
                                      (imc/inv-mixColumns n))))
        final-transformation #(-> @state
                                  (isr/inv-shift-rows 0)
                                  (isb/inv-sub-bytes 0)
                                  (ark/addRoundKey expanded-key 0))]
    
    (println "\nStarting decryption")
    (println (str "Cipher TEXT:       " merged-cipher-text))
    (println (str "KEY:           " merged-secret-key))
    (utils/print-array @state "round start" 0)
    (reset! state (ark/addRoundKey
                   (utils/matrix-transposition @state)
                   expanded-key num-rounds))
    (loop-transform)
    (reset! state (final-transformation))
    (utils/print-array @state "decryption finished" num-rounds)
    (utils/debug-aes num-rounds  "output" (utils/matrix-transposition @state))
    (println "Cipher text  " merged-cipher-text " successfully decrypted as"
             (apply str (flatten
                         (utils/byte2-to-hex-string
                          (utils/matrix-transposition @state)))))
    (println "Decryption finished.")
    
    (utils/matrix-transposition @state)))
