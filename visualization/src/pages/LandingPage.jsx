import GraphCard from "../components/GraphCard";

export default function LandingPage({ className = "", children, ...props }) {
    const graphs = [
        {
            dirName: "/video-example",
            edges: 12,
            nodes: 9,
            image: "/assets/images/exampleGraph.png",
            title: "Example Graph",
            source: "https://youtu.be/Xt0vBtBY2BU?si=hA_cAw5-2F8XhbVC&t=2327"
        },
        {
            dirName: "/hamsterfriendships",
            edges: 12534,
            nodes: 1858,
            image: "/assets/images/hamstrer.png",
            title: "Hamsterster friendships",
            source: "http://konect.cc/networks/petster-friendships-hamster/"
        },
        {
            dirName: "/musae-ptbr",
            edges: 31299,
            nodes: 1912,
            image: "/assets/images/ptbr.png",
            title: "Twitch Social Networks - PT",
            source: "https://snap.stanford.edu/data/twitch-social-networks.html"
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
            dirName: "/biogridthaliana",
            edges: 47916,
            nodes: 10417,
            image: "/assets/images/thalina.png",
            title: "Biogrid: Arabidopsis Thaliana Columbia",
            source: "https://github.com/microgravitas/network-corpus/blob/master/networks/BioGrid-Arabidopsis-Thaliana-Columbia.info"
        },
        {
            dirName: "/deezer-europe",
            edges: 92752,
            nodes: 28281,
            image: "/assets/images/deezer.png",
            title: "Deezer Europe Social Network",
            source: "https://snap.stanford.edu/data/feather-deezer-social.html"
        },
        {
            dirName: "/astroph",
            edges: 198110,
            nodes: 18772,
            image: "/assets/images/astroph.png",
            title: "Astro Physics Collaboration Cetwork",
            source: "https://snap.stanford.edu/data/ca-AstroPh.html"
        },
        {
            dirName: "/musae-facebook",
            edges: 171002,
            nodes: 22470,
            image: "/assets/images/musaefacebook.png",
            title: "Facebook Large Page-Page Network",
            source: "https://snap.stanford.edu/data/facebook-large-page-page-network.html"
        },
        // {
        //     dirName: "/Gnutella31",
        //     edges: 147892,
        //     nodes: 62586,
        //     image: "/assets/images/Gnutella02.png",
        //     title: "Gnutella peer-to-peer network, August 31 2002",
        //     source: "https://snap.stanford.edu/data/p2p-Gnutella31.html"
        // },

    ]
    return (
        <div className={`px-[2rem] sm:px-[4rem] py-[2rem] h-full min-h-[100vh] w-full ${className}`} {...props}>
            <h1 className="text-[2rem] mb-[2rem]">Graph Community Detection Using Louvain Algorithm</h1>

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