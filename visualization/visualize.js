import {Network} from 'vis-network';
import {DataSet} from 'vis-data';

// converts JSON nodes to dataset nodes
const getNodes = (graph) => {
    return new DataSet(graph.nodes);
}

// converts JSON edges to dataset edges
const getEdges = (graph) => {
    return new DataSet(graph.edges);
}

// draws the graph
const draw = (graph)=>{
    // contains the canva
    const container = document.getElementById("network");

    const data = {
        nodes: getNodes(graph),
        edges: getEdges(graph)
    }
    const options = {
        nodes: {
            shape: "dot",
            size:Math.min(1000, graph.nodes.length * 10 / 4),
            font: {
                size: 0,
            },
        },
        edges: {
            width: 0.15,
            color: { inherit: "from" },
            smooth: {
                type: "continuous",
            },
        },
        physics: {
            enabled:false,
            stabilization: false,
            barnesHut: {
                gravitationalConstant: -500000,
                springConstant: 0.001,
                springLength: 50,
                centralGravity:0.3,
            },
        },
        interaction: {
            tooltipDelay: 200,
            hideEdgesOnDrag: true,
        }
    };

    const network = new Network(container,data,options);

    setTimeout(()=>{// stop moving after 15 seconds
        network.setOptions({physics:{
            enabled:false
            }});
    },15000)
}
const display = async (url)=> {
    const resposnse = await fetch(url);
    if(!resposnse.ok)return;
    const graph = await resposnse.json();
    draw(graph);
}
display("/initialGraph.json");