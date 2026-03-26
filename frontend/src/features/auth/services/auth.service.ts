import env from '@/config/env'
import {
  mockLoginCredentialsByEmail,
  mockLoginResponsesByEmail,
  mockRegisterResponse,
} from '@/features/auth/mocks/auth.mock'
import type {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
} from '@/features/auth/types/auth.types'

const MOCK_DELAY_MS = 600
<<<<<<< HEAD:frontend/src/services/auth.service.ts
=======

>>>>>>> f22d01380321d6ba9c910c2baaa681bf96dc73a8:frontend/src/features/auth/services/auth.service.ts
const mockDelay = () =>
  new Promise<void>(resolve => setTimeout(resolve, MOCK_DELAY_MS))

const getResponseErrorMessage = async (
  res: Response
): Promise<string | undefined> => {
  try {
    const data = (await res.clone().json()) as { message?: unknown }
    if (typeof data?.message === 'string') return data.message
  } catch {
    void 0
  }

  try {
    const text = await res.clone().text()
    if (!text) return
    return text
  } catch {
    return
  }
}

export const authService = {
  login: async ({ email, password }: LoginRequest): Promise<LoginResponse> => {
    if (env.useMock) {
      await mockDelay()

      if (!email.includes('@')) {
        throw new Error("Format d'email invalide")
      }

      if (password.length < 6) {
        throw new Error('Email ou mot de passe incorrect')
      }

      const expectedPassword = mockLoginCredentialsByEmail[email]
      if (!expectedPassword || expectedPassword !== password) {
        throw new Error('Email ou mot de passe incorrect')
      }

      const response = mockLoginResponsesByEmail[email]
      if (!response) {
        throw new Error('Email ou mot de passe incorrect')
      }

      return response
    }

    const res = await fetch(`${env.apiUrl}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    })

    if (!res.ok) {
      const message =
        (await getResponseErrorMessage(res)) ||
        'Email ou mot de passe incorrect'
      throw new Error(message)
    }

    return (await res.json()) as LoginResponse
  },

  register: async ({
    name,
    email,
    password,
  }: RegisterRequest): Promise<RegisterResponse> => {
    if (env.useMock) {
      await mockDelay()
      return mockRegisterResponse
    }

    const res = await fetch(`${env.apiUrl}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, email, password }),
    })

    if (!res.ok) {
      const message =
        (await getResponseErrorMessage(res)) ||
        'Erreur lors de la création du compte'
      throw new Error(message)
    }

    return (await res.json()) as RegisterResponse
  },

  logout: async (token?: string): Promise<void> => {
    if (env.useMock) {
      await mockDelay()
      return
    }

<<<<<<< HEAD:frontend/src/services/auth.service.ts
    const token =
      typeof window === 'undefined'
        ? null
        : localStorage.getItem('postflow-auth')

=======
>>>>>>> f22d01380321d6ba9c910c2baaa681bf96dc73a8:frontend/src/features/auth/services/auth.service.ts
    await fetch(`${env.apiUrl}/api/auth/logout`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
    })
  },
}
