import { Network } from 'vis-network';
import { DataSet } from 'vis-data';

// converts JSON nodes to dataset nodes
const getNodes = (graph) => {
    return new DataSet(graph.nodes);
}

// converts JSON edges to dataset edges
const getEdges = (graph) => {
    return new DataSet(graph.edges);
}

// draws the graph
export const draw = (graph) => {
    // contains the canva
    if (!graph) return;
    const container = document.getElementById("network");

    const data = {
        nodes: getNodes(graph),
        edges: getEdges(graph)
    }
    const options = {
        nodes: {
            margin: 15,
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

    setTimeout(() => {// stop moving after 15 seconds
        network.setOptions({
            physics: {
                enabled: false
            }
        });
    }, 15000)
}
const display = async (url) => {
    const resposnse = await fetch(url);
    if (!resposnse.ok) return;
    const graph = await resposnse.json();
    draw(graph);
}
