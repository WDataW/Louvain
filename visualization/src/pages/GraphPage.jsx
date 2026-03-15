import { useEffect, useState } from "react";
import { draw } from "../../visualize";
import { NavLink, useParams } from "react-router";

let once = true;
export default function GraphPage({ className = "", children, ...props }) {
    const { dirName } = useParams();
    const [graphs, setGraphs] = useState([]);
    const [titles, setTitles] = useState([]);
    const [index, setIndex] = useState(0);
    const fetchGraphs = async () => {
        let i = 0
        const tempGraphs = [];
        const tempTitles = [];
        while (true) {
            try {
                const response1 = await fetch(`/${dirName}/initialGraph${i}.json`);
                if (!response1.ok) throw new Error("Fetching done");
                const initialGraph = await response1.json();
                tempGraphs.push(initialGraph);
                tempTitles.push(`Level ${i}: Initial State`);

                const response2 = await fetch(`/${dirName}/optimizedGraph${i}.json`);
                const optimizedGraph = await response2.json();
                tempGraphs.push(optimizedGraph);
                tempTitles.push(`Level ${i}: Final State`);
                i++;
            } catch (error) {
                console.log(error.message);
                break;
            }
        }
        setGraphs(tempGraphs);
        setTitles(tempTitles);
    }

    useEffect(() => {
        fetchGraphs();
    }, []);
    useEffect(() => {
        if (graphs.length) {
            draw(graphs[0]);
        }
    }, [graphs]);
    useEffect(() => {
        if (graphs.length != 0) draw(graphs[index]);
    }, [index]);
    return (
        <div className="relative">
            <div className="px-[2rem] flex justify-around text-[1.3rem] w-full z-1 absolute top-[1.5rem] leading-0">
                <button onClick={() => setIndex(i => Math.max(i - 1, 0))}>{"◀"}</button>
                <h1>{titles[index]}</h1>
                <button onClick={() => setIndex(i => Math.min(i + 1, graphs.length - 1))}>{"▶"}</button>
            </div>
            <div className="absolute top-[0.7rem] z-1 right-[1.5rem] text-red-500 text-bold">
                <NavLink to={'/'}>
                    EXIT
                </NavLink>
            </div>
            {!graphs[index] && <div className="absolute top-[50%] w-full text-center -y-translate-1/2 -x-translate-1/2">LOADING...</div>}
            <div id="network" className={`${className}`} {...props}>

            </div>
        </div>
    );
}