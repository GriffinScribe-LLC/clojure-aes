(ns clojure-aes.shared)

(defn msb-1?
  "Determines if the most significant bit in the byte is 1."
  [byte]
  (= 1 (bit-shift-right byte 7)))

(defn number-of-rounds
  "Computes the number of rounds based on the number of bits passed in."
  [num-bits]
  (cond (= num-bits (or 128 "128")) 10
        (= num-bits (or 192 "192")) 12
        (= num-bits (or 256 "256")) 14
        :else nil))

(defn number-of-32bit-words-in-key
  "Computes the number of 32-bit words in the key."
  [num-bits]
  (cond (= num-bits (or 128 "128")) 4
        (= num-bits (or 192 "192")) 6
        (= num-bits (or 256 "256")) 8
        :else nil))

(defn combine-four-bytes
  "Takes an array of four bytes, and combines them into a single word."
  [byte-array]
  (let [[byte1 byte2 byte3 byte4] byte-array
        word (+ byte4
                (bit-shift-left byte3 8)
                (bit-shift-left byte2 16)
                (bit-shift-left byte1 24))]
    word))

(defn separate-four-bytes
  "Takes a word (4 bytes) and returns a vector with each individual byte of the word."
  [word]
  (let [extract-fn (fn [offset mask]
                     (bit-shift-right
                      (bit-and word mask)
                      offset))
        byte-1 (extract-fn 24 0xff000000)
        byte-2 (extract-fn 16 0xff0000)
        byte-3 (extract-fn 8 0xff00)
        byte-4 (extract-fn 0 0xff)
        separated [byte-1 byte-2 byte-3 byte-4]]
    separated))

(defn ffAdd
  "Adds two bytes in a finite field via xor operation."
  [byte1 byte2]
  (bit-xor byte1 byte2))

(defn xtime
  "Multiplies a byte by two in a finite field, bit-xoring if the high bit is set."
  [byte-in]
  (let [irred-poly 0x1b
        shift-left (fn [byte]
                     (bit-shift-left byte 1))
        full-mask 0xff
        should-xor? (fn [byte]
                      (if (msb-1? byte) 
                        (bit-and full-mask
                                 (bit-xor irred-poly
                                          (shift-left byte)))
                        (bit-and full-mask
                                 (shift-left byte))))]
    (should-xor? byte-in)))

(defn ffmultiply
  "Multiplies two bytes (8 bits apiece) together in a finite field."
  [byte-a byte-b]
  (let [a (max byte-a byte-b)
        b (min byte-a byte-b)
        one a
        two (xtime one)
        three (xtime two)
        four (xtime three)
        five (xtime four)
        six (xtime five)
        seven (xtime six)
        eight (xtime seven)
        bit-and-equal?  (fn [mask]
                          (= mask (bit-and mask b)))]
    (cond-> 0x0
      (bit-and-equal? 128) (bit-xor eight)
      (bit-and-equal? 64) (bit-xor seven)
      (bit-and-equal? 32) (bit-xor six)
      (bit-and-equal? 16) (bit-xor five)
      (bit-and-equal? 8) (bit-xor four)
      (bit-and-equal? 4) (bit-xor three)
      (bit-and-equal? 2) (bit-xor two)
      (bit-and-equal? 1) (bit-xor one))))
