const express = require('express');
const { graphqlHTTP } = require('express-graphql');
const { buildSchema } = require('graphql');

// Definiere das Schema
const schema = buildSchema(`
  type Query {
    book(id: ID!): Book
    books: [Book]
    hello: String
  }
  
  type Book {
    id: ID!
    title: String!
    author: String!
  }
`);

// Beispiel Daten
const booksData = [
    { id: '1', title: 'Harry Potter and the Philosopher\'s Stone', author: 'J.K. Rowling' },
    { id: '2', title: 'The Hobbit', author: 'J.R.R. Tolkien' },
];

// Resolver-Funktionen
const root = {
    book: ({ id }) => booksData.find(book => book.id === id),
    books: () => booksData,
    hello: () => "Hallo"
};

// Erstelle den Express-Server
const app = express();

// Endpoint für GraphQL
app.use('/graphql', graphqlHTTP({
    schema: schema,
    rootValue: root,
    graphiql: true, // Aktiviere GraphiQL-UI
}));

// Starte den Server
app.listen(4000, () => {
    console.log('GraphQL Server läuft auf http://localhost:4000/graphql');
});
