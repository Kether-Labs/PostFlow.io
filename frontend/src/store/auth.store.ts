'use client'

import { create } from 'zustand'
import { createJSONStorage, persist } from 'zustand/middleware'
import type { User } from '@/features/auth/types/auth.types'

interface AuthState {
  user: User | null
  token: string | null
  isAuthenticated: boolean
  setUser: (user: User, token: string) => void
  clearAuth: () => void
}

const setAuthCookie = (user: User, token: string) => {
  if (typeof document === 'undefined') return

  const cookieValue = encodeURIComponent(
    JSON.stringify({ state: { token, user, isAuthenticated: true } })
  )

  document.cookie = `postflow-auth=${cookieValue}; path=/; max-age=${60 * 60 * 24 * 7}; SameSite=Lax`
}

const clearAuthCookie = () => {
  if (typeof document === 'undefined') return
  document.cookie = 'postflow-auth=; path=/; max-age=0'
}

export const useAuthStore = create<AuthState>()(
  persist(
    set => ({
      user: null,
      token: null,
      isAuthenticated: false,
      setUser: (user, token) => {
        setAuthCookie(user, token)
        set({ user, token, isAuthenticated: true })
      },
      clearAuth: () => {
        clearAuthCookie()
        set({ user: null, token: null, isAuthenticated: false })
      },
    }),
    {
      name: 'postflow-auth',
      storage: createJSONStorage(() => localStorage),
      partialize: state => ({
        token: state.token,
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
)
