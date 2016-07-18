(ns imperimetric.api
  (:require [ajax.core :refer [GET]]))

(defn convert [text from to options]
  (GET "/convert" (merge options {:params {:text text :from from :to to}})))

