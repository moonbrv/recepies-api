(ns recepies-api.recepies-test
  (:require [clojure.test :refer :all]
            [integrant.repl.state :as state]
            [ring.mock.request :as mock]
            [muuntaja.core :as m]
            [recepies-api.system-test :as st]))

(use-fixtures :once st/token-fixture)

(deftest recipes
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

(def step
  {:sort 0
   :description "sort decription"})

(def update-step
  (update step :sort inc))

(def ingredient
  {:name "some ingredient"
   :sort 0
   :measure "kg"
   :amount 1})

(def update-ingredient
  (update ingredient :sort inc))

(deftest recipe-test
  (let [recipe-id (atom nil)
        step-id (atom nil)
        ingredient-id (atom nil)]
    (testing "create recipe"
      (let [{:keys [status body]} (st/test-endpoint :post "/v1/recipes" {:auth true
                                                                         :body recipe})]
        (is (= status 201))
        (reset! recipe-id (:recipe-id body))))

    (testing "update recipe"
      (let [{:keys [status]} (st/test-endpoint :put (str "/v1/recipes/" @recipe-id) {:auth true
                                                                                     :body update-recipe})]
        (is (= status 204))))

    (testing "create step"
      (let [{:keys [status body]} (st/test-endpoint :post
                                                    (str "/v1/recipes/" @recipe-id "/steps")
                                                    {:auth true
                                                     :body step})]
        (is (= status 201))
        (reset! step-id (:step-id body))))

    (testing "update step"
      (let [{:keys [status]} (st/test-endpoint :put
                                               (str "/v1/recipes/" @recipe-id "/steps")
                                               {:auth true
                                                :body (assoc update-step
                                                             :step-id @step-id)})]
        (is (= status 204))))

    (testing "delete step"
      (let [{:keys [status]} (st/test-endpoint :delete
                                               (str "/v1/recipes/" @recipe-id "/steps")
                                               {:auth true
                                                :body {:step-id @step-id}})]
        (is (= status 204))))

    (testing "create ingredient"
      (let [{:keys [status body]} (st/test-endpoint :post
                                                    (str "/v1/recipes/" @recipe-id "/ingredients")
                                                    {:auth true
                                                     :body ingredient})]
        (is (= status 201))
        (reset! ingredient-id (:ingredient-id body))))

    (testing "update ingredient"
      (let [{:keys [status]} (st/test-endpoint :put
                                               (str "/v1/recipes/" @recipe-id "/ingredients")
                                               {:auth true
                                                :body (assoc update-ingredient
                                                             :ingredient-id @ingredient-id)})]
        (is (= status 204))))

    (testing "delete ingredient"
      (let [{:keys [status]} (st/test-endpoint :delete
                                               (str "/v1/recipes/" @recipe-id "/ingredients")
                                               {:auth true
                                                :body {:ingredient-id @ingredient-id}})]
        (is (= status 204))))

    (testing "favorite recipe"
      (let [{:keys [status]} (st/test-endpoint :post
                                               (str "/v1/recipes/" @recipe-id "/favorite")
                                               {:auth true})]
        (is (= status 204))))

    (testing "unfavorite recipe"
      (let [{:keys [status]} (st/test-endpoint :delete
                                               (str "/v1/recipes/" @recipe-id "/favorite")
                                               {:auth true})]
        (is (= status 204))))

    (testing "delete recipe"
      (let [{:keys [status]} (st/test-endpoint :delete (str "/v1/recipes/" @recipe-id) {:auth true})]
        (is (= status 204))))))

(comment
  (st/test-endpoint :post "/v1/recipes" {:auth true
                                         :body recipe})
  (st/test-endpoint :post
                    (str "/v1/recipes/de174076-8304-44bc-bb1e-ce2c1ce2e66b/ingredients")
                    {:auth true
                     :body {:name "some name"
                            :amount 1
                            :measure "kg"
                            :sort 1}}))
