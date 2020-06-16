(ns danhable.util
  (:require [cljs.core.async :as async :refer [<!] :include-macros true]
            [clojure.pprint :refer [pprint]]))

(defn consume-chan
 [f ch]
  (async/go-loop [x (<! ch)]
    (pprint x)
   (when-not (nil? x)
     (f x)
     (recur (<! ch)))))

