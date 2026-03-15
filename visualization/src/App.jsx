import { useState } from "react";
import LandingPage from "./pages/LandingPage";
import GraphPage from "./pages/GraphPage";
import { BrowserRouter, Route, Routes } from "react-router";


export default function App() {

    return <div className="bg-black">
        <BrowserRouter>
            <Routes>
                <Route index element={<LandingPage />} />
                <Route path="/visualize/:dirName" element={<GraphPage />} />
            </Routes>
        </BrowserRouter>
    </div>
}