import React, { useState } from 'react'
import 'leaflet/dist/leaflet.css'
import { MapContainer, TileLayer, Marker, useMapEvents } from 'react-leaflet'
import LatLng from 'leaflet'

const LocationPicker = ({ onSelect }: { onSelect: (lat: number, lon: number) => void }) => {
  useMapEvents({
    click(e) {
      onSelect(e.latlng.lat, e.latlng.lng)
    }
  })
  return null
}



const UploadVideo = () => {
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [tags, setTags] = useState<string[]>([])
  const [video, setVideo] = useState<File | null>(null)
  const [thumbnail, setThumbnail] = useState<File | null>(null)
  const [location, setLocation] = useState<{ lat: number; lon: number } | null>(null)
  const [error, setError] = useState<string>('')

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault()

    if (!title || !description || !video || !thumbnail) {
      setError('Sva polja su obavezna!')
      return
    }

    const formData = new FormData()
    formData.append('title', title)
    formData.append('description', description)
    tags.forEach(tag => formData.append('tags', tag))
    formData.append('video', video)
    formData.append('thumbnail', thumbnail)
    if (location) {
      formData.append('lat', location.lat.toString())
      formData.append('lon', location.lon.toString())
    }

    try {
      const API_URL = import.meta.env.VITE_API_URL as string;

      const response = await fetch(`${API_URL}/upload-video`, {
        method: "POST",
        credentials: "include",
        body: formData,
      })

      if (!response.ok) {
        console.log(response)
        throw new Error('Došlo je do greške')
      }

      const data = await response.json()
      console.log('Video uspešno postavljen:', data)
    } catch (error) {
      console.error('Greška prilikom slanja zahteva:', error)
    }
  }

  // Handler for file inputs
  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>, setFile: React.Dispatch<React.SetStateAction<File | null>>) => {
    const file = event.target.files ? event.target.files[0] : null
    if (file) {
      setFile(file)
    }
  }

  // Handle adding/removing tags
  const handleTagsChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value
    if (value && !tags.includes(value)) {
      setTags([...tags, value])
    }
  }
  const handleLatChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const lat = parseFloat(e.target.value)
    setLocation(prev => ({
      lat,
      lon: prev ? prev.lon : 0 
    }))
  }

  const handleLonChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const lon = parseFloat(e.target.value)
    setLocation(prev => ({
      lat: prev ? prev.lat : 0,
      lon
    }))
  }


  return (
    <div>
      <h1>Upload Video</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label>Naslov:</label>
          <input
            type="text"
            value={title}
            onChange={e => setTitle(e.target.value)}
            required
          />
        </div>

        <div>
          <label>Opis:</label>
          <textarea
            value={description}
            onChange={e => setDescription(e.target.value)}
            required
          />
        </div>

        <div>
          <label>Tagovi (razdvojeni zarezom):</label>
          <input
            type="text"
            onBlur={handleTagsChange} // Add tag on blur
          />
          <ul>
            {tags.map(tag => (
              <li key={tag}>{tag}</li>
            ))}
          </ul>
        </div>

        <div>
          <label>Thumbnail (slika):</label>
          <input
            type="file"
            accept="image/*"
            onChange={e => handleFileChange(e, setThumbnail)}
            required
          />
        </div>

        <div>
          <label>Video (MP4):</label>
          <input
            type="file"
            accept="video/mp4"
            onChange={e => handleFileChange(e, setVideo)}
            required
          />
        </div>

        <div>
          <label>Izaberi lokaciju na mapi:</label>

          <MapContainer
            center={[44.7866, 20.4489]} // Beograd default
            zoom={6}
            style={{ height: '300px', width: '100%' }}
          >
            <TileLayer
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />

            <LocationPicker
              onSelect={(lat, lon) => setLocation({ lat, lon })}
            />

            {location && (
              <Marker position={[location.lat, location.lon]} />
            )}
          </MapContainer>

          {location && (
            <p>
              Lat: {location.lat.toFixed(6)} | Lon: {location.lon.toFixed(6)}
            </p>
          )}
        </div>


        <button type="submit">Postavi Video</button>
      </form>
    </div>
  )
}

export default UploadVideo
