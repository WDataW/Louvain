import GraphCard from "../components/GraphCard";

export default function LandingPage({ className = "", children, ...props }) {
    const graphs = [
        {
            dirName: "/video-example",
            edges: 12,
            nodes: 7,
            image: "/assets/images/exampleGraph.png",
            title: "Example Graph",
            source: "https://youtu.be/Xt0vBtBY2BU?si=hA_cAw5-2F8XhbVC&t=2327"
        },
        {
            dirName: "/email-eu",
            edges: 25571,
            nodes: 1005,
            image: "/assets/images/emailEU.png",
            title: "email-Eu-core network",
            source: "https://snap.stanford.edu/data/email-Eu-core.html"
        },
        {
            dirName: "/facebook",
            edges: 88234,
            nodes: 4039,
            image: "/assets/images/facebook.png",
            title: "Social circles: Facebook",
            source: "https://snap.stanford.edu/data/ego-Facebook.html"
        },
        {
            dirName: "/Gnutella05",
            edges: 31839,
            nodes: 8846,
            image: "/assets/images/Gnutella05.png",
            title: "Gnutella peer-to-peer network",
            source: "https://snap.stanford.edu/data/p2p-Gnutella05.html"
        },
        {
            dirName: "/deezer-europe",
            edges: 92752,
            nodes: 28281,
            image: "/assets/images/deezer.png",
            title: "Deezer Europe Social Network",
            source: "https://snap.stanford.edu/data/feather-deezer-social.html"
        },

    ]
    return (
        <div className={`px-[2rem] sm:px-[4rem] py-[2rem] h-full min-h-[100vh] w-full ${className}`} {...props}>
            <h1 className="text-[1.5rem]">Graph-Based Community Detection Using Louvain Algorithm</h1>
            <hr className="mb-[2rem]" />
            <ul className="grid gap-[1rem] grid-cols-1 sm:grid-cols-[repeat(auto-fit,minmax(20rem,1fr))]">
                {
                    graphs.map((g) =>
                        <li key={g.dirName}>
                            <GraphCard dirName={g.dirName} edges={g.edges} nodes={g.nodes} image={g.image} title={g.title} source={g.source}></GraphCard>
                        </li>
                    )
                }
            </ul>
        </div>
    );
}