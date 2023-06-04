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
            [graphql-tester.test-runner :as runner]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [malli.generator :as mg]
            [clojure.spec.alpha :as s]
            [clojure.pprint]
            [clojure.string]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia :as lacinia]
            [com.walmartlabs.lacinia.parser.schema :as schema]
            [clojure.edn :as edn]))

(def urlToTest "https://rickandmortyapi.com/graphql")
(def result-db (atom []))
(def generation-db (atom []))
(def queries (atom []))
(def generated-queries (atom []))

; allgemeine Funktionen
(defn run-query
  [url query-str]
  (let [reply (client/post url {:form-params      {"query" query-str}
                                :throw-exceptions false})
        body  (when (:body reply) (json/read-str (:body reply) :key-fn keyword))]
    (merge
      {:status-code (:status reply)}
      {:body body})))

(defn query-schema
  [url]
  (let [schema-query-response (run-query url q/graphiQL)]
    (-> schema-query-response :body :data :__schema)))

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

(defn query-schema!
  [url]
  (let [schema-query-response (run-query url q/graphiQL)]
    (-> schema-query-response :body :data :__schema)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Utils
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn filter-empty-braces
  [strings]
  (filter #(not (clojure.string/blank? %)) strings))

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

(defn run-coverage-test
  [query-gen n]
  (let [property (prop/for-all
                   [generated query-gen]
                   (do
                     (swap! generation-db conj generated)
                     true))]
    (tc/quick-check n property)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; property-based generation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn run-property-based
  [url size recursion-limit]
  (let [gql-schema         (query-schema! url)
        query-type-name    (:name (:queryType gql-schema))]
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
            test-result (run-coverage-test query-generator size)]
        {:test-result test-result
         :call-result @result-db
         :generated   @generation-db})
      {:test-result :no-query-node}))
  (doseq [el @generation-db]
    ; generting actual graphql queries here
    ; sometimes they are empty! -> known bug in research tool! (gql.bahnql.clj Line 548/549
    (swap! queries conj (make-gql-string (:fields el)))
    )
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; experimental property-based generation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn run-test-gen
  [url]
  (println "hi")
  (let [schema (query-schema! url)
        query-tree (gqlgen/gen-gql-nodes
                     (nth (:types schema) 2)
                     (:types schema)
                     3
                     gqlgen/resolve-arg-gen-alphanumeric)
        samples (gen/sample query-tree 4)
        queries (map gqlgen/make-query samples)
        ]
    (reset! generated-queries queries)
    ;(println (map gqlgen/make-query query-tree))
    ;(clojure.pprint/pprint (gen/sample query-tree 4)  )
    (println @generated-queries)
    )
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
  (clojure.pprint/pprint (first query-node-map))
      )
)

(defn -main
  "main Function to run the test-Tool; mode defines which algorithm to run for testgeneration"
  [url mode]
  (cond
    (= mode "prime") (run-prime-path url)
    (= mode "test") (run-test-gen url)
    (= mode "property") (run-property-based url 10 5))
  (let [responses (map #(run-query url %) @queries)]
    (spit (str mode "generated-tests.txt") (clojure.string/join "\n" @queries))
    (println "generierte Tests in " (str mode "generated-test.txt"))
    (println "Generierte Tests: " (count responses))
    ; nur status-Code Überprüfung! -> prüft nur nach richtigem Schemata; (Property-based: viele fehler sind hausgemacht vom Tool)!
    (println "Erfolgreiche Tests: " (count (filter #(= (:status-code %) 200) responses)))
    (println "Fehlerhafte Tests: " (count (filter #(= (:status-code %) 400) responses)))
    )
  )
