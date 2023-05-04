function generatePrimePaths(cfg) {
    // Step 1: Initialize data structures
    const visited = new Set();
    const stack = [];
    const paths = [];

    // Step 2: Perform depth-first search to find all paths
    function dfs(node) {
        visited.add(node);
        stack.push(node);

        cfg[node].forEach((edge) => {
            const nextNode = edge.split(' -> ')[1];

            if (!visited.has(nextNode)) {
                dfs(nextNode);
            } else if (stack.includes(nextNode)) {
                const path = [...stack.slice(stack.indexOf(nextNode)), nextNode].join(' -> ');
                paths.push(path);
            }
        });

        stack.pop();
    }

    Object.keys(cfg).forEach((node) => {
        if (!visited.has(node)) {
            dfs(node);
        }
    });

    // Step 3: Identify the prime paths
    const primePaths = [];
    const edgesToCover = new Set(Object.values(cfg).flat());
    const pathsToCover = new Set(paths);

    paths.forEach((path) => {
        const edgesInPath = path.split(' -> ');
        const edgesNotInOtherPaths = edgesInPath.filter((edge) => {
            const otherPaths = paths.filter((p) => p !== path);
            return !otherPaths.some((p) => p.includes(edge));
        });

        if (edgesNotInOtherPaths.every((edge) => edgesToCover.has(edge))) {
            primePaths.push(path);
            edgesNotInOtherPaths.forEach((edge) => edgesToCover.delete(edge));
            pathsToCover.delete(path);
        }
    });

    // Step 4: Cover the remaining paths with additional test cases
    const primePathsToCover = new Set();
    pathsToCover.forEach((path) => {
        const edgesInPath = path.split(' -> ');
        const uncoveredEdges = edgesInPath.filter((edge) => edgesToCover.has(edge));
        if (uncoveredEdges.length > 0) {
            uncoveredEdges.forEach((edge) => primePathsToCover.add(edge));
        } else {
            primePathsToCover.add(edgesInPath[0]);
        }
    });

    // If there are still uncovered edges, add them to the prime paths to cover
    edgesToCover.forEach((edge) => primePathsToCover.add(edge));

    // Step 5: Generate test cases for the prime paths
    const testCases = primePathsToCover.map((edge) => {
        const [startNode, endNode] = edge.split(' -> ');
        const pathNodes = primePaths.filter((path) => path.includes(startNode) && path.includes(endNode));
        const query = buildQueryFromPathNodes(pathNodes);
        return { pathNodes, query };
    });

    return testCases;
}

function buildQueryFromPathNodes(pathNodes) {
    // Build a GraphQL query that executes all the nodes in the path
    const query = `{
    ${pathNodes.map((node) => node.split('(')[0]).join(' ')}
  }`;

    return query;
}
