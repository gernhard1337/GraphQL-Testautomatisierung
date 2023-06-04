(ns graphql-tester.primepath-generators
  (:require [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.pprint]
            [clojure.java.io :as io]
            [graphql-tester.specs :as specs]
            [clojure.walk]))

(defn resolve-field-type
  [field-type]
  (let [kind         (:kind field-type)
        type-name    (:name field-type)
        of-type-kind (:kind (:ofType field-type))
        of-type-name (:name (:ofType field-type))]
    (cond
      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "String"])
      :gql.scalar/non-null-string

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "Boolean"])
      :gql.scalar/non-null-boolean

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "Int"])
      :gql.scalar/non-null-int

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "ID"])
      :gql.scalar/non-null-id

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "Time"])
      :gql.scalar/non-null-time

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "Float"])
      :gql.scalar/non-null-float

      (= [kind type-name]
         ["SCALAR" "Boolean"])
      :gql.scalar/boolean

      (= [kind type-name]
         ["SCALAR" "Int"])
      :gql.scalar/int

      (= [kind type-name]
         ["SCALAR" "Float"])
      :gql.scalar/float

      (= [kind type-name]
         ["SCALAR" "String"])
      :gql.scalar/string

      (= [kind type-name]
         ["SCALAR" "Time"])
      :gql.scalar/time

      (= [kind of-type-kind]
         ["NON_NULL" "ENUM"])
      :gql/enum

      (= [kind of-type-kind]
         ["NON_NULL" "OBJECT"])
      :gql/object

      ;; TODO - might need a null collection
      (= [kind of-type-kind
          (-> field-type :ofType :ofType :kind)
          (-> field-type :ofType :ofType :ofType :kind)
          ]
         ["NON_NULL" "LIST" "NON_NULL" "OBJECT"])
      :gql.list/object

      (= [kind of-type-kind
          (-> field-type :ofType :ofType :kind)
          (-> field-type :ofType :ofType :ofType :kind)
          ]
         ["NON_NULL" "LIST" "NON_NULL" "ENUM"])
      :gql.list/enum

      (= [kind of-type-kind
          (-> field-type :ofType :ofType :kind)
          (-> field-type :ofType :ofType :ofType :kind)
          ]
         ["NON_NULL" "LIST" "NON_NULL" "SCALAR"])
      (condp = (-> field-type :ofType :ofType :ofType :name)
        "String"
        :gql.list.scalar/non-null-string
        "ID"
        :gql.list.scalar/non-null-id)

      (= [kind of-type-kind]
         ["NON_NULL" "LIST"])
      (if-let [type-name (:name (:ofType field-type))]
        :gql/list
        :bad-format-non-null-list)

      (= [kind of-type-kind (-> field-type :ofType :ofType :kind)]
         ["LIST" "NON_NULL" "OBJECT"])
      :gql.list/object

      (= [kind of-type-kind]
         ["LIST" "OBJECT"])
      :gql.list/object

      (= [kind]
         ["OBJECT"])
      :gql/object

      :default
      :not-implemented
      )))

(defn get-field-type
  [field-type]
  (let [kind         (:kind field-type)
        type-name    (:name field-type)
        of-type-kind (:kind (:ofType field-type))
        of-type-name (:name (:ofType field-type))]
    (cond
      (= [kind of-type-kind
          (-> field-type :ofType :ofType :kind)
          (-> field-type :ofType :ofType :ofType :kind)]
         ["NON_NULL" "LIST" "NON_NULL" "OBJECT"])
      {:type :gql/object :name (-> field-type :ofType :ofType :ofType :name)}

      (= [kind of-type-kind (-> field-type :ofType :ofType :kind)]
         ["LIST" "NON_NULL" "OBJECT"])
      {:type :gql/object :name (-> field-type :ofType :ofType :name)}

      (= [kind of-type-kind]
         ["LIST" "OBJECT"])
      {:type :gql/object :name of-type-name}

      (= [kind]
         ["OBJECT"])
      {:type :gql/object :name type-name}

      (= [kind of-type-kind]
         ["NON_NULL" "OBJECT"])
      {:type :gql/object :name of-type-name}

      :default
      {:type :not-an-object})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Generators for GQL types

(defn gen-fields
  [types type-name max-elements]
  ;;(println (str "type-name " type-name))
  (let [fields (:fields (first (filter #(= (:name %) type-name) types)))]
    ;;(println (str "Fields for type " fields))
    (gen/vector-distinct
      (gen/elements
        (:fields (first (filter #(= (:name %) type-name) types))))
      {:min-elements 1
       :max-elements max-elements
       })))

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

(defn resolve-arg-gen-string
  [arg-type types]
  (let [kind         (:kind arg-type)
        type-name    (:name arg-type)
        of-type-kind (:kind (:ofType arg-type))
        of-type-name (:name (:ofType arg-type))]
    (cond
      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "String"])
      gen/string
      ;;gen/string-alphanumeric

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "Boolean"])
      ;;gen/boolean
      gen/string

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "Int"])
      ;;gen/large-integer
      gen/string

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "ID"])
      ;; CHANGE THIS TO GET INPUT VALIDATION BUG
      ;;gen/string-alphanumeric
      gen/string

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "Time"])
      :gql.scalar/non-null-time

      (= [kind of-type-kind of-type-name]
         ["NON_NULL" "SCALAR" "Float"])
      :gql.scalar/non-null-float

      (= [kind type-name]
         ["SCALAR" "Boolean"])
      ;;gen/boolean
      gen/string

      (= [kind type-name]
         ["SCALAR" "Int"])
      ;;gen/large-integer
      gen/string

      (= [kind type-name]
         ["SCALAR" "Float"])
      ;;:gql.scalar/float
      gen/string

      (= [kind type-name]
         ["SCALAR" "String"])
      gen/string
      ;;gen/string-alphanumeric

      (= [kind type-name]
         ["SCALAR" "Time"])
      gen/string

      (= [kind type-name]
         ["SCALAR" "ID"])
      gen/string
      ;;gen/string-alphanumeric

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
        :gql.list.scalar/non-null-id)

      (= [kind of-type-kind
          (-> arg-type :ofType :ofType :kind) (-> arg-type :ofType :ofType :name)]
         ["LIST" "NON_NULL" "SCALAR" "ID"])
      ;;(gen/vector gen/string)
      ;;gen/string-alphanumeric
      ;;gen/string
      ;;(gen/fmap (fn [id] (str "gid://abc/abc/" id)) gen/small-integer)
      (gen/fmap (fn [id] (str "gid://abc/abc/" id)) gen/string)

      ;; "gid://abc/abc/1"
      ;;(gen/generate (gen/fmap (fn [id] (str "gid://abc/abc/" id)) gen/small-integer))

      (= [kind of-type-kind
          (-> arg-type :ofType :ofType :kind) (-> arg-type :ofType :ofType :name)]
         ["LIST" "NON_NULL" "SCALAR" "String"])
      gen/string
      ;;gen/string-alphanumeric

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
        (gen/elements (map #(symbol (:name %)) enums))
        ;;gen/string
        )


      (= [kind of-type-kind]
         ["LIST" "OBJECT"])
      :gql.list/object

      (= [kind of-type-kind of-type-name]
         ["LIST" "SCALAR" "String"])
      (gen/vector gen/string)
      ;;(gen/vector gen/string-alphanumeric)

      (= [kind]
         ["OBJECT"])
      :gql/object

      :default
      :not-implemented
      )))

(defn gen-args
  [args types size arg-gen-fn]
  (mapv
    (fn [arg]
      (let [arg-generator (arg-gen-fn (:type arg) types)]
        (assoc arg :value (gen/generate arg-generator size))))
    args))

;;;;;;
; my implementation
;;;;;;
; path ends on non node that has no other object fields
(defn node-has-object-field?
  [node]
  false
  )

(defn extract-fields [x]
  (if (and (sequential? x)
           (= (first x) :fields)
           (sequential? (second x))
           (= (first (second x)) :map))
    (rest (second x))
    x))

;; Breitensuche übers Schema
(defn generate-prime-paths
  [start-nodes other-nodes visited path]
  (let [field-name (first start-nodes)
        field-optional? (-> start-nodes second :optional)
        field-type (-> other-nodes second :type)
        rootFields (second (clojure.walk/postwalk extract-fields (-> start-nodes :object/Query)))
        nodeFields (second (clojure.walk/postwalk extract-fields (-> other-nodes :object/Query)))
        ]
    ;(clojure.pprint/pprint other-nodes)

    ;(println (type rootFields))

  )
)




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; maybe rename in node-map-to-prime-path-nodes ?
(defn node-map-to-prime-path-generators
  [query-node-map other-nodes-map]
  (let [
        ; root-field = QueryType
        root-field (second (second (:object/Query query-node-map)))
        prime-paths (generate-prime-paths query-node-map other-nodes-map [] [])
        ]

    )
  )








