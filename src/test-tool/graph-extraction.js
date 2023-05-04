const { buildSchema, parse, printSchema } = require('graphql');
const { makeExecutableSchema } = require('graphql-tools');

/**
 *
 */
module.exports = class GraphExtractor {
    typeDefinitionString;
    schema;
    ast;
    paths;

    constructor(typeDefinitionString) {
        this.typeDefinitionString = typeDefinitionString;
        this.ast = parse(this.typeDefinitionString);
        this.schema = buildSchema(this.typeDefinitionString);
        this.paths = [];
    }

    /**
     *
     * @param type 1 for only Paths routing from Query Type, 0 for all paths routing from any Type
     * @return [json] array of json. With this keys:
     * node: node name
     * adjacent: nodes that are neighbours
     * type: type of the node
     * arguments: arguments a node might take
     * subfields: subfields that may be queried at runtime
     */
    generateAllPaths(type){
        let foundPaths = [];

        this.ast.definitions.forEach((field) => {
            foundPaths.push(
                {
                    "node": field.name.value,
                    "adjacent": [],
                    "type": field.kind,
                    "arguments": this.getArguments(field.fields),
                    "subfields": field.fields.filter( field => field.kind === "FieldDefinition")
                }
            );

            /*
            if(type === 1){
                if(field.kind === "ObjectTypeDefinition" && field.name.value === "Query") {
                    foundPaths.push(...this.getPaths(field, [field.name.value]));
                }
            }else{
                foundPaths.push(...this.getPaths(field, [field.name.value]));
            }
            */

        });
        this.paths = foundPaths;
        return foundPaths;
    }

    /**
     *
     * @param astNode
     * @param currentPath
     * @return {*[]}
     */
    getPaths(astNode, currentPath = []) {
        const paths = [];

        if (astNode.kind === 'ObjectTypeDefinition') {
            astNode.fields.forEach((field) => {
                const newPath = [...currentPath, field.name.value];
                paths.push(...this.getPaths(field, newPath));
            });
        } else if (astNode.kind === 'FieldDefinition') {
            if (astNode.type.kind === 'NamedType' || astNode.type.kind === 'NonNullType') {
                paths.push(currentPath);
            }
            if (astNode.type.kind === 'ListType') {
                const listTypePath = [...currentPath, '[' + astNode.type.type.name.value + ']'];
                paths.push(...this.getPaths(astNode.type, listTypePath));
            }
        } else if (astNode.kind === 'ListType' || astNode.kind === 'NonNullType') {
            paths.push(...this.getPaths(astNode.type, currentPath));
        }
        else if (astNode.kind === 'NamedType') {
            paths.push(currentPath);
        }
        return paths;
    }

    /**
     *
     * @param field
     * @return {*[]}
     */
    getArguments(field){
        let args = [];
        field.forEach((field) => {
            if(field.arguments.length > 0){
                field.arguments.forEach((argument) => {
                    if(argument.kind === "InputValueDefinition"){
                        args.push(argument);
                    }
                });
            }
        });
        return args;
    }
}