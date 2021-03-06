(ns imperimetric.convert.system-conversions
  (:require [imperimetric.convert.precision :refer [significant-digits default-precision]]
            [imperimetric.convert.convert-str :refer [convert-str convert-units]]))

(def english-systems #{:us :imperial})
(def english-units #{:mile :yard :foot :inch :pound :oz :sqmile :sqyard :sqfoot :sqinch :acre :mph :fps :fahrenheit})
(def metric-to-english-units #{:km :m :dm :cm :mm :kg :hg :g :mg :km2 :m2 :dm2 :cm2 :mm2 :hectare :kph :mps :celsius})

(defmulti convert
  (fn [from-system to-system quantity unit]
    (cond
      (and (english-systems from-system) (english-units unit)) [:english to-system unit]
      (and (english-systems to-system) (metric-to-english-units unit)) [from-system :english unit]
      :else [from-system to-system unit])))

;; English units (units that are the same in US and Imperial)
(defmethod convert [:english :metric :mile] [_ _ q _] (convert-str :mile :km q))
(defmethod convert [:english :metric :yard] [_ _ q _] (convert-str :yard :meter q))
(defmethod convert [:english :metric :foot] [_ _ q _] (convert-str :foot :meter q))
(defmethod convert [:english :metric :inch] [_ _ q _] (convert-str :inch :cm q))
(defmethod convert [:english :metric :pound] [_ _ q _] (convert-str :pound :kg q))
(defmethod convert [:english :metric :oz] [_ _ q _] (convert-str :oz :g q))
(defmethod convert [:english :metric :sqmile] [_ _ q _] (convert-str :sqmile :km2 q))
(defmethod convert [:english :metric :sqyard] [_ _ q _] (convert-str :sqyard :m2 q))
(defmethod convert [:english :metric :sqfoot] [_ _ q _] (convert-str :sqfoot :m2 q))
(defmethod convert [:english :metric :sqinch] [_ _ q _] (convert-str :sqinch :cm2 q))
(defmethod convert [:english :metric :acre] [_ _ q _] (convert-str :acre :hectare q))
(defmethod convert [:english :metric :mph] [_ _ q _] (convert-str :mph :kph q))
(defmethod convert [:english :metric :fps] [_ _ q _] (convert-str :fps :mps q))
(defmethod convert [:english :metric :fahrenheit] [_ _ q _] (convert-str :fahrenheit :celsius q))

(defmethod convert [:metric :english :km] [_ _ q _] (convert-str :km :mile q))
(defmethod convert [:metric :english :m] [_ _ q _] (convert-str :meter :yard q))
(defmethod convert [:metric :english :dm] [_ _ q _] (convert-str :dm :feet q))
(defmethod convert [:metric :english :cm] [_ _ q _] (convert-str :cm :inch q))
(defmethod convert [:metric :english :mm] [_ _ q _] (convert-str :mm :inch q))
(defmethod convert [:metric :english :kg] [_ _ q _] (convert-str :kg :pound q))
(defmethod convert [:metric :english :hg] [_ _ q _] (convert-str :hg :oz q))
(defmethod convert [:metric :english :g] [_ _ q _] (convert-str :g :oz q))
(defmethod convert [:metric :english :mg] [_ _ q _] (convert-str :mg :oz q))
(defmethod convert [:metric :english :km2] [_ _ q _] (convert-str :km2 :sqmile q))
(defmethod convert [:metric :english :m2] [_ _ q _] (convert-str :m2 :sqyard q))
(defmethod convert [:metric :english :dm2] [_ _ q _] (convert-str :dm2 :sqfoot q))
(defmethod convert [:metric :english :cm2] [_ _ q _] (convert-str :cm2 :sqinch q))
(defmethod convert [:metric :english :mm2] [_ _ q _] (convert-str :mm2 :sqinch q))
(defmethod convert [:metric :english :hectare] [_ _ q _] (convert-str :hectare :acre q))
(defmethod convert [:metric :english :kph] [_ _ q _] (convert-str :kph :mph q))
(defmethod convert [:metric :english :mps] [_ _ q _] (convert-str :mps :fps q))
(defmethod convert [:metric :english :celsius] [_ _ q _] (convert-str :celsius :fahrenheit q))

;; US customary units
(defmethod convert [:us :metric :cup] [_ _ q _] (convert-str :cup :ml q))
(defmethod convert [:us :metric :floz] [_ _ q _] (convert-str :floz :cl q))
(defmethod convert [:us :metric :tablespoon] [_ _ q _] (convert-str :tbsp :ml q))
(defmethod convert [:us :metric :teaspoon] [_ _ q _] (convert-str :tsp :ml q))
(defmethod convert [:us :metric :gallon] [_ _ q _] (convert-str :gallon :liter q))
(defmethod convert [:us :metric :pint] [_ _ q _] (convert-str :pint :liter q))
(defmethod convert [:us :metric :quart] [_ _ q _] (convert-str :quart :liter q))
(defmethod convert [:us :metric :gill] [_ _ q _] (convert-str :gill :cl q))
(defmethod convert [:us :metric :ton] [_ _ q _] (convert-str :ton :metricton q))

(defmethod convert [:us :imperial :cup] [_ _ q _] (convert-str :cup :brcup q))
(defmethod convert [:us :imperial :floz] [_ _ q _] (convert-str :floz :brfloz q))
(defmethod convert [:us :imperial :tablespoon] [_ _ q _] (convert-str :tbsp :brtablespoon q))
(defmethod convert [:us :imperial :teaspoon] [_ _ q _] (convert-str :tsp :brtsp q))
(defmethod convert [:us :imperial :gallon] [_ _ q _] (convert-str :gallon :brgallon q))
(defmethod convert [:us :imperial :pint] [_ _ q _] (convert-str :pint :brpint q))
(defmethod convert [:us :imperial :quart] [_ _ q _] (convert-str :quart :brquart q))
(defmethod convert [:us :imperial :gill] [_ _ q _] (convert-str :gill :brgill q))
(defmethod convert [:us :imperial :ton] [_ _ q _] (convert-str :ton :brton q))

;; Imperial
(defmethod convert [:imperial :metric :cup] [_ _ q _] (convert-str :brcup :ml q))
(defmethod convert [:imperial :metric :floz] [_ _ q _] (convert-str :brfloz :cl q))
(defmethod convert [:imperial :metric :tablespoon] [_ _ q _] (convert-str :brtablespoon :ml q))
(defmethod convert [:imperial :metric :teaspoon] [_ _ q _] (convert-str :brtsp :ml q))
(defmethod convert [:imperial :metric :gallon] [_ _ q _] (convert-str :brgallon :liter q))
(defmethod convert [:imperial :metric :pint] [_ _ q _] (convert-str :brpint :liter q))
(defmethod convert [:imperial :metric :quart] [_ _ q _] (convert-str :brquart :liter q))
(defmethod convert [:imperial :metric :gill] [_ _ q _] (convert-str :brgill :cl q))
(defmethod convert [:imperial :metric :ton] [_ _ q _] (convert-str :brton :metricton q))

(defmethod convert [:imperial :us :cup] [_ _ q _] (convert-str :brcup :cup q))
(defmethod convert [:imperial :us :floz] [_ _ q _] (convert-str :brfloz :floz q))
(defmethod convert [:imperial :us :tablespoon] [_ _ q _] (convert-str :brtablespoon :tbsp q))
(defmethod convert [:imperial :us :teaspoon] [_ _ q _] (convert-str :brtsp :tsp q))
(defmethod convert [:imperial :us :gallon] [_ _ q _] (convert-str :brgallon :gallon q))
(defmethod convert [:imperial :us :pint] [_ _ q _] (convert-str :brpint :pint q))
(defmethod convert [:imperial :us :quart] [_ _ q _] (convert-str :brquart :quart q))
(defmethod convert [:imperial :us :gill] [_ _ q _] (convert-str :brgill :gill q))
(defmethod convert [:imperial :us :ton] [_ _ q _] (convert-str :brton :ton q))

;; Metric
(defmethod convert [:metric :us :l] [_ _ q _] (convert-str :liter :pint q))
(defmethod convert [:metric :us :dl] [_ _ q _] (convert-str :dl :cup q))
(defmethod convert [:metric :us :cl] [_ _ q _] (convert-str :cl :floz q))
(defmethod convert [:metric :us :ml] [_ _ q _] (let [to (cond
                                                          (< q 14) :tsp
                                                          (< q 100) :tbsp
                                                          :else :cup)]
                                                 (convert-str :ml to q)))
(defmethod convert [:metric :us :ton] [_ _ q _] (convert-str :metricton :ton q))

(defmethod convert [:metric :imperial :l] [_ _ q _] (convert-str :liter :brpint q))
(defmethod convert [:metric :imperial :dl] [_ _ q _] (convert-str :dl :brcup q))
(defmethod convert [:metric :imperial :cl] [_ _ q _] (convert-str :cl :brfloz q))
(defmethod convert [:metric :imperial :ml] [_ _ q _] (let [to (cond
                                                                (< q 14) :brtsp
                                                                (< q 100) :brtablespoon
                                                                :else :brcup)]
                                                       (convert-str :ml to q)))
(defmethod convert [:metric :imperial :ton] [_ _ q _] (convert-str :metricton :brton q))

(defn convert-combined [larger-unit-q [larger-unit _] smaller-unit-q [smaller-unit _]]
  (let [smaller-in-larger (convert-units smaller-unit larger-unit smaller-unit-q)
        total (with-precision
                (max (significant-digits larger-unit-q) (significant-digits smaller-unit-q) default-precision)
                (+ larger-unit-q smaller-in-larger))]
    (convert :english :metric total larger-unit)))

(defn convert-interval [from-system to-system q1 dash q2 unit]
  (str (convert from-system to-system q1 unit) dash (convert from-system to-system q2 unit)))
