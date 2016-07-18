(ns imperimetric.handlers
    (:require [re-frame.core :refer [register-handler trim-v dispatch]]
              [imperimetric.db :as db]
              [imperimetric.api :as api]
              [imperimetric.js-utils :refer [log]]))

(defn failed-response-handler [db [{:keys [status status-text]}]]
  (log (str "Something went wrong: " status " " status-text))
  db)

(defn convert-response-handler [db [text]]
  (assoc db :converted-text text))

(defn text-changed-handler [db [text]]
  (api/convert
    (js/encodeURIComponent text)
    "us"
    "metric"
    {:handler       #(dispatch [:convert-response %])
     :error-handler #(dispatch [:failed-response %])})
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

(register-handler
  :convert-response
  trim-v
  convert-response-handler)

(register-handler
  :failed-response
  trim-v
  failed-response-handler)
