import { NavLink } from "react-router";

export default function GraphCard({ dirName, title, nodes, edges, image, source, className = "", children, ...props }) {
    return (
        <div className="min-h-full grid grid-cols-1 px-[1rem] py-[0.7rem] text-start border border-[rgba(255,255,255,0.4)]  rounded-[1rem]">
            <div  className={`text-start`} {...props}>
                <div className="flex items-center overflow-hidden aspect-square">
                    <img className="w-full object-contain" src={image} alt="" />
                </div>
                <div className="ps-[0.5rem] flex flex-col gap-[0.5rem]">
                    <h1 className="text-green-400 text-[1.2rem] font-bold ">{title}</h1>
                    <div className={`flex gap-[1.5rem] `}>
                        <p><span className="">Nodes:</span> {nodes}</p>
                        <p><span className="">Edges:</span> {edges}</p>
                    </div>
                </div>
            </div>
            {source && <p className="my-[0.5rem] ps-[0.5rem]">Source: <a target="_blank" className="underline break-all text-blue-400" href={source}>{source}</a></p>}
            <NavLink to={`visualize/${dirName}`} className={"block text-center h-[2.75rem] self-end mt-[1rem] mb-[0.2rem] p-[0.5rem] w-full border hover:bg-green-500 hover:text-black transition border-green-500 rounded-[0.5rem]" }>Explore</NavLink>
        </div>
    );
}