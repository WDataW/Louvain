# Louvain Community Detection (Java)

A Java implementation of the Louvain algorithm for community detection in weighted graphs.

The algorithm detects hierarchical community structure by maximizing modularity and produces a dendrogram representing successive graph aggregations.

Graph layouts are computed using the ForceAtlas2 (FA2) algorithm via the Gephi Toolkit. Optional JSON exports enable interactive visualization in the browser using vis-network.

---

## Overview

Community detection identifies groups of nodes that are more densely connected to each other than to the rest of the network. The Louvain method is widely used because it is fast, scalable, and produces a hierarchy of communities.

This implementation performs the full Louvain process in Java, including graph aggregation across multiple levels. The result is a dendrogram describing how communities merge over time.

Optional components support visualization by exporting graph data and layout coordinates for use in a web interface.

---

## Features

- Louvain community detection algorithm
- Modularity optimization
- Hierarchical community structure (dendrogram)
- Multi-level graph aggregation
- ForceAtlas2 (FA2) layout computation via Gephi Toolkit
- Optional JSON export for visualization
- Interactive client-side rendering with vis-network
- Built with Maven

---

## Requirements

### Core Application (Java)

- Java Development Kit (JDK) 21 or newer
- Maven 3.6 or newer

### Visualization Client (Optional)

- Node.js 18 or newer
- npm

---

## Dependencies

### Core Application

- Gephi Toolkit 0.10.0 — ForceAtlas2 layout computation

Dependencies are managed automatically via Maven (`pom.xml`).

### Client

Frontend dependencies (React, Tailwind CSS, vis-network, etc.) are managed via `npm` in the `visualization` directory.

---

## Installation & Build

### Clone the repository

```bash
git clone https://github.com/WDataW/Louvain.git
cd Louvain
```

### Build with Maven

```bash
mvn clean install
```

This compiles the project and downloads dependencies.

---

## Input Format

The program expects a graph as an edge list:

```
source target weight
```

If weights are omitted, edges are treated as unweighted and assigned a default weight of 1.

Example (weighted):

```
1 2 1.0
2 3 1.0
3 4 0.5
4 1 0.8
```

Example (unweighted):

```
1 2
2 3
3 4
```

---

## Running the Program

The application is intended to be run from an IDE.

1. Open the project as a Maven project.
2. Ensure JDK 21 or newer is configured.
3. Place your dataset file in `src/main/resources`.
4. Edit the main class to select the dataset.
5. Run the program.

Example:

```java
Locale.setDefault(Locale.US); // Essential

Graph graph = Graph.readGraph("/my-dataset.txt", " ");
List<Map<Integer, Set<Integer>>> dendrogram =
    Louvain.louvain(graph, "output");
```

### Important Notes

- `Locale.setDefault(Locale.US)` ensures decimal numbers use a dot (`.`) separator.
- Removing this line may cause parsing errors on systems that use commas as decimal separators.
- The second argument to `readGraph` specifies the delimiter used in the dataset.

Examples:

```java
Graph graph = Graph.readGraph("/p2p-Gnutella31.txt", "\t"); // tab-delimited
Graph graph = Graph.readGraph("/deezer_europe.csv", ",");   // comma-separated
Graph graph = Graph.readGraph("/facebook_combined.txt", " "); // space-separated
```

---

## Workflow

1. Place a dataset file in the `src/main/resources` folder.
2. Load the graph using `Graph.readGraph`.
3. Pass the graph to `Louvain.louvain`.

Example:

```java
Locale.setDefault(Locale.US); // Essential — do not remove

Graph graph = Graph.readGraph("/facebook_combined.txt", " ");
List<Map<Integer, Set<Integer>>> dendrogram =
    Louvain.louvain(graph, "output");
```

The dataset path is relative to the resources directory.

---

## Output

### Dendrogram (Primary Output)

The algorithm returns a dendrogram representing the hierarchical community structure:

```
List<Map<Integer, Set<Integer>>>
```

- Each list element corresponds to one level of the hierarchy
- Each map associates a super-node (community) with the nodes merged to form it
- Higher levels represent progressively aggregated graphs
- The final level corresponds to the coarsest community partition

---

### JSON Export (Optional)

For visualization purposes, the program can export graph data as JSON files.

For each level of the hierarchy, two files are generated:

- Pre-optimization graph (before community refinement)
- Post-optimization graph (after modularity improvement)

Each JSON file may include:

- Nodes
- Edges
- Community assignments
- ForceAtlas2 layout coordinates
- Connectivity information

These files are intended for client-side rendering using vis-network.

---

## Graph Layout (ForceAtlas2)

Node positions are computed using the ForceAtlas2 algorithm via the Gephi Toolkit.

ForceAtlas2 is a force-directed layout that:

- Pulls connected nodes together
- Pushes unrelated nodes apart
- Produces visually meaningful clusters
- Scales well to large graphs

The computed coordinates are exported for visualization.

---

## Client-Side Visualization (Optional)

An optional web interface renders the graph interactively in the browser using vis-network.

### Technologies Used

- React — user interface
- React Router — navigation
- Tailwind CSS — styling
- vis-network — graph rendering
- vis-data — graph data management

The client consumes the exported JSON files to display communities with pan, zoom, and interaction.

### Running the Visualization

```bash
cd visualization
npm install --legacy-peer-deps
npm run dev
```

> `--legacy-peer-deps` is required due to peer dependency conflicts with React 19.

Open the local URL shown in the terminal (typically `http://localhost:5173`).

### Loading Graph Data

Graph data is loaded from the public directory based on the route:

```
/visualize/<graphdirname>
```

For example:

```
/visualize/facebook
```

This loads precomputed JSON files from:

```
visualization/public/facebook/
```

---

## Project Structure

```
src/main/java/                       Louvain algorithm and graph processing
src/main/java/graph/                 Graph modelling classes
src/main/resources/                  Dataset files
pom.xml                              Maven configuration
visualization/                       React-based client for graph visualization
visualization/public/<graphdirname>  Precomputed graph JSON files (served to client)
```

---

## Example Use Case

Given a network with densely connected regions, the algorithm identifies communities at multiple levels while preserving the hierarchical structure of the network.

---

## References

Blondel, V. D., Guillaume, J.-L., Lambiotte, R., & Lefebvre, E. (2008).  
Fast unfolding of communities in large networks.

---

## Authors

- Wael Kweder
- Abdulrahman Zwobe
- Tamim Al-Qurashi
- Abdulwahid Ghalib