(ns danhable.components
  (:require [reagent-material-ui.core :as ui]))

(defn link
  ([url] (link url nil))
  ([url display]
   [:a {:href url} display]))

(defn icon [name]
  [:i {:class "material-icons"} name])

(defn link-icon [url icon-name]
  [link url
   [icon icon-name]])

(defn title-card
  []
  [:div
   [:h1 "Dan Hable"]
   [:h2 "Software Engineer"]
   [:p [link "mailto:dan@danhable.com" "dan@danhable.com"]]])

(defn company-positions
  [positions]
  (for [position positions]
    [:div {:class "position"}
     [:h2 (:title position)]
     ]
  ))

(defn work-history
  [companies]
  (for [company companies]
    [:div {:class "company"}
     [:h1 (:name company)]
     [company-positions (:positions company)]] ;; TODO: Do I want to output a overall timeline? What to do with link?
    )
  )


(defn resume [content-data]
  (let [t (:danhable.data/career @content-data)]
  [:div
   [title-card]
   [work-history t]
   ]
))
