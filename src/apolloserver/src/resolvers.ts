import data from "./fakeDatabase.js";

const authors = data.authors;
const books = data.books;



function findAuthor(id){
    return authors.filter(function (author){
        return author.id == id;
    })[0]
}

function findBooks(bookIds) {
    let bookList = [];
    books.forEach(function (book) {
        if(book.id in bookIds){
            bookList.push(book);
        }
    })
    return bookList;
}

const resolvers = {
    Query: {
        hello() {
            return "Hi"
        },
        books() {
            console.log(books)
            return books
        },
        authors: () => authors,
        bookWrittenBy(parent, args, contextValue, info){
            var author = authors.filter(function (author) {
                return author.name = args.name;
            })[0];
            var authorid = author.id;
            return books.filter(function (book) {
                return book.author == authorid;
            })[0];
        },
        booksWrittenBy(parent, args, contextValue, info){
            return books.filter(function (book) {
                return book.author == args.name;
            })
        },
        writtenBy(parent, args, contextValue, info){
            var book = books.filter(function (book) {
                return book.title == args.title;
            })[0];

            let authorId = book.author;

            return authors.filter(function (author) {
                return author.id == authorId;
            })[0]
        }
    },
    Book: {
        title: ({ title }) => title,
        id: ({ id }) => id,
        author: ({ author }) => { return findAuthor(author) }
    },
    Author: {
        name: ({ name }) => name,
        id: ({ id }) => id,
        books: ({ books }) => { return findBooks(books) }
    }
};

export default resolvers