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

(defn button-clicked-helper [db clicked-system-type other-system-type system]
  (if (= system (db clicked-system-type))
    db
    (let [updated-db (assoc db clicked-system-type system)
          adjusted-db (if (= system (db other-system-type))
                        (assoc updated-db other-system-type (system-switches system))
                        updated-db)]
      (if-not (str/blank? (:text adjusted-db))
        (do
          (api-convert-call adjusted-db (:text adjusted-db))
          (assoc adjusted-db :loading true))
        adjusted-db))))

(defn from-button-clicked-handler [db [from-system]]
  (button-clicked-helper db :from-system :to-system from-system))

(defn to-button-clicked-handler [db [to-system]]
  (button-clicked-helper db :to-system :from-system to-system))

(defn convert-response-handler [db [{original-text  :original-text
                                     converted-text :converted-text}]]
  (let [updated-db (dissoc db :loading)]
    (cond
      (str/blank? (:text db)) (dissoc updated-db :converted-text)
      (= (:text db) original-text) (assoc updated-db :converted-text converted-text)
      :else db)))

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

(def ounce-pattern (js/RegExp. "ounces?|ozs?" "ig"))
(def fluid-ounce-pattern #"(?i)fluid ounces?|flozs?|fl\.\s?oz")

(defn make-ounces-fluid [text]
  (if-not (or (str/blank? text) (re-find fluid-ounce-pattern text))
    (.replace text ounce-pattern "fl. oz")
    text))

(defn ounce-button-clicked-handler [db _]
  (let [changed-text (make-ounces-fluid (:text db))]
    (if-not (= changed-text (:text db))
      (do
        (api-convert-call db changed-text)
        (-> db
            (assoc :loading true)
            (assoc :text changed-text)))
      db)))

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

(register-handler
  :ounce-button-clicked
  trim-v
  ounce-button-clicked-handler)
