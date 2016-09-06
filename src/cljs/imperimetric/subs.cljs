(ns imperimetric.subs
    (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))

(reg-sub
  :converted-text
  (fn [db _]
    (:converted-text db)))

(reg-sub
  :from-system
  (fn [db _]
    (:from-system db)))

(reg-sub
  :loading
  (fn [db _]
    (:loading db)))

(reg-sub
  :to-system
  (fn [db _]
    (:to-system db)))

(reg-sub
  :text
  (fn [db _]
    (:text db)))
