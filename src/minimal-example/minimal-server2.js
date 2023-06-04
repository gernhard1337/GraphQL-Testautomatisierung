const express = require('express');
const { graphqlHTTP } = require('express-graphql');
const { buildSchema } = require('graphql');

// GraphQL Schema
const schema = buildSchema(`
  type Query {
    book(id: ID): Book
    author(id: ID): Author
    publisher(id: ID): Publisher
  }

  type Book {
    id: ID
    title: String
    author: Author
    publisher: Publisher
  }

  type Author {
    id: ID
    name: String
    books: [Book]
  }

  type Publisher {
    id: ID
    name: String
    books: [Book]
  }
`);

// Root resolver
const root = {
    book: ({ id }) => {
        return bookData.find(book => book.id === id);
    },
    author: ({ id }) => {
        return authorData.find(author => author.id === id);
    },
    publisher: ({ id }) => {
        return publisherData.find(publisher => publisher.id === id);
    }
};

// Beispieldaten
const authorData = [
    {
        id: '1',
        name: 'Herman Melville'
    },
    {
        id: '2',
        name: 'Sebastian Fitzek'
    },
    {
        id: '3',
        name: 'Neale Donald Walsch'
    },
    {
        id: '4',
        name: 'Gavin Extence'
    },
    {
        id: '5',
        name: 'Hermann Hesse'
    },
    {
        id: '6',
        name: 'Friedrich Nietzsche'
    },
    {
        id: '7',
        name: 'George Orwell'
    },
    {
        id: '8',
        name: 'H.G. Wells'
    }
];

const publisherData = [
    {
        id: '1',
        name: 'Penguin Random House'
    },
    {
        id: '2',
        name: 'Simon & Schuster'
    },
    {
        id: '3',
        name: 'Suhrkamp Verlag'
    },
    {
        id: '4',
        name: 'Houghton Mifflin Harcourt'
    },
    {
        id: '5',
        name: 'Macmillan Publishers'
    }
];

const bookData = [
    {
        id: '1',
        title: 'Moby Dick',
        author: authorData[0],
        publisher: publisherData[0]
    },
    {
        id: '2',
        title: 'Das Perfekte Verbrechen',
        author: authorData[1],
        publisher: publisherData[0]
    },
    {
        id: '3',
        title: 'Gespräche mit Gott',
        author: authorData[2],
        publisher: publisherData[1]
    },
    {
        id: '4',
        title: 'Das unerhörte Leben des Alex Woods',
        author: authorData[3],
        publisher: publisherData[1]
    },
    {
        id: '5',
        title: 'Steppenwolf',
        author: authorData[4],
        publisher: publisherData[2]
    },
    {
        id: '6',
        title: 'Also sprach Zarathustra',
        author: authorData[5],
        publisher: publisherData[2]
    },
    {
        id: '7',
        title: '1984',
        author: authorData[6],
        publisher: publisherData[3]
    },
    {
        id: '8',
        title: 'Farm der Tiere',
        author: authorData[6],
        publisher: publisherData[3]
    },
    {
        id: '9',
        title: 'Die Zeitmaschine',
        author: authorData[7],
        publisher: publisherData[4]
    },
    {
        id: '10',
        title: 'Die unsichtbare Frau',
        author: authorData[7],
        publisher: publisherData[4]
    }
];

// Verbinde die Bücher mit ihren Autoren und Verlagen
authorData[0].books = [bookData[0]];
authorData[1].books = [bookData[1]];
authorData[2].books = [bookData[2]];
authorData[3].books = [bookData[3]];
authorData[4].books = [bookData[4]];
authorData[5].books = [bookData[5]];
authorData[6].books = [bookData[6], bookData[7]];
authorData[7].books = [bookData[8], bookData[9]];

publisherData[0].books = [bookData[0], bookData[1]];
publisherData[1].books = [bookData[2], bookData[3]];
publisherData[2].books = [bookData[4], bookData[5]];
publisherData[3].books = [bookData[6], bookData[7]];
publisherData[4].books = [bookData[8], bookData[9]];

const app = express();

app.use('/graphql', graphqlHTTP({
    schema: schema,
    rootValue: root,
    graphiql: true,
}));

app.listen(4000, () => console.log('Express GraphQL Server läuft auf localhost:4000/graphql'));
