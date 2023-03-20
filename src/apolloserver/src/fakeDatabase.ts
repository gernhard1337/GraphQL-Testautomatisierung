let books = [
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

let authors = [
    {
        name: "Paul Auster",
        id: 1,
        books: [1,2]
    },
    {
        name: "Kate Chopin",
        id: 2,
        books: [1,2]
    },
    {
        name: "Ken Folet",
        id: 3,
        books: [1,2]
    }
]

const data = {books, authors}

export default data