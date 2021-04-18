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

(def recipe
  {:img "https://upload.wikimedia.org/wikipedia/commons/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg"
   :prep-time 30
   :name "Pizza Margarita"})

(def update-recipe
  (assoc recipe :public true))

(deftest recipe-test
  (let [recipe-id (atom nil)]
    (testing "create recipe"
      (let [{:keys [status body]} (st/test-endpoint :post "/v1/recipes" {:auth true
                                                                         :body recipe})]
        (is (= status 201))
        (reset! recipe-id (:recipe-id body))))
    (testing "update recipe"
      (let [{:keys [status]} (st/test-endpoint :put (str "/v1/recipes/" @recipe-id) {:auth true
                                                                                     :body update-recipe})]
        (is (= status 204))))
    (testing "delete recipe"
      (let [{:keys [status]} (st/test-endpoint :delete (str "/v1/recipes/" @recipe-id) {:auth true})]
        (is (= status 204))))))
