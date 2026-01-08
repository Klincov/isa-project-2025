import React, { useState } from 'react'
import 'leaflet/dist/leaflet.css'
import { MapContainer, TileLayer, Marker, useMapEvents } from 'react-leaflet'
import "./UploadVideo.css"

const LocationPicker = ({ onSelect }: { onSelect: (lat: number, lon: number) => void }) => {
  useMapEvents({
    click(e) {
      onSelect(e.latlng.lat, e.latlng.lng)
    }
  })
  return null
}



const UploadVideo = () => {
  const [success, setSuccess] = useState<string>('')
  const [tagInput, setTagInput] = useState('')
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
      const API_URL = import.meta.env.VITE_API_URL as string

      const response = await fetch(`${API_URL}/upload-video`, {
        method: "POST",
        credentials: "include",
        body: formData,
      })

      if (!response.ok) {
        const err = await response.json()
        setError(err.message || 'Greška na serveru')
        return
      }


      const data = await response.json()

      setSuccess('Video je uspešno postavljen!')
      setError('')

      setTitle('')
      setDescription('')
      setTags([])
      setVideo(null)
      setThumbnail(null)
      setLocation(null)

      console.log('Video uspešno postavljen:', data)
    } catch (error) {
      setSuccess('')
      setError('Greška prilikom slanja zahteva')
    }
  }
  const addTag = () => {
    const trimmed = tagInput.trim()

    if (!trimmed) return
    if (tags.includes(trimmed)) return

    setTags([...tags, trimmed])
    setTagInput('')
  }

  const removeTag = (tagToRemove: string) => {
    setTags(tags.filter(tag => tag !== tagToRemove))
  }

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>, setFile: React.Dispatch<React.SetStateAction<File | null>>) => {
    const file = event.target.files ? event.target.files[0] : null
    if (file) {
      setFile(file)
    }
  }


  return (
    <div className="prompt">
      <h1>Upload Video</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label>Naslov:</label>
          <br></br>
          <input
            type="text"
            value={title}
            onChange={e => setTitle(e.target.value)}
            required
          />
        </div>

        <div>
          <label>Opis:</label>
          <br></br>

          <textarea
            value={description}
            onChange={e => setDescription(e.target.value)}
            required
          />
        </div>

        <div>
          <label>Tagovi:</label>

          <div style={{ display: 'flex', gap: '8px' }}>
            <input
              type="text"
              value={tagInput}
              onChange={e => setTagInput(e.target.value)}
              placeholder="Unesi tag"
            />
            <button type="button" onClick={addTag}>
              Dodaj tag
            </button>
          </div>

          <div style={{ marginTop: '8px', display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
            {tags.map(tag => (
              <span
                key={tag}
                style={{
                  padding: '4px 8px',
                  borderRadius: '6px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px'
                }}
              >
                {tag}
                <button
                  type="button"
                  onClick={() => removeTag(tag)}
                  style={{
                    border: 'none',
                    cursor: 'pointer',
                    fontWeight: 'bold'
                  }}
                >
                  ✕
                </button>
              </span>
            ))}
          </div>
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
