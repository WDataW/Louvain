import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App'
import Stages from './contexts/Stages'

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <App />
    </StrictMode>
)