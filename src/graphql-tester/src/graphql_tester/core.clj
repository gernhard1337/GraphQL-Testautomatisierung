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
            [paren.serene.schema :as schema]
            [malli.generator :as mg]
            [clojure.spec.alpha :as s]
            [clojure.pprint]
            [clojure.java.io]
            [clojure.string]))

(def urlToTest "https://rickandmortyapi.com/graphql")
(def result-db (atom []))
(def generation-db (atom []))
(def queries (atom []))

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

(defn run-query
  [url query-str]
  (let [reply (client/post url {:form-params      {"query" query-str}
                              :throw-exceptions false})
        body  (when (:body reply) (json/read-str (:body reply) :key-fn keyword))]
    (merge
      {:status-code (:status reply)}
      {:body body})
    ))

(defn run-test [url query]
  (if (= query "{}")
    {:status-code 400}
    (let [result (run-query url query)]
      (swap! result-db conj [query result])
      result)))

(defn make-property
  [query-gen url]
  (prop/for-all
    [generated query-gen]
    (let [gql-str (gu/make-gql-string (:fields generated))
          result  (run-test url gql-str)]
      (swap! generation-db conj generated)
      (not= (:status-code result) 500))))

(defn run-test-on-generator
  [generator n url]
  (reset! result-db [])
  (let [test-result (tc/quick-check n (make-property generator url))]
    (if (:pass? test-result)
      test-result
      {:failing-example
       (gu/make-gql-string (:fields (first (:smallest (:shrunk test-result)))))}
      ))
  )

(defn permutate-query
  [query]
  (let [query-fields
        (second (second query))]
    (reduce
      (fn [acc field-node]
        (conj acc [:map [:fields [:map (into [] (remove :optional field-node))]]]))
      []
      (rest query-fields))))

(defn query-schema!
  [url]
  (let [schema-query-response (run-query url q/graphiQL)]
    (-> schema-query-response :body :data :__schema)))

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

(def test-property
  (let [schema (query-schema! urlToTest)]
    (prop/for-all [
                   query-tree (gqlgen/gen-gql-nodes
                                (first (filter #(= (:name %) "QueryRoot")
                                               (:types schema)))
                                (:types schema)
                                3
                                ;;gqlgen/resolve-arg-gen-alphanumeric
                                gqlgen/resolve-arg-gen-string
                                )]
      (let [result (run-query urlToTest (gqlgen/make-query query-tree))]
        (swap! result-db conj
               {:query      (gqlgen/make-query query-tree)
                :result     result
                :query-tree query-tree})
        (swap! queries conj (gqlgen/make-query query-tree))
        (and
          (= (:status-code result) 200)
          (valid-result? (:data (:body result)))
          )))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Utils
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn make-query-of-nodes
  [nodes]
  (map gqlgen/make-query (gen/sample nodes 100)))

(defn make-fields
  [acc node-name fields]
  ;; empty fields means node should not be included
  (if (seq fields)
    (conj acc [node-name (conj (into [" { "] fields) "} ")])
    acc))

(defn make-arg-string
  [arg]
  (clojure.string/replace
    (apply
      str
      (conj
        (reduce
          (fn [acc [arg-name arg-value]]
            (conj
              acc
              (let [arg-str (if (string? arg-value)
                              (str "\"" arg-value "\"")
                              (str arg-value))]
                (str arg-name ": " arg-str ", "))))
          ["("]
          arg)
        ")"))
    ", )"
    ")"))

(defn make-gql
  [gql-nodes]
  (reduce
    (fn [acc gql-node]
      (let [node-name      (first gql-node)
            node-value     (second gql-node)
            node-type      (:type node-value)
            node-args      (:args node-value)
            node-with-args (if node-args
                             (str node-name (make-arg-string node-args))
                             node-name)]
        ;; value can be vector
        (if (vector? node-type)
          (let [fields (mapcat (fn [field] (make-gql (:fields field))) node-type)]
            ;; vector fields could be empty if only object wo fields
            (make-fields acc node-with-args fields))
          ;; not vector
          (if (= node-value :scalar)
            (conj acc (str node-name " "))
            (let [field (make-gql (:fields node-type))]
              (make-fields acc node-with-args field))))))
    []
    gql-nodes))

(defn make-gql-string
  [gql-node]
  (str "{"
       (apply str
              (-> gql-node
                  make-gql
                  flatten))
       "}"))





;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; property-based generation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def generated-queries (atom []))

(defn run-coverage-test
  [query-gen n]
  (let [property (prop/for-all
                   [generated query-gen]
                   (do
                     (swap! generation-db conj generated)
                     true))]
    (tc/quick-check n property)))

(defn run-property-based
  [url size recursion-limit]
  (let [gql-schema         (query-schema! url)
        query-type-name    (:name (:queryType gql-schema))
        mutation-type-name (:name (:mutationType gql-schema))]
    (reset! result-db [])
    (reset! generation-db [])
    (if query-type-name
      (let [query-node-map  (g/object-node->specification-map
                              (gu/find-type gql-schema query-type-name))
            query-generator (mg/generator
                              (first (vals query-node-map))
                              {:registry
                               (g/generation-registry
                                 b/base-registry (:types gql-schema))
                               :malli.generator/recursion-limit recursion-limit})
            test-result
            (run-coverage-test query-generator size)]
        {:test-result test-result
         :call-result @result-db
         :generated   @generation-db})
      {:test-result :no-query-node}))
  (doseq [el @generation-db]
    (println el)
    (println (make-gql-string (:fields el)))
    (swap! queries conj (make-gql-string (:fields el)))
    )
  (println @result-db)
  (println "######")
  (println @generation-db)
  (println "########")
  (println @queries)
  )


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; prime-path generation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn run-prime-path
  [url]
  (reset! result-db [])
  (reset! generation-db [])
  (reset! queries [])
  (let [gql-schema (query-schema url)
        query-type-name (:name (:queryType gql-schema))
        mutation-type-name (:name (:mutationType gql-schema))
        subscription-type-name (:name (:subscriptionType gql-schema))
        other-types-names  (map :name (:types gql-schema))
        query-node-map (g/object-node->specification-map
                         (gu/find-type gql-schema query-type-name))
        other-nodes-map (map #(g/object-node->specification-map (gu/find-type gql-schema %)) other-types-names)
        prime-paths (pgqlgen/node-map-to-prime-path-generators query-node-map other-nodes-map)
        ]
      )
  )


(defn -main
  "main Function to run the test-Tool - size and recursion-limit are only needed by legacy test generation"
  [url mode]
  (cond
    (= mode "1") (run-prime-path url)
    (= mode "2") (run-property-based url 4 4))
)
