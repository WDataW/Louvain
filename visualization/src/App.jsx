import { useState } from "react";
import LandingPage from "./pages/LandingPage";
import GraphPage from "./pages/GraphPage";
import { BrowserRouter, Route, Routes } from "react-router";
import NotFound from "./pages/NotFound";


export default function App() {

    return <div className="bg-black">
        <BrowserRouter>
            <Routes>
                <Route index element={<LandingPage />} />
                <Route path="/visualize/:dirName" element={<GraphPage />} />
                <Route path="/*" element={<NotFound />} />
            </Routes>
        </BrowserRouter>
    </div>
}