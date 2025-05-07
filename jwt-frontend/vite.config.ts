import { defineConfig } from 'vite';

export default defineConfig({
  resolve: {
    alias: {
      // '@rollup/rollup-win32-x64-msvc' 요청을 rollup 패키지로 매핑
      '@rollup/rollup-win32-x64-msvc': 'rollup'
    }
  }
});