(ns recepies-api.system-test
  (:require [clojure.test :as t]
            [environ.core :refer [env]]
            [integrant.repl.state :as state]
            [ring.mock.request :as mock]
            [muuntaja.core :as m]
            [recipes-api.auth :as auth]))

(def token (atom nil))

(defn token-fixture [f]
  (reset! token (auth/get-test-token env))
  (f)
  (reset! token nil))

(defn test-endpoint
  ([method path] (test-endpoint method path nil))
  ([method path {:keys [body auth]}]
   (let [app (:recipes/app state/system)
         response (app (cond-> (mock/request method path)
                         auth (mock/header :authorization (str "Bearer " @token))
                         body (mock/json-body body)))]
     (update response :body (partial m/decode "application/json")))))

(comment
  (test-endpoint :get "/v1/recipes")
  (test-endpoint :post "/v1/recipes" {:body {:name "test-recipe"
                                             :img "random-str"
                                             :prep-time 30}}))
