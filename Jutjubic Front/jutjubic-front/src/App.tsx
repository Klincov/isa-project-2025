import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import UploadVideo from './UploadVideo' 
import Home from './Home.tsx' 
import Login from "./Login";
import Register from "./Register";
import Feed from "./VideoFeed";
import Video from "./VideoShowcase.tsx";

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} /> {/* Početna stranica */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/create-post" element={<UploadVideo />} /> {/* Ruta za upload videa */}
        <Route path="/feed" element={<Feed/>} /> 
        <Route path="/video/:id" element={<Video />} />
      </Routes>
    </Router>
  )
}

export default App
