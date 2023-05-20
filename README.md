# GraphQL-Testautomatisierung

Code & Latex-Code für die Masterarbeit "Testentwurf & Automatisierung für GraphQL".

Eine Verbesserung von "Property-based Testing on GraphQL Apis" mit verbesserter Pfadgenerierung. 

Run the Property-based Tool: 

cd src/clojuretool/GraphQL/implementation
clj -M -m gql.namespace wobei namespace in den jeweiligen clojure files steht. 

Run the leinigen Tool with selection for path generation:
cd src/graphql-tester 
lein run url mode 
wobei "url" der zu testende graphQL Endpoint ist und "mode" 0 oder 1 sein kann.
0 steht für standard Property-based Testing, 1 steht für Testing mit PrimePath Coverage.  