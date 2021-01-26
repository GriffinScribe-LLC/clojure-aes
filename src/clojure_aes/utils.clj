(ns clojure-aes.utils)

(defn hexify
  "Convert byte sequence to hex string."
  [coll]
  (let [hex [\0 \1 \2 \3 \4 \5 \6 \7 \8 \9 \a \b \c \d \e \f]]
    (letfn [(hexify-byte [b]
              (let [v (bit-and b 0xFF)]
                [(hex (bit-shift-right v 4))
                 (hex (bit-and v 0x0F))]))]
      (apply str (mapcat hexify-byte coll)))))

(defn hexify-str
  "Extracts bytes, converts to hex, and outputs a joined version."
  [s]
  (hexify (.getBytes s)))

(defn unhexify "Convert hex string to byte sequence."
  [s] 
  (letfn [(unhexify-2 [c1 c2] 
            (unchecked-byte 
             (+ (bit-shift-left (Character/digit c1 16) 4)
                (Character/digit c2 16))))]
    (map #(apply unhexify-2 %) (partition 2 s))))

(defn unhexify-str
  "Concatenate the unhexified characters."
  [s]
  (apply str (map char (unhexify s))))

(defn break-message-to-bytes
  "Takes a string of 16 concatenated hex vals and extracts the values."
  [message]
  (let [bytes (re-seq #".{1,2}" message) ]
    (vec
     (for [byte bytes ]
       (.intValue (BigInteger. byte 16))))))


(defn matrix-transposition
  "Tranposes the matrix `m`"
  [m]
  (apply mapv vector m))

(defn byte-to-hex-string
  "Converts a one-dimension array into hex formatted string."
  [byte-array]
  (map #(format "%02x" %) byte-array))

(defn byte2-to-hex-string
  "Converts a two-dimension array into hex formatted string."
  [state]
  (map #(map (fn [e] (format "%02x" e))
               %)
       state))

(defn print-array
  "Print debug info for each round: hex array format"
  [state round-type round-num]
  (println "round: " round-num "stage: " round-type )
  (doseq [row state]
    (prn (byte-to-hex-string row))))

(defn debug-aes
  "Print debug info for each round: single string hex-format."
  [round-num round-type state]
  (let [byte-string (byte2-to-hex-string state)]
    (prn (str "round[" round-num "]."
              round-type "   "
              (apply str (flatten byte-string))))))
