(ns recepies-api.system-test
  (:require [clojure.test :as t]
            [environ.core :refer [env]]
            [integrant.repl.state :as state]
            [ring.mock.request :as mock]
            [muuntaja.core :as m]
            [recipes-api.auth :as auth]))

(defn get-teset-user-credentials [{:keys [auth0-test-token-username
                                          auth0-test-token-password]}]
  {:username auth0-test-token-username
   :password auth0-test-token-password})

(def token (atom nil))

(defn token-fixture [f]
  (reset! token (auth/get-test-token env (get-teset-user-credentials env)))
  (f)
  (reset! token nil))

(def accounts-test-user
  {:connection "Username-Password-Authentication"
   :email "account.test@recipe.api"
   :password "s0m3P@ssword"})

(defn account-fixture [f]
  (let [management-token (auth/get-management-token env)]
    (auth/create-auth-user env
                           accounts-test-user
                           management-token)
    (reset! token (auth/get-test-token env accounts-test-user))
    (f)
    (reset! token nil)))

(defn test-endpoint
  ([method path] (test-endpoint method path nil))
  ([method path {:keys [body auth]}]
   (let [app (:recipes/app state/system)
         response (app (cond-> (mock/request method path)
                         auth (mock/header :authorization (str "Bearer " (or @token
                                                                             (auth/get-test-token env (get-teset-user-credentials env)))))
                         body (mock/json-body body)))]
     (update response :body (partial m/decode "application/json")))))

(comment
  (test-endpoint :get "/v1/recipes")
  (test-endpoint :post "/v1/recipes" {:body {:name "test-recipe"
                                             :img "random-str"
                                             :prep-time 30}}))
