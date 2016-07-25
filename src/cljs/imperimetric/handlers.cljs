(ns imperimetric.handlers
  (:require [re-frame.core :refer [register-handler trim-v dispatch]]
            [imperimetric.db :as db]
            [imperimetric.api :as api]
            [imperimetric.utils-js :refer [log]]
            [clojure.string :as str]))

(defn failed-response-handler [db [{:keys [status status-text]}]]
  (log (str "Something went wrong: " status " " status-text))
  db)

(def system-switches
  {:us       :metric
   :metric   :us
   :imperial :metric})

(defn from-button-clicked-handler [db [system]]
  (let [updated-db (assoc db :from-system system)]
    (if (= system (:to-system db))
      (assoc updated-db :to-system (system-switches system))
      updated-db)))

(defn to-button-clicked-handler [db [system]]
  (let [updated-db (assoc db :to-system system)]
    (if (= system (:from-system db))
      (assoc updated-db :from-system (system-switches system))
      updated-db)))

(defn convert-response-handler [db [text]]
  (if (str/blank? (:text db))
    (dissoc db :converted-text)
    (assoc db :converted-text text)))

(defn text-changed-handler [db [text]]
  (if-not (str/blank? text)
    (do (api/convert
          (js/encodeURIComponent text)
          (name (:from-system db))
          (name (:to-system db))
          {:handler       #(dispatch [:convert-response %])
           :error-handler #(dispatch [:failed-response %])})
        (assoc db :text text))
    (-> db
        (dissoc :converted-text)
        (dissoc :text))))

(register-handler
  :text-changed
  trim-v
  text-changed-handler)

(register-handler
  :initialize-db
  (fn [_ _]
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

(register-handler
  :from-button-clicked
  trim-v
  from-button-clicked-handler)

(register-handler
  :to-button-clicked
  trim-v
  to-button-clicked-handler)
