(ns graphql-tester.core
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clj-http.client :as client]
            [clojure.test.check.properties :as prop]
            [graphql-tester.gql-queries :as q]
            [graphql-tester.generation :as g]
            [graphql-tester.schema-utils :as gu]
            [graphql-tester.base-specifications :as b]
            [graphql-tester.generators :as gqlgen]
            [graphql-tester.primepath-generators :as pgqlgen]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [paren.serene :as serene]
            [malli.generator :as mg]))

(def testUrl "https://rickandmortyapi.com/graphql")
(def testToken "glpat-LPP8P6H9pHSs6V18RdWs")

(def result-db (atom []))
(def generation-db (atom []))

; allgemeine Funktionen
(defn run-query
  [url query-str]
  (let [reply (client/post url {:form-params      {"query" query-str}
                                :throw-exceptions false})
        body  (when (:body reply) (json/read-str (:body reply) :key-fn keyword))]
    (merge
      {:status-code (:status reply)}
      {:body body})
    ))

(defn query-schema
  [url]
  (let [schema-query-response (run-query url q/graphiQL)]
    (-> schema-query-response :body :data :__schema)))









; spezielle funktionen für property-based
(defn run-property-based
  [url size recursion-limit]










  )














; spezielle funktion für prime path
(defn run-prime-path
  [url]
  (let [graphql-schema (query-schema url)
        guery-tree (gqlgen/gen-gql-nodes
                     (nth (:types graphql-schema) 2) (:types graphql-schema) 10000000000
                     gqlgen/resolve-arg-gen-alphanumeric)
        query-tree-prime-paths (pgqlgen/gen-gql-nodes
                                 (nth (:types graphql-schema) 2) (:types graphql-schema) 10000000000
                                 pgqlgen/resolve-arg-gen-alphanumeric)
        generated-nodes (gen/sample guery-tree)
        ;generated-nodes-prime-path (gen/sample query-tree-prime-paths)
        ]
    ;(println generated-nodes)
    (spit "schema.edn" graphql-schema)
    (println (map gqlgen/make-query generated-nodes))

    )
  )







(defn -main
  "main Function to run the test-Tool - size and recursion-limit are only needed by legacy test generation"
  [url mode size recursion-limit]
  (cond
    (= mode "1") (run-prime-path url)
    (= mode "2") (run-property-based url size recursion-limit ))
)






























