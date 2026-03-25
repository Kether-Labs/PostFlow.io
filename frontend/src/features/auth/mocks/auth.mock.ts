import type {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
} from '@/features/auth/types/auth.types'

export const mockUser = {
  id: 'mock-user-001',
  name: 'Jean Dupont',
  email: 'jean@postflow.io',
}

export const mockUser2 = {
  id: 'mock-user-002',
  name: 'Sarah Bernard',
  email: 'sarah@postflow.io',
}

export const mockUser3 = {
  id: 'mock-user-003',
  name: 'Alex Martin',
  email: 'alex@postflow.io',
}

export const mockUsers = [mockUser, mockUser2, mockUser3] as const

export const mockLoginRequest = {
  email: mockUser.email,
  password: 'Test1234',
} satisfies LoginRequest

export const mockLoginRequest2 = {
  email: mockUser2.email,
  password: 'Test1234',
} satisfies LoginRequest

export const mockLoginRequest3 = {
  email: mockUser3.email,
  password: 'Test1234',
} satisfies LoginRequest

export const mockLoginCredentialsByEmail: Readonly<Record<string, string>> = {
  [mockLoginRequest.email]: mockLoginRequest.password,
  [mockLoginRequest2.email]: mockLoginRequest2.password,
  [mockLoginRequest3.email]: mockLoginRequest3.password,
}

export const mockRegisterRequest = {
  name: 'Marie Martin',
  email: 'marie@postflow.io',
  password: 'SecurePass123',
} satisfies RegisterRequest

export const mockLoginResponse: LoginResponse = {
  success: true,
  user: mockUser,
  token: 'mock-jwt-token-postflow-2025',
}

export const mockLoginResponse2: LoginResponse = {
  success: true,
  user: mockUser2,
  token: 'mock-jwt-token-postflow-2025-2',
}

export const mockLoginResponse3: LoginResponse = {
  success: true,
  user: mockUser3,
  token: 'mock-jwt-token-postflow-2025-3',
}

export const mockLoginResponsesByEmail: Readonly<Record<string, LoginResponse>> = {
  [mockUser.email]: mockLoginResponse,
  [mockUser2.email]: mockLoginResponse2,
  [mockUser3.email]: mockLoginResponse3,
}

export const mockRegisterResponse: RegisterResponse = {
  success: true,
  message: 'Compte créé avec succès',
  user: mockUser,
  token: 'mock-jwt-token-postflow-2025',
}
