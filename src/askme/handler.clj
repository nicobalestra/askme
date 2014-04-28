(ns askme.handler
  (:require [compojure.core :refer [defroutes]]
            [askme.routes.static :refer [static-routes]]
            [askme.routes.rest :refer [askme-rest-routes]]
            [noir.util.middleware :as middleware]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [com.postspectacular.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [askme.routes.auth :refer [auth-routes auth-rest]]
            [askme.models.schema :as schema]
            [askme.routes.cljsexample :refer [cljs-routes]]
            [askme.routes.restapi :refer [liberator-res]]
            [liberator.dev :refer [wrap-trace]]
            [askme.logic.security :as security]))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config! [:appenders :rotor]
      {:min-level :info,
       :enabled? true,
       :async? false,
       :max-message-per-msecs nil,
       :fn rotor/append})
  (timbre/set-config! [:shared-appender-config :rotor]
      {:path "askme.log",
       :max-size (* 512 1024),
       :backlog 10})
  (if (env :selmer-dev)
    (parser/cache-off!))

  (if-not (schema/initialized?)
    (throw (Exception. "Database not initialized.")))

  (timbre/info "askme started successfully"))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "askme is shutting down..."))

(defn template-error-page [handler]
  (if (env :selmer-dev)
    (fn [request]
       (when (env :log-requests)
          (timbre/info "Request: " request))
      (try
        (handler request)
        (catch clojure.lang.ExceptionInfo ex
          (let [{:keys [type error-template], :as data} (ex-data ex)]
            (do
              (timbre/info "Error " ex)
              (if (= :selmer-validation-error type)
                {:status 500,
                 :body (parser/render error-template data)}
                (throw ex)))))))
    handler))

(defn jwt-check [handler]
  (fn [request]
    (if-let [jwt-token (get-in request [:headers "token"])]
      (let [decrypted (security/verify-token jwt-token)]
        (timbre/info "Token found in http request header.. need to decrypt: " decrypted)
        (-> (handler request)
            (assoc :auth decrypted)))
      (do
        (timbre/info "token header not found")
        (handler request)))))

(defn liberator-tracer [handler]
  (wrap-trace handler :header :ui))

(def app
 (middleware/app-handler [liberator-res askme-rest-routes cljs-routes auth-routes auth-rest static-routes app-routes]
   :middleware  [template-error-page liberator-tracer jwt-check ]
   :access-rules []
   :formats [:json :json-kw :edn]))
