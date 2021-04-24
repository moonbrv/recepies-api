(ns recipes-api.account.handlers
  (:require
   [ring.util.response :as rr]
   [recipes-api.auth :as auth]
   [recipes-api.account.db :as account-db]
   [clj-http.client :as http]
   [recipes-api.utils :refer [get-uid
                              get-recipe-id]]))

(defn create-account! [db]
  (fn [request]
    (let [{:keys [sub picture]
           uname :name} (:claims request)]
      (account-db/insert-account! db {:uid sub
                                      :name uname
                                      :picture picture})
      (rr/status 204))))

(defn delete-account! [db auth-base-url]
  (fn [request]
    (let [uid (get-uid request)
          oauth-result (http/delete (str auth-base-url "/api/v2/users/" uid)
                                    {:headers {"Authorization" (str "Bearer "
                                                                    (auth/get-management-token))}})]
      (when (= (:status oauth-result) 204)
        (account-db/delete-account! db uid)
        (rr/status 204)))))
