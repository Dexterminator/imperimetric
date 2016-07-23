(ns imperimetric.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :refer [register-sub]]))

(register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))

(register-sub
  :converted-text
  (fn [db _]
    (reaction (:converted-text @db))))

(register-sub
  :from-system
  (fn [db _]
    (reaction (:from-system @db))))

(register-sub
  :to-system
  (fn [db _]
    (reaction (:to-system @db))))
