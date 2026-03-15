import { NavLink } from "react-router";

export default function GraphCard({ dirName, title, nodes, edges, image, source, className = "", children, ...props }) {
    return (
        <div className="min-h-full block px-[1rem] py-[0.7rem] text-start border  rounded-[2rem]">
            <NavLink to={`visualize/${dirName}`} className={` block  text-start    `} {...props}>
                <div className="flex items-center overflow-hidden aspect-square">
                    <img className="w-full object-contain" src={image} alt="" />
                </div>
                <div className="ps-[0.5rem] flex flex-col gap-[0.5rem]">
                    <h1 className="text-green-400 text-[1.2rem] font-bold ">{title}</h1>
                    <div className={`flex gap-[1.5rem] ${!source && 'mb-[0.5rem]'}`}>
                        <p><span className="">Nodes:</span> {nodes}</p>
                        <p><span className="">Edges:</span> {edges}</p>
                    </div>
                </div>
            </NavLink>
            {source && <p className="my-[0.5rem] ps-[0.5rem]">Source: <a target="_blank" className="underline text-blue-400" href={source}>{source}</a></p>}
        </div>
    );
}