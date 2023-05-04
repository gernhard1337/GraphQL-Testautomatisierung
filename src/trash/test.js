const { buildSchema, parse } = require('graphql');
const { printSchema } = require('graphql/utilities');

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

const graph = {};

function extractType(type) {
    switch (type.kind) {
        case 'NamedType':
            return type.name.value;
        case 'ListType':
            return `[${extractType(type.type)}]`;
        case 'NonNullType':
            return `${extractType(type.type)}!`;
        default:
            throw new Error(`Unexpected type kind: ${type.kind}`);
    }
}

ast.definitions.forEach((definition) => {
    if (definition.kind === 'ObjectTypeDefinition') {
        const typeName = definition.name.value;
        const fields = {};

        definition.fields.forEach((field) => {
            const fieldName = field.name.value;
            const fieldType = extractType(field.type);

            fields[fieldName] = fieldType;
        });

        graph[typeName] = Object.values(fields);
    }
});

console.log(graph);