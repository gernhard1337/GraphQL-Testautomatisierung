import { ApolloServer } from '@apollo/server';
import { startStandaloneServer } from '@apollo/server/standalone';

const typeDefs = `#graphql
type Book {
    id: Int
    title: String
    author: Author
}
type Author {
    id: Int
    name: String
    wrote: [Book]
}

# The "Query" type is special: it lists all of the available queries that
# clients can execute, along with the return type for each. In this
# case, the "books" query returns an array of zero or more Books (defined above).
type Query {
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

const books = [
    {
        id: 1,
        title: 'The Awakening',
        author: 1,
    },
    {
        id: 2,
        title: 'City of Glass',
        author: 2,
    },
];

const authors = [
    {
        name: "Paul Auster",
        id: 1
    },
    {
        name: "Kate Chopin",
        id: 2
    },
    {
        name: "Ken Folet",
        id: 3
    }
]


// Resolvers define how to fetch the types defined in your schema.
// This resolver retrieves books from the "books" array above.
const resolvers = {
    Query: {
        books: () => books,
        authors: () => authors,
        bookWrittenBy(parent, args, contextValue, info){
            var author = authors.filter(function (author) {
                return author.name = args.name;
            })[0];
            var authorid = author.id;
            var writtenBook = books.filter(function (book) {
                return book.author == authorid;
            })[0];
            return writtenBook;
        },

        booksWrittenBy(parent, args, contextValue, info){
            console.log(args.name);
            return books.filter(function (book) {
                return book.author == args.name;
            })
        },

        writtenBy(parent, args, contextValue, info){
            console.log(args.title);
            var book = books.filter(function (book) {
                return book.title == args.title;
            })[0];

            var authorid = book.author;

            return authors.filter(function (author) {
                return author.id == authorid;
            })[0]
        }
    },
};

// The ApolloServer constructor requires two parameters: your schema
// definition and your set of resolvers.
const server = new ApolloServer({
    typeDefs,
    resolvers,
});

// Passing an ApolloServer instance to the `startStandaloneServer` function:
//  1. creates an Express app
//  2. installs your ApolloServer instance as middleware
//  3. prepares your app to handle incoming requests
const { url } = await startStandaloneServer(server, {
    listen: { port: 4000 },
});

console.log(`🚀  Server ready at: ${url}`);