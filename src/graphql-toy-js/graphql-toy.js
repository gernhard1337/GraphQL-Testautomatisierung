const {ApolloServer, gql} = require('apollo-server');

const typeDefs = gql`
    type User {
        id: ID!
        name: String!
        age: Int!
        projects: [Project!]!
    }

    type Project {
        id: ID!
        name: String!
        description: String!
        owner: User!
        members: [User!]!
    }

    type Query {
        project(id: ID!): Project
        userProjects(id: ID!): [Project!]!
    }
`;

const db = {
    projects: [
        {
            id: "1",
            name: "Project 1",
            description: "Awesome project!",
            owner: "100",
            members: ["100", "200"],
        },
        {
            id: "2",
            name: "Project 2",
            description: "Not an awesome project!",
            owner: "200",
            members: ["200"],
        },
    ],
    users: [
        {
            id: "100",
            name: "Burt",
            age: 23,
            projects: ["1", "2"],
        },
        {
            id: "200",
            name: "Earnie",
            age: 32,
            projects: ["2"],
        },
    ],
};

const resolvers = {
    Query: {
        project: (_, {id}, context, info) => {
            // Example bug 1 - Syntax mistake
            // return db.projects.find(project => project.id ===);

            // Example bug 2 - Give "foo", input validation
            // return db.projects[parseInt(id)];

            // Example bug 3 - Input type validation bug
            // return db.projects[id];

            // Example bug 4 - Using the wrong field
            // return db.projects.find(project => project.name === id);

            // Example bug 5 - wrong type "error"
            // return { ...db.projects.find(project => project.id === id), name: ["a", "b"] };

            // Example bug 6 - IndexOutOfBounds
            // return db.projects[parseInt(id)];

            // Correct implementation
            return db.projects.find(project => project.id === id);
        },
        userProjects: (_, {id}, context, info) => {
            const user = db.users.find(user => user.id === id);

            // Example bug 1 - Syntax Error
            // return db.projects.filter(project => user.projects.includes());

            // Example bug 2 - Using the wrong field
            // return db.projects.filter(project => user.projects.includes(project.name));

            // Example bug 3 - wrong type "errors"
            // return db.projects.filter(project => user.projects.includes);

            // Correct implementation
            return db.projects.filter(project => user.projects.includes(project.id));
        },
    },
    Project: {
        owner: (project) => {
            // Example bug 1 - Syntax mistake
            // return db.users.find(user => user.id ===);

            // Example bug 2 - Using the wrong field
            // return db.users.find(user => user.name === project.owner);

            // Example bug 3 - wrong type "error"
            // return { ...db.users.find(user => user.id === project.owner), name: ["a", "b"] };

            // Correct implementation
            return db.users.find(user => user.id === project.owner);
        },
        members: (project) => {
            // Example bug 1 - logic error
            // return db.users.filter(user => project.members.includes());

            // Example bug 2 - Using the wrong field
            // return db.users.filter(user => project.members.includes(user.name));

            // Example bug 3 - wrong type "errors"
            // return db.users.filter(user => project.members.includes);

            // Correct implementation
            return db.users.filter(user => project.members.includes(user.id));
        },
    },
    User: {
        projects: (user) => {
            // Example bug 1 - Syntax Error
            // return db.projects.filter(project => user.projects.includes());

            // Example bug 2 - Using the wrong field
            // return db.projects.filter(project => user.projects.includes(project.name));

            // Example bug 3 - wrong type "errors"
            // return db.projects.filter(project => user.projects.includes);

            // Correct implementation
            return db.projects.filter(project => user.projects.includes(project.id));
        },
    },
};


const server = new ApolloServer({typeDefs, resolvers});

server.listen().then(({url}) => {
    console.log(`Server ready at ${url}`);
});