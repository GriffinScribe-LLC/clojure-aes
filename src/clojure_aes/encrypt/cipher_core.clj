(ns clojure-aes.encrypt.cipher-core
  (:require [clojure-aes.shared :as shared]
            [clojure-aes.encrypt.sub-bytes :as sb]
            [clojure-aes.encrypt.mix-columns :as mc]
            [clojure-aes.encrypt.shift-row :as sr]
            [clojure-aes.add-round-key :as ark]
            [clojure-aes.utils :as utils]))

(defn cipher
  "Encrypts the passed-in `plain-text`, a 4x4 byte array, using the designated `secret-key` (a byte arrray), the `expanded-key` (another byte array) and the number of bits in the secret-key."
  [plain-text expanded-key num-bits secret-key]
  (let [merged-plaintext (apply str (flatten
                                     (utils/byte2-to-hex-string plain-text)))
        merged-secret-key (apply str (flatten
                                      (utils/byte-to-hex-string secret-key)))
        state (atom plain-text)
        num-rounds (shared/number-of-rounds num-bits)
        loop-transform #(doseq [n (range 1 num-rounds)]
                          (utils/debug-aes n "start " (utils/matrix-transposition @state))
                                        ;(utils/print-array @state "round start" n)
                          (reset! state
                                  (-> @state
                                      (sb/subBytes n)
                                      (sr/shift-rows n)
                                      (mc/mixColumns n)
                                      (ark/addRoundKey expanded-key n))))
        final-transformation #(-> @state
                                  (sb/subBytes num-rounds)
                                  (sr/shift-rows num-rounds)
                                  (ark/addRoundKey expanded-key
                                                   num-rounds))]
    (println "\nStarting encryption")
    (println (str "PLAINTEXT:  " merged-plaintext))
    (println (str "KEY:        " merged-secret-key))
    (println "\nCIPHER (ENCRYPT):")
    (utils/debug-aes 0 "input " plain-text)
                                        ;(utils/print-array @state "round start" 0)
    
    (reset! state (ark/addRoundKey (utils/matrix-transposition @state) expanded-key 0))
    (loop-transform)
    (reset! state (final-transformation))
                                        ;(utils/print-array @state "encryption finished" num-rounds)
    (utils/debug-aes num-rounds  "output" (utils/matrix-transposition @state))
    (println "Plaintext " merged-plaintext
             " successfully encrypted to"
             (apply str (flatten
                         (utils/byte2-to-hex-string
                          (utils/matrix-transposition @state)))))
    (println "Encryption finished.")
    (utils/matrix-transposition @state)))
