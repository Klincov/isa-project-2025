import { Link } from 'react-router-dom'
import './Center.css'

const Home = () => {
  return (
    <div className='center'>
      <div>
      <h1>Dobrodošli!</h1>
      <div style={{ display: "flex", gap: "12px", justifyContent: "center", marginTop: "16px" }}>
        <Link to="/login">
          <button>Prijava</button>
        </Link>

        <Link to="/register">
          <button>Registracija</button>
        </Link>

        <Link to="/create-post">
          <button>Postavi Video</button>
        </Link>

        <Link to="/feed">
          <button>Video Feed</button>
        </Link>
        
      </div>
      </div>
    </div>
  )
}

export default Home
