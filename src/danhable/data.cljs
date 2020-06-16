(ns danhable.data
  (:require [cljs.core.async :refer [pipeline chan] :include-macros true]
            [schema.core :as schema :refer [optional-key validate] :include-macros true]
            [cljs-http.client :as http]
            [cljsjs.js-yaml]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Data Set Schema Definitions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def DateString schema/Str)

(def DateRange
  {:from DateString
   (optional-key :to) DateString})

(def PositionSchema
  {:title schema/Str
   :start DateString
   (optional-key :end) DateString
   (optional-key :brief) schema/Str
   (optional-key :highlights) [schema/Str]
   (optional-key :tech) [schema/Str]})

(def CompanySchema
  {:name schema/Str
   :positions [PositionSchema]
   (optional-key :url) schema/Str})

(def CareerSchema [CompanySchema])

(def DegreeSchema
  {:level schema/Str
   :college schema/Str
   :program schema/Str
   :start DateString
   (optional-key :end) DateString})

(def EducationSchema
  {:degrees [DegreeSchema]
   :certifications [schema/Any]
   :courses [schema/Any]})

(def ProjectSchema
  {:title schema/Str
   :description schema/Str
   :active schema/Any ;(schema/conditional map? DateRange DateString)
   (optional-key :url) schema/Str
   (optional-key :tech) [schema/Str]})

(def ProjectsSchema [ProjectSchema])

(def dataset-schemas {::career CareerSchema
                      ::education EducationSchema
                      ::projects ProjectsSchema})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- dataset-url
  [ds]
  (str "/data/" (name ds) ".yaml"))

(defn- validate-dataset
  [ds val]
  (validate (get dataset-schemas ds) val))

(defn load
  [ds]
  (let [ch (chan 1)
        load-xform (comp (map :body)
                         (map js/jsyaml.safeLoad)
                         (map #(js->clj % :keywordize-keys true))
                         (map #(validate-dataset ds %)))]
    (pipeline 1 ch load-xform (http/get (dataset-url ds)))
    ch))
