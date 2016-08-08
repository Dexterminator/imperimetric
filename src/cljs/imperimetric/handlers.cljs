(ns imperimetric.handlers
  (:require [re-frame.core :refer [register-handler trim-v dispatch]]
            [imperimetric.db :as db]
            [imperimetric.api :as api]
            [imperimetric.utils-js :refer [log]]
            [clojure.string :as str]))

(defn failed-response-handler [db [{:keys [status status-text]}]]
  (log (str "Something went wrong: " status " " status-text))
  (-> db
      (dissoc :loading)
      (assoc :converted-text "Something went wrong when converting text :(")))

(defn api-convert-call [db text]
  (api/convert
    (js/encodeURIComponent text)
    (name (:from-system db))
    (name (:to-system db))
    {:handler       #(dispatch [:convert-response %])
     :error-handler #(dispatch [:failed-response %])}))

(def system-switches
  {:us       :metric
   :metric   :us
   :imperial :metric})

(defn from-button-clicked-handler [db [from-system]]
  (let [updated-db (assoc db :from-system from-system)
        adjusted-db (if (= from-system (:to-system db))
                      (assoc updated-db :to-system (system-switches from-system))
                      updated-db)]
    (api-convert-call adjusted-db (:text adjusted-db))
    adjusted-db))

(defn to-button-clicked-handler [db [to-system]]
  (let [updated-db (assoc db :to-system to-system)
        adjusted-db (if (= to-system (:from-system db))
                      (assoc updated-db :from-system (system-switches to-system))
                      updated-db)]
    (api-convert-call adjusted-db (:text adjusted-db))
    adjusted-db))

(defn convert-response-handler [db [text]]
  (let [updated-db (dissoc db :loading)]
    (if (str/blank? (:text updated-db))
      (dissoc updated-db :converted-text)
      (assoc updated-db :converted-text text))))

(defn text-changed-handler [db [text]]
  (if-not (str/blank? text)
    (do
      (api-convert-call db text)
      (-> db
          (assoc :loading true)
          (assoc :text text)))
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
