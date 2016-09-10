(ns imperimetric.db
  (:require [cljs.spec :as s]))

(def default-db
  {:from-system :us
   :to-system   :metric})

(def systems #{:us :imperial :metric})

(s/def ::from-system systems)
(s/def ::to-system systems)
(s/def ::text string?)
(s/def ::result-text string?)
(s/def ::latest-requested-text string?)
(s/def ::latest-text-timetamp integer?)
(s/def ::loading boolean?)

(s/def ::db (s/keys :req-un [::from-system ::to-system]
                    :opt-un [::text ::result-text ::latest-text-timestamp ::latest-requested-text ::loading]))
