(ns imperimetric.handler-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [imperimetric.handlers :refer [make-ounces-fluid failed-response-handler
                                           from-button-clicked-handler to-button-clicked-handler
                                           text-wait-over-handler convert-response-handler
                                           text-changed-handler ounce-button-clicked-handler]]
            [pjstadig.humane-test-output]))

(deftest make-ounces-fluid-test
  (testing "make-ounces-fluid"
    (testing "converts 'oz' to 'fl. oz'"
      (is (= "1000 fl. oz, 3 fl. oz"
             (make-ounces-fluid "1000 oz, 3 oz"))))
    (testing "does not convert if text contains fluid ounces"
      (is (= "1000 fl. oz, 3 oz"
             (make-ounces-fluid "1000 fl. oz, 3 oz"))))))

(deftest failed-response-handler-test
  (testing "failed-response-handler"
    (testing "removes loading and adds an error text"
      (is (= {:converted-text "Something went wrong when converting text :("}
             (failed-response-handler {:loading? true} [{:status 404 :status-text ""}]))))))

(deftest button-click-handlers-test
  (testing "from-button-clicked-handler"
    (testing "performs an api call when the from system changes and updates db"
      (is (= {:db               {:from-system :imperial
                                 :to-system   :metric
                                 :loading?    true
                                 :text        "3 oz"}
              :api-convert-call [:imperial :metric "3 oz"]}
             (from-button-clicked-handler {:db {:from-system :us
                                                :to-system   :metric
                                                :text        "3 oz"}}
                                          [:imperial]))))
    (testing "switches the to-system when systems are identical"
      (is (= {:db               {:from-system :imperial
                                 :to-system   :metric
                                 :loading?    true
                                 :text        "3 oz"}
              :api-convert-call [:imperial :metric "3 oz"]}
             (from-button-clicked-handler {:db {:from-system :us
                                                :to-system   :imperial
                                                :text        "3 oz"}}
                                          [:imperial]))))
    (testing "does not perform api call if text is blank"
      (is (= {:db {:from-system :imperial
                   :to-system   :metric
                   :text        ""}}
             (from-button-clicked-handler {:db {:from-system :us
                                                :to-system   :imperial
                                                :text        ""}}
                                          [:imperial]))))
    (testing "does not do anything if system did not change"
      (is (= nil
             (from-button-clicked-handler {:db {:from-system :us
                                                :to-system   :metric
                                                :text        "3 oz"}}
                                          [:us])))))
  (testing "to-button-clicked-handler"
    (testing "performs an api call when the from system changes and updates db"
      (is (= {:db               {:from-system :us
                                 :to-system   :imperial
                                 :loading?    true
                                 :text        "3 oz"}
              :api-convert-call [:us :imperial "3 oz"]}
             (to-button-clicked-handler {:db {:from-system :us
                                              :to-system   :metric
                                              :text        "3 oz"}}
                                        [:imperial]))))
    (testing "switches the from-system when systems are identical"
      (is (= {:db               {:from-system :metric
                                 :to-system   :us
                                 :loading?    true
                                 :text        "3 oz"}
              :api-convert-call [:metric :us "3 oz"]}
             (to-button-clicked-handler {:db {:from-system :us
                                              :to-system   :metric
                                              :text        "3 oz"}}
                                        [:us]))))))

(deftest text-wait-over-handler-test
  (testing "text-wait-over-handler"
    (testing "doesn't return any effects when timestamp is not the latest one"
      (is (= nil
             (text-wait-over-handler {:db {:latest-text-timestamp 2
                                           :text                  "3 oz"
                                           :from-system           :us
                                           :to-system             :metric}}
                                     [1]))))
    (testing "makes an api call if the timestamp is the latest one"
      (is (= {:db               {:latest-text-timestamp 2
                                 :latest-requested-text "3 oz"
                                 :loading?              true
                                 :text                  "3 oz"
                                 :from-system           :us
                                 :to-system             :metric}
              :api-convert-call [:us :metric "3 oz"]}
             (text-wait-over-handler {:db {:latest-text-timestamp 2
                                           :text                  "3 oz"
                                           :from-system           :us
                                           :to-system             :metric}}
                                     [2]))))))

(deftest convert-response-handler-test
  (testing "convert-response-handler"
    (testing "adds the converted text to the db"
      (is (= {:text                  "3 oz"
              :converted-text        "85.0 g"
              :latest-requested-text "3 oz"}
             (convert-response-handler {:loading?              true
                                        :text                  "3 oz"
                                        :latest-requested-text "3 oz"}
                                       [{:original-text "3 oz" :converted-text "85.0 g"}]))))
    (testing "removes converted text from db if input text is empty"
      (is (= {:text ""}
             (convert-response-handler {:loading?       true
                                        :text           ""
                                        :converted-text "3 o"}
                                       [{:original-text "3 oz" :converted-text "85.0 g"}]))))))

(deftest text-changed-handler-test
  (testing "text-changed-handler"
    (testing "updates db with new text and timestamp"
      (let [now 1482418660170
            result (text-changed-handler {:db {} :now now} ["3 oz"])
            db (select-keys (:db result) [:latest-text-timestamp :text])
            dispatch-later-args (:dispatch-later result)]
        (is (= db {:latest-text-timestamp now
                   :text                  "3 oz"})
            (= dispatch-later-args [{:ms 300 :dispatch [:text-wait-over now]}]))))
    (testing "removes all values related to text when it is blank"
      (is (= {:db {}}
             (text-changed-handler {:db  {:converted-text              "85.0 g"
                                          :text                        "3 oz"
                                          :text-contains-fluid-ounces? false
                                          :text-contains-ounces?       true}
                                    :now 1}
                                   [""]))))))

(deftest ounce-button-clicked-handler-test
  (testing "ounce-button-clicked-handler"
    (testing "changes text and performs an api call"
      (let [changed-text "3 fl. oz"]
        (is (= {:db               {:text                        changed-text
                                   :text-contains-fluid-ounces? true
                                   :text-contains-ounces?       false
                                   :loading?                    true
                                   :latest-requested-text       changed-text
                                   :from-system                 :us
                                   :to-system                   :metric}
                :api-convert-call [:us :metric changed-text]}
               (ounce-button-clicked-handler {:db {:text        "3 oz"
                                                   :from-system :us
                                                   :to-system   :metric}} [])))))))
