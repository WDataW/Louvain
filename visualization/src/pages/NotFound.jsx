import { NavLink } from "react-router";

export default function NotFound({ className = "", children, ...props }) {
    return (
        <div className={`h-screen w-screen font-bold flex items-cetner flex-col text-center justify-center ${className}`} {...props}>
            <h1 className="text-[10rem]  leading-none">404</h1>
            <h1 className="text-[3rem] leading-none">Resource Not Found</h1>
            <NavLink className={'mt-[3rem] text-[2rem] text-blue-500 underline'} to={'/'}>
                Go Home
            </NavLink>
        </div>
    );
}