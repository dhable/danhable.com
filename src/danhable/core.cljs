(ns danhable.core
  (:require [cljs.core.async :as async :include-macros true]
            [reagent.core :as r]
            [reagent.dom :as rdom]
            [danhable.components :as components]
            [danhable.data :as data]
            [danhable.util :as util]))

(enable-console-print!)

(defonce content-data (r/atom {}))

(defn fetch-datasets ;; probably can be moved into data?
  []                 ;; do i want to eagerly load all datasets?
  (async/merge
   (for [dataset (keys data/dataset-schemas)]
     (async/map #(hash-map dataset %)
                [(data/load dataset)]))))

(defonce init (do
                (util/consume-chan #(swap! content-data merge %)
                                   (fetch-datasets))))

(defn ^:export run []
  (rdom/render [components/resume content-data]
               (js/document.getElementById "app")))
