'use client'

import { create } from 'zustand'

type ThemeMode = 'light' | 'dark' | 'system'

interface UIState {
  sidebarOpen: boolean
  theme: ThemeMode
  toggleSidebar: () => void
  setSidebarOpen: (open: boolean) => void
  setTheme: (theme: ThemeMode) => void
}

export const useUIStore = create<UIState>(set => ({
  sidebarOpen: true,
  theme: 'system',
  toggleSidebar: () => set(state => ({ sidebarOpen: !state.sidebarOpen })),
  setSidebarOpen: sidebarOpen => set({ sidebarOpen }),
  setTheme: theme => set({ theme }),
}))
