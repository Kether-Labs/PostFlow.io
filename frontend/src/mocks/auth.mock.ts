import type {
  LoginResponse,
  RegisterResponse,
} from '@/components/features/auth/types/auth.types'

export const mockUser = {
  id: 'mock-user-001',
  name: 'Jean Dupont',
  email: 'jean@postflow.io',
}

export const mockLoginResponse: LoginResponse = {
  success: true,
  user: mockUser,
  token: 'mock-jwt-token-postflow-2025',
}

export const mockRegisterResponse: RegisterResponse = {
  success: true,
  message: 'Compte créé avec succès',
  user: mockUser,
  token: 'mock-jwt-token-postflow-2025',
}
