'use client'

import { useAuthStore } from '@/store/auth.store'

export const useCurrentUser = () => {
  const { user, token, isAuthenticated } = useAuthStore()

  return { user, token, isAuthenticated }
}
