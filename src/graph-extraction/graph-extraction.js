const { buildSchema, parse, printSchema } = require('graphql');
const { graphql } = require("graphql");
const util = require('util')

const typeDefs = `
type Book {
    id: Int
    title: String
    author: Author
}
type Author {
    id: Int
    name: String
    books: [Book]
}
type Query {
    # test query
    hello: String
    # all books
    books: [Book]
    # all authors 
    authors: [Author]
    # a single book from a author name
    bookWrittenBy(name: String!): Book
    # all books from a author
    booksWrittenBy(name: String!): [Book]
    # a author for a book title
    writtenBy(title: String!): Author
}
`;

const schema = buildSchema(typeDefs);
const ast = parse(printSchema(schema));

function checkQueryType (definition) {
    return definition.kind === "ObjectTypeDefinition" && definition.name.value === "Query";
}

function extractQueryTypeFields(ast) {
    const queryType = ast.definitions.find(checkQueryType);
    return queryType ? queryType.fields : [];
}

function getPaths(astNode, currentPath = []) {
    const paths = [];

    if (astNode.kind === "FieldDefinition") {
        paths.push(currentPath);

        if (astNode.type.kind === "NamedType" && astNode.type.name.value !== "String" && astNode.type.name.value !== "Int" && astNode.type.name.value !== "Float" && astNode.type.name.value !== "Boolean" && astNode.type.name.value !== "ID") {
            const objectTypeDefinition = ast.definitions.find(
                (definition) =>
                    definition.kind === "ObjectTypeDefinition" &&
                    definition.name.value === astNode.type.name.value
            );
            if (objectTypeDefinition) {
                objectTypeDefinition.fields.forEach((field) => {
                    const newPath = [...currentPath, field.name.value];
                    paths.push(...getPaths(field, newPath));
                });
            }
        }
    }

    return paths;
}

function getAllPaths(ast) {
    const paths = [];

    ast.definitions.forEach((definition) => {
        if (definition.kind === "ObjectTypeDefinition") {
            definition.fields.forEach((field) => {
                paths.push(...getPaths(field, [definition.name.value, field.name.value]));
            });
        }
    });

    return paths;
}

const paths = getAllPaths(ast);

paths.forEach((path) => {
    console.log(path.join(" -> "));
});

function getInputVariables(path) {
    const objectTypeDefinition = ast.definitions.find(
        (definition) =>
            definition.kind === "ObjectTypeDefinition" &&
            definition.name.value === path[0]
    );

    if (objectTypeDefinition) {
        const fieldDefinition = objectTypeDefinition.fields.find(
            (field) => field.name.value === path[1]
        );

        if (fieldDefinition && fieldDefinition.arguments.length > 0) {
            return fieldDefinition.arguments;
        }
    }

    return [];
}



function generateDummyValue(type) {
    switch (type) {
        case "Int":
            return 1;
        case "Float":
            return 1.0;
        case "String":
            return "dummy";
        case "Boolean":
            return true;
        case "ID":
            return "1";
        default:
            return null;
    }
}

const testQuery = (path) => {
    const fieldsPath = path.slice(1); // Remove the type name
    const inputVariables = getInputVariables(path);
    console.log(inputVariables);
    const inputValues = inputVariables
        .map((arg) => `${arg.name.value}: ${generateDummyValue(arg.type.name)}`)
        .join(", ");

    const query = `
    query {
      ${fieldsPath.join("{ ")}${inputValues ? `(${inputValues})` : ""}${"}".repeat(fieldsPath.length)}
    }
  `;
    console.log(query);
    return graphql(schema, query);
};

paths.forEach((path, index) => {
        const result = testQuery(path);
        console.log(result);
});












