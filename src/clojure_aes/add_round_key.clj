(ns clojure-aes.add-round-key
  (:require [clojure-aes.utils :as utils]
            [clojure-aes.shared :as shared]))

(defn addRoundKey
  "The round key is XORed with the result of the above layer
  `State` is a 4 x nb (nb usually = 4) matrix, `expanded-key` is a 4 x nk matrix, where nk = number of 32 bit in key, for 128 bits this is 4. 192 bits nk = 6, 256 bits nk = 8"
  [state expanded-key round-num]
  (let [round-key (utils/matrix-transposition
                   (vec
                    (for [x  (range (* 4 round-num)
                                    (+ (* round-num 4) 4))]
                      (shared/separate-four-bytes
                       (nth expanded-key x)))))
        xor-cols (fn [state-col round-key-col]
                   (vec (for [z (range 0 4)]
                          (bit-xor (nth state-col z)
                                   (nth round-key-col z)))))
        new-state (vec
                   (for [y (range 0 4)]
                     (let [state-col (nth state y)
                           round-key-col  (nth round-key y)]
                       (xor-cols state-col round-key-col))))]
    (utils/debug-aes round-num "k_sch "
                     (utils/matrix-transposition round-key))
    (utils/print-array round-key "round key value" round-num)
    (utils/print-array new-state "state after addRoundKey:" round-num)
    new-state))
