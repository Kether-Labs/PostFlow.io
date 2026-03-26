export type {
  User,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
} from './types/auth.types'

<<<<<<< HEAD
// Schémas et types de formulaires (nouveaux)
export { loginSchema, registerSchema } from './types/auth.schemas'

export type { LoginFormData, RegisterFormData } from './types/auth.schemas'
=======
export {
  loginSchema,
  registerSchema,
} from './types/auth.schemas'

export type {
  LoginFormData,
  RegisterFormData,
} from './types/auth.schemas'

export { useLogin, useRegister, useLogout } from './hooks/useAuth'
>>>>>>> f22d01380321d6ba9c910c2baaa681bf96dc73a8
