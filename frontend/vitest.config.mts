import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true,
    // ATTENTION : Si ton setup est dans src, ajoute 'src/' devant
    setupFiles: ['./vitest.setup.ts'],
    alias: {
      // On pointe vers 'src' pour que @/components devienne src/components
      '@': path.resolve(__dirname, './src'),
    },
    coverage: {
      provider: 'v8',
      // On cible tout le code source
      include: ['src/**/*.{ts,tsx}'],
      // On exclut les fichiers de config Next.js du rapport de couverture
      exclude: [
        'src/app/layout.tsx',
        'src/app/providers.tsx',
        '**/*.d.ts',
        '**/*.test.tsx',
      ],
    },
  },
})
