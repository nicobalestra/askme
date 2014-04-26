(ns askme.routes.auth
  (:use compojure.core)
  (:require [askme.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.cookies :as cookies]
            [askme.dailycred :as dailycred]
            [environ.core :refer [env]]
            [askme.models.users :as users-db]
            [askme.util :refer [check-content-type parse-json jsonify get-errors-map]]
            [ring.util.response :refer [content-type response]]
            [taoensso.timbre :as timbre]
            [clojure.string :as string]
            [askme.logic.user :as user]
            [liberator.core :refer [resource defresource]]
            [clojure.data.json :as json]
            ))


(defn valid? [email username pass pass1]
  (vali/rule (and (vali/has-value? email)
                  (vali/is-email? email))
             [:email "Email is mandatory and must be a valid email address"])
  (vali/rule (or (not (vali/has-value? username))
                 (and  (vali/has-value? username)
                       (vali/min-length?  username 8)))
             [:username "Username must be at least 8 characters long when provided"])
  (vali/rule (vali/min-length? pass 5)
             [:password "Password must be at least 5 characters"])
  (vali/rule (= pass pass1)
             [:password1 "Password and confirm password do not match"])
  (not (vali/errors? :email :username :password :password1)))

(defn register [& [email username]]
  (layout/render
    "join.html"))

(defn handle-dailycred-join [email username pass pass1]
  (if (valid? email username pass pass1)
    (try
      (let [response (dailycred/sign-up email username pass)]
        (if (= (:worked response) true)
          (do
            (session/put! :user (-> response :user :id))

            (resp/redirect "/"))
          (do
            (vali/rule false [:email (-> response :errors first :message)])
            (register email username))))
      (catch Exception ex
        (vali/rule false [:email (.getMessage ex)])
        (register email username)))
    (register email username)))

(defn handle-local-join [email username pass pass1]
  (timbre/info "Call to local join ")
  (if (valid? email username pass pass1)
    (try
      (let [result (users-db/create-user email username pass)
            user (-> result :user)]
        (do
          (timbre/info "Put user " user " in the session" )
          (session/put! :user user)
          (content-type (response {:response "ok"}) "application/json")))
       (catch Exception e
         (do
           (timbre/error "Exception while creating user " e))
           (content-type (response {:response "error"}) "application/json")))
    (do
      (timbre/info "Errors " (get-errors-map))
      (content-type (response  {:response "error" :errors (get-errors-map)}) "application/json"))))

(defn handle-login [login pass]
  (let [response (dailycred/sign-in login pass)]
    (when (= (:worked response) true)
      (session/put! :user (-> response :user :id)))
    (resp/redirect "/")))

(defn handle-local-login [login pass]
  (let [result (users-db/login login pass)]
    (timbre/info "User login:" result)
    (when (= (:worked result) true)
      (cookies/put! :askme-session {:value (-> result :user :id)})
      (session/put! :user (result :user)))
    (content-type (response {:result (result :worked)}) "application/json")))


(defn logout []
  (session/clear!)
  (resp/redirect "/"))

(defn login []
  (layout/render "login.html"))

(defn join []
  (layout/render "join.html"))

(defn handle-join [email username pass pass1]
        (if (env :local-auth)
          (handle-local-join email username pass pass1)
          (handle-dailycred-join email username pass pass1)))


(defn count-users-by [field value]
  (timbre/info "Call to count-users with " (name field) ": " value)
  (let [totals (if (or (nil? field)
                       (= (string/trim field) ""))
                0
                (users-db/count-by field value))]
    (timbre/info "Users by " field " : " value ": " totals)
      (content-type (response {:count totals}) "application/json")))

(defroutes auth-routes
  (GET "/users/login.html" []
       (login))

  (GET "/users/by/email/count" {:keys [params]}
       (count-users-by :email (get params :q)))

  (GET "/users/by/username/count" {:keys [params]}
       (count-users-by :username (get params :q)))

  (GET "/users/join.html" []
       (join))

  (POST "/users/join" {:keys [body-params]}
        (handle-join (body-params "email")
                     (body-params "username")
                     (body-params "password")
                     (body-params "password1" )))


;  (POST "/users/login" {:keys [params]}
;        (let[login (get params :username)
;             password (get params :password)]
;          (if (env :local-auth)
;            (handle-local-login login password)
;            (handle-login login password))))

  (POST "/users/logout" []
        (logout))

  )


(defresource join []
  :known-content-type? #(check-content-type % ["application/json"])
  :available-media-types ["application/json"]
  :malformed? #(parse-json % ::data)
  :allowed-methods [:put]
  :exists? (fn [{{username "username" password "password" email "email"}  ::data}]
                       (let [user (users-db/get-user (or {:username username}
                                                         {:email email}))]
                         (do
                           (timbre/info "Checking whether user " username " already exists")
                           (->
                            (pos? (count user))
                            (as-> exists?
                              (-> [exists?]
                                 (conj
                                    (if exists?
                                       {:message "There is already an user with the specified username" :conflict exists?}
                                       {:message "Putting new user" :conflict exists?}
                                     ))))))))
  :put-to-existing? (fn [{conflict :conflict}]
                      conflict)
  :can-put-to-missing? true
  :conflict? (fn [{conflict :conflict}]
               (timbre/info "Conflict? " conflict)
               conflict)
  :put! (fn [{data ::data}]
          (timbre/info "Putting user " data)
            (let [result (users-db/create-user (data "email") (data "username" ) (data "password" ))
                  user (-> result :user)]
              (do
                (timbre/info "Put user " user " in the session" )
                (session/put! :user user)
                (cookies/put! :askme-session {:value (:id user)})
                  {:new-user user})))

  :respond-with-entity? (fn [{new-user :new-user}]
                          (timbre/info "call to respond with entity"))

  :handle-created (fn[{new-user :new-user}]
               (timbre/info "USER CREATED " new-user)
               (json/write-str (dissoc new-user :password) :value-fn jsonify))
  )


(defresource user-login [login-name]
  :known-content-type? #(check-content-type % ["application/json"])
  :available-media-types ["application/json"]
  :malformed? #(parse-json % ::data)
  :allowed-methods [:post :get]
  :handle-ok (fn [ctx]
               (timbre/debug "Call to get login")
               (json/write-str (dissoc (:loggedin-user ctx) :password) :value-fn jsonify))

  :authorized? (fn [{{request-method :request-method} :request}]
                 (if (and (= request-method :get)
                          login-name
                          (user/is-anonymous))
                   [false {:message "You need to login to perform this API call"}]
                   true))
  :post-to-existing? (fn [{{request-method :request-method} :request}]
                       (= request-method :post))
  :exists? (fn [{{username "username" password "password"} ::data {request-method :request-method} :request}]
                (let [login (or username login-name)
                      result (users-db/get-user {:username login})
                      return-map {:loggedin-user  (first result)}]
                  (let [return (if (= request-method :get)
                                [(not (nil? (:loggedin-user return-map))) return-map]
                                [(user/check-login (:loggedin-user return-map) username password) return-map])]
                    (timbre/info "exists? returning " return)
                    return)))


  :post! (fn [ctx]
            (timbre/info "Se sono qui il login e stato successfull " ctx)
            (cookies/put! :askme-session {:value (-> ctx :loggedin-user :id)})
            (session/put! :user (ctx :loggedin-user))
           {:value (-> ctx :loggedin-user :id)}
           )
  
  :post-redirect? (fn [ctx] 
                    {:location (format "/users/login/%s" (-> ctx :loggedin-user :username))})

  )


(defroutes auth-rest
  (POST "/users/login" [] (user-login nil))
  (GET  "/users/login/:login-name" [login-name] (user-login login-name))
  (ANY "/users/join" [] (join))
)
