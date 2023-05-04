const { buildSchema, parse, printSchema } = require('graphql');
const { makeExecutableSchema } = require('graphql-tools');
const { readFileSync } = require('fs');
const { join } = require('path');
let GraphExtractor = require("./graph-extraction");

const typeDefs = readFileSync(join(__dirname + "/schemas/" , "schema.graphql")).toString();

let extractor = new GraphExtractor(typeDefs);
extractor.generateAllPaths(1);






console.log(extractor.paths);
//console.log(extractor.paths[0].subfields);


/**
const schema = buildSchema(typeDefs);
const ast = parse(typeDefs);
const allPaths = getAllPaths(ast);

let tests = [];

allPaths.forEach((path, index) => {
    tests.push(...queryFromPath(path));
});
*/



/**
 * returns the type of a Node without its ListType or NonNullType
 * @param type the Type to inspect
 * @returns the real Type of that field

function getNamedType(type) {
    if (type.kind === "NonNullType") {
        return getNamedType(type.type);
    } else if (type.kind === "ListType") {
        return getNamedType(type.type);
    } else {
        return type;
    }
}
 */
/**
 *
 * @param astNode
 * @param currentPath
 * @param visitedTypes
 * @returns {*[]}

function getPaths(astNode, currentPath = [], visitedTypes = new Set()) {
    const paths = [];

    if (astNode.kind === "FieldDefinition") {
        const namedType = getNamedType(astNode.type);
        const isObjectType = namedType.kind === "NamedType" && namedType.name.value !== "String" && namedType.name.value !== "Int" && namedType.name.value !== "Float" && namedType.name.value !== "Boolean" && namedType.name.value !== "ID";

        if (isObjectType) {
            if (visitedTypes.has(namedType.name.value)) {
                return paths;
            }

            visitedTypes.add(namedType.name.value);
            paths.push(currentPath);

            const objectTypeDefinition = ast.definitions.find(
                (definition) =>
                    definition.kind === "ObjectTypeDefinition" &&
                    definition.name.value === namedType.name.value
            );

            if (objectTypeDefinition) {
                objectTypeDefinition.fields.forEach((field) => {
                    const newPath = [...currentPath, field.name.value];
                    paths.push(...getPaths(field, newPath, new Set(visitedTypes)));
                });
            }
        }
    }
    return paths;
}
*/
/**
 * searches the tree for all paths that can be queryd. Root is always the type "Query"
 * @param ast the tree to search
 * @returns {*[]} the found paths from the tree

function getAllPaths(ast) {
    const paths = [];
    // Find the Query type
    const queryTypeDefinition = ast.definitions.find(
        (definition) =>
            definition.kind === "ObjectTypeDefinition" && definition.name.value === "Query"
    );
    // search all Paths with Root in Query
    if (queryTypeDefinition) {
        queryTypeDefinition.fields.forEach((field) => {
            paths.push(...getPaths(field, ["Query", field.name.value]));
        });
    }
    return paths;
}

function queryFromPath(path) {
    const fieldCombinations = generateAllFieldCombinations(path);
    const queries = [];

    fieldCombinations.forEach((combinations) => {
        const fieldsPath = path.slice(1); // Remove the type name
        const inputVariables = getInputVariables(path);
        const inputValues = inputVariables
            .map((arg) => `${arg.name.value}: ${generateDummyValue(arg.type.name)}`)
            .join(", ");

        let query = `{ ${fieldsPath[0]}${inputValues ? `(${inputValues})` : ""}`;
        query += generateSubFields(path, 1, combinations);
        query += `}`;

        queries.push(query);
    });

    return queries;
}


function generateSubFields(path, index, combinations) {
    if (index >= path.length) return "";

    const objectTypeDefinition = getNamedTypeFromPath(path, index);

    if (!objectTypeDefinition) return "";

    let subFields = "";
    const fieldNames = objectTypeDefinition.fields.map((field) => {
        const args = field.arguments.map(arg => `${arg.name.value}: ${generateDummyValue(arg.type)}`).join(', ');
        return args ? `${field.name.value}(${args})` : field.name.value;
    });
    const selectedFields = combinations[index - 1].map((isSelected, i) => isSelected && fieldNames[i]).filter(Boolean);
    if (selectedFields.length > 0) {
        subFields += "{ ";
        subFields += selectedFields.join(" ");
        subFields += `}`;
    }

    const nextPath = path.slice(0, index + 1);
    const nestedSubFields = generateSubFields(nextPath, index + 1, combinations);
    return `${subFields}${nestedSubFields}`;
}

function generateAllFieldCombinations(path) {
    const objectTypeDefinitions = path.slice(1).map((typeName) =>
        ast.definitions.find(
            (definition) =>
                definition.kind === "ObjectTypeDefinition" && definition.name.value === typeName
        )
    ).filter(Boolean);
    const fieldCounts = objectTypeDefinitions.map((definition) => definition.fields.length);
    const combinationCounts = fieldCounts.map((count) => Math.pow(2, count) - 1);

    const allCombinations = combinationCounts.map((count, index) =>
        new Array(count).fill(0).map((_, i) =>
            new Array(fieldCounts[index]).fill(false).map((_, j) => i & (1 << j))
        )
    );

    return cartesianProduct(allCombinations);
}

function cartesianProduct(arrays) {
    return arrays.reduce((a, b) => a.flatMap((x) => b.map((y) => [...x, y])), [[]]);
}

function generateDummyValue(type) {
    switch (type) {
        case "Int":
            return 1;
        case "Float":
            return 1.0;
        case "String":
            return "dummy";
        case "Boolean":
            return true;
        case "ID":
            return "1";
        default:
            return null;
    }
}

function getNamedTypeFromPath(path, index) {
    const typeName = path[index];
    return ast.definitions.find(
        (definition) =>
            definition.kind === "ObjectTypeDefinition" && definition.name.value === typeName
    );
}

function getInputVariables(path) {
    const objectTypeDefinition = ast.definitions.find(
        (definition) =>
            definition.kind === "ObjectTypeDefinition" &&
            definition.name.value === path[0]
    );

    if (objectTypeDefinition) {
        const fieldDefinition = objectTypeDefinition.fields.find(
            (field) => field.name.value === path[1]
        );

        if (fieldDefinition && fieldDefinition.arguments.length > 0) {
            return fieldDefinition.arguments;
        }
    }

    return [];
}
 */