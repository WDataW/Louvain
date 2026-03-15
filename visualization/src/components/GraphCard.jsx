import { NavLink } from "react-router";

export default function GraphCard({ dirName, title, nodes, edges, image, className = "", children, ...props }) {
    return (
        <NavLink to={`visualize/${dirName}`} className={`block px-[1rem] py-[0.7rem] text-start border min-h-[10rem] rounded-[2rem] ${className}`} {...props}>
            <div className="flex items-center overflow-hidden aspect-square">
                <img className="w-full object-contain" src={image} alt="" />
            </div>
            <h1 className="text-blue-400 font-bold">{title}</h1>
            <div className="flex gap-[1.5rem] mb-[0.5rem]">
                <p><span className="">Nodes:</span> {nodes}</p>
                <p><span className="">Edges:</span> {edges}</p>
            </div>
        </NavLink>
    );
}