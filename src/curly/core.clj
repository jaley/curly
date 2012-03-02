(ns curly.core
  (:require [criterium.core :as criterium])
  (:import  [curly RemoveCurly])
  (:gen-class))

(set! *warn-on-reflection* true)

(defn remove-curlied-idiomatic
  [text]
  (loop [state     0
         remaining text
         filtered  []]
    (cond
     (empty? remaining) (apply str filtered)
     (= (first remaining) \{) (recur (inc state) (rest remaining) filtered)
     (= (first remaining) \}) (recur (dec state) (rest remaining) filtered)
     (zero? state) (recur state (rest remaining) (conj filtered (first remaining)))
     :else (recur state (rest remaining) filtered))))

(defn remove-curlied-optimized
  [^String text]
  (let [sb  (StringBuilder.)
        end (int (count text))]
    (loop [state (int 0)
           pos   (int 0)]
      (if (= pos end)
        (.toString sb)
        (let [c (int (.charAt text pos))]
         (cond
          (= c (int \{)) (recur (inc state) (inc pos))
          (= c (int \})) (recur (dec state) (inc pos))
          (zero? state)  (do
                           (.append sb (char c))
                           (recur state (inc pos)))
          :else          (recur state (inc pos))))))))

(defn remove-curlied-java
  [^String text]
  (RemoveCurly/removeCurlies text))

(defn remove-curlied-regex
  [^String text]
  (loop [t text]
    (let [filtered (.replaceAll t "\\{[^\\{\\}.]*\\}" "")]
      (if (= filtered t)
        filtered
        (recur filtered)))))

(defn -main
  [& args]
  (let [test-string "Here is {{a string containing}} some {curly} braces."]

    (println "\n\n---> Benchmarking: Idiomatic Clojure implementation")
    (criterium/with-progress-reporting
      (criterium/quick-bench (remove-curlied-idiomatic test-string)))
    
    (println "\n\n---> Benchmarking: Optimized Clojure implementation")
    (criterium/with-progress-reporting
      (criterium/quick-bench (remove-curlied-optimized test-string)))
    
    (println "\n\n---> Benchmarking: Java implementation")
    (criterium/with-progress-reporting
      (criterium/quick-bench (remove-curlied-java test-string)))

    (println "\n\n---> Benchmarking: Regex implementation")
    (criterium/with-progress-reporting
      (criterium/quick-bench (remove-curlied-regex test-string)))))

