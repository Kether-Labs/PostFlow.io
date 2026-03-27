'use client'

import { useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { useAuthStore } from '@/store/auth.store'
import type { ReactNode } from 'react'

interface AuthGuardProps {
  children: ReactNode
}

export const AuthGuard = ({ children }: AuthGuardProps) => {
  const hasHydrated = useAuthStore(state => state.hasHydrated)
  const isAuthenticated = useAuthStore(state => state.isAuthenticated)
  const router = useRouter()

  useEffect(() => {
    if (hasHydrated && !isAuthenticated) {
      router.push('/login')
    }
  }, [hasHydrated, isAuthenticated, router])

  if (!hasHydrated) {
    return null
  }

  if (!isAuthenticated) {
    return null
  }

  return <>{children}</>
}
