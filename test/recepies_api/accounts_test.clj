(ns recepies-api.accounts-test
  (:require [clojure.test :refer :all]
            [integrant.repl.state :as state]
            [ring.mock.request :as mock]
            [muuntaja.core :as m]
            [recepies-api.system-test :as st]))

(use-fixtures :once st/account-fixture)

(deftest account-test
  (testing "user account"
    (let [{:keys [status]} (st/test-endpoint :post "/v1/account" {:auth true})]
      (is (= status 201))))

  (testing "update user role"
    (let [{:keys [status]} (st/test-endpoint :put "/v1/account" {:auth true})]
      (is (= status 204))))

  (testing "delete user account"
    (let [{:keys [status]} (st/test-endpoint :delete "/v1/account" {:auth true})]
      (is (= status 204)))))
