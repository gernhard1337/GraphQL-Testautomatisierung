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
            [clojure.test.check :as tc]
            [paren.serene :as serene]
            [malli.generator :as mg]))

(def testUrl "https://rickandmortyapi.com/graphql")
(def testToken "glpat-LPP8P6H9pHSs6V18RdWs")

(def result-db (atom []))
(def generation-db (atom []))


(defn run-query
  [query-str]
  (let [response @(http/request {:url    "http://localhost/api/graphql"
                                 :method :post
                                 :as     :text
                                 :body   query-str})]
    {:body        (json/read-str (:body response) :key-fn keyword)
     :status-code (:status response)}))

(comment
  ;; Write specs to file, only needed when schema changed
  (serene/spit-specs
    "small-example-specs.clj"
    'gql.small_example_specs
    (:body (run-query schema/query))))

(defn valid-result?
  [payload]
  ;;(println (str "Validating " payload))
  (every?
    identity
    (map
      eval
      (reduce
        (fn [acc p]
          (conj
            acc
            `(s/valid?
               ~(keyword "gql.small-example-specs.QueryRoot" (name (key p)))
               ~(val p))))
        []
        payload))))

(def small-example-schema
  (:body (run-query query/graphiQL)))

(def results-db (atom []))
(def queries (atom []))

(def test-property
  (let [schema (-> small-example-schema :data :__schema)]
    (prop/for-all [
                   query-tree (gqlgen/gen-gql-nodes
                                (first (filter #(= (:name %) "QueryRoot")
                                               (:types schema)))
                                (:types schema)
                                3
                                gqlgen/resolve-arg-gen-alphanumeric
                                ;;gqlgen/resolve-arg-gen-string
                                )]
      (let [result (run-query (gqlgen/make-query query-tree))]
        (swap! results-db conj
               {:query      (gqlgen/make-query query-tree)
                :result     result
                :query-tree query-tree})
        (swap! queries conj (gqlgen/make-query query-tree))
        (and
          (= (:status-code result) 200)
          (valid-result? (:data (:body result)))
          ;; Inspect :errors or not
          ;;(= (:errors (:body result)) nil)
          )))))

(defn run-tests
  []
  (reset! results-db [])
  (reset! queries [])
  (clojure.test.check/quick-check 100 test-property))

(defn result-errors
  []
  (reduce
    (fn [acc result]
      (let [error (-> result :result :body :errors)]
        (if error
          (conj acc error)
          acc)))
    []
    @results-db))

(defn run-tests-and-report-smallest-failed-query
  []
  (if-let [result (first (:smallest (:shrunk (run-tests))))]
    (gqlgen/make-query result)
    :error-not-found))


(defn -main
  "main Function to run the test-Tool"
  [url mode size recursion-limit]
  (run-tests)
)






























