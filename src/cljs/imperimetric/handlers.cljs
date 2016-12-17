(ns imperimetric.handlers
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx trim-v dispatch debug after]]
            [imperimetric.db :as db]
            [imperimetric.api :as api]
            [imperimetric.utils-js :refer [log]]
            [clojure.string :as str]
            [imperimetric.config :as config]
            [cljs.spec :as s]))

(defn failed-response-handler [db [{:keys [status status-text]}]]
  (log (str "Something went wrong: " status " " status-text))
  (-> db
      (dissoc :loading?)
      (assoc :converted-text "Something went wrong when converting text :(")))

(defn api-convert-call [from-system to-system text]
  (api/convert
    (js/encodeURIComponent text)
    (name from-system)
    (name to-system)
    {:handler       #(dispatch [:convert-response %])
     :error-handler #(dispatch [:failed-response %])}))

(def system-switches
  {:us       :metric
   :metric   :us
   :imperial :metric})

(defn button-clicked-helper [db clicked-system-type other-system-type system]
  (if (= system (db clicked-system-type))
    {:db db}
    (let [updated-db (assoc db clicked-system-type system)
          adjusted-db (if (= system (db other-system-type))
                        (assoc updated-db other-system-type (system-switches system))
                        updated-db)]
      (if-not (str/blank? (:text adjusted-db))
        {:api-convert-call [(:from-system adjusted-db) (:to-system adjusted-db) (:text adjusted-db)]
         :db               (assoc adjusted-db :loading? true)}
        {:db adjusted-db}))))

(defn from-button-clicked-handler [{db :db} [from-system]]
  (button-clicked-helper db :from-system :to-system from-system))

(defn to-button-clicked-handler [{db :db} [to-system]]
  (button-clicked-helper db :to-system :from-system to-system))

(defn text-wait-over-handler [{db :db} [timestamp]]
  (if (< timestamp (:latest-text-timestamp db))
    {:db db}
    {:api-convert-call [(:from-system db) (:to-system db) (:text db)]
     :db               (assoc db
                         :latest-requested-text (:text db)
                         :loading? true)}))

(defn convert-response-handler [db [{:keys [original-text converted-text]}]]
  (let [updated-db (dissoc db :loading?)]
    (cond
      (str/blank? (:text db)) (dissoc updated-db :converted-text)
      (= (:latest-requested-text db) original-text) (assoc updated-db :converted-text converted-text)
      :else db)))

(def ounce-pattern (js/RegExp. "ounces?(?!\\w)|ozs?(?!\\w)" "ig"))
(def fluid-ounce-pattern (js/RegExp. "fluid ounces?(?!\\w)|flozs?(?!\\w)|fl\\.\\s?ozs?(?!\\w)" "ig"))

(defn text-changed-handler [{db :db} [text]]
  (if-not (str/blank? text)
    (let [now (.now js/Date)]
      {:db             (assoc db
                         :latest-text-timestamp now
                         :text text
                         :text-contains-fluid-ounces? (boolean (.match text fluid-ounce-pattern))
                         :text-contains-ounces? (boolean (.match text ounce-pattern)))
       :dispatch-later [{:ms 300 :dispatch [:text-wait-over now]}]})
    {:db (dissoc db
                 :converted-text
                 :text-contains-fluid-ounces?
                 :text-contains-ounces?
                 :text)}))

(defn make-ounces-fluid [text]
  (if-not (or (str/blank? text) (.match text fluid-ounce-pattern))
    (.replace text ounce-pattern "fl. oz")
    text))

(defn ounce-button-clicked-handler [{db :db} _]
  (let [changed-text (make-ounces-fluid (:text db))]
    (if-not (= changed-text (:text db))
      {:api-convert-call [(:from-system db) (:to-system db) changed-text]
       :db               (assoc db
                           :text-contains-fluid-ounces? true
                           :text-contains-ounces? false
                           :loading? true
                           :latest-requested-text changed-text
                           :text changed-text)}
      {:db db})))

(defn check-and-throw
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor (after (partial check-and-throw :imperimetric.db/db)))

(def standard-interceptors [(when config/debug? [debug check-spec-interceptor]) trim-v])

(reg-fx
  :api-convert-call
  (fn [[from-system to-system text]]
    (api-convert-call from-system to-system text)))

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
  :text-wait-over
  standard-interceptors
  text-wait-over-handler)

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
