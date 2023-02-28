var express = require('express');
var { graphqlHTTP } = require('express-graphql');
var { buildSchema } = require('graphql');

// Construct a schema, using GraphQL schema language
var schema = buildSchema(`
  type Query {
    quoteOfTheDay: String
    random: Float!
    rollThreeDice: [Int]
    person(id: Int!): Person
    personKnows(id: Int!): [Person]
  }
  type Person{
    id: Int
    name: String
    age: Int
    knows: [Int]
  }
`);

var fakeDatabase = [
    {
        id: 1,
        name: "Peter",
        age: 23,
        knows: [2,3,4]
    },
    {
        id: 2,
        name: "Michael",
        age: 18,
        knows: [1]
    },
    {
        id: 3,
        name: "Lisa",
        age: 26,
        knows: [2,3]
    },
    {
        id: 4,
        name: "Angelina",
        age: 27,
        knows: [1, 2]
    },
    {
        id: 5,
        name: "Angelina",
        age: 27,
        knows: []
    },
]

var getPerson = function (args){
    console.log("getPerson called");
    if(args.id){
        var id = args.id;
        var response = fakeDatabase.filter(function (el) {
            if(el.id == id){
                return el;
            }
        });
        return response[0];
    }else{
        return fakeDatabase;
    }
}

var getPersonKnows = function (args) {
    console.log("getPersonKnows called");
    if(args.id){
        var response = fakeDatabase.filter(function (el) {
            if(el.knows.includes(args.id)){
                return el;
            }
        });
        console.log(response);
        return response;
    }else{
        return [];
    }


}


// The root provides a resolver function for each API endpoint
var root = {
    quoteOfTheDay: () => {
        return Math.random() < 0.5 ? 'Take it easy' : 'Salvation lies within';
    },
    random: () => {
        return Math.random();
    },
    rollThreeDice: () => {
        return [1, 2, 3].map(_ => 1 + Math.floor(Math.random() * 6));
    },
    person: getPerson,
    personKnows: getPersonKnows
};

var app = express();
app.use('/graphql', graphqlHTTP({
    schema: schema,
    rootValue: root,
    graphiql: true,
}));
app.listen(4000);
console.log('Running a GraphQL API server at localhost:4000/graphql');