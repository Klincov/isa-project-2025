import React from 'react'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import UploadVideo from './UploadVideo' 
import Home from './Home.tsx' 

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} /> {/* Početna stranica */}
        <Route path="/create-post" element={<UploadVideo />} /> {/* Ruta za upload videa */}
      </Routes>
    </Router>
  )
}

export default App
