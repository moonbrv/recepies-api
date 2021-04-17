(ns recepies-api.recepies-test
  (:require [clojure.test :refer :all]
            [integrant.repl.state :as state]
            [ring.mock.request :as mock]
            [muuntaja.core :as m]
            [recepies-api.system-test :as st]))

(deftest redipes-test
  (testing "list of all recipes"
    (testing "authorised user have public and drafts"
      (let [{:keys [status body]} (st/test-endpoint :get "/v1/recipes" {:auth true})]
        (is (= 200 status))
        (is (vector? (:public body)))
        (is (vector? (:drafts body)))))

    (testing "unauthorised user have only public"
      (let [{:keys [status body]} (st/test-endpoint :get "/v1/recipes")]
        (is (= 200 status))
        (is (vector? (:public body)))
        (is (nil? (:drafts body)))))))
