(defproject imperimetric "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.293"]
                 [reagent "0.6.0"]
                 [binaryage/devtools "0.8.3"]
                 [re-frame "0.9.1"]
                 [secretary "1.2.3"]
                 [cljs-ajax "0.5.8"]
                 [compojure "1.5.1"]
                 [instaparse "1.4.3"]
                 [liberator "0.14.1"]
                 [medley "0.8.4"]
                 [frinj "0.2.5"]
                 [cheshire "5.6.3"]
                 [yogthos/config "0.8"]
                 [cljsjs/clipboard "1.5.13-0"]
                 [ring "1.5.0"]
                 [ring/ring-mock "0.3.0"]
                 [devcards "0.2.2"]]

  :uberjar-name "imperimetric-standalone.jar"

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-less "1.7.5"]
            [lein-kibit "0.1.3"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"]

  :figwheel {:css-dirs         ["resources/public/css"]
             :nrepl-host       "0.0.0.0"
             :nrepl-port       7888
             :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]}

  :less {:source-paths ["less"]
         :target-path  "resources/public/css"}

  :hooks [leiningen.less]

  :profiles
  {:dev
   {:dependencies [[midje "1.8.3"]
                   [pjstadig/humane-test-output "0.8.1"]
                   [figwheel-sidecar "0.5.0"]
                   [com.cemerick/piggieback "0.2.1"]
                   [org.clojure/test.check "0.9.0"]]
    :plugins      [[lein-figwheel "0.5.4-3"]
                   [lein-doo "0.1.7"]]
    }}

  :uberjar {:aot :all}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "imperimetric.core/mount-root"}
     :compiler     {:main                 imperimetric.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true}}
    {:id           "min"
     :source-paths ["src/cljs"]
     :jar          true
     :compiler     {:main            imperimetric.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}
    {:id           "devcards"
     :source-paths ["src/devcards" "src/cljs"]
     :figwheel     {:devcards true}
     :compiler     {:main                 imperimetric.core-card
                    :output-to            "resources/public/js/compiled/devcards.js"
                    :output-dir           "resources/public/js/compiled/devcards_out"
                    :asset-path           "js/compiled/devcards_out"
                    :source-map-timestamp true}}
    {:id           "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:output-to     "resources/public/js/compiled/test.js"
                    :main          imperimetric.runner
                    :optimizations :none}}
    ]}

  :main imperimetric.server

  :aot [imperimetric.server]

  :prep-tasks [["cljsbuild" "once" "min"] "compile"]
  )
