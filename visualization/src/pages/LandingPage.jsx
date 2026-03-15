import GraphCard from "../components/GraphCard";

export default function LandingPage({ className = "", children, ...props }) {
    return (
        <div className={`px-[4rem] py-[2rem] h-full min-h-[100vh] w-full ${className}`} {...props}>
            <h1 className="text-[1.5rem]">Graph-Based Community Detection Using Louvain Algorithm</h1>
            <hr className="mb-[2rem]" />
            <ul className="grid gap-[1rem] grid-cols-[repeat(auto-fit,minmax(10rem,1fr))]">
                <li className="w-full">
                    <GraphCard dirName="/video-example" edges={7} nodes={12} image={"/assets/images/smallGraph.png"} title={"Small Graph"}></GraphCard>
                </li>
                <li>
                    <GraphCard dirName="/email-eu" edges={1005} nodes={25571} image={"/assets/images/mediumGraph.png"} title={"Medium Graph"}></GraphCard>

                </li>
                <li>
                    <GraphCard dirName="/Gnutella05" edges={31839} nodes={8846} image={"/assets/images/largeGraph.png"} title={"Large Graph"}></GraphCard>

                </li>
            </ul>
        </div>
    );
}