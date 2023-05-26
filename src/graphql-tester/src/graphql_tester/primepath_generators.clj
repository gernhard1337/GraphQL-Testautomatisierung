(ns graphql-tester.primepath-generators
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(defn gen-gql-nodes
  [gql-type types max-elements arg-gen-fn]
  (println gql-type)
  (println types max-elements arg-gen-fn)


  )

(defn resolve-arg-gen-alphanumeric
  [arg-type types]
  (let [kind         (:kind arg-type)
        type-name    (:name arg-type)
        of-type-kind (:kind (:ofType arg-type))
        of-type-name (:name (:ofType arg-type))]
    (cond
      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "String"])
      ;;gen/string
      gen/string-alphanumeric

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "Boolean"])
      gen/boolean

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "Int"])
      gen/large-integer

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "ID"])
      ;; CHANGE THIS TO GET INPUT VALIDATION BUG
      gen/string-alphanumeric
      ;;gen/string
      ;;(gen/return "root/test-project")
      ;;;;
      ;; Changes for small example errors
      ;;(gen/fmap (fn [id] (str id)) gen/small-integer)

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "Time"])
      :gql.scalar/non-null-time

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "Float"])
      :gql.scalar/non-null-float

      (= [kind type-name]
         ["SCALAR" "Boolean"])
      gen/boolean

      (= [kind type-name]
         ["SCALAR" "Int"])
      gen/large-integer

      (= [kind type-name]
         ["SCALAR" "Float"])
      :gql.scalar/float

      (= [kind type-name]
         ["SCALAR" "String"])
      ;;gen/string
      gen/string-alphanumeric

      (= [kind type-name]
         ["SCALAR" "Time"])
      gen/string

      (= [kind type-name]
         ["SCALAR" "ID"])
      ;;gen/string
      gen/string-alphanumeric

      (= [kind]
         ["ENUM"])
      (let [enum-type-name type-name
            enum-type      (first (filter #(= (:name %) enum-type-name) types))
            enums          (:enumValues enum-type)]
        (gen/elements (map #(symbol (:name %)) enums)))

      (= [kind of-type-kind]
         ["NON_NULL" "ENUM"])
      :gql/enum

      (= [kind of-type-kind]
         ["NON_NULL" "OBJECT"])
      :gql/object

      ;; TODO - might need a null collection
      (= [kind of-type-kind
          (-> arg-type :ofType :ofType :kind)
          (-> arg-type :ofType :ofType :ofType :kind)
          ]
         ["NON_NULL" "LIST" "NON_NULL" "OBJECT"])
      :gql.list/object

      (= [kind of-type-kind
          (-> arg-type :ofType :ofType :kind)
          (-> arg-type :ofType :ofType :ofType :kind)
          ]
         ["NON_NULL" "LIST" "NON_NULL" "ENUM"])
      :gql.list/enum

      (= [kind of-type-kind
          (-> arg-type :ofType :ofType :kind)
          (-> arg-type :ofType :ofType :ofType :kind)
          ]
         ["NON_NULL" "LIST" "NON_NULL" "SCALAR"])
      (condp = (-> arg-type :ofType :ofType :ofType :name)
        "String"
        :gql.list.scalar/non-null-string
        "ID"
        gen/string-alphanumeric)

      (= [kind of-type-kind
          (-> arg-type :ofType :ofType :kind) (-> arg-type :ofType :ofType :name)]
         ["LIST" "NON_NULL" "SCALAR" "ID"])
      ;;(gen/vector gen/string)
      ;;gen/string-alphanumeric
      ;;gen/string
      (gen/fmap (fn [id] (str "gid://abc/abc/" id)) gen/small-integer)

      ;; "gid://abc/abc/1"
      ;;(gen/generate (gen/fmap (fn [id] (str "gid://abc/abc/" id)) gen/small-integer))

      (= [kind of-type-kind
          (-> arg-type :ofType :ofType :kind) (-> arg-type :ofType :ofType :name)]
         ["LIST" "NON_NULL" "SCALAR" "String"])
      ;;gen/string
      gen/string-alphanumeric

      (= [kind of-type-kind]
         ["NON_NULL" "LIST"])
      (if-let [type-name (:name (:ofType arg-type))]
        :gql/list
        :bad-format-non-null-list)

      (= [kind of-type-kind (-> arg-type :ofType :ofType :kind)]
         ["LIST" "NON_NULL" "OBJECT"])
      :gql.list/object

      (= [kind of-type-kind (-> arg-type :ofType :ofType :kind)]
         ["LIST" "NON_NULL" "ENUM"])
      ;;(gen/vector gen/symbol)
      ;;(gen/vector gen/small-integer)
      (let [enum-type-name (-> arg-type :ofType :ofType :name)
            enum-type      (first (filter #(= (:name %) enum-type-name) types))
            enums          (:enumValues enum-type)]
        (gen/elements (map #(symbol (:name %)) enums)))


      (= [kind of-type-kind]
         ["LIST" "OBJECT"])
      :gql.list/object

      (= [kind of-type-kind of-type-name]
         ["LIST" "SCALAR" "String"])
      ;;(gen/vector gen/string)
      (gen/vector gen/string-alphanumeric)

      (= [kind]
         ["OBJECT"])
      :gql/object

      :default
      :not-implemented
      )))