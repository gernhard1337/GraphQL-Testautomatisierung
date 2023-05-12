import {afterAll, jest, test} from '@jest/globals';
import 'jest';
import {ApolloServer} from "@apollo/server";
import resolvers from "./resolvers";
import typeDefs from "./typeDefs";
import assert = require("assert");

jest.setTimeout(2000)
jest.retryTimes(3)

let testServer = new ApolloServer({
        typeDefs, resolvers
})

test("Test Query1", async () => {
   const query1 = "{ hello }"
   const result = await testServer.executeOperation({
           query: query1
   });
    assert(result.body.kind === 'single');
    expect(result.body.singleResult.errors).toBeUndefined();
    expect(result.body.singleResult.data?.hello).toBe("Hi")
    console.log( result.body.singleResult.data?.hello )
});

test("Test Query2", async () => {
    const query2 = '{ books { title } }'
    const result = await testServer.executeOperation({
        query: query2
    });
    assert(result.body.kind === 'single');
    expect(result.body.singleResult.errors).toBeUndefined();
    expect(result.body.singleResult.data?.books).toEqual([{"title": "The Awakening"}, {"title": "City of Glass"}])
});

afterAll(async () => {
        await testServer.stop()
})