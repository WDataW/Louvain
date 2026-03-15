import { createContext, useState } from "react";

const StagesContext = createContext();
export default function Stages({ className = "", children }) {
    const [stages, setStages] = useState([]);
    return (
        <StagesContext value={[stages, setStages]}>
            {children}
        </StagesContext>
    );
}