(ns imperimetric.handlers
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx reg-cofx get-effect get-coeffect
                                   inject-cofx trim-v dispatch debug after ->interceptor]]
            [imperimetric.db :as db]
            [imperimetric.api :as api]
            [clojure.string :as str]
            [imperimetric.config :as config]
            [cljs.spec.test :as stest]
            [cljs.spec :as s]))

(defn failed-response-handler [db _]
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
  (if (not= system (db clicked-system-type))
    (let [updated-db (assoc db clicked-system-type system)
          adjusted-db (if (= system (db other-system-type))
                        (assoc updated-db other-system-type (system-switches system))
                        updated-db)
          {:keys [text from-system to-system]} adjusted-db]
      (if-not (str/blank? text)
        {:api-convert-call [from-system to-system text]
         :db               (assoc adjusted-db :loading? true)}
        {:db adjusted-db}))))

(defn from-button-clicked-handler [{:keys [db]} [from-system]]
  (button-clicked-helper db :from-system :to-system from-system))

(defn to-button-clicked-handler [{:keys [db]} [to-system]]
  (button-clicked-helper db :to-system :from-system to-system))

(defn text-wait-over-handler [{:keys [db]} [timestamp]]
  (let [{:keys [latest-text-timestamp from-system to-system text]} db]
    (when (= timestamp latest-text-timestamp)
      {:api-convert-call [from-system to-system text]
       :db               (assoc db
                           :latest-requested-text text
                           :loading? true)})))

(defn convert-response-handler [db [{:keys [original-text converted-text]}]]
  (let [updated-db (dissoc db :loading?)]
    (cond
      (str/blank? (:text db)) (dissoc updated-db :converted-text)
      (= (:latest-requested-text db) original-text) (assoc updated-db :converted-text converted-text)
      :else db)))

(def ounce-pattern (js/RegExp. "ounces?(?!\\w)|ozs?(?!\\w)" "ig"))
(def fluid-ounce-pattern (js/RegExp. "fluid ounces?(?!\\w)|flozs?(?!\\w)|fl\\.\\s?ozs?(?!\\w)" "ig"))

(defn text-changed-handler [{:keys [db now]} [text]]
  (if-not (str/blank? text)
    {:db             (assoc db
                       :latest-text-timestamp now
                       :text text
                       :text-contains-fluid-ounces? (boolean (.match text fluid-ounce-pattern))
                       :text-contains-ounces? (boolean (.match text ounce-pattern)))
     :dispatch-later [{:ms 300 :dispatch [:text-wait-over now]}]}
    {:db (dissoc db
                 :converted-text
                 :text-contains-fluid-ounces?
                 :text-contains-ounces?
                 :text)}))

(defn make-ounces-fluid [text]
  (if-not (or (str/blank? text) (.match text fluid-ounce-pattern))
    (.replace text ounce-pattern "fl. oz")
    text))

(s/fdef make-ounces-fluid
        :args (s/cat :text string?)
        :ret string?)

(comment
  (stest/check `make-ounces-fluid)
  (make-ounces-fluid "lol oz")
  (make-ounces-fluid nil))

(defn ounce-button-clicked-handler [{:keys [db]} _]
  (let [{:keys [text from-system to-system]} db
        changed-text (make-ounces-fluid text)]
    (when (not= changed-text text)
      {:api-convert-call [from-system to-system changed-text]
       :db               (assoc db
                           :text-contains-fluid-ounces? true
                           :text-contains-ounces? false
                           :loading? true
                           :latest-requested-text changed-text
                           :text changed-text)})))

(defn check-spec
  [a-spec db event]
  (when-not (nil? db)
    (when-let [problems (::s/problems (s/explain-data a-spec db))]
      (js/console.group "Spec errors after event: " event)
      (doseq [{:keys [pred val via in path]} problems]
        (js/console.error "Spec check failed")
        (js/console.log "val: " val)
        (js/console.log "in: " in)
        (js/console.log "failed spec: " (last via))
        (js/console.log "on predicate: " pred)
        (js/console.log "path: " path))
      (js/console.groupEnd))))

(def check-spec-interceptor
  (->interceptor
    :id :check-spec
    :after (fn check-spec-after [context]
             (let [db (get-effect context :db)
                   event (get-coeffect context :event)]
               (check-spec ::imperimetric.db/db db event)
               context))))

(def standard-interceptors [(when config/debug? [debug check-spec-interceptor]) trim-v])

(reg-fx
  :api-convert-call
  (fn [[from-system to-system text]]
    (api-convert-call from-system to-system text)))

(reg-cofx
  :now
  (fn [cofx _]
    (assoc cofx :now (.now js/Date))))

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
  [standard-interceptors (inject-cofx :now)]
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
