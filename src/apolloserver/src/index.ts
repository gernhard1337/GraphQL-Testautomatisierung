import {ApolloServer} from '@apollo/server';
import {startStandaloneServer} from '@apollo/server/standalone';
import {ApolloServerErrorCode} from "@apollo/server/errors";
import resolvers from "./resolvers.js";
import typeDefs from "./typeDefs.js";


async function startApolloServer(){
    try{
        // The ApolloServer constructor requires two parameters: your schema
        // definition and your set of resolvers.
        const server = new ApolloServer({
            typeDefs,
            resolvers,
        });
        await new Promise<void>((resolve => startStandaloneServer(server, {
            listen: { port: 4000 },
            })));
    }catch (err){
        throw new Error("Something wrong");
    }
}

const server = startApolloServer()

console.log("server running")

