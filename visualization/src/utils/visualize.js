import { Network } from 'vis-network';
import { DataSet } from 'vis-data';

// converts JSON nodes to dataset nodes
const getNodes = (graph) => {
    return new DataSet(graph.nodes.filter((n) => !n.disconnected));
}

// converts JSON edges to dataset edges
const getEdges = (graph) => {
    const connectedIds = getConnectedIds(graph);
    return new DataSet(graph.edges.filter((e) => {
        return connectedIds.has(e.from) && connectedIds.has(e.to);
    }));
}
const getConnectedIds = (graph) => {
    const nodes = graph.nodes;
    const connectedIds = new Set();
    for (let n of nodes) {
        if (!n.disconnected) connectedIds.add(n.id);
    }
    return connectedIds;
}
// draws the graph
export const draw = (graph, setRendering) => {
    // contains the canva
    if (!graph) return;
    const container = document.getElementById("network");

    const data = {
        nodes: getNodes(graph),
        edges: getEdges(graph)
    }
    const options = {
        nodes: {
            margin: 50,
            borderWidth: 2,
            color: {
                border: "#ffffff",
                background: "black"
            },
            shape: "ellipse",
            font: {
                size: 4 * Math.sqrt(graph.nodes.length),
            },
        },
        edges: {
            width: 2,
            smooth: {
                type: "continuous",
            },
        },
        physics: {
            enabled: false,
            stabilization: false,
        },
        interaction: {
            tooltipDelay: 200,
            hideEdgesOnDrag: true,
        }
    };

    const network = new Network(container, data, options);
    network.once("afterDrawing", () => {
        setRendering(false);
    })

}
const display = async (url) => {
    const resposnse = await fetch(url);
    if (!resposnse.ok) return;
    const graph = await resposnse.json();
    draw(graph);
}
