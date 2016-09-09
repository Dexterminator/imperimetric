(ns imperimetric.handlers
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx trim-v dispatch debug]]
            [imperimetric.db :as db]
            [imperimetric.api :as api]
            [imperimetric.utils-js :refer [log]]
            [clojure.string :as str]
            [imperimetric.config :as config]))

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
        {:api-convert-call [adjusted-db (:text adjusted-db)]
         :db               (assoc adjusted-db :loading true)}
        {:db adjusted-db}))))

(defn from-button-clicked-handler [{db :db} [from-system]]
  (button-clicked-helper db :from-system :to-system from-system))

(defn to-button-clicked-handler [{db :db} [to-system]]
  (button-clicked-helper db :to-system :from-system to-system))

(defn convert-response-handler [db [{original-text  :original-text
                                     converted-text :converted-text}]]
  (let [updated-db (dissoc db :loading)]
    (cond
      (str/blank? (:text db)) (dissoc updated-db :converted-text)
      (= (:text db) original-text) (assoc updated-db :converted-text converted-text)
      :else db)))

(defn text-changed-handler [{db :db} [text]]
  (if-not (str/blank? text)
    {:api-convert-call [db text]
     :db               (-> db
                           (assoc :loading true)
                           (assoc :text text))}
    {:db (-> db
             (dissoc :converted-text)
             (dissoc :text))}))

(def ounce-pattern (js/RegExp. "ounces?|ozs?" "ig"))
(def fluid-ounce-pattern #"(?i)fluid ounces?|flozs?|fl\.\s?oz")

(defn make-ounces-fluid [text]
  (if-not (or (str/blank? text) (re-find fluid-ounce-pattern text))
    (.replace text ounce-pattern "fl. oz")
    text))

(defn ounce-button-clicked-handler [{db :db} _]
  (let [changed-text (make-ounces-fluid (:text db))]
    (if-not (= changed-text (:text db))
      {:api-convert-call [db changed-text]
       :db               (-> db
                             (assoc :loading true)
                             (assoc :text changed-text))}
      {:db db})))

(def standard-interceptors [(when config/debug? debug) trim-v])

(reg-fx
  :api-convert-call
  (fn [[db text]]
    (api-convert-call db text)))

(reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(reg-event-db
  :set-active-panel
  standard-interceptors
  (fn [db [active-panel]]
    (assoc db :active-panel active-panel)))

(reg-event-db
  :convert-response
  standard-interceptors
  convert-response-handler)

(reg-event-db
  :failed-response
  standard-interceptors
  failed-response-handler)

(reg-event-fx
  :text-changed
  standard-interceptors
  text-changed-handler)

(reg-event-fx
  :from-button-clicked
  standard-interceptors
  from-button-clicked-handler)

(reg-event-fx
  :to-button-clicked
  standard-interceptors
  to-button-clicked-handler)

(reg-event-fx
  :ounce-button-clicked
  standard-interceptors
  ounce-button-clicked-handler)
