(ns imperimetric.frinj-setup
  (:require [frinj.jvm :refer [frinj-init!]]
            [frinj.ops :refer [fj add-unit! to]]))

(defn frinj-setup! []
  (frinj-init!)
  (add-unit! :km2 (fj :km :km))
  (add-unit! :m2 (fj :m :m))
  (add-unit! :dm2 (fj :dm :dm))
  (add-unit! :cm2 (fj :cm :cm))
  (add-unit! :mm2 (fj :mm :mm))
  (add-unit! :sqmile (fj :mile :mile))
  (add-unit! :sqyard (fj :yard :yard))
  (add-unit! :sqfoot (fj :foot :foot))
  (add-unit! :sqinch (fj :inch :inch)))
