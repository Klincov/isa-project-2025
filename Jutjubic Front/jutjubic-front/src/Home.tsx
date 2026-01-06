import React from 'react'
import { Link } from 'react-router-dom'

const Home = () => {
  return (
    <div>
      <h1>Dobrodošli!</h1>
      <Link to="/create-post">
        <button>Postavi Video</button>
      </Link>
    </div>
  )
}

export default Home
