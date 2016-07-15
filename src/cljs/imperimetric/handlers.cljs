(ns imperimetric.handlers
    (:require [re-frame.core :refer [register-handler trim-v]]
              [imperimetric.db :as db]
              [imperimetric.js-utils :refer [log]]))

(defn text-changed-handler [db [text]]
  (log text)
  db)

(register-handler
  :text-changed
  trim-v
  text-changed-handler)

(register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))
