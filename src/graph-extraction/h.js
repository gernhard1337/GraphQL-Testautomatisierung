const cfg = {
    Author: ['Author -> Int', 'Author -> String', 'Author -> [Book]'],
    Book: ['Book -> Int', 'Book -> String', 'Book -> Author'],
    Query: [
        'Query -> String',
        'Query -> [Book]',
        'Query -> [Author]',
        'Query -> Book',
        'Query -> [Book]',
        'Query -> Author'
    ]
};

function findPaths(startNode, visited, currentPath, allPaths) {
    if (visited[startNode]) {
        return;
    }

    visited[startNode] = true;

    const edges = cfg[startNode];
    for (let i = 0; i < edges.length; i++) {
        const [fromNode, toNode] = edges[i].split(' -> ');
        currentPath.push(fromNode);

        if (toNode === startNode) {
            allPaths.push([...currentPath, toNode]);
        } else {
            findPaths(toNode, visited, currentPath, allPaths);
        }

        currentPath.pop();
    }

    visited[startNode] = false;
}

function findPrimePaths() {
    const allPaths = [];
    const visited = {};

    for (const startNode in cfg) {
        findPaths(startNode, visited, [], allPaths);
    }

    const primePaths = [];
    for (let i = 0; i < allPaths.length; i++) {
        const currentPath = allPaths[i];
        let isPrime = true;

        for (let j = 0; j < allPaths.length; j++) {
            if (i !== j && allPaths[j].every((node) => currentPath.includes(node))) {
                isPrime = false;
                break;
            }
        }

        if (isPrime) {
            primePaths.push(currentPath);
        }
    }

    return primePaths;
}

console.log(findPrimePaths());