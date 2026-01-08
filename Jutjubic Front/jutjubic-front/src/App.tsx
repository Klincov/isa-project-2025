import React from 'react'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import UploadVideo from './UploadVideo' 
import Home from './Home.tsx' 
import Login from "./Login";
import Register from "./Register";

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} /> {/* Početna stranica */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/create-post" element={<UploadVideo />} /> {/* Ruta za upload videa */}
      </Routes>
    </Router>
  )
}

export default App
