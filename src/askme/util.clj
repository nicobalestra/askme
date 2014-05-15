(ns askme.util
  (:require [noir.io :as io]
            [markdown.core :as md]
            [noir.validation :as vali]
            [taoensso.timbre :as timbre]
            [clojure.data.json :as json]
            [clojure.java.io :as clj-io]
            [clojure.data.json :as json]))

(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (->>
    (io/slurp-resource filename)
    (md/md-to-html-string)))


(defn get-errors-map []
  (timbre/info "Errors map: " @vali/*errors*)
  (into {} (map (fn [key val] [key  val])
                (keys @vali/*errors*)
                (vals @vali/*errors*))))

;; convert the body to a reader. Useful for testing in the repl
;; where setting the body to a string is much simpler.
(defn body-as-string [ctx]
  (if-let [body (get-in ctx [:request :body])]
    (do
      (condp instance? body
        java.lang.String body
        (slurp (clj-io/reader body))))))

(defn- drop-charset
  "Drop the charset spec from the content-type string"
  [content-type]
  (if (pos? (.indexOf content-type ";"))
    (subs content-type 0 (.indexOf content-type ";"))
    content-type))

;; For PUT and POST check if the content type is json.
(defn check-content-type [ctx content-types]
   (if (#{:put :post} (get-in ctx [:request :request-method]))
    (or
     (some #{(drop-charset (get-in ctx [:request :headers "content-type"]))}
           content-types)
     [false {:message "Unsupported Content-Type"}])
    true))

;; For PUT and POST parse the body as json and store in the context
;; under the given key.
(defn parse-json [liberator-context key]
  (when (#{:put :post} (get-in liberator-context [:request :request-method]))
    (try
      (if-let [body (body-as-string liberator-context)]
        (do
          (timbre/debug "Parsing JSON body '" body "'")
          (let [data (json/read-str body)]
            [false {key data}]))
        {:message "No body"})
      (catch Exception e
        (.printStackTrace e)
        {:message (format "Invalid JSON string while parsing : '" (body-as-string liberator-context) "'"  (.getMessage e))}))))


(defn generates-user-sign-key [user-id]

  (str user-id "-ASKme"))

(defn jsonify [key value]
  (condp instance? value
    java.sql.Timestamp (.toString value)
    value))
