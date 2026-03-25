'use client'

import { useMutation } from '@tanstack/react-query'
import { useRouter } from 'next/navigation'
import { authService } from '@/features/auth/services/auth.service'
import { useAuthStore } from '@/store/auth.store'
import type {
  LoginRequest,
  RegisterRequest,
} from '@/features/auth/types/auth.types'

export const useLogin = () => {
  const setUser = useAuthStore(state => state.setUser)
  const router = useRouter()

  return useMutation({
    mutationFn: (credentials: LoginRequest) => authService.login(credentials),
    onSuccess: data => {
      setUser(data.user, data.token)
      router.push('/dashboard')
    },
    onError: (error: Error) => {
      console.error('Erreur login:', error.message)
    },
  })
}

export const useRegister = () => {
  const setUser = useAuthStore(state => state.setUser)
  const router = useRouter()

  return useMutation({
    mutationFn: (data: RegisterRequest) => authService.register(data),
    onSuccess: data => {
      setUser(data.user, data.token)
      router.push('/dashboard')
    },
    onError: (error: Error) => {
      console.error('Erreur register:', error.message)
    },
  })
}

export const useLogout = () => {
  const clearAuth = useAuthStore(state => state.clearAuth)
  const token = useAuthStore(state => state.token)
  const router = useRouter()

  return useMutation({
    mutationFn: () => authService.logout(token || undefined),
    onSuccess: () => {
      clearAuth()
      router.push('/login')
    },
  })
}
