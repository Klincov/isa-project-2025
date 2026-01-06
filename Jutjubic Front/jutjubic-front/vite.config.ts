import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/upload-video': {
        target: 'http://localhost:8080/upload-video',
        changeOrigin: true,
      },
    },
  },
})
