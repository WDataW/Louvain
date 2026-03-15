import { useEffect, useState } from "react";
import { draw } from "../utils/visualize";
import { NavLink, useParams } from "react-router";
import xIcon from '../assets/icons/x.svg';
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
                tempTitles.push(`Level ${i}: Initial`);

                const response2 = await fetch(`/${dirName}/optimizedGraph${i}.json`);
                const optimizedGraph = await response2.json();
                tempGraphs.push(optimizedGraph);
                tempTitles.push(`Level ${i}: Final`);
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
            <div className="px-[3rem] flex justify-around text-[1.3rem] w-full z-1 absolute bottom-[2rem] ">
                <button disabled={index == 0} className={`p-[0.5rem] ${index == 0 && 'opacity-30'}`} onClick={() => setIndex(i => Math.max(i - 1, 0))}>{"◀"}</button>
                <h1 className="p-[0.5rem]">{titles[index]}</h1>
                <button disabled={index == graphs.length - 1} className={`p-[0.5rem] ${index == graphs.length - 1 && 'opacity-30'}`} onClick={() => setIndex(i => Math.min(i + 1, graphs.length - 1))}>{"▶"}</button>
            </div>
            <div className="absolute  z-1 right-0 text-[1.7rem] text-red-500 text-bold">
                <NavLink className={'block  p-[1rem] '} to={'/'}>
                    <img className="h-[1rem] w-[1rem] object-cover" src={xIcon} alt="exit" />
                </NavLink>
            </div>
            {!graphs[index] && <div className="absolute top-[50%] w-full text-center -y-translate-1/2 -x-translate-1/2">LOADING...</div>}
            <div id="network" className={`${className}`} {...props}>

            </div>
        </div>
    );
}