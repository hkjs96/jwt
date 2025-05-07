import { defineConfig } from 'vite';

import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    // SPA history mode를 위한 기본 fallback
    fs: { strict: true }
  }
});