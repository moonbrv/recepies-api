(ns recipes-api.auth
  (:require
   [environ.core :refer [env]]
   [clj-http.client :as http]
   [muuntaja.core :as m]))

(defn get-test-token [{:keys [auth0-token-url auth0-test-token-client-id auth0-audience]}
                      {:keys [email password]}]
  (-> (http/post auth0-token-url
                 {:content-type :json
                  :cookie-policy :standard
                  :body (m/encode "application/json"
                                  {:client_id auth0-test-token-client-id
                                   :audience auth0-audience
                                   :grant_type "password"
                                   :username email
                                   :password password
                                   :scope "openid profile email"})})
      m/decode-response-body
      :access_token))

(defn get-management-token [{:keys [auth0-manage-client-id
                                    auth0-manage-client-secret
                                    auth0-audience
                                    auth0-token-url]}]
  (->> {:content-type :json
        :cookie-policy :standard
        :throw-exceptions false
        :body (m/encode "application/json"
                        {:client_id auth0-manage-client-id
                         :client_secret auth0-manage-client-secret
                         :audience auth0-audience
                         :grant_type "client_credentials"})}
       (http/post auth0-token-url)
       m/decode-response-body
       :access_token))

(defn get-manage-role-id [env token]
  (let [{:keys [auth0-api-base-url auth0-manage-role-name]} env]
    (->> {:headers {"Authorization" (str "Bearer " token)}
          :throw-exceptions false
          :content-type :json
          :cookie-policy :standard}
         (http/get (str auth0-api-base-url "/api/v2/roles"))
         m/decode-response-body
         (filter (fn [role]
                   (= (:name role) auth0-manage-role-name)))
         first
         :id)))

(defn create-auth-user
  ([env params] (create-auth-user env params nil))
  ([{:keys [auth0-api-base-url] :as env}
    {:keys [connection
            email
            password]}
    management-token]
   (let [token (or management-token
                   (get-management-token env))]
     (->> {:headers {"Authorization" (str "Bearer " token)}
           :throw-exceptions false
           :content-type :json
           :cookie-policy :standard
           :body (m/encode "application/json"
                           {:connection connection
                            :email email
                            :password password})}
          (http/post (str auth0-api-base-url "/api/v2/users"))
          m/decode-response-body))))

(defn delete-auth-user
  ([env uid] (delete-auth-user env uid nil))
  ([{:keys [auth0-api-base-url] :as env} uid management-token]
   (let [token (or management-token
                   (get-management-token env))]
     (->> {:headers {"Authorization" (str "Bearer " token)}
           :throw-exceptions false
           :content-type :json
           :cookie-policy :standard}
          (http/delete (str auth0-api-base-url "/api/v2/users/" uid))))))
